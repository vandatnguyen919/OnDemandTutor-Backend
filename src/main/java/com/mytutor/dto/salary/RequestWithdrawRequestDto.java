package com.mytutor.dto.salary;

import com.mytutor.entities.Account;
import com.mytutor.entities.WithdrawRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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

    @Min(value = 1, message = "must be greater than or equal to 1")
    @Max(value = 12, message = "must be less than or equal to 12")
    private int month;

    @Min(value = 0, message = "must be greater than or equal to 0")
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
