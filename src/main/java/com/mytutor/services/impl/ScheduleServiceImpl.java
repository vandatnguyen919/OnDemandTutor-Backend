package com.mytutor.services.impl;

import com.mytutor.dto.timeslot.InputWeeklyScheduleDto;
import com.mytutor.dto.timeslot.ResponseWeeklyScheduleDto;
import com.mytutor.dto.timeslot.ScheduleDto;
import com.mytutor.dto.timeslot.ScheduleItemDto;
import com.mytutor.dto.timeslot.TimeslotDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputWeeklyScheduleDto> weeklyScheduleDtos) {
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
                return ResponseEntity.status(HttpStatus.OK)
                        .body(overlapSchedules);
            }

        } catch (AccountNotFoundException | TimeslotValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the schedule.");
        }
    }

    private List<WeeklySchedule> saveValidTimeslotAndGetOverlapTimeslot(List<InputWeeklyScheduleDto> inputWeeklyScheduleDtos,
            Account account)
            throws TimeslotValidationException {
        List<WeeklySchedule> overlapSchedules = new ArrayList<>();
        List<WeeklySchedule> validatedSchedules = new ArrayList<>();
            for (InputWeeklyScheduleDto inputWeeklyScheduleDto : inputWeeklyScheduleDtos) {
//                LocalDate scheduleDate = calculateDateFromDayOfWeek(inputWeeklyScheduleDto.getDayOfWeek(), weekNo);
                WeeklySchedule schedule = modelMapper.map(inputWeeklyScheduleDto, WeeklySchedule.class);
//                timeslot.setScheduleDate(scheduleDate);
                if (weeklyScheduleRepository.findOverlapSchedule(account.getId(), inputWeeklyScheduleDto.getDayOfWeek(),
                        inputWeeklyScheduleDto.getStartTime(), inputWeeklyScheduleDto.getEndTime()).isEmpty()) {
//                    timeslot.setOccupied(false);
                    schedule.setAccount(account);
                    validatedSchedules.add(schedule);
                } else {
                    overlapSchedules.add(schedule);
                }
        }
        weeklyScheduleRepository.saveAll(validatedSchedules);
        return overlapSchedules;
    }

    private LocalDate calculateDateFromDayOfWeek(int dayOfWeek, int weekNo) {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue() + 1;
        int distance = dayOfWeek > day ? (dayOfWeek - day) : (dayOfWeek + 7 - day);
        return today.plusDays(distance + (weekNo * 7L) );
    }

    @Override
    public ResponseEntity<?> updateScheduleStatus(Integer tutorId, Integer scheduleId, Boolean status) {
        WeeklySchedule weeklySchedule = weeklyScheduleRepository.findById(scheduleId).orElseThrow(
                () -> new TimeslotValidationException("Schedule not found!"));
        if (!Objects.equals(weeklySchedule.getAccount().getId(), tutorId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This schedule is not belongs to this tutor!");
        }
        weeklySchedule.setOccupied(status);
        weeklyScheduleRepository.save(weeklySchedule);
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelMapper.map(weeklySchedule, ResponseWeeklyScheduleDto.class));
    }

    // remove schedule (only allow for not occupied schedules)
    @Override
    public ResponseEntity<?> removeSchedule(Integer tutorId, Integer scheduleId) {
        WeeklySchedule weeklySchedule = weeklyScheduleRepository.findById(scheduleId).orElseThrow(
                () -> new TimeslotValidationException("Schedule not found!"));
        if (!Objects.equals(weeklySchedule.getAccount().getId(), tutorId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This schedule is not belongs to this tutor!");
        }
        if (weeklySchedule.isOccupied()) {
            throw new TimeslotValidationException("This schedule is occupied!");
        }
        ResponseWeeklyScheduleDto dto = modelMapper.map(weeklySchedule, ResponseWeeklyScheduleDto.class);
        weeklyScheduleRepository.delete(weeklySchedule);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @Override
    public ResponseEntity<?> getTutorWeeklySchedule(Integer tutorId) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);

        ScheduleDto scheduleDto = new ScheduleDto();
        List<ScheduleItemDto> items = scheduleDto.getSchedules();
        int today = startDate.getDayOfWeek().getValue() - 1;

        for (int i = 0; i < 7; i++) {

            int d = (today + i) % 7 + 2; // Monday is 2 and Sunday is 8
            List<WeeklySchedule> weeklySchedules = weeklyScheduleRepository.findByTutorId(tutorId);

            LocalDate date = startDate.plusDays(i);
            String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
            int dayOfMonth = date.getDayOfMonth();
            List<TimeslotDto> timeslotDtos = weeklySchedules.stream().map(t -> TimeslotDto.mapToDto(t)).toList();

            ScheduleItemDto scheduleItemDto = new ScheduleItemDto(dayOfWeek, dayOfMonth, timeslotDtos);
            items.add(scheduleItemDto);
        }

        scheduleDto.setStartDate(startDate);
        scheduleDto.setEndDate(endDate);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
    }

    // hien ra lich trinh cua tutor 7 ngay gan nhat theo tutor id (trong tuong lai)
//    @Override
//    public ResponseEntity<?> getNext7DaysSchedulesByTutorId(Integer tutorId) {
//
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = startDate.plusDays(6);
//
//        ScheduleDto scheduleDto = new ScheduleDto();
//        List<ScheduleItemDto> items = scheduleDto.getSchedules();
//
//        int today = startDate.getDayOfWeek().getValue() - 1;    // Monday is 0 and Sunday is 6
//        for (int i = 0; i < 7; i++) {
//
//            int d = (today + i) % 7 + 2; // Monday is 2 and Sunday is 8
//            List<Timeslot> timeslots = timeslotRepository.findByTutorIdAndDayOfWeekAndDateRange(tutorId, startDate, endDate, d);
//            if (timeslots == null) {
//                timeslots = new ArrayList<>();
//            }
//
//            LocalDate date = startDate.plusDays(i);
//            String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
//            int dayOfMonth = date.getDayOfMonth();
//            List<TimeslotDto> timeslotDtos = timeslots.stream().map(t -> TimeslotDto.mapToDto(t)).toList();
//
//            ScheduleItemDto scheduleItemDto = new ScheduleItemDto(dayOfWeek, dayOfMonth, timeslotDtos);
//            items.add(scheduleItemDto);
//        }
//
//        scheduleDto.setStartDate(startDate);
//        scheduleDto.setEndDate(endDate);
//
//        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
//    }





}
