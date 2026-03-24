package com.example.threadlearning.infrastructure.runners;

import com.example.threadlearning.application.port.SimulationRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FixedPoolRunner implements SimulationRunner {

    private final int poolSize;

    public FixedPoolRunner(int poolSize) {
        if (poolSize <= 0) {
            throw new IllegalArgumentException("poolSize must be greater than 0");
        }
        this.poolSize = poolSize;
    }

    @Override
    public <T> List<T> runTasks(List<Callable<T>> tasks) {
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        try {
            List<Future<T>> futures = executorService.invokeAll(tasks);
            List<T> results = new ArrayList<>();

            for (Future<T> future : futures) {
                results.add(future.get());
            }

            return results;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while executing tasks", e);

        } catch (ExecutionException e) {
            throw new RuntimeException("Task execution failed", e.getCause());

        } finally {
            executorService.shutdown();
        }
    }
}