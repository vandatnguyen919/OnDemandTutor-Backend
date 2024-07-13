package com.mytutor.services;

import com.mytutor.dto.PaginationDto;
import com.mytutor.dto.salary.RequestWithdrawRequestDto;
import com.mytutor.dto.salary.ResponseWithdrawRequestDto;
import com.mytutor.dto.salary.UpdateWithdrawRequestDto;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface SalaryService {
    double getMonthlySalaryOfATutor(Integer accountId, Integer month, Integer year);

    // send emails for tutors to announce they have salary
    void sendSalaryAnnouncementEmail(Integer month, Integer year);

    ResponseWithdrawRequestDto createWithdrawRequest(Principal principal, Integer tutorId, RequestWithdrawRequestDto withdrawRequest);

    //admins only
    PaginationDto<ResponseWithdrawRequestDto> getListOfWithdrawRequest(Integer pageNo, Integer pageSize);

    ResponseWithdrawRequestDto updateWithdrawRequest(UpdateWithdrawRequestDto requestToUpdateDto);

    void sendWithdrawRequestEmail(UpdateWithdrawRequestDto requestToUpdateDto);
}
