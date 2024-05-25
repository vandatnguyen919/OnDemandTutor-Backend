package com.mytutor.services.impl;

import com.mytutor.dto.PaginationDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<?> addNewSchedule(Integer tutorId, List<InputTimeslotDto> tutorScheduleDto) {
        try {

            Account account = accountRepository.findById(tutorId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found!"));

            List<Timeslot> validatedTimeslots = validateTimeslots(tutorScheduleDto, account);

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


    private List<Timeslot> validateTimeslots(List<InputTimeslotDto> inputTimeslotDtos, Account account)
            throws TimeslotValidationException {
        List<Timeslot> validatedTimeslots = new ArrayList<>();
        for (InputTimeslotDto inputTimeslotDto : inputTimeslotDtos) {
            Timeslot timeslot = modelMapper.map(inputTimeslotDto, Timeslot.class);
            timeslot.setScheduleDate(calculateDateFromDayOfWeek(inputTimeslotDto.getDayOfWeek()));
            timeslot.setOccupied(false);
            timeslot.setAccount(account);

            validatedTimeslots.add(timeslot);
        }
        return validatedTimeslots;
    }

    private LocalDate calculateDateFromDayOfWeek (int dayOfWeek) {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfWeek().getValue() + 1;
        int distance = dayOfWeek > day ? dayOfWeek - day : dayOfWeek + 7 - day;
        return today.plusDays(distance);
    }

    @Override
    public ResponseEntity<?> updateTimeslotStatus(Integer tutorId, Integer timeslotId, Boolean status) {
        Timeslot timeslot = timeslotRepository.findById(timeslotId).orElseThrow(
                () -> new RuntimeException("Timeslot not found!"));
        if (timeslot.getAccount().getId() != tutorId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You cannot update status of this timeslot!");
        }
        timeslot.setOccupied(status);
        timeslotRepository.save(timeslot);
        return ResponseEntity.status(HttpStatus.OK)
                .body(modelMapper.map(timeslot, ResponseTimeslotDto.class));
    }

    // remove timeslot (only allow for not occupied timeslots)


    // hien ra lich trinh cua tutor bat ky (tu hien tai toi tuong lai),
    // phan trang lay 7 ngay 1 trang
    @Override
    public ResponseEntity<?> getSchedulesByTutorId(Integer tutorId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Timeslot> timeslots = timeslotRepository.findByAccountId(tutorId, pageable);
        List<Timeslot> listOfTimeslots = timeslots.getContent();
        List<ResponseTimeslotDto> timeslotDtos = new ArrayList<>();
        for (Timeslot t : listOfTimeslots) {
            ResponseTimeslotDto dto = modelMapper.map(t, ResponseTimeslotDto.class);
            dto.setAccountId(t.getAccount().getId());
            timeslotDtos.add(dto);
        }
        PaginationDto<ResponseTimeslotDto> result = new PaginationDto<>();
        result.setContent(timeslotDtos);
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotalElements(timeslots.getTotalElements());
        result.setTotalPages(timeslots.getTotalPages());
        result.setLast(timeslots.isLast());
        return ResponseEntity.status(HttpStatus.OK)
                .body(result);
    }





}
