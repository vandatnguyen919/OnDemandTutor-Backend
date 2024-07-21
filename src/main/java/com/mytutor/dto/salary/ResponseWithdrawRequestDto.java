package com.mytutor.dto.salary;

import com.mytutor.constants.WithdrawRequestStatus;
import com.mytutor.dto.tutor.TutorInfoDto;
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
public class ResponseWithdrawRequestDto {

        private int id;

        private String bankAccountNumber;

        private String bankAccountOwner;

        private String bankName;

        private double amount;

        private int month;

        private int year;

        private TutorInfoDto tutor;

        private WithdrawRequestStatus status;

        private String paidSalaryProvider;

        private String paidSalaryTransactionId;

        public ResponseWithdrawRequestDto(WithdrawRequest withdrawRequest, Account tutor) {
                this.id = withdrawRequest.getId();
                this.bankAccountNumber = withdrawRequest.getBankAccountNumber();
                this.bankAccountOwner = withdrawRequest.getBankAccountOwner();
                this.bankName = withdrawRequest.getBankName();
                this.amount = withdrawRequest.getAmount();
                this.month = withdrawRequest.getMonth();
                this.year = withdrawRequest.getYear();
                this.status = withdrawRequest.getStatus();
                this.tutor = TutorInfoDto.mapToDto(tutor, tutor.getTutorDetail());
                this.paidSalaryProvider = withdrawRequest.getSalaryPaidProvider();
                this.paidSalaryTransactionId = withdrawRequest.getSalaryPaidTransactionId();
        }

}
