package com.mytutor.constants;


public enum WithdrawRequestStatus {
    PROCESSING(1),
    REJECTED(2),
    DONE(3);

    private final int ranking;

    WithdrawRequestStatus(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return ranking;
    }

    public static WithdrawRequestStatus getWithdrawRequestStatus(String withdrawRequestStatus) {
        if (withdrawRequestStatus == null) return null;
        if (withdrawRequestStatus.equalsIgnoreCase(PROCESSING.toString())) {
            return PROCESSING;
        } else if (withdrawRequestStatus.equalsIgnoreCase(REJECTED.toString())) {
            return REJECTED;
        } else if (withdrawRequestStatus.equalsIgnoreCase(DONE.toString())) {
            return DONE;
        }
        return null;
    }
}
