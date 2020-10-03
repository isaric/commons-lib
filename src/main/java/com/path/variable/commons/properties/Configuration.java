package com.path.variable.commons.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

    private static final String BASE_FILENAME = "app.properties";

    private static final String COMMONS_ENVIRONMENT = "commons.environment";

    private static final Configuration configuration;

    static {
        configuration = new Configuration();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    private final Properties properties;

    private Configuration() {
        this.properties = new Properties();
        loadResourceAndFile(properties, BASE_FILENAME);

        var env = System.getProperty("commons.environment");
        env = env == null ? properties.getProperty(COMMONS_ENVIRONMENT) : env;
        var envFilename = env != null ? "%s_%S".formatted(env, BASE_FILENAME) : null;
        if (envFilename != null) loadResourceAndFile(properties, envFilename);

    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public Integer getInteger(String key) {
        var stringValue = getString(key);
        return Integer.valueOf(stringValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        var stringValue = getString(key);
        if (stringValue == null) {
            return defaultValue;
        }
        return Integer.valueOf(stringValue);
    }

    public Double getDouble(String key) {
        var stringValue = getString(key);
        return Double.valueOf(stringValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        var stringValue = getString(key);
        if (stringValue == null) {
            return defaultValue;
        }
        return Double.valueOf(stringValue);
    }

    private static void loadResourceAndFile(Properties properties, String filename) {
        var resource = Configuration.class.getResourceAsStream(filename);
        if (resource != null) {
            try {
                properties.load(resource);
            } catch (IOException e) {
                LOG.error("Error while reading base resources property file.");
            }
        }

        try {
            var fis = new FileInputStream(new File(filename));
            properties.load(fis);
        } catch (FileNotFoundException e) {
            LOG.debug("no properties file outside of resources folder found for name {}", filename);
        } catch (IOException e) {
            LOG.debug("Error while reading properties file {}", filename);
        }
    }
}
