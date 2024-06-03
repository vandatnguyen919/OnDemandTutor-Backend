package com.mytutor.services.impl;

import com.mytutor.dto.timeslot.InputTimeslotDto;
import com.mytutor.dto.timeslot.ResponseTimeslotDto;
import com.mytutor.dto.timeslot.ScheduleDto;
import com.mytutor.dto.timeslot.ScheduleItemDto;
import com.mytutor.dto.timeslot.TimeslotDto;
import com.mytutor.entities.Account;
import com.mytutor.entities.Timeslot;
import com.mytutor.exceptions.AccountNotFoundException;
import com.mytutor.exceptions.TimeslotValidationException;
import com.mytutor.repositories.AccountRepository;
import com.mytutor.repositories.TimeslotRepository;
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

    @Override
    public ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputTimeslotDto> timeslotDtos,
            Integer numberOfWeeks) {
        try {

            Account account = accountRepository.findById(tutorId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

            List<Timeslot> overlapTimeslots = saveValidTimeslotAndGetOverlapTimeslot(timeslotDtos, account, numberOfWeeks);

            if (overlapTimeslots.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                    .body("All timeslots are saved successfully");
            } else {
                // return overlapped timeslot to FE to show to the customer
                // FE will show annoucement that timeslots saved, except these slots are overlap...
                return ResponseEntity.status(HttpStatus.OK)
                        .body(overlapTimeslots);
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

    private List<Timeslot> saveValidTimeslotAndGetOverlapTimeslot(List<InputTimeslotDto> inputTimeslotDtos,
            Account account, Integer numberOfWeeks)
            throws TimeslotValidationException {
        List<Timeslot> overlapTimeslots = new ArrayList<>();
        List<Timeslot> validatedTimeslots = new ArrayList<>();
        for (int weekNo = 0; weekNo < numberOfWeeks; weekNo++) {
            for (InputTimeslotDto inputTimeslotDto : inputTimeslotDtos) {
                LocalDate scheduleDate = calculateDateFromDayOfWeek(inputTimeslotDto.getDayOfWeek(), weekNo);
                Timeslot timeslot = modelMapper.map(inputTimeslotDto, Timeslot.class);
                timeslot.setScheduleDate(scheduleDate);
                if (timeslotRepository.findOverlapTimeslot(account.getId(), scheduleDate,
                        inputTimeslotDto.getStartTime(), inputTimeslotDto.getEndTime()).isEmpty()) {
                    timeslot.setOccupied(false);
                    timeslot.setAccount(account);
                    validatedTimeslots.add(timeslot);
                } else {
                    overlapTimeslots.add(timeslot);
                }
            }
        }
        timeslotRepository.saveAll(validatedTimeslots);
        return overlapTimeslots;
    }

    private LocalDate calculateDateFromDayOfWeek(int dayOfWeek, int weekNo) {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue() + 1;
        int distance = dayOfWeek > day ? (dayOfWeek - day) : (dayOfWeek + 7 - day);
        return today.plusDays(distance + (weekNo * 7L) );
    }

    @Override
    public ResponseEntity<?> updateTimeslotStatus(Integer tutorId, Integer timeslotId, Boolean status) {
        Timeslot timeslot = timeslotRepository.findById(timeslotId).orElseThrow(
                () -> new RuntimeException("Timeslot not found!"));
        if (!Objects.equals(timeslot.getAccount().getId(), tutorId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This timeslot is not belongs to this tutor!");
        }
        timeslot.setOccupied(status);
        timeslotRepository.save(timeslot);
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelMapper.map(timeslot, ResponseTimeslotDto.class));
    }

    // remove timeslot (only allow for not occupied timeslots)
    @Override
    public ResponseEntity<?> removeTimeslot(Integer tutorId, Integer timeslotId) {
        Timeslot timeslot = timeslotRepository.findById(timeslotId)
                .orElseThrow(() -> new TimeslotValidationException("Timeslot not exists!"));
        if (timeslot.getAccount().getId() != tutorId) {
            throw new TimeslotValidationException("The timeslot with id is not belongs to this tutor!");
        }
        if (timeslot.isOccupied()) {
            throw new TimeslotValidationException("This timeslot with id is occupied!");
        }
        ResponseTimeslotDto dto = modelMapper.map(timeslot, ResponseTimeslotDto.class);
        timeslotRepository.delete(timeslot);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    // hien ra lich trinh cua tutor 7 ngay gan nhat theo tutor id (trong tuong lai)
    @Override
    public ResponseEntity<?> getNext7DaysSchedulesByTutorId(Integer tutorId) {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(6);

        ScheduleDto scheduleDto = new ScheduleDto();
        List<ScheduleItemDto> items = scheduleDto.getSchedules();

        int today = startDate.getDayOfWeek().getValue() - 1;    // Monday is 0 and Sunday is 6
        for (int i = 0; i < 7; i++) {

            int d = (today + i) % 7 + 2; // Monday is 2 and Sunday is 8
            List<Timeslot> timeslots = timeslotRepository.findByTutorIdAndDayOfWeekAndDateRange(tutorId, startDate, endDate, d);
            if (timeslots == null) {
                timeslots = new ArrayList<>();
            }

            LocalDate date = startDate.plusDays(i);
            String dayOfWeek = date.getDayOfWeek().toString().substring(0, 3);
            int dayOfMonth = date.getDayOfMonth();
            List<TimeslotDto> timeslotDtos = timeslots.stream().map(t -> TimeslotDto.mapToDto(t)).toList();

            ScheduleItemDto scheduleItemDto = new ScheduleItemDto(dayOfWeek, dayOfMonth, timeslotDtos);
            items.add(scheduleItemDto);
        }

        scheduleDto.setStartDate(startDate);
        scheduleDto.setEndDate(endDate);

        return ResponseEntity.status(HttpStatus.OK).body(scheduleDto);
    }

}
