package com.mytutor.services.impl;

import com.mytutor.dto.TimeSlot.InputTimeslotDto;
import com.mytutor.dto.TimeSlot.ResponseTimeslotDto;
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

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // tutor tu kiem tra xem lich cua minh con khong va tu tay set tiep lich cho 7 ngay tiep theo
    @Override
    public ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputTimeslotDto> timeslotDtos,
                                            Integer numberOfWeeks) {
        try {

            Account account = accountRepository.findById(tutorId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

            List<Timeslot> validatedTimeslots = validateTimeslots(timeslotDtos, account, numberOfWeeks);
            timeslotRepository.saveAll(validatedTimeslots);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Schedule saved successfully!");
        } catch (AccountNotFoundException | TimeslotValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the schedule.");
        }
    }


    private List<Timeslot> validateTimeslots(List<InputTimeslotDto> inputTimeslotDtos, Account account,
                                             Integer numberOfWeeks)
            throws TimeslotValidationException {
        List<Timeslot> validatedTimeslots = new ArrayList<>();
        for (int weekNo = 0; weekNo < numberOfWeeks; weekNo++) {
            for (InputTimeslotDto inputTimeslotDto : inputTimeslotDtos) {
                LocalDate scheduleDate = calculateDateFromDayOfWeek(inputTimeslotDto.getDayOfWeek(), weekNo);
                if (timeslotRepository.findAnExistedTimeslot(account.getId(), scheduleDate,
                        inputTimeslotDto.getStartTime(), inputTimeslotDto.getEndTime()) != null) {
                    throw new TimeslotValidationException("Timeslot existed!");
                }
                Timeslot timeslot = modelMapper.map(inputTimeslotDto, Timeslot.class);
                timeslot.setScheduleDate(scheduleDate);
                timeslot.setOccupied(false);
                timeslot.setAccount(account);

                validatedTimeslots.add(timeslot);
            }
        }
        return validatedTimeslots;
    }

    private LocalDate calculateDateFromDayOfWeek (int dayOfWeek, int weekNo) {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue() + 1;
        int distance = dayOfWeek > day ? (dayOfWeek - day) : (dayOfWeek + 7 - day);
        return today.plusDays(distance + (weekNo * 7L) );
    }

    @Override
    public ResponseEntity<?> updateTimeslotStatus(Integer tutorId, Integer timeslotId, Boolean status) {
        Timeslot timeslot = timeslotRepository.findById(timeslotId).orElseThrow(
                () -> new RuntimeException("Timeslot not found!"));
        if (timeslot.getAccount().getId() != tutorId) {
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
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(7);
        List<Timeslot> timeslots = timeslotRepository
                .findByTutorIdOrderedByScheduleDate(tutorId, currentDate, endDate);
       if (timeslots.isEmpty()) {
           return ResponseEntity.status(HttpStatus.OK)
                   .body("This tutor has no available timeslots from now to next 7 days!");
       }
        List<ResponseTimeslotDto> timeslotDtos = new ArrayList<>();
        for (Timeslot t : timeslots) {
            ResponseTimeslotDto dto = modelMapper.map(t, ResponseTimeslotDto.class);
            dto.setAccountId(t.getAccount().getId());
            timeslotDtos.add(dto);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(timeslotDtos);
    }

}
