package com.mytutor.services.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mytutor.config.VNPayConfig;
import com.mytutor.constants.AppointmentStatus;
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
import com.mytutor.services.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public ResponseEntity<?> createPayment(Principal principal, HttpServletRequest req, Integer appointmentId) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT cannot be found or trusted");
        }
        Account payer = accountRepository.findByEmail(principal.getName()).orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        int tutorId = appointment.getTutor().getId();
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Tutor not found"));

        long amount = appointment.getTuition().longValue();

        return createPaymentWithVNPay(amount, req);
    }

    @Override
    @Transactional
    public ResponseEntity<?> checkVNPayPayment(Principal principal,
                                               HttpServletRequest req,
                                               String vnp_TxnRef,
                                               String vnp_TransDate) throws IOException {
        String vnp_RequestId = VNPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        //String vnp_TransactionNo = req.getParameter("transactionNo");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

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

        String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hash_Data);

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(VNPayConfig.vnp_ApiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
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

        String resTranStatus = jsonObject.get("vnp_TransactionStatus").getAsString();

        if (!"00".equals(resTranStatus)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed");
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

        return processToDatabase(currentAppointment, vnp_TxnRef, vnp_TransDate);
    }

    public ResponseEntity<?> processToDatabase(Appointment appointment,
                                               String transactionId,
                                               String transactionDate) {
        appointment.setStatus(AppointmentStatus.PAID);
        Payment payment = new Payment();
        payment.setMoneyAmount(appointment.getTuition());
        payment.setTransactionTime(LocalDateTime.now());
        payment.setTransactionId(transactionId);
        payment.setTransactionDate(transactionDate);
        payment.setAppointment(appointment);
        payment.setProvider("VNPay");

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

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
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
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        ResponsePaymentDto responsePaymentDto = new ResponsePaymentDto();
        responsePaymentDto.setPaymentUrl(paymentUrl);

        return ResponseEntity.status(HttpStatus.OK).body(responsePaymentDto);
    }
}
