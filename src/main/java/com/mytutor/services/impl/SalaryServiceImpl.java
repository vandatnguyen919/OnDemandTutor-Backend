package com.mytutor.services.impl;

import com.mytutor.constants.AccountStatus;
import com.mytutor.constants.Role;
import com.mytutor.constants.WithdrawRequestStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.salary.RequestWithdrawRequestDto;
import com.mytutor.dto.salary.ResponseWithdrawRequestDto;
import com.mytutor.dto.salary.UpdateWithdrawRequestDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.WithdrawRequest;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.InvalidStatusException;
import com.mytutor.exceptions.WithdrawRequestNotFoundException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.WithdrawRequestRepository;
import com.mytutor.services.SalaryService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author vothimaihoa
 */
@Service
public class SalaryServiceImpl implements SalaryService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AccountRepository accountRepository;

    @Value("${mytutor.url.client}")
    private String clientUrl;

    @Value("${spring.mail.username}")
    private String myTutorSenderEmail;

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;

    @Override
    public double getMonthlySalaryOfATutor(Integer tutorId, Integer month, Integer year) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDate = startDate.plusMonths(1);
        List<Appointment> appointments = appointmentRepository.findAppointmentsInTimeRangeByTutor(tutorId, startDate, endDate);
        return getTotalIncome(tutorId, appointments);
    }

    // This function is automatically called at 00:00 of 5th of every month
    @Scheduled(cron = "0 0 0 5 * ?", zone = "Asia/Ho_Chi_Minh") // This cron expression runs at midnight on the 5th of every month
    public void scheduleSalaryAnnouncement() {
        LocalDate today = LocalDate.now();
        int month = today.minusMonths(1).getMonthValue();
        int year = today.minusMonths(1).getYear();

        sendSalaryAnnouncementEmail(month, year);
    }

    @Override
    public void sendSalaryAnnouncementEmail(Integer month, Integer year) {
        // How to send the same email to all tutor not cost much time and performance => send common announcement only and use BCC
        // the email only has a button to go to MyTutor and message reminds that they must go to profile session to fill the form
        // profile session has a button to get money, this button will redirect to the transaction info form page

        // 1. check current date is date 5 and (month = current month - 1, year = year) OR (current month = 1, month = 12, year = current year -1)
        LocalDate today = LocalDate.now();
//        if (today.getDayOfMonth() != 5) {
//            throw new InvalidStatusException("Today is not the date for Salary Announcement");
//        }
        int maxPaymentMonth = today.minusMonths(1).getMonthValue(); // payment request must be from the previous month
        int maxPaymentYear = today.minusMonths(1).getMonthValue();
        if (!(month <= maxPaymentMonth && year <= maxPaymentYear)) {
            throw new InvalidStatusException("This month and year is not allowed to pay salary for this Salary Announcement");
        }
        // send email
        List<Account> activeTutors = accountRepository.findByRoleAndStatus(Role.TUTOR, AccountStatus.ACTIVE);
        String subject = getSalaryAnnouncementEmailContent(month, year)[0];
        String content = getSalaryAnnouncementEmailContent(month, year)[1];
        sendEmail(activeTutors, subject, content);
    }

    private String[] getSalaryAnnouncementEmailContent(Integer month, Integer year) {
        String subject = "[MyTutor] Salary Announcement for month " + month + " year " + year;
        String content = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Salary Announcement</title>\n" +
                getStyle() +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Salary Announcement</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear Tutor,</p>\n" +
                "            <p>It's time to get your income at MyTutor for " + month + "/" + year + "!</p>\n" +
                "            <p>Please visit your profile session on MyTutor to fill out the necessary forms to receive your salary.</p>\n" +
                "            <div class=\"center-text\">\n" +
                "                <a href=\"" + clientUrl + "/tutor-profile" + "\" class=\"button\">Go to Tutor Profile to get Salary</a>\n" +
                "            </div>\n" +
                "            <p>Thank you for your hard work and dedication.</p>\n" +
                "        </div>\n" +
                "           " +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
        String[] subjectAndContent = new String[2];
        subjectAndContent[0] = subject;
        subjectAndContent[1] = content;
        return subjectAndContent;
    }

    @Override
    public void sendWithdrawRequestEmail(UpdateWithdrawRequestDto requestToUpdateDto) {
        WithdrawRequest withdrawRequest = withdrawRequestRepository.findById(requestToUpdateDto.getWithdrawRequestId())
                .orElseThrow(() -> new WithdrawRequestNotFoundException("Withdraw Not Found!"));
        Account tutor = withdrawRequest.getTutor();
        List<Account> tutorCastedTolist = new ArrayList<>();
        tutorCastedTolist.add(tutor);
        String subject = getWithdrawRequestEmailContent(withdrawRequest, requestToUpdateDto)[0];
        String content = getWithdrawRequestEmailContent(withdrawRequest, requestToUpdateDto)[1];
        sendEmail(tutorCastedTolist, subject, content);
    }

    private String[] getWithdrawRequestEmailContent(WithdrawRequest withdrawRequest, UpdateWithdrawRequestDto requestToUpdateDto) {
        String subject = "[MyTutor] Withdraw Request Announcement";
        String content;


        if ("REJECTED".equalsIgnoreCase(requestToUpdateDto.getUpdatedStatus())) {
            content = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Withdraw Request Announcement</title>\n" +
                    getStyle() +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Withdraw Request Update</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Dear Tutor,</p>\n" +
                    "            <p>We regret to inform you that your withdraw request of month " + withdrawRequest.getMonth() + "/" + withdrawRequest.getYear() + " has been <strong>rejected</strong>.</p>\n" +
                    "            <p><strong>Reason for rejection:</strong> " + requestToUpdateDto.getRejectReason() + "</p>\n" +
                    "            <p>If you have any questions, please contact our support team.</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p>© 2024 MyTutor. All rights reserved.</p>\n" +
                    "            <p><a href=\"" + clientUrl + "\" class=\"button\">Visit Our Website</a></p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";
        } else {
            content = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    getStyle() +
                    "</head>\n" +
                    "<body>\n" +
                    "    <div class=\"container\">\n" +
                    "        <div class=\"header\">\n" +
                    "            <h1>Withdraw Request Update</h1>\n" +
                    "        </div>\n" +
                    "        <div class=\"content\">\n" +
                    "            <p>Dear Tutor,</p>\n" +
                    "            <p>We are pleased to inform you that your withdraw request of month " + withdrawRequest.getMonth() + "/" + withdrawRequest.getYear() + " has been <strong>approved</strong> and processed.</p>\n" +
                    "            <p>Please check your transaction history for more details.</p>\n" +
                    "            <p>If you have any questions, please contact our support team.</p>\n" +
                    "        </div>\n" +
                    "        <div class=\"footer\">\n" +
                    "            <p>© 2024 MyTutor. All rights reserved.</p>\n" +
                    "            <p><a href=\"" + clientUrl + "\" class=\"button\">Visit Our Website</a></p>\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "</body>\n" +
                    "</html>\n";
        }

        String[] subjectAndContent = new String[2];
        subjectAndContent[0] = subject;
        subjectAndContent[1] = content;
        return subjectAndContent;
    }

    private String getStyle() {
        return "<style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f3f2f7;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100%;\n" +
                "            max-width: 600px;\n" +
                "            margin: 20px auto;\n" +
                "            background-color: #ffffff;\n" +
                "            padding: 20px;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            color: #ffffff;\n" +
                "            padding: 10px 0;\n" +
                "            text-align: center;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            margin-top: 20px;\n" +
                "            text-align: center;\n" +
                "            color: #777;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 10px 20px;\n" +
                "            margin-top: 10px;\n" +
                "            font-size: 16px;\n" +
                "            color: #ffffff !important;\n" +
                "            background: linear-gradient(90deg, #672DEF 0%, #FA6EAD 100%);\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 5px;\n" +
                "        }\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        th, td {\n" +
                "            border: 1px solid #ddd;\n" +
                "            padding: 8px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        th {\n" +
                "            background-color: #672DEF;\n" +
                "            color: #ffffff;\n" +
                "        }\n" +
                "        .appointment-details {\n" +
                "            font-size: 1.2em;\n" +
                "            text-align: center;\n" +
                "            font-weight: bold;\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "        .center-text {\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "    </style>\n";
    }

    private void sendEmail(List<Account> tutors, String subject, String content) {
        String[] emailsOfTutors = new String[tutors.size()];
        for (int i = 0; i < tutors.size(); i++) {
            emailsOfTutors[i] = tutors.get(i).getEmail();
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(myTutorSenderEmail);
            helper.setBcc(emailsOfTutors);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
    }

    @Override
    public ResponseWithdrawRequestDto createWithdrawRequest(Principal principal, Integer tutorId, RequestWithdrawRequestDto withdrawRequest) {
        // 1. check current user has same tutor id as tutorId
        Account tutor = accountRepository.findById(tutorId).orElseThrow( () -> new AccountNotFoundException("Account not found!"));
//        if (!principal.getName().equals(tutor.getEmail())) {
//            throw new AccountNotFoundException("You are not permitted to perform this operation on this account!");
//        }

        // 2. check current user has withdraw request for this month, this year in status DONE or PROCESSING yet
        WithdrawRequest existedDoneRequest = withdrawRequestRepository.findByTutorAndMonthAndYearAndStatus(tutor, withdrawRequest.getMonth(), withdrawRequest.getYear(), WithdrawRequestStatus.DONE);
        WithdrawRequest existedProcessingRequest = withdrawRequestRepository.findByTutorAndMonthAndYearAndStatus(tutor, withdrawRequest.getMonth(), withdrawRequest.getYear(), WithdrawRequestStatus.PROCESSING);
        if (existedDoneRequest != null || existedProcessingRequest != null) {
            throw new InvalidStatusException("You already requested for withdraw in this month!");
        }

        // check current date is not (>= 5th of the month > month in the form) -> throw error
        LocalDate today = LocalDate.now();
        LocalDate startDateToGetSalary = LocalDate.of(withdrawRequest.getYear(), withdrawRequest.getMonth() + 1, 5);
        if (withdrawRequest.getMonth() == 12) {
            startDateToGetSalary = LocalDate.of(withdrawRequest.getYear() + 1, 1, 5);
        }
        if (today.isBefore(startDateToGetSalary)) {
            throw new InvalidStatusException("You are not permitted to request salary in this time interval");
        }

        // insert into withdraw_request table
        WithdrawRequest newWithdrawRequest = withdrawRequest.mapToEntity(tutor);

        // set amount using getMonthlySalaryOfATutor in this service
        newWithdrawRequest.setCreatedAt(LocalDateTime.now());
        newWithdrawRequest.setAmount(getMonthlySalaryOfATutor(tutor.getId(), withdrawRequest.getMonth(), withdrawRequest.getYear()));
        withdrawRequestRepository.save(newWithdrawRequest);

        // map to response dto and return
        return new ResponseWithdrawRequestDto(newWithdrawRequest, tutor);
    }

    @Override
    public PaginationDto<ResponseWithdrawRequestDto> getListOfWithdrawRequest(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        // get paginated withdraw requests from repository
        Page<WithdrawRequest> listOfWithdrawRequest = withdrawRequestRepository.findAndSortByStatusAndTime(pageable);

        // convert to pagination dto and return
        return getPaginationDto(listOfWithdrawRequest);
    }

    private PaginationDto<ResponseWithdrawRequestDto> getPaginationDto(Page<WithdrawRequest> withdrawRequests) {
        List<WithdrawRequest> listOfAppointments = withdrawRequests.getContent();

        List<ResponseWithdrawRequestDto> content = listOfAppointments.stream()
                .map(w -> {
                    Account tutor = accountRepository.findById(w.getTutor().getId())
                            .orElseThrow(() -> new AccountNotFoundException("Tutor Not Found!"));
                    return new ResponseWithdrawRequestDto(w, tutor);
                })
                .collect(Collectors.toList());

        PaginationDto<ResponseWithdrawRequestDto> withdrawRequestPaginationDto = new PaginationDto<>();
        withdrawRequestPaginationDto.setContent(content);
        withdrawRequestPaginationDto.setPageNo(withdrawRequests.getNumber());
        withdrawRequestPaginationDto.setPageSize(withdrawRequests.getSize());
        withdrawRequestPaginationDto.setTotalElements(withdrawRequests.getTotalElements());
        withdrawRequestPaginationDto.setTotalPages(withdrawRequests.getTotalPages());
        withdrawRequestPaginationDto.setLast(withdrawRequests.isLast());

        return withdrawRequestPaginationDto;
    }

    @Override
    public ResponseWithdrawRequestDto updateWithdrawRequest(UpdateWithdrawRequestDto requestToUpdateDto) {
        // get request to update
        WithdrawRequest requestToUpdate = withdrawRequestRepository.findById(requestToUpdateDto.getWithdrawRequestId())
                .orElseThrow(() -> new WithdrawRequestNotFoundException("Withdraw Request Not Found!"));

        if (!requestToUpdate.getStatus().equals(WithdrawRequestStatus.PROCESSING)) {
            throw new InvalidStatusException("This withdraw request has been resolved!");
        }

        // change status
        String newStatus = requestToUpdateDto.getUpdatedStatus();
        if (!newStatus.equalsIgnoreCase("rejected") &&
                !newStatus.equalsIgnoreCase("processing")&&
                !newStatus.equalsIgnoreCase("done")) {
            throw new InvalidStatusException("Invalid status!");
        }

        requestToUpdate.setStatus(WithdrawRequestStatus.valueOf(newStatus.toUpperCase()));
        if (requestToUpdate.getStatus().equals(WithdrawRequestStatus.DONE)) {
            requestToUpdate.setSalaryPaidProvider(requestToUpdateDto.getSalaryPaidProvider());
            requestToUpdate.setSalaryPaidTransactionId(requestToUpdateDto.getSalaryPaidTransactionId());
        }
        withdrawRequestRepository.save(requestToUpdate);
        return new ResponseWithdrawRequestDto(requestToUpdate, requestToUpdate.getTutor());
    }

    public double getTotalIncome(int tutorId, List<Appointment> appointments) {
        double income = 0;

        for (Appointment a : appointments) {
            Account tutor = a.getTutor();
            if (tutor.getId() == tutorId) {
                income += a.getTuition() * (100 - a.getTutor().getTutorDetail().getPercentage()) / 100;
            }
        }
        return income;
    }
}
