package com.example.threadlearning.application.dto;

public record MemberSimulationResult(
        String memberId,
        int totalOperations,
        int depositAttempts,
        int withdrawAttempts,
        int successfulWithdrawals,
        int failedWithdrawals,
        int totalDepositedAmount,
        int totalWithdrawnAmount
) {
}
