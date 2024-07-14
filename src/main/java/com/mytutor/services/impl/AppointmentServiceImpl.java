package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.constants.Role;
import com.mytutor.constants.WithdrawRequestStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.SubjectDto;
import com.mytutor.dto.appointment.AppointmentSlotDto;
import com.mytutor.dto.appointment.InputAppointmentDto;
import com.mytutor.dto.appointment.RequestReScheduleDto;
import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.dto.statistics.StudentLessonStatisticDto;
import com.mytutor.dto.statistics.TutorLessonStatisticDto;
import com.mytutor.entities.*;
import com.mytutor.exceptions.*;
import com.mytutor.repositories.*;
import com.mytutor.services.AppointmentService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author vothimaihoa
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private WithdrawRequestRepository withdrawRequestRepository;

    @Value("${mytutor.url.client}")
    private String clientUrl;

    @Override
    public ResponseAppointmentDto getAppointmentById(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        ResponseAppointmentDto dto = ResponseAppointmentDto.mapToDto(appointment);
        return dto;
    }

    @Override
    public PaginationDto<ResponseAppointmentDto> getAppointmentsByAccountId(Integer accountId,
                                                                                            AppointmentStatus status,
                                                                                            Integer pageNo,
                                                                                            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments;
        appointments = appointmentRepository.findAppointmentByAccountId(accountId, status, pageable);
        return getPaginationDto(appointments);
    }

    @Override
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointments(AppointmentStatus status,
                                                                                 Integer pageNo,
                                                                                 Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments;
        appointments = appointmentRepository.findAppointments(status, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(getPaginationDto(appointments));
    }

    @Override
    public ResponseEntity<StudentLessonStatisticDto> getStudentStatistics(Integer studentId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsInTimeRangeByStudent(
                studentId, null, null);
        StudentLessonStatisticDto dto = new StudentLessonStatisticDto();
        dto.setAccountId(studentId);
        List<SubjectDto> subjectDtos = new ArrayList<>();
        if (!appointments.isEmpty()) {
            Set<Subject> subjects = getSubjectsFromAppointments(appointments);
            for (Subject s : subjects) {
                subjectDtos.add(SubjectDto.mapToDto(s));
            }
            Set<Account> tutors = getTutorsFromAppointments(appointments);

            // total
            dto.setTotalSubjects(subjectDtos);
            dto.setTotalLessons(getTotalLessons(appointments));
            dto.setTotalLearntTutor(tutors.size());
        }

        // current month
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        System.out.println(startDate);
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Appointment> thisMonthAppointments = appointmentRepository.findAppointmentsInTimeRangeByStudent(
                studentId, startDate, endDate
        );
        List<SubjectDto> thisMonthSubjectDtos = new ArrayList<>();
        if (!thisMonthAppointments.isEmpty()) {
            Set<Subject> thisMonthSubjects = getSubjectsFromAppointments(thisMonthAppointments);
            Set<Account> thisMonthTutors = getTutorsFromAppointments(thisMonthAppointments);
            for (Subject s : thisMonthSubjects) {
                thisMonthSubjectDtos.add(SubjectDto.mapToDto(s));
            }
            dto.setThisMonthSubjects(thisMonthSubjectDtos);
            dto.setThisMonthLessons(getTotalLessons(thisMonthAppointments));
            dto.setThisMonthTutor(thisMonthTutors.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Override
    public TutorLessonStatisticDto getTutorStatistics(Integer tutorId, Integer month, Integer year) {
        Account tutor = accountRepository.findById(tutorId).orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        TutorLessonStatisticDto dto = new TutorLessonStatisticDto();
        dto.setAccountId(tutorId);
        List<Appointment> appointments;
        if (month == null && year == null) {
            appointments = appointmentRepository.findAppointmentsInTimeRangeByTutor(tutorId, null, null);
        }
        // if month and year is specified
        else {
            LocalDateTime startDate = LocalDate.of(year, month, 1).atStartOfDay();
            LocalDateTime endDate = LocalDate.of(year, month + 1, 1).atStartOfDay();

            appointments = appointmentRepository.findAppointmentsInTimeRangeByTutor(
                    tutorId, startDate, endDate
            );
            WithdrawRequest withdrawRequest = withdrawRequestRepository.findTopByTutorAndMonthAndYearOrderByCreatedAtDesc(tutor, month, year);
            if (withdrawRequest == null) {
                dto.setWithdrawRequestStatus("notRequested");
            }
            else {
            dto.setWithdrawRequestStatus(withdrawRequest.getStatus().toString());
            }
        }
        List<SubjectDto> subjectDtos = new ArrayList<>();
        if (!appointments.isEmpty()) {
            Set<Subject> subjects = getSubjectsFromAppointments(appointments);
            Set<Account> students = getStudentsFromAppointments(appointments);
            for (Subject s : subjects) {
                subjectDtos.add(SubjectDto.mapToDto(s));
            }
            dto.setTotalSubjects(subjectDtos);
            dto.setTotalTaughtStudent(students.size());
            dto.setTotalLessons(getTotalLessons(appointments));
            dto.setTotalIncome(getTotalIncome(tutorId, appointments));

        }
        return dto;
    }

    private Set<Subject> getSubjectsFromAppointments(List<Appointment> appointments) {
        Set<Subject> subjects = new HashSet<>();
        for (Appointment a : appointments) {
            subjects.add(a.getSubject());
        }
        return subjects;
    }

    private Set<Account> getStudentsFromAppointments(List<Appointment> appointments) {
        Set<Account> students = new HashSet<>();
        for (Appointment a : appointments) {
            students.add(a.getStudent());
        }
        return students;
    }

    private Set<Account> getTutorsFromAppointments(List<Appointment> appointments) {
        Set<Account> tutors = new HashSet<>();
        for (Appointment a : appointments) {
            tutors.add(a.getTutor());
        }
        return tutors;
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

    private int getTotalLessons(List<Appointment> appointments) {
        int count = 0;
        for (Appointment a : appointments) {
            count += a.getTimeslots().size();
        }
        return count;
    }

    // convert from Page to PaginationDto
    private PaginationDto<ResponseAppointmentDto> getPaginationDto(Page<Appointment> appointments) {
        List<Appointment> listOfAppointments = appointments.getContent();

        List<ResponseAppointmentDto> content = listOfAppointments.stream()
                .map(a -> {
                    Appointment appointment = appointmentRepository.findById(a.getId())
                            .orElse(new Appointment());
                    return ResponseAppointmentDto.mapToDto(appointment);
                })
                .collect(Collectors.toList());

        PaginationDto<ResponseAppointmentDto> appointmentResponseDto = new PaginationDto<>();
        appointmentResponseDto.setContent(content);
        appointmentResponseDto.setPageNo(appointments.getNumber());
        appointmentResponseDto.setPageSize(appointments.getSize());
        appointmentResponseDto.setTotalElements(appointments.getTotalElements());
        appointmentResponseDto.setTotalPages(appointments.getTotalPages());
        appointmentResponseDto.setLast(appointments.isLast());

        return appointmentResponseDto;
    }



    // student create appointment (not paid yet)
    @Override
    @Transactional
    public ResponseEntity<?> createAppointment(Integer studentId,
                                               InputAppointmentDto inputAppointmentDto) {

        // forbid a student make a booking when haven't finished payment for another
        if (!appointmentRepository.findAppointmentsWithPendingPayment(studentId,
                AppointmentStatus.PENDING_PAYMENT).isEmpty()) {
            throw new PaymentFailedException("This student is having another booking " +
                    "in pending payment status!");
        }

        Account student = accountRepository.findById(studentId)
                .orElseThrow(() -> new AccountNotFoundException("Student not found!"));

        if (!student.getRole().equals(Role.STUDENT)) {
            throw new AccountNotFoundException("Only student can book lessons!");
        }

        Appointment appointment = createAppointmentInstance(student, inputAppointmentDto);

        // save entities
        timeslotRepository.saveAll(appointment.getTimeslots());
        appointmentRepository.save(appointment);

        // response
        ResponseAppointmentDto dto = ResponseAppointmentDto.mapToDto(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    private Appointment createAppointmentInstance(Account student,
                                                  InputAppointmentDto inputAppointmentDto) {
        Account tutor = accountRepository.findById(inputAppointmentDto.getTutorId())
                .orElseThrow(() -> new AccountNotFoundException("Tutor not found!"));

        if (Objects.equals(student.getId(), inputAppointmentDto.getTutorId())) {
            throw new AppointmentNotFoundException("Cannot book yourself!");
        }

        // create appointment instance
        Appointment appointment = new Appointment();
        appointment.setStudent(student);
        appointment.setTutor(tutor);
        appointment.setDescription(inputAppointmentDto.getDescription());
        if (inputAppointmentDto.getSubjectName() == null || inputAppointmentDto.getSubjectName().isBlank()) {
            throw new SubjectNotFoundException("Not provided subject!");
        } else {
            Subject s = subjectRepository.findBySubjectName(
                            inputAppointmentDto.getSubjectName())
                    .orElseThrow(()-> new SubjectNotFoundException("Subject not found!")
                    );
            appointment.setSubject(s);
        }
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setStatus(AppointmentStatus.PENDING_PAYMENT);

        // check timeslots of tutor is occupied
        List<Timeslot> timeslotsAfterCheckTutorOverlap = getAndCheckTimeslotsOfTutorAreOccupied(inputAppointmentDto, appointment);
        List<Timeslot> timeslotsAfterCheckStudentOverlap = getAndCheckStudentHasOverlappedSlots(student, timeslotsAfterCheckTutorOverlap);

        appointment.setTimeslots(timeslotsAfterCheckStudentOverlap);

        // calculate and set tuition = total hours * teach price per hour
        appointment.setTuition(tutor.getTutorDetail().getTeachingPricePerHour()
                * calculateTotalHoursBySlots(appointment.getTimeslots()));

        return appointment;
    }

    private List<Timeslot> getAndCheckTimeslotsOfTutorAreOccupied(InputAppointmentDto inputAppointmentDto, Appointment appointment) {
        List<Timeslot> validTimeslots = new ArrayList<>();
        for (Integer i : inputAppointmentDto.getTimeslotIds()) {
            WeeklySchedule w = weeklyScheduleRepository.findById(i)
                    .orElseThrow(() -> new TimeslotValidationException("Schedule not found!"));

            LocalDate bookDate = calculateDateFromDayOfWeek(w.getDayOfWeek());

            if (timeslotRepository.findByDateAndWeeklySchedule(w.getId(), bookDate) != null) {
                throw new ConflictTimeslotException("Cannot book because " +
                        "some timeslots are occupied!");
            }
            else {
                Timeslot t = new Timeslot();
                t.setWeeklySchedule(w);
                t.setScheduleDate(bookDate);
                t.setAppointment(appointment);
                validTimeslots.add(t);
            }
        }
        return validTimeslots;
    }

    private List<Timeslot> getAndCheckStudentHasOverlappedSlots(Account student, List<Timeslot> timeslots) {
        // trong cac appointment da book, cai nao co 1 slot bat ki overlap voi 1 cai slot bat ki trong timeslots dang book
        // => throw exception
        for (Timeslot newSlot : timeslots) {
            if (timeslotRepository.findOverlapExistedSlot(
                                    newSlot.getScheduleDate(),
                                    newSlot.getWeeklySchedule().getStartTime(),
                                    newSlot.getWeeklySchedule().getEndTime(),
                                    student) != null) { // goi repo check
                throw new ConflictTimeslotException("Cannot book because some of the slots here are conflict with your schedule. \n" +
                                "Please check your schedule in Schedule Session carefully before booking!");
            }
        }
        return timeslots;
    }

    private LocalDate calculateDateFromDayOfWeek(int dayOfWeek) {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue() + 1; // LocalDate: sunday = 0, my app: sunday = 8
        int distance = dayOfWeek >= day ? (dayOfWeek - day) : (dayOfWeek + 7 - day);
        return today.plusDays(distance);
    }

    private double calculateTotalHoursBySlots(List<Timeslot> timeslots) {
        double totalHours = 0;
        for (Timeslot t : timeslots) {
            LocalTime startLocalTime = t.getWeeklySchedule().getStartTime().toLocalTime();
            LocalTime endLocalTime = t.getWeeklySchedule().getEndTime().toLocalTime();
            Duration duration = Duration.between(startLocalTime, endLocalTime);
            totalHours += duration.toHours() + (duration.toMinutesPart() / 60.0);
        }
        return totalHours;
    }

    private double calculateTotalHoursSchedules(WeeklySchedule weeklySchedule) {
        double totalHours = 0;
        LocalTime startLocalTime = weeklySchedule.getStartTime().toLocalTime();
        LocalTime endLocalTime = weeklySchedule.getEndTime().toLocalTime();
        Duration duration = Duration.between(startLocalTime, endLocalTime);
        totalHours += duration.toHours() + (duration.toMinutesPart() / 60.0);
        return totalHours;
    }

    // everytime reschedule == only reschedule a slot of the appointment
    @Override
    public ResponseEntity<ResponseAppointmentDto> updateAppointmentSchedule(int appointmentId, RequestReScheduleDto dto) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found!"));
        LocalDate today = LocalDate.now();
        LocalDateTime todayTime = LocalDateTime.now();

        // if new weekly schedule has booked timeslot -> error
        WeeklySchedule newWeeklySchedule = weeklyScheduleRepository.findById(dto.getNewWeeklyScheduleId())
                .orElseThrow(() -> new TimeslotValidationException("Timeslot not found!"));
        LocalDate newScheduleDate = calculateDateFromDayOfWeek(newWeeklySchedule.getDayOfWeek());
        if (timeslotRepository.findByDateAndWeeklySchedule(dto.getNewWeeklyScheduleId(), newScheduleDate) != null) {
            throw new ConflictTimeslotException("Timeslot has been occupied!");
        }

        // if appointment is not in PAID status -> error
        if (!appointment.getStatus().equals(AppointmentStatus.PAID)) {
            throw new InvalidStatusException("Not allowed to reschedule an appointment not in PAID status");
        }

        // 1. if current time before old slot < 1 days -> error
        // (only allows if current time >= 1 days with old slot)
        Timeslot oldTimeslot = timeslotRepository.findById(dto.getOldTimeslotId())
                .orElseThrow(() -> new TimeslotValidationException("Timeslot not found!"));
        LocalDate oldDate = oldTimeslot.getScheduleDate();
        LocalTime oldTime = oldTimeslot.getWeeklySchedule().getStartTime().toLocalTime();
        LocalDateTime oldDateTime = oldDate.atTime(oldTime); // datetime of booked slot
        if (todayTime.isAfter(oldDateTime.minusHours(24))) {
            throw new ConflictTimeslotException("Cannot reschedule because it is " +
                    "less than 24 hours before booked slot");
        }

        // 2. new slot must be > date than current date
        if (!newScheduleDate.isAfter(today)) {
            throw new ConflictTimeslotException("New schedule must be after current day!");
        }

        // 3. new slot must has length <= old slot
        double oldLength = calculateTotalHoursSchedules(oldTimeslot.getWeeklySchedule());
        double newLength = calculateTotalHoursSchedules(newWeeklySchedule);
        if (newLength > oldLength) {
            throw new ConflictTimeslotException("New slot cannot longer than old slot!");
        }

        // add new slot
        Timeslot newTimeslot = new Timeslot();
        newTimeslot.setWeeklySchedule(newWeeklySchedule);
        newTimeslot.setScheduleDate(newScheduleDate);
        newTimeslot.setAppointment(appointment);

        // send emails
        String mailSubject = getRescheduleEmailContent(appointment, oldTimeslot, newTimeslot)[0];
        String content = getRescheduleEmailContent(appointment, oldTimeslot, newTimeslot)[1];
        String[] receivers = new String[] {appointment.getStudent().getEmail(), appointment.getTutor().getEmail()};
        sendEmail(receivers, mailSubject, content);

        // remove old slot, add new slot and save
        appointment.getTimeslots().remove(oldTimeslot);
        appointment.getTimeslots().add(newTimeslot);

        appointmentRepository.save(appointment);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseAppointmentDto.mapToDto(appointment));
    }

    @Override
    public ResponseEntity<AppointmentSlotDto> cancelSlotsInAppointment(int accountId, int timeslotId) {
        Timeslot timeslotToDelete = timeslotRepository.findById(timeslotId)
                .orElseThrow(() -> new TimeslotValidationException("Timeslot not exists!"));
        if (timeslotToDelete.getAppointment().getStudent().getId() != accountId) {
            throw new InvalidStatusException("This account is not allowed to cancel this slot!");
        }
        if (timeslotToDelete.getScheduleDate().isBefore(LocalDate.now())) {
            throw new InvalidStatusException("Not allowed to cancel this slot!");
        }

        Appointment appointment = timeslotToDelete.getAppointment();
        if (!appointment.getStatus().equals(AppointmentStatus.PAID)) {
            throw new InvalidStatusException("Not allowed to cancel this slot!");
        }

        AppointmentSlotDto dto = AppointmentSlotDto.mapToDto(timeslotToDelete);
        timeslotRepository.delete(timeslotToDelete);
        if (appointment.getTimeslots().isEmpty()) {
            appointment.setStatus(AppointmentStatus.CANCELED);
        }
        appointmentRepository.save(appointment);

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Override
    public void sendCreateBookingEmail(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found!"));
        String mailSubject = getBookEmailContent(appointment)[0];
        String content = getBookEmailContent(appointment)[1];
        String[] receivers = new String[] {appointment.getStudent().getEmail(), appointment.getTutor().getEmail()};
        sendEmail(receivers, mailSubject, content);
    }

    private void sendEmail(String[] receivers, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo("hoavo.dev.demo@gmail.com");
            helper.setBcc(receivers);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(message);
    }

    @Contract(pure = true)
    private @NotNull String getStyle() {
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

    private String getTimeslotTable(List<Timeslot> timeslots) {
        String[] timeslotsHtml = timeslots.stream()
                .map(timeslot -> "<tr>" +
                        "<td style=\"border: 1px solid #ddd; padding: 8px;\">" + timeslot.getScheduleDate() + "</td>" +
                        "<td style=\"border: 1px solid #ddd; padding: 8px;\">" + timeslot.getWeeklySchedule().getStartTime() + "</td>" +
                        "<td style=\"border: 1px solid #ddd; padding: 8px;\">" + timeslot.getWeeklySchedule().getEndTime() + "</td>" +
                        "</tr>")
                .toArray(String[]::new);

        return "<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0;\">" +
                "<thead>" +
                "<tr>" +
                "<th style=\"border: 1px solid #ddd; padding: 8px; background-color: #672DEF; color: #ffffff;\">Date</th>" +
                "<th style=\"border: 1px solid #ddd; padding: 8px; background-color: #672DEF; color: #ffffff;\">Start Time</th>" +
                "<th style=\"border: 1px solid #ddd; padding: 8px; background-color: #672DEF; color: #ffffff;\">End Time</th>" +
                "</tr>" +
                "</thead>" +
                "<tbody>" +
                String.join("", timeslotsHtml) +
                "</tbody>" +
                "</table>";
    }

    private String[] getBookEmailContent(Appointment appointment) {
        String[] result = new String[2]; // 0: subject, 1: content
        String studentName = appointment.getStudent().getFullName();
        String tutorName = appointment.getTutor().getFullName();
        String subjectName = appointment.getSubject().getSubjectName();
        LocalDateTime appointmentDate = appointment.getCreatedAt();
        List<Timeslot> timeslots = appointment.getTimeslots();
        String meetingLink = appointment.getTutor().getTutorDetail().getMeetingLink();
        String description = appointment.getDescription();

        String emailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>New Booking</title>\n" +
                getStyle() +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>New Booking</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear User,</p>\n" +
                "            <p>You have a new booking at MyTutor!</p>\n" +
                "            <p class=\"appointment-details\"><strong>Booking Details:</strong></p>\n" +
                "            <p><strong>Created Date: </strong> " + appointmentDate.toLocalDate() + " <strong>At: </strong>" + appointmentDate.toLocalTime() + "</p>\n" +
                "            <p><strong>Student: </strong> " + studentName + "</p>\n" +
                "            <p><strong>Tutor: </strong> " + tutorName + "</p>\n" +
                "            <p><strong>Subject: </strong> " + subjectName + "</p>\n" +
                "            <p><strong>Tuition: </strong> " + Math.round(appointment.getTuition()) + " VND</p>\n" +
                "            <p><strong>Description:</strong> " + description + "</p>\n" +
                "            <p><strong>Schedules: </strong> </p>" + getTimeslotTable(timeslots) + "\n" +
                "            <div class=\"center-text\">\n" +
                "                <a href=\"" + meetingLink + "\" class=\"button\">Meeting link</a>\n" +
                "            </div>\n" +
                "            <p>Thank you for choosing MyTutor. We look forward to connecting tutors and students to achieve your learning goals.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2024 MyTutor. All rights reserved.</p>\n" +
                "            <p><a href=\"" + clientUrl + "\" class=\"button\">Visit Our Website</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";

        result[0] = "[MyTutor] New Booking!";
        result[1] = emailContent;
        return result;
    }

    private String[] getRescheduleEmailContent(Appointment appointment, Timeslot oldSlot, Timeslot newSlot) {
        String[] result = new String[2]; // 0: subject, 1: content
        String studentName = appointment.getStudent().getFullName();
        String tutorName = appointment.getTutor().getFullName();
        String subjectName = appointment.getSubject().getSubjectName();
        String studentEmailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reschedule Announcement</title>\n" +
                getStyle() +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <h1>Reschedule Announcement</h1>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <p>Dear User,</p>\n" +
                "            <p>A booking of yours has been rescheduled!</p>\n" +
                "            <p class=\"appointment-details\"><strong>Schedule Details:</strong></p>\n" +
                "            <p><strong>Student: </strong> " + studentName + "</p>\n" +
                "            <p><strong>Tutor: </strong> " + tutorName + "</p>\n" +
                "            <p><strong>Subject: </strong> " + subjectName + "</p>\n" +
                "            <p><strong>Old Schedule: </strong> " + oldSlot.getScheduleDate() + ", time: "
                + oldSlot.getWeeklySchedule().getStartTime() + " - " + oldSlot.getWeeklySchedule().getEndTime() + "</p>\n" +
                "            <p><strong>New Schedule: </strong> " + newSlot.getScheduleDate() + ", time: "
                + newSlot.getWeeklySchedule().getStartTime() + " - " + newSlot.getWeeklySchedule().getEndTime() + "</p>\n" +
                "            <p>Thank you for choosing MyTutor. We look forward to helping you achieve your learning goals.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2024 MyTutor. All rights reserved.</p>\n" +
                "            <p><a href=\"" + clientUrl + "\" class=\"button\">Visit Our Website</a></p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";

        result[0] = "[MyTutor] Reschedule Announcement!";
        result[1] = studentEmailContent;
        return result;
    }

    // tutor update appointment status: DONE from PAID or CANCELED from PAID
    @Override
    public ResponseEntity<?> updateAppointmentStatus(Integer accountId, Integer appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found!"));

        if(!Objects.equals(accountId, appointment.getTutor().getId()) &&
                !Objects.equals(accountId, appointment.getStudent().getId())) {
            throw new AppointmentNotFoundException("This appointment is not belong to this account");
        }
        if (!appointment.getStatus().equals(AppointmentStatus.PAID)) {
            throw new InvalidStatusException("Account can only update paid appointment!");
        }

        if (status.equals((AppointmentStatus.DONE).toString())) {
            // check dieu kien chua day xong ko cho DONE
            appointment.setStatus(AppointmentStatus.DONE);
        }

        else if (status.equalsIgnoreCase((AppointmentStatus.CANCELED).toString())) {
            appointment.setStatus(AppointmentStatus.CANCELED);
            // goi service hoan tien cho student...
        } else {
            throw new InvalidStatusException("This status is invalid!");
        }

        appointmentRepository.save(appointment);

        return ResponseEntity.ok("Appointment status updated successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<?> rollbackAppointment(int appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found!"));
        if (!appointment.getStatus().equals(AppointmentStatus.PENDING_PAYMENT)) {
            throw new InvalidStatusException("This appointment cannot be rollback!");
        }
        rollbackAppointment(appointment);
        return ResponseEntity.status(HttpStatus.OK).body("Appointment rollback successfully");
    }

    // viết hàm rollback (xóa appointment + timeslot isOccupied = false + appointmentId = null)
    public void rollbackAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // Run to check every minute - 15p ch thanh toan => rollback
    public void checkPendingAppointments() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(15);
        List<Appointment> pendingAppointments = appointmentRepository.findByStatusAndCreatedAtBefore(
                AppointmentStatus.PENDING_PAYMENT, thirtyMinutesAgo
        );

        for (Appointment appointment : pendingAppointments) {
            rollbackAppointment(appointment);
        }
    }
}
