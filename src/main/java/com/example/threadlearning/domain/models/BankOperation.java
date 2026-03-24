package com.example.threadlearning.domain.models;

public record BankOperation (
    FamilyMember member,
    OperationType operationType,
    int amount
) {
    public BankOperation {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}
