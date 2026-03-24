package com.example.threadlearning.infrastructure.generators;

import com.example.threadlearning.application.port.OperationSource;
import com.example.threadlearning.domain.models.BankOperation;
import com.example.threadlearning.domain.models.FamilyMember;
import com.example.threadlearning.domain.models.OperationType;

import java.util.Random;

public class RandomOperationGenerator implements OperationSource {
    // mivel az RandomOperationGenerator is szándékosan shared resource, ezért nem a ThreadLocalRandom-ot használom

    private final Random random;
    private final int minAmount;
    private final int maxAmount;

    public RandomOperationGenerator(Random random, int minAmount, int maxAmount) {
        this.random = random;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    @Override
    public synchronized BankOperation nextOperation(FamilyMember familyMember) {
            OperationType operationType = random.nextBoolean()
                    ? OperationType.DEPOSIT
                    : OperationType.WITHDRAW;
            int amount = random.nextInt(minAmount, maxAmount + 1);
            return new BankOperation(familyMember, operationType, amount);
        }
}
