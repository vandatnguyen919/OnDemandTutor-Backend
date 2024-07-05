package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.appointment.AppointmentSlotDto;
import com.mytutor.dto.appointment.InputAppointmentDto;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.appointment.RequestReScheduleDto;
import com.mytutor.dto.appointment.ResponseAppointmentDto;
import com.mytutor.dto.LessonStatisticDto;
import com.mytutor.dto.timeslot.TimeslotDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Appointment;
import com.mytutor.entities.Subject;
import com.mytutor.entities.Timeslot;
import com.mytutor.entities.WeeklySchedule;
import com.mytutor.exceptions.*;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.AppointmentRepository;
import com.mytutor.repositories.SubjectRepository;
import com.mytutor.repositories.TimeslotRepository;
import com.mytutor.repositories.WeeklyScheduleRepository;
import com.mytutor.services.AppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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
    private ModelMapper modelMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public ResponseEntity<ResponseAppointmentDto> getAppointmentById(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));
        ResponseAppointmentDto dto = ResponseAppointmentDto.mapToDto(appointment);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Override
    public ResponseEntity<PaginationDto<ResponseAppointmentDto>> getAppointmentsByAccountId(Integer accountId,
                                                                                            AppointmentStatus status,
                                                                                            Integer pageNo,
                                                                                            Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Appointment> appointments;
        appointments = appointmentRepository.findAppointmentByAccountId(accountId, status, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(getPaginationDto(appointments));
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
    public ResponseEntity<LessonStatisticDto> getStudentStatistics(Integer studentId) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsInTimeRange(
                studentId, null, null);
        LessonStatisticDto dto = new LessonStatisticDto();
        dto.setAccountId(studentId);
        if (!appointments.isEmpty()) {
            Set<Subject> subjects = getSubjectsFromAppointments(appointments);
            Set<Account> tutors = getTutorsFromAppointments(appointments);

            // total
            dto.setTotalSubjects(subjects);
            dto.setTotalLessons(getTotalLessons(appointments));
            dto.setTotalLearntTutor(tutors.size());
        }

        // current month
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Appointment> thisMonthAppointments = appointmentRepository.findAppointmentsInTimeRange(
                studentId, startDate, endDate
        );
        if (!thisMonthAppointments.isEmpty()) {
            Set<Subject> thisMonthSubjects = getSubjectsFromAppointments(thisMonthAppointments);
            Set<Account> thisMonthTutors = getTutorsFromAppointments(thisMonthAppointments);

            dto.setThisMonthSubjects(thisMonthSubjects);
            dto.setThisMonthLessons(getTotalLessons(thisMonthAppointments));
            dto.setThisMonthTutor(thisMonthTutors.size());
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Override
    public ResponseEntity<LessonStatisticDto> getTutorStatistics(Integer tutorId) {

        LessonStatisticDto dto = new LessonStatisticDto();
        dto.setAccountId(tutorId);

        // total
        List<Appointment> appointments = appointmentRepository.findAppointmentsInTimeRange(
                tutorId, null, null);

        Set<Subject> subjects = getSubjectsFromAppointments(appointments);
        Set<Account> students = getStudentsFromAppointments(appointments);
        if (!appointments.isEmpty()) {
            dto.setTotalSubjects(subjects);
            dto.setTotalTaughtStudent(students.size());
            dto.setTotalLessons(getTotalLessons(appointments));
            dto.setTotalIncome(getTotalIncome(tutorId, appointments));
        }

        // current month
        LocalDateTime startDate = LocalDateTime.now().withDayOfMonth(1);
        LocalDateTime endDate = startDate.plusMonths(1);

        List<Appointment> thisMonthAppointments = appointmentRepository.findAppointmentsInTimeRange(
                tutorId, startDate, endDate
        );
        if (!thisMonthAppointments.isEmpty()) {
        Set<Subject> thisMonthSubjects = getSubjectsFromAppointments(thisMonthAppointments);
        Set<Account> thisMonthStudents = getStudentsFromAppointments(thisMonthAppointments);

        dto.setThisMonthSubjects(thisMonthSubjects);
        dto.setThisMonthStudent(thisMonthStudents.size());
        dto.setThisMonthLessons(getTotalLessons(thisMonthAppointments));
        dto.setTotalMonthlyIncome(getTotalIncome(tutorId, thisMonthAppointments));
        }
        return ResponseEntity.status(HttpStatus.OK).body(dto);
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

    private double getTotalIncome(int tutorId, List<Appointment> appointments) {
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

        Appointment appointment = createAppointmentInstance(studentId, inputAppointmentDto);

        // save entities
        timeslotRepository.saveAll(appointment.getTimeslots());
        appointmentRepository.save(appointment);

        // response
        ResponseAppointmentDto dto = ResponseAppointmentDto.mapToDto(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    private Appointment createAppointmentInstance(Integer studentId,
                                                  InputAppointmentDto inputAppointmentDto) {
        Account tutor = accountRepository.findById(inputAppointmentDto.getTutorId())
                .orElseThrow(() -> new AccountNotFoundException("Tutor not found!"));

        if (Objects.equals(studentId, inputAppointmentDto.getTutorId())) {
            throw new AppointmentNotFoundException("Cannot book yourself!");
        }
        // create appointment instance
        Appointment appointment = new Appointment();
        appointment.setStudent(accountRepository.findById(studentId).get());
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
//                t.setOccupied(true);
                appointment.getTimeslots().add(t);
                t.setAppointment(appointment);
            }
        }

        // calculate and set tuition = total hours * teach price per hour
        appointment.setTuition(tutor.getTutorDetail().getTeachingPricePerHour()
                * calculateTotalHoursBySlots(appointment.getTimeslots()));

        return appointment;
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

        // 1. if current time before old slot <= 1 days -> error
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

        // 3. new slot must has length == old slot
        List<Timeslot> timeslots = new ArrayList<>();
        timeslots.add(oldTimeslot);
        double oldLength = calculateTotalHoursBySlots(timeslots);
        double newLength = calculateTotalHoursSchedules(newWeeklySchedule);
        if (newLength > oldLength) {
            throw new ConflictTimeslotException("New slot cannot longer than old slot!");
        }

        // update timeslot for appointment

        // remove old slot
        appointment.getTimeslots().remove(oldTimeslot);

        // add new slot
        Timeslot t = new Timeslot();
        t.setWeeklySchedule(newWeeklySchedule);
        t.setScheduleDate(newScheduleDate);
//        t.setOccupied(true);
        t.setAppointment(appointment);
        appointment.getTimeslots().add(t);

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
    @Override
    @Transactional
    public void rollbackAppointment(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // Run to check every minute
    public void checkPendingAppointments() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        List<Appointment> pendingAppointments = appointmentRepository.findByStatusAndCreatedAtBefore(
                AppointmentStatus.PENDING_PAYMENT, thirtyMinutesAgo
        );

        for (Appointment appointment : pendingAppointments) {
            rollbackAppointment(appointment);
        }
    }

}
