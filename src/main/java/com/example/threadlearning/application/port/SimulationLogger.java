package com.example.threadlearning.application.port;

public interface SimulationLogger {
    void log(String message);
    void log(String message, Object... args);

}
