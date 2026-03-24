package com.example.threadlearning.application.usecase;

import com.example.threadlearning.application.dto.OperationExecutionResult;
import com.example.threadlearning.application.port.OperationSource;
import com.example.threadlearning.application.port.SimulationLogger;
import com.example.threadlearning.domain.models.BankAccount;
import com.example.threadlearning.domain.models.BankOperation;
import com.example.threadlearning.domain.models.FamilyMember;
import com.example.threadlearning.domain.models.OperationType;

public class ProcessOperationUseCase {

    private final OperationSource operationSource;
    private final BankAccount bankAccount;
    private final SimulationLogger simulationLogger;

    public ProcessOperationUseCase(
            OperationSource operationSource,
            BankAccount bankAccount,
            SimulationLogger simulationLogger
    ) {
        this.operationSource = operationSource;
        this.bankAccount = bankAccount;
        this.simulationLogger = simulationLogger;
    }

    public OperationExecutionResult execute(FamilyMember familyMember) {
        BankOperation operation = operationSource.nextOperation(familyMember);

        boolean success;

        if (operation.operationType() == OperationType.DEPOSIT) {
            bankAccount.deposit(operation.amount());
            success = true;
            simulationLogger.log(
                    "{} deposited {}",
                    operation.member().getMemberId(),
                    operation.amount()
            );
        } else {
            success = bankAccount.withdraw(operation.amount());
            simulationLogger.log(
                    "{} withdrew {}, success: {}",
                    operation.member().getMemberId(),
                    operation.amount(),
                    success
            );
        }

        return new OperationExecutionResult(
                familyMember.getMemberId(),
                operation.operationType(),
                operation.amount(),
                success
        );
    }
}