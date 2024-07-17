package com.mytutor.services.impl;

import com.mytutor.constants.AppointmentStatus;
import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.appointment.AppointmentSlotDto;
import com.mytutor.dto.timeslot.*;
import com.mytutor.entities.Account;
import com.mytutor.entities.Timeslot;
import com.mytutor.entities.WeeklySchedule;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.TimeslotValidationException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.TimeslotRepository;
import com.mytutor.repositories.WeeklyScheduleRepository;
import com.mytutor.services.ScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author vothimaihoa
 *
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    TimeslotRepository timeslotRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private WeeklyScheduleRepository weeklyScheduleRepository;

    @Override
    public ResponseEntity<?> addNewSchedule(Integer tutorId, List<RequestWeeklyScheduleDto> weeklyScheduleDtos) {
        try {

            Account account = accountRepository.findById(tutorId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

            List<WeeklySchedule> overlapSchedules = saveValidTimeslotAndGetOverlapTimeslot(weeklyScheduleDtos, account);

            if (overlapSchedules.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body("All timeslots are saved successfully");
            } else {
                // return overlapped timeslot to FE to show to the customer
                // FE will show annoucement that timeslots saved, except these slots are overlap...
                List<ResponseWeeklyScheduleDto> overlapScheduleDtos = overlapSchedules.stream()
                        .map(schedule -> ResponseWeeklyScheduleDto.mapToDto(schedule))
                        .toList();
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(overlapScheduleDtos);
            }

        } catch (AccountNotFoundException | TimeslotValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the schedule.");
        }
    }

    private List<WeeklySchedule> saveValidTimeslotAndGetOverlapTimeslot
            (List<RequestWeeklyScheduleDto> requestWeeklyScheduleDtos,
            Account account)
            throws TimeslotValidationException {
        List<WeeklySchedule> overlapSchedules = new ArrayList<>();
        List<WeeklySchedule> validatedSchedules = new ArrayList<>();
            for (RequestWeeklyScheduleDto requestWeeklyScheduleDto : requestWeeklyScheduleDtos) {
                WeeklySchedule schedule = modelMapper.map(requestWeeklyScheduleDto, WeeklySchedule.class);
                // if new slot not overlap with any timeslot that is using => added to valid using slots
                if (weeklyScheduleRepository.findOverlapUsingSchedule(
                        account.getId(),
                        requestWeeklyScheduleDto.getDayOfWeek(),
                        requestWeeklyScheduleDto.getStartTime(),
                        requestWeeklyScheduleDto.getEndTime()).isEmpty()) {

                    // if new slot match time with a existedNotUsingSlot => set that existedNotUsingSlot to isUsing = true
                    WeeklySchedule existedNotUsingSlot = weeklyScheduleRepository.findNotUsingSlotByTutor(
                            account.getId(),
                            requestWeeklyScheduleDto.getDayOfWeek(),
                            requestWeeklyScheduleDto.getStartTime(),
                            requestWeeklyScheduleDto.getEndTime()
                    );
                    if (existedNotUsingSlot != null) {
                        System.out.println(existedNotUsingSlot.getId());
                        existedNotUsingSlot.setUsing(true);
                        weeklyScheduleRepository.save(existedNotUsingSlot);
                    } else {
                        System.out.println("not found!!!");
                        schedule.setAccount(account);
                        schedule.setUsing(true);
                        validatedSchedules.add(schedule);
                    }
                } else {
                    // if overlap with isUsing slot
                    schedule.setAccount(account);
                    overlapSchedules.add(schedule);
                }
        }
        weeklyScheduleRepository.saveAll(validatedSchedules);
        return overlapSchedules;
    }

    // update schedule => kiem tra slot can them da có trong db chua (dayOfWeek, startTime, endTime trung)
    // co roi thi de nguyen,
    // chua co thi them vao,
    // old schedule co ma new ko co => xoa di
    @Override
    public ResponseEntity<?> updateSchedule(Integer tutorId, List<RequestWeeklyScheduleDto> newSchedules) {

        Account account = accountRepository.findById(tutorId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found!"));
        // Fetch existing schedules for the tutor
        List<WeeklySchedule> existingSchedules = weeklyScheduleRepository.findByTutorId(tutorId);

        // Create maps for quick lookup
        Map<String, RequestWeeklyScheduleDto> newScheduleMap = newSchedules.stream()
                .collect(Collectors.toMap(
                        s -> s.getDayOfWeek()
                                + "-" + s.getStartTime()
                                + "-" + s.getEndTime(),
                        s -> s
                ));

        Map<String, WeeklySchedule> existingScheduleMap = existingSchedules.stream()
                .collect(Collectors.toMap(
                        s -> s.getDayOfWeek()
                                + "-" + s.getStartTime()
                                + "-" + s.getEndTime(),
                        s -> s
                ));

        // Add or update schedules
        addOrUpdateNewSchedule(newSchedules, account, existingScheduleMap);

        // Mark old schedules that are not in the new schedules as not using
        handleOldSchedule(existingSchedules, newScheduleMap);

        return ResponseEntity.ok().body("Schedule updated successfully");
    }

    private void addOrUpdateNewSchedule(List<RequestWeeklyScheduleDto> newSchedules, Account account,
                                        Map<String, WeeklySchedule> existingScheduleMap) {
        for (RequestWeeklyScheduleDto newSchedule : newSchedules) {
            String key = newSchedule.getDayOfWeek() + "-"
                    + newSchedule.getStartTime() + "-"
                    + newSchedule.getEndTime();
            if (!existingScheduleMap.containsKey(key)) {
                // Schedule does not exist, add it
                WeeklySchedule newScheduleDB = modelMapper.map(newSchedule, WeeklySchedule.class);
                newScheduleDB.setAccount(account);
                newScheduleDB.setUsing(true);
                weeklyScheduleRepository.save(newScheduleDB);
            } else {
                // Schedule exists, ensure it's marked as using
                WeeklySchedule existingSchedule = existingScheduleMap.get(key);
                existingSchedule.setUsing(true);
                weeklyScheduleRepository.save(existingSchedule);
            }
        }
    }

    private void handleOldSchedule(List<WeeklySchedule> existingSchedules, Map<String,
            RequestWeeklyScheduleDto> newScheduleMap) {
        for (WeeklySchedule existingSchedule : existingSchedules) {
            String key = existingSchedule.getDayOfWeek() + "-"
                    + existingSchedule.getStartTime()
                    + "-" + existingSchedule.getEndTime();
            if (!newScheduleMap.containsKey(key)) {
                // if this slot not in new schedule but it has been booked in the past => set isUsing = false
                if (!existingSchedule.getTimeslots().isEmpty()) {
                    existingSchedule.setUsing(false);
                    weeklyScheduleRepository.save(existingSchedule);
                }
                else {
                    weeklyScheduleRepository.delete(existingSchedule);
                }
            }
        }
    }

    @Override
    public ResponseEntity<?> getTutorProfileSchedule(Integer tutorId) {
        ScheduleDto scheduleDto = new ScheduleDto();
        List<ScheduleItemDto> items = scheduleDto.getSchedules();
        int d;
        for (d = 2; d <= 8; d++) {
            String dayOfWeek = DayOfWeek.of((d - 2) % 7 + 1).toString().substring(0, 3);
            List<WeeklySchedule> weeklySchedules = weeklyScheduleRepository
                    .findByTutorIdAnDayOfWeek(tutorId, d);
            if(weeklySchedules.isEmpty()) {
                weeklySchedules = new ArrayList<>();
            }
            List<TimeslotDto> timeslotDtos = weeklySchedules.stream()
                    .map(t -> TimeslotDto.mapToDto(t)).toList();

            ScheduleItemDto scheduleItemDto = new ScheduleItemDto(dayOfWeek, 0, timeslotDtos);
            items.add(scheduleItemDto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
    }

    // lay ra cac timeslot cua 7 ngay gan nhat theo schedule
    // Tính timeslots dựa theo schedule và xuất ra
    @Override
    public ResponseEntity<?> getTutorWeeklySchedule(Integer tutorId) {
        ScheduleDto scheduleDto = generateWeeklySchedule(
                tutorId, 0, false);
        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
    }

    @Override
    public ResponseEntity<?> getScheduleForReschedule(Integer timeslotId, Integer tutorId) {
        Timeslot oldTimeslot = timeslotRepository.findById(timeslotId)
                .orElseThrow(() -> new TimeslotValidationException("Timeslot not found!"));
        double oldSlotLength = calculateTotalHoursSchedules(oldTimeslot.getWeeklySchedule());
        ScheduleDto scheduleDto = generateWeeklySchedule(
                tutorId, oldSlotLength, true);
        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
    }

    @Override
    public PaginationDto<AppointmentSlotDto> getBookedSlotsByAccount(Integer accountId,
                                                                     boolean isDone,
                                                                     boolean isLearner,
                                                                     Integer pageNo,
                                                                     Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Timeslot> responseTimeslots;
        LocalDate currentDate = LocalDate.now();
        Time currentTime = Time.valueOf(LocalTime.now());

        if (isLearner) {
            if (isDone) {
                responseTimeslots = timeslotRepository.findPastTimeslotByStudent(
                        accountId, currentDate, currentTime, pageable);
            } else {
                responseTimeslots = timeslotRepository.findUpcomingTimeslotByStudent(
                        accountId, AppointmentStatus.PAID, currentDate, currentTime, pageable);
            }
        } else {
            if (isDone) {
                responseTimeslots = timeslotRepository.findPastTimeslotByTutor(
                        accountId, currentDate, currentTime, pageable);
            } else {
                responseTimeslots = timeslotRepository.findUpcomingTimeslotByTutor(
                        accountId, AppointmentStatus.PAID, currentDate, currentTime, pageable);
            }
        }
        return getPaginationDto(responseTimeslots);
    }

    private ScheduleDto generateWeeklySchedule(Integer tutorId, double oldSlotLength, boolean forReschedule) {
        LocalDate startDate = LocalDate.now();
        LocalDateTime currentTimePlus12Hours = LocalDateTime.now().plusHours(12);
        if (currentTimePlus12Hours.isAfter(startDate.atTime(23, 59, 59))) {
            startDate = LocalDate.now().plusDays(1);
        }
        LocalDate endDate = startDate.plusDays(6);
        ScheduleDto scheduleDto = new ScheduleDto();
        List<ScheduleItemDto> items = scheduleDto.getSchedules();

        int today = startDate.getDayOfWeek().getValue() - 1;// get day of week of start day
        List<WeeklySchedule> weeklySchedules;
        for (int i = 0; i < 7; i++) {
            int d = (today + i) % 7 + 2; // Monday is 2 and Sunday is 8
            if (i == 0) {
                weeklySchedules = weeklyScheduleRepository
                        .findByTutorIdAndDayOfWeekWithMinStartTime(tutorId, d, Time.valueOf(currentTimePlus12Hours.toLocalTime())); // thêm điều kiện truước 12 tiếng ms book
            }
            else {
                weeklySchedules = weeklyScheduleRepository
                        .findByTutorIdAnDayOfWeek(tutorId, d); // thêm điều kiện truước 12 tiếng ms book
            }
            LocalDate date = startDate.plusDays(i);

            if (forReschedule) {
                removeBookedOrLongerSlots(oldSlotLength, weeklySchedules, date);
            } else {
                removeBookedSlot(weeklySchedules, date);
            }

            if (weeklySchedules.isEmpty()) {
                weeklySchedules = new ArrayList<>();
            }
            String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
            int dayOfMonth = date.getDayOfMonth();
            List<TimeslotDto> timeslotDtos = weeklySchedules.stream()
                    .map(t -> TimeslotDto.mapToDto(t)).toList();

            ScheduleItemDto scheduleItemDto = new ScheduleItemDto(dayOfWeek, dayOfMonth, timeslotDtos);
            items.add(scheduleItemDto);
        }

        scheduleDto.setStartDate(startDate);
        scheduleDto.setEndDate(endDate);

        return scheduleDto;
    }

    private PaginationDto<AppointmentSlotDto> getPaginationDto(Page<Timeslot> timeslots) {
        List<Timeslot> listOfTimeslots = timeslots.getContent();

        List<AppointmentSlotDto> content = listOfTimeslots.stream()
                .map(a -> {
                    Timeslot timeslot = timeslotRepository.findById(a.getId())
                            .orElse(new Timeslot());
                    return AppointmentSlotDto.mapToDto(timeslot);
                })
                .toList();

        PaginationDto<AppointmentSlotDto> appointmentResponseDto = new PaginationDto<>();
        appointmentResponseDto.setContent(content);
        appointmentResponseDto.setPageNo(timeslots.getNumber());
        appointmentResponseDto.setPageSize(timeslots.getSize());
        appointmentResponseDto.setTotalElements(timeslots.getTotalElements());
        appointmentResponseDto.setTotalPages(timeslots.getTotalPages());
        appointmentResponseDto.setLast(timeslots.isLast());

        return appointmentResponseDto;
    }

    private void removeBookedSlot(List<WeeklySchedule> weeklySchedules, LocalDate date) {
        weeklySchedules.removeIf(weeklySchedule ->
                timeslotRepository.findByDateAndWeeklySchedule(weeklySchedule.getId(), date) != null);
    }

    private void removeBookedOrLongerSlots(double oldTimeSlotLength, List<WeeklySchedule> weeklySchedules,
                                           LocalDate date) {
        weeklySchedules.removeIf(weeklySchedule ->
                timeslotRepository.findByDateAndWeeklySchedule(weeklySchedule.getId(), date) != null
        || calculateTotalHoursSchedules(weeklySchedule) > oldTimeSlotLength);
    }

    private double calculateTotalHoursSchedules(WeeklySchedule weeklySchedule) {
        double totalHours = 0;
        LocalTime startLocalTime = weeklySchedule.getStartTime().toLocalTime();
        LocalTime endLocalTime = weeklySchedule.getEndTime().toLocalTime();
        Duration duration = Duration.between(startLocalTime, endLocalTime);
        totalHours += duration.toHours() + (duration.toMinutesPart() / 60.0);
        return totalHours;
    }
}
