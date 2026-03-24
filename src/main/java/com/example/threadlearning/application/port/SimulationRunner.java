package com.example.threadlearning.application.port;

import java.util.List;
import java.util.concurrent.Callable;

public interface SimulationRunner {
    <T> List<T> runTasks(List<Callable<T>> tasks);
}