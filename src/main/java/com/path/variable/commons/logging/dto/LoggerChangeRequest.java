package com.path.variable.commons.logging.dto;

import java.time.Duration;

public class LoggerChangeRequest {

    private final String loggerName;

    private final String level;

    private final Duration duration;

    public LoggerChangeRequest(String loggerName, String level, Duration duration) {
        this.loggerName = loggerName;
        this.level = level;
        this.duration = duration;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getLevel() {
        return level;
    }

    public Duration getDuration() {
        return duration;
    }
}
