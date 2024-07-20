package com.mytutor.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mytutor.config.MomoConfig;
import com.mytutor.config.VNPayConfig;
import com.mytutor.constants.AppointmentStatus;
import com.mytutor.constants.PaymentProvider;
import com.mytutor.dto.payment.CompletedPaypalOrderDto;
import com.mytutor.dto.payment.ResponsePaymentDto;
import com.mytutor.dto.payment.ResponseTransactionDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.Payment;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.AppointmentNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.PaymentRepository;
import com.mytutor.services.AppointmentService;
import com.mytutor.services.ExrateService;
import com.mytutor.services.PaymentService;
import com.mytutor.utils.EncryptionUtils;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PayPalHttpClient payPalHttpClient;

    @Autowired
    private ExrateService exrateService;

    @Value("${mytutor.url.client}")
    private String clientUrl;

    @Value("${mytutor.url.confirm}")
    private String confirmUrl;

    @Value("${paypal.feePercentage}")
    private double feePercentage;

    @Value("${paypal.fixedFee}")
    private double fixedFee;

    @Override
    public ResponseEntity<?> createPayment(Principal principal, HttpServletRequest req, Integer appointmentId, PaymentProvider provider) {
        if (principal == null) {
            throw new BadCredentialsException("Token cannot be found or trusted");
        }
        Account payer = accountRepository.findByEmail(principal.getName()).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        int tutorId = appointment.getTutor().getId();
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        long amount = appointment.getTuition().longValue();

        if (provider == PaymentProvider.VNPAY)
            return createPaymentWithVNPay(amount, req);
        else if (provider == PaymentProvider.MOMO)
            return createPaymentWithMoMo(amount);
        else if (provider == PaymentProvider.PAYPAL) {
            double totalTuition = getTuitionInDollarPlusPayPalFee(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createPaymentWithPaypal(totalTuition));
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private double getTuitionInDollarPlusPayPalFee(Appointment appointment) {
        String exrateString = (String)exrateService.getExrateByCurrencyCode("USD").get("Transfer");
        String cleanedString = exrateString.replaceAll(",", "");
        Double exrate = Double.parseDouble(cleanedString);
        double tuitionInDollar = appointment.getTuition() / exrate;

        // double feePercentage = 0.044; // 4.4%
        // double fixedFee = 0.30; // Fixed fee in USD for international transactions
        double totalFee =  (tuitionInDollar * feePercentage) + fixedFee;
        double totalAmount = tuitionInDollar + totalFee;
        return Math.round(totalAmount * 100) / 100.0;
    }

    @Override
    @Transactional
    public ResponseEntity<?> checkVNPayPayment(Principal principal, HttpServletRequest req, String vnp_TxnRef, String vnp_TransDate) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token cannot be found or trusted");
        }

        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        //String vnp_TransactionNo = req.getParameter("transactionNo");

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);

        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject();

        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
        //vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = EncryptionUtils.hmacSHA512(VNPayConfig.secretKey, hash_Data);

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(VNPayConfig.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuilder response = new StringBuilder();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response);

        JsonObject jsonObject = JsonParser.parseString(String.valueOf(response)).getAsJsonObject();

        String resCode = jsonObject.get("vnp_ResponseCode").getAsString();
        String resMessage = jsonObject.get("vnp_Message").getAsString();

        if (!"00".equals(resCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resMessage);  // message "Request is duplicated", if there are multiple requests to check payment
        }

        // get current payment
        Account payer = accountRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        List<Appointment> appointments = appointmentRepository
                .findAppointmentsWithPendingPayment(payer.getId(), AppointmentStatus.PENDING_PAYMENT);

        if (appointments == null || appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no pending payment");
        }

        Appointment currentAppointment = appointments.get(0);

        String resTranStatus = jsonObject.get("vnp_TransactionStatus").getAsString();

        if (!"00".equals(resTranStatus)) {
            appointmentService.rollbackAppointment(currentAppointment);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed");
        }

        return processToDatabase(currentAppointment, vnp_TxnRef, vnp_TransDate, PaymentProvider.VNPAY);
    }

    @Override
    public ResponseEntity<?> checkMomoPayment(Principal principal, String orderId) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token cannot be found or trusted");
        }

        String rawData = generateRawHashDataForQuery(MomoConfig.momo_AccessKey, orderId, MomoConfig.momo_PartnerCode, orderId);

        // Calculate the HMAC SHA-256 signature
        String signature = EncryptionUtils.hmacSHA256(MomoConfig.momo_SecretKey, rawData);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", MomoConfig.momo_PartnerCode);
        requestBody.put("requestId", orderId);
        requestBody.put("orderId", orderId);
        requestBody.put("lang", "en");
        requestBody.put("signature", signature);

        // Set headers for JSON content
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP entity for the request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Use RestTemplate to send the POST request to MoMo
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(MomoConfig.momo_QueryApiUrl, requestEntity, Map.class);

        try {
            // get current payment
            Account payer = accountRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            List<Appointment> appointments = appointmentRepository
                    .findAppointmentsWithPendingPayment(payer.getId(), AppointmentStatus.PENDING_PAYMENT);

            if (appointments == null || appointments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is no pending payment");
            }
            Appointment currentAppointment = appointments.get(0);

            Map body = response.getBody();
            int resultCode = (Integer) body.get("resultCode");
            String message = (String) body.get("message");

            if (resultCode != 0) {
                appointmentService.rollbackAppointment(currentAppointment);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }

            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String transactionDate = now.format(formatter);

            return processToDatabase(currentAppointment, orderId, transactionDate, PaymentProvider.MOMO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! Message: " + ex.getMessage());
        }
    }

    public ResponseEntity<?> processToDatabase(Appointment appointment, String transactionId, String transactionDate, PaymentProvider provider) {
        appointment.setStatus(AppointmentStatus.PAID);
        Payment payment = new Payment();
        payment.setMoneyAmount(appointment.getTuition());
        payment.setTransactionTime(LocalDateTime.now());
        payment.setTransactionId(transactionId);
        payment.setTransactionDate(transactionDate);
        payment.setAppointment(appointment);
        payment.setProvider(provider.toString());

        appointment.getPayments().add(payment);
        paymentRepository.save(payment);
        appointmentRepository.save(appointment);

        return ResponseEntity.status(HttpStatus.OK)
                .body(modelMapper.map(payment, ResponseTransactionDto.class));
    }

    private ResponseEntity<?> createPaymentWithVNPay(long amountParam, HttpServletRequest req) {
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_Command = "pay";

        long amount = amountParam * 100;

        String vnp_IpAddr = VNPayConfig.getIpAddress(req);

        //create param for vnpay
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_BankCode", "NCB"); // default NCB for testing
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        String locate = req.getParameter("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_OrderType", "other");   //optional
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);

        ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String vnp_CreateDate = now.format(formatter);

        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        ZonedDateTime expireDate = now.plusMinutes(15);

        String vnp_ExpireDate = expireDate.format(formatter);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);

        //encode all fields for export url
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();

        //create hash for checksum
        String vnp_SecureHash = EncryptionUtils.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        ResponsePaymentDto responsePaymentDto = new ResponsePaymentDto();
        responsePaymentDto.setProvider(PaymentProvider.VNPAY);
        responsePaymentDto.setPaymentUrl(paymentUrl);
        System.out.println(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(responsePaymentDto);
    }

    private ResponseEntity<?> createPaymentWithMoMo(long amount) {
        // MoMo parameters
        String orderInfo = "MyTutor - Pay with MoMo";
        String extraData = "";
        String orderId = MomoConfig.momo_PartnerCode + System.currentTimeMillis();

        // Create the raw data for the signature
        String rawData = generateRawHashData(MomoConfig.momo_AccessKey, amount, extraData, MomoConfig.momo_IpnUrl, orderId, orderInfo, MomoConfig.momo_PartnerCode, MomoConfig.momo_RedirectUrl, orderId, MomoConfig.momo_RequestType);

        // Calculate the HMAC SHA-256 signature
        String signature = EncryptionUtils.hmacSHA256(MomoConfig.momo_SecretKey, rawData);

        // Create the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", MomoConfig.momo_PartnerCode);
        requestBody.put("accessKey", MomoConfig.momo_AccessKey);
        requestBody.put("requestId", orderId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("orderExpireTime", 15);
        requestBody.put("redirectUrl", MomoConfig.momo_RedirectUrl);
        requestBody.put("ipnUrl", MomoConfig.momo_IpnUrl);
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", MomoConfig.momo_RequestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");

        // Set headers for JSON content
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP entity for the request
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Use RestTemplate to send the POST request to MoMo
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(MomoConfig.momo_ApiUrl, requestEntity, Map.class);

        try {
            String paymentUrl = (String) response.getBody().get("payUrl");

            ResponsePaymentDto responsePaymentDto = new ResponsePaymentDto();
            responsePaymentDto.setProvider(PaymentProvider.MOMO);
            responsePaymentDto.setPaymentUrl(paymentUrl);

            return ResponseEntity.status(HttpStatus.OK).body(responsePaymentDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong! Message: " + ex.getMessage());
        }
    }

    public String generateRawHashData(String accessKey, long amount, String extraData, String ipnUrl,
                                         String orderId, String orderInfo, String partnerCode,
                                         String redirectUrl, String requestId, String requestType) {
        return "accessKey=" + accessKey + "&amount=" + amount + "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl + "&orderId=" + orderId + "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode + "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId + "&requestType=" + requestType;
    }

    public String generateRawHashDataForQuery(String accessKey, String orderId, String partnerCode, String requestId) {
        return "accessKey=" + accessKey + "&orderId=" + orderId + "&partnerCode=" + partnerCode + "&requestId=" + requestId;
    }

    public ResponsePaymentDto createPaymentWithPaypal(double fee) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");
        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown().currencyCode("USD").value(String.valueOf(fee));
        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amountBreakdown);
        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(clientUrl + confirmUrl) // link phía FE cho màn hình thanh toán ok
                .cancelUrl(clientUrl + confirmUrl);
        orderRequest.applicationContext(applicationContext);
        OrdersCreateRequest ordersCreateRequest = new OrdersCreateRequest().requestBody(orderRequest);

        try {
            HttpResponse<com.paypal.orders.Order> orderHttpResponse = payPalHttpClient.execute(ordersCreateRequest);
            com.paypal.orders.Order order = orderHttpResponse.result();

            String redirectUrl = order.links().stream()
                    .filter(link -> "approve".equals(link.rel()))
                    .findFirst()
                    .orElseThrow(NoSuchElementException::new)
                    .href();

            return new ResponsePaymentDto(PaymentProvider.PAYPAL, redirectUrl);
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponsePaymentDto(PaymentProvider.PAYPAL, "");
        }
    }


    public ResponseEntity<?> checkPaypalPayment(Principal principal, String token) {
        try {
            // get current payment
            Account payer = accountRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            List<Appointment> appointments = appointmentRepository
                    .findAppointmentsWithPendingPayment(payer.getId(), AppointmentStatus.PENDING_PAYMENT);

            if (appointments == null || appointments.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CompletedPaypalOrderDto("There is no pending payment"));
            }
            Appointment currentAppointment = appointments.get(0);

            // Get the order details
            OrdersGetRequest ordersGetRequest = new OrdersGetRequest(token);
            HttpResponse<com.paypal.orders.Order> getOrderResponse = payPalHttpClient.execute(ordersGetRequest);
            com.paypal.orders.Order order = getOrderResponse.result();

            if (order.status().equalsIgnoreCase("APPROVED")) {
                // Capture the order
                OrdersCaptureRequest ordersCaptureRequest = new OrdersCaptureRequest(token);
                HttpResponse<com.paypal.orders.Order> httpResponse = payPalHttpClient.execute(ordersCaptureRequest);
                if (httpResponse.result().status().equalsIgnoreCase("COMPLETED")) {
                    ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
                    ZonedDateTime now = ZonedDateTime.now(zoneId);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    String transactionDate = now.format(formatter);

                    return processToDatabase(currentAppointment, token, transactionDate, PaymentProvider.PAYPAL);
                } else if (httpResponse.result().status().equalsIgnoreCase("VOIDED")) {
                    appointmentService.rollbackAppointment(currentAppointment);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment was voided!");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment was not successful!");
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order not approved by the user");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CompletedPaypalOrderDto("error"));
        }
    }

}
