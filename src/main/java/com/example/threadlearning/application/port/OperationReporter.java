package com.example.threadlearning.application.port;

import com.example.threadlearning.application.dto.OperationExecutionResult;

public interface OperationReporter {
    void report(OperationExecutionResult result);
}
