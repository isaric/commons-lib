package com.path.variable.commons.properties;

import ch.qos.logback.classic.Level;
import com.path.variable.commons.logging.LogLevelService;
import com.path.variable.commons.logging.dto.LogResponseStatus;
import com.path.variable.commons.logging.dto.LoggerChangeRequest;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class LogLevelServiceTest {

    private static final Logger LOG = LoggerFactory.getLogger(LogLevelServiceTest.class);

    private final LogLevelService logLevelService = new LogLevelService();

    @Before
    public void setLevel() {
        ((ch.qos.logback.classic.Logger) LOG).setLevel(Level.INFO);
    }

    @Test
    public void willSetLogLevel() {
        LoggerChangeRequest request = new LoggerChangeRequest(LogLevelServiceTest.class.getName(), "TRACE", null);

        LogResponseStatus response = logLevelService.setLogLevel(request);

        assertEquals(LogResponseStatus.CHANGED, response);
        assertTrue(LOG.isTraceEnabled());
    }

    @Test
    public void willGetLogLevel() {
        Level level = logLevelService.getLogLevel(LogLevelServiceTest.class.getName());

        assertEquals(Level.INFO, level);
    }

    @Test
    public void willGetDefaultLogLevelForNonExistantLogger() {
        Level level = logLevelService.getLogLevel("FlyingSaucer");

        assertEquals(Level.ERROR, level);
    }

    @Test
    public void willRevertTimedRequest() throws InterruptedException {
        LoggerChangeRequest request = new LoggerChangeRequest(LogLevelServiceTest.class.getName(), "TRACE", Duration.ofMillis(3000));

        LogResponseStatus response = logLevelService.setLogLevel(request);

        assertEquals(LogResponseStatus.TIMER_SUCCESSFUL, response);
        assertTrue(LOG.isTraceEnabled());

        sleep(4000);

        assertFalse(LOG.isTraceEnabled());
    }

    @Test
    public void willFailNullLoggerNameOrNullLevel() {
        LoggerChangeRequest nullNameRequest = new LoggerChangeRequest(null, "TRACE", null);
        LogResponseStatus response = logLevelService.setLogLevel(nullNameRequest);

        assertEquals(LogResponseStatus.INVALID_REQUEST, response);

        LoggerChangeRequest nullLevelRequest = new LoggerChangeRequest(LogLevelServiceTest.class.getName(), null, null);
        response = logLevelService.setLogLevel(nullLevelRequest);

        assertEquals(LogResponseStatus.INVALID_REQUEST, response);
    }

    @Test
    public void willErrorOnNonExistentLogger() {
        LoggerChangeRequest request = new LoggerChangeRequest("com.banana.SpaceInvasion", "INFO", null);

        LogResponseStatus response = logLevelService.setLogLevel(request);

        assertEquals(LogResponseStatus.LOGGER_NOT_FOUND, response);
    }

    @Test
    public void willErrorOnNonExistentLevel() {
        LoggerChangeRequest request = new LoggerChangeRequest(LogLevelServiceTest.class.getName(), "TRIPPING", null);

        LogResponseStatus response = logLevelService.setLogLevel(request);

        assertEquals(LogResponseStatus.LEVEL_NOT_FOUND, response);
    }
}
