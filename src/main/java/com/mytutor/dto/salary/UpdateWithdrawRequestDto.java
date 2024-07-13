package com.mytutor.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateWithdrawRequestDto {
    Integer withdrawRequestId;
    String updatedStatus;
}
