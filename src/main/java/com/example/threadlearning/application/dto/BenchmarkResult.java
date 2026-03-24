package com.example.threadlearning.application.dto;

public record BenchmarkResult(
        String runnerType,
        int memberCount,
        int operationsPerMember,
        int totalOperations,
        long elapsedMillis,
        double operationsPerSecond,
        boolean consistent
) {
}