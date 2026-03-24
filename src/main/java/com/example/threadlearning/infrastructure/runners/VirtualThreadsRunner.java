package com.example.threadlearning.infrastructure.runners;

import com.example.threadlearning.application.port.SimulationRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class VirtualThreadsRunner implements SimulationRunner {

    @Override
    public <T> List<T> runTasks(List<Callable<T>> tasks) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<T>> futures = tasks.stream()
                    .map(executor::submit)
                    .toList();

            List<T> results = new ArrayList<>();
            for (Future<T> future : futures) {
                results.add(future.get());
            }
            return results;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Task failed", e.getCause());
        }
    }
}