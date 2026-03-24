package com.example.threadlearning.infrastructure;

import com.example.threadlearning.application.port.SimulationLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleSimulationLogger implements SimulationLogger {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleSimulationLogger.class);

    @Override
    public void log(String message) {
        // a leírás szerint a logger backendje kezeli a többszálúságot
        logger.info(message);

    }


    @Override
    public void log(String message, Object... args) {
        logger.info(message, args);
    }

}

