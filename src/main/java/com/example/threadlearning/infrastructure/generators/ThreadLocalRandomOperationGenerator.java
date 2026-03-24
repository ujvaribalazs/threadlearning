package com.example.threadlearning.infrastructure.generators;

import com.example.threadlearning.application.port.OperationSource;
import com.example.threadlearning.domain.models.BankOperation;
import com.example.threadlearning.domain.models.FamilyMember;
import com.example.threadlearning.domain.models.OperationType;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomOperationGenerator implements OperationSource {

    private final int minAmount;
    private final int maxAmount;

    public ThreadLocalRandomOperationGenerator(int minAmount, int maxAmount) {
        if (minAmount <= 0) {
            throw new IllegalArgumentException("minAmount must be positive");
        }
        if (maxAmount < minAmount) {
            throw new IllegalArgumentException("maxAmount must be >= minAmount");
        }

        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public BankOperation nextOperation(FamilyMember familyMember) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        OperationType operationType = random.nextBoolean()
                ? OperationType.DEPOSIT
                : OperationType.WITHDRAW;

        int amount = random.nextInt(minAmount, maxAmount + 1);

        return new BankOperation(familyMember, operationType, amount);
    }
}