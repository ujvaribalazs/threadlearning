package com.example.threadlearning.application.usecase;

import com.example.threadlearning.application.dto.MemberSimulationResult;
import com.example.threadlearning.application.dto.OperationExecutionResult;
import com.example.threadlearning.application.dto.SimulationRequest;
import com.example.threadlearning.application.dto.SimulationResult;
import com.example.threadlearning.application.port.SimulationLogger;
import com.example.threadlearning.application.port.SimulationRunner;
import com.example.threadlearning.domain.models.BankAccount;
import com.example.threadlearning.domain.models.FamilyMember;
import com.example.threadlearning.domain.models.OperationType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RunSimulationUseCase {

    private final SimulationRunner simulationRunner;
    private final ProcessOperationUseCase processOperationUseCase;
    private final BankAccount bankAccount;
    private final SimulationLogger simulationLogger;

    public RunSimulationUseCase(
            SimulationRunner simulationRunner,
            ProcessOperationUseCase processOperationUseCase,
            BankAccount bankAccount,
            SimulationLogger simulationLogger
    ) {
        this.simulationRunner = simulationRunner;
        this.processOperationUseCase = processOperationUseCase;
        this.bankAccount = bankAccount;
        this.simulationLogger = simulationLogger;
    }

    public SimulationResult execute(SimulationRequest request) {
        int initialBalance = bankAccount.getBalance();

        simulationLogger.log(
                "Starting simulation with {} family members and {} operations per member",
                request.familyMembers().size(),
                request.operationsPerMember()
        );

        List<Callable<MemberSimulationResult>> tasks = new ArrayList<>();

        for (FamilyMember familyMember : request.familyMembers()) {
            tasks.add(() -> runMemberSimulation(familyMember, request.operationsPerMember()));
        }

        List<MemberSimulationResult> memberResults = simulationRunner.runTasks(tasks);

        SimulationResult simulationResult =
                aggregate(initialBalance, bankAccount.getBalance(), memberResults);

        simulationLogger.log(
                "Simulation finished. Final balance: {}, total operations: {}",
                simulationResult.finalBalance(),
                simulationResult.totalOperations()
        );

        return simulationResult;
    }

    private MemberSimulationResult runMemberSimulation(
            FamilyMember familyMember,
            int operationsPerMember
    ) {
        int depositAttempts = 0;
        int withdrawAttempts = 0;
        int successfulWithdrawals = 0;
        int failedWithdrawals = 0;
        int totalDepositedAmount = 0;
        int totalWithdrawnAmount = 0;

        for (int i = 0; i < operationsPerMember; i++) {
            OperationExecutionResult result = processOperationUseCase.execute(familyMember);

            if (result.operationType() == OperationType.DEPOSIT) {
                depositAttempts++;
                totalDepositedAmount += result.amount();
            } else {
                withdrawAttempts++;
                if (result.success()) {
                    successfulWithdrawals++;
                    totalWithdrawnAmount += result.amount();
                } else {
                    failedWithdrawals++;
                }
            }
        }

        return new MemberSimulationResult(
                familyMember.getMemberId(),
                operationsPerMember,
                depositAttempts,
                withdrawAttempts,
                successfulWithdrawals,
                failedWithdrawals,
                totalDepositedAmount,
                totalWithdrawnAmount
        );
    }

    private SimulationResult aggregate(
            int initialBalance,
            int finalBalance,
            List<MemberSimulationResult> memberResults
    ) {
        int totalOperations = 0;
        int depositAttempts = 0;
        int withdrawAttempts = 0;
        int successfulWithdrawals = 0;
        int failedWithdrawals = 0;
        int totalDepositedAmount = 0;
        int totalWithdrawnAmount = 0;

        for (MemberSimulationResult result : memberResults) {
            totalOperations += result.totalOperations();
            depositAttempts += result.depositAttempts();
            withdrawAttempts += result.withdrawAttempts();
            successfulWithdrawals += result.successfulWithdrawals();
            failedWithdrawals += result.failedWithdrawals();
            totalDepositedAmount += result.totalDepositedAmount();
            totalWithdrawnAmount += result.totalWithdrawnAmount();
        }

        return new SimulationResult(
                initialBalance,
                finalBalance,
                totalOperations,
                depositAttempts,
                withdrawAttempts,
                successfulWithdrawals,
                failedWithdrawals,
                totalDepositedAmount,
                totalWithdrawnAmount,
                memberResults
        );
    }
}