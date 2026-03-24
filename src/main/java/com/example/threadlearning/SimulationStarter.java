package com.example.threadlearning;

import com.example.threadlearning.application.dto.BenchmarkResult;
import com.example.threadlearning.application.dto.SimulationRequest;
import com.example.threadlearning.application.dto.SimulationResult;
import com.example.threadlearning.application.port.OperationReporter;
import com.example.threadlearning.application.port.OperationSource;
import com.example.threadlearning.application.port.SimulationLogger;
import com.example.threadlearning.application.port.SimulationRunner;
import com.example.threadlearning.application.usecase.ProcessOperationUseCase;
import com.example.threadlearning.application.usecase.RunSimulationUseCase;
import com.example.threadlearning.config.SimulationProperties;
import com.example.threadlearning.domain.models.BankAccount;
import com.example.threadlearning.domain.models.FamilyMember;
import com.example.threadlearning.infrastructure.HttpOperationReporter;
import com.example.threadlearning.infrastructure.generators.RandomOperationGenerator;
import com.example.threadlearning.infrastructure.ConsoleSimulationLogger;
import com.example.threadlearning.infrastructure.runners.FixedPoolRunner;
import com.example.threadlearning.infrastructure.runners.PlainThreadsRunner;
import com.example.threadlearning.infrastructure.runners.VirtualThreadsRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class SimulationStarter {

    @Bean
    CommandLineRunner runSimulation(SimulationProperties properties) {
        return args -> {
            BankAccount bankAccount = new BankAccount(properties.getInitialBalance());

            OperationSource operationSource = new RandomOperationGenerator(
                    new Random(),
                    properties.getMinAmount(),
                    properties.getMaxAmount()
            );

            SimulationLogger simulationLogger = new ConsoleSimulationLogger();

            SimulationRunner simulationRunner = switch (properties.getRunnerType()) {
                case "plain" -> new PlainThreadsRunner();
                case "fixed" -> new FixedPoolRunner(properties.getPoolSize());
                case "virtual" -> new VirtualThreadsRunner();
                default -> throw new IllegalArgumentException(
                        "Unknown runner type: " + properties.getRunnerType()
                );
            };

            OperationReporter reporter = new HttpOperationReporter("https://httpbin.org/post");

            ProcessOperationUseCase processOperationUseCase =
                    new ProcessOperationUseCase(operationSource, bankAccount, simulationLogger, reporter);

            RunSimulationUseCase runSimulationUseCase =
                    new RunSimulationUseCase(
                            simulationRunner,
                            processOperationUseCase,
                            bankAccount,
                            simulationLogger
                    );

            List<FamilyMember> familyMembers = new ArrayList<>();
            for (int i = 0; i < properties.getMemberCount(); i++) {
                familyMembers.add(new FamilyMember());
            }

            SimulationRequest request = new SimulationRequest(
                    familyMembers,
                    properties.getOperationsPerMember()
            );

            long start = System.nanoTime();
            SimulationResult result = runSimulationUseCase.execute(request);
            long end = System.nanoTime();
            long elapsedMillis = (end - start) / 1_000_000;
            double operationsPerSecond =
                    result.totalOperations() / ((end - start) / 1_000_000_000.0);
            boolean consistent =
                    result.initialBalance()
                            + result.totalDepositedAmount()
                            - result.totalWithdrawnAmount()
                            == result.finalBalance();

            BenchmarkResult benchmarkResult = new BenchmarkResult(
                    properties.getRunnerType(),
                    request.familyMembers().size(),
                    request.operationsPerMember(),
                    result.totalOperations(),
                    elapsedMillis,
                    operationsPerSecond,
                    consistent
            );

            System.out.println("=== Benchmark result ===");
            System.out.println("Runner type: " + benchmarkResult.runnerType());
            System.out.println("Member count: " + benchmarkResult.memberCount());
            System.out.println("Operations per member: " + benchmarkResult.operationsPerMember());
            System.out.println("Total operations: " + benchmarkResult.totalOperations());
            System.out.println("Elapsed time (ms): " + benchmarkResult.elapsedMillis());
            System.out.printf("Operations per second: %.2f%n", benchmarkResult.operationsPerSecond());
            System.out.println("Consistent: " + benchmarkResult.consistent());

            System.out.println();
            System.out.println("=== Simulation result ===");
            System.out.println("Initial balance: " + result.initialBalance());
            System.out.println("Final balance: " + result.finalBalance());
            System.out.println("Total deposited amount: " + result.totalDepositedAmount());
            System.out.println("Total withdrawn amount: " + result.totalWithdrawnAmount());
        };
    }
}