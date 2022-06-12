package com.path.variable.commons.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.path.variable.commons.logging.dto.LogResponseStatus;
import com.path.variable.commons.logging.dto.LoggerChangeRequest;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import static com.path.variable.commons.logging.dto.LogResponseStatus.*;

/**
 * This component provides an interface to access and manage an applications Loggers at runtime.
 * Also allows the client to specify a duration after which the altered log level reverts to the
 * original level.
 * The reasoning behind the component is simply convenience. It might get phased out in future releases.
 */
public class LogLevelService {

    private static final Logger LOG = (Logger) LoggerFactory.getLogger(LogLevelService.class);

    private final LoggerContext loggerContext;

    private final Timer timer;

    public LogLevelService() {
        this.loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.timer = new Timer();
    }

    public LogResponseStatus setLogLevel(LoggerChangeRequest request) {
        Duration duration = request.getDuration();
        return invalidRequest(request) ? INVALID_REQUEST : duration == null ?
                execute(request) : executeWithTimer(request);
    }

    public Level getLogLevel(String loggerName) {
        return loggerContext.getLogger(loggerName).getEffectiveLevel();
    }

    private boolean invalidRequest(LoggerChangeRequest request) {
        return request.getLoggerName() == null || request.getLevel() == null;
    }

    private LogResponseStatus execute(LoggerChangeRequest request) {
        String name = request.getLoggerName();
        String level = request.getLevel();
        try {
            Logger logger = loggerContext.exists(name);
            Level resolvedLevel = Level.toLevel(level);

            if (logger == null ) {
                return LOGGER_NOT_FOUND;
            }

            // Level returns DEBUG if no Levels mathced. We want to error out in this case.
            if (resolvedLevel.equals(Level.DEBUG) && !level.equals(Level.DEBUG.toString())) {
                return LEVEL_NOT_FOUND;
            }

            logger.setLevel(resolvedLevel);
            return CHANGED;
        } catch (Exception ex) {
            LOG.error("Exception while changing log level {} for logger {}", request.getLevel(), request.getLoggerName(), ex);
            return ERROR;
        }
    }

    private LogResponseStatus executeWithTimer(LoggerChangeRequest request) {
        Level currentLevel = Optional.ofNullable(loggerContext.getLogger(request.getLoggerName()))
                                     .map(Logger::getLevel)
                                     .orElse(null);
        LogResponseStatus status = execute(request);
        return status == ERROR ? ERROR : revertOnTimer(request, currentLevel);
    }

    private LogResponseStatus revertOnTimer(LoggerChangeRequest request, Level currentLevel) {
        try {
            LoggerChangeRequest revert = new LoggerChangeRequest(request.getLoggerName(), currentLevel.toString(), null);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    execute(revert);
                }
            }, request.getDuration().toMillis());
        } catch (Exception ex) {
            return ERROR;
        }
        return TIMER_SUCCESSFUL;
    }
}
