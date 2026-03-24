package com.example.threadlearning.application.dto;

import com.example.threadlearning.domain.models.OperationType;

public record OperationExecutionResult(
        String memberId,
        OperationType operationType,
        int amount,
        boolean success
) {
}