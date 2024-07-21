package com.mytutor.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWithdrawRequestDto {
    private Integer withdrawRequestId;
    private String updatedStatus;
    private String rejectReason;
    private String salaryPaidProvider;
    private String salaryPaidTransactionId;
}
