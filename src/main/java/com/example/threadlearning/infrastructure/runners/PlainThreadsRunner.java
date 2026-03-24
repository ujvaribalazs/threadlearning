package com.example.threadlearning.infrastructure.runners;

import com.example.threadlearning.application.port.SimulationRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class PlainThreadsRunner implements SimulationRunner {

    @Override
    public <T> List<T> runTasks(List<Callable<T>> tasks) {
        List<TaskExecution<T>> executions = new ArrayList<>();

        for (Callable<T> task : tasks) {
            TaskExecution<T> execution = new TaskExecution<>(task);
            executions.add(execution);
            execution.thread.start();
        }

        for (TaskExecution<T> execution : executions) {
            try {
                execution.thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread was interrupted while waiting for tasks to finish", e);
            }
        }

        List<T> results = new ArrayList<>();

        for (TaskExecution<T> execution : executions) {
            if (execution.exception != null) {
                throw new RuntimeException("Task execution failed", execution.exception);
            }
            results.add(execution.result);
        }

        return results;
    }

    private static class TaskExecution<T> {
        private final Thread thread;
        private T result;
        private Exception exception;

        private TaskExecution(Callable<T> task) {
            this.thread = new Thread(() -> {
                try {
                    result = task.call();
                } catch (Exception e) {
                    exception = e;
                }
            }, "simulation-thread");
        }
    }
}