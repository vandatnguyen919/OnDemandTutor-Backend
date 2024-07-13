package com.mytutor.dto.salary;

import com.mytutor.entities.Account;
import com.mytutor.entities.WithdrawRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author vothimaihoa
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestWithdrawRequestDto {
    private String bankAccountNumber;

    private String bankAccountOwner;

    private String bankName;

    private int month;

    private int year;

    public WithdrawRequest mapToEntity(Account tutor) {
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setBankAccountNumber(this.bankAccountNumber);
        withdrawRequest.setBankAccountOwner(this.bankAccountOwner);
        withdrawRequest.setBankName(this.bankName);
        withdrawRequest.setMonth(this.month);
        withdrawRequest.setYear(this.year);
        withdrawRequest.setTutor(tutor);
        return withdrawRequest;
    }
}
