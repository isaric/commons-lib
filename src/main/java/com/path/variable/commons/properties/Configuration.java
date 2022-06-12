package com.path.variable.commons.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.function.Function.identity;

/**
 * The main class for the configuration component. The component is initialized statically and concurrency concerns
 * are handled by propagating to dependent components from a constants class (ex. ApplicationConstants.java) that is
 * initialized at application startup.
 * Supports loading properties with wrappers for Java primitive types Integer, Double, Boolean and String.
 * Also includes a convenience method for getting a comma-separated list from a property entry.
 * Supports environment specific property files based on a prefix. The environment prefix can be passed as a
 * JVM argument on application startup.
 * The base filename for the properties file is "app.properties".
 * An example file for a hypothetical "dev" environment would be:
 * "dev_app.properties".
 * The order of the property file loading is defined as follows:
 *  1) Inside the same folder as the deployed JAR file:
 *    a) Environment specific file
 *    b) Generic file
 *    c) System properties
 *  2) Inside the JAR file itself:
 *    a) Environment specific file
 *    b) Generic file
 * Also provides helper methods that allow for specifying a default value when getting the property.
 * Uses MessageFormat to allow for interpolation of arguments within the property key.
 */
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

        var env = System.getProperty(COMMONS_ENVIRONMENT);
        env = env == null ? properties.getProperty(COMMONS_ENVIRONMENT) : env;
        var envFilename = env != null ? format("%s_%s", env, BASE_FILENAME) : null;
        if (envFilename != null) loadResourceAndFile(properties, envFilename);
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getString(String key, Object... args) {
        return properties.getProperty(new MessageFormat(key).format(args));
    }

    public String getString(String key, String defaultValue, Object... args) {
        var result = properties.getProperty(new MessageFormat(key).format(args));
        return result == null ? defaultValue : result;
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

    public Integer getInteger(String key, Integer defaultValue, Object... args) {
        var stringValue = getString(new MessageFormat(key).format(args));
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

    public Double getDouble(String key, Object args) {
        var stringValue = getString(new MessageFormat(key).format(args));
        return Double.valueOf(stringValue);
    }

    public Double getDouble(String key, Double defaultValue, Object args) {
        var stringValue = getString(new MessageFormat(key).format(args));
        if (stringValue == null) {
            return defaultValue;
        }
        return Double.valueOf(stringValue);
    }

    public Boolean getBoolean(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        var stringValue = getString(key);
        if (stringValue == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(stringValue);
    }

    public Boolean getBoolean(String key, Object... args) {
        return Boolean.parseBoolean(getString(new MessageFormat(key).format(args)));
    }

    public Boolean getBoolean(String key, Boolean defaultValue, Object... args) {
        var stringValue = getString(new MessageFormat(key).format(args));
        if (stringValue == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(stringValue);
    }

    public Long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    public Long getLong(String key, Long defaultValue) {
        var stringValue = getString(key);
        if (stringValue == null) {
            return defaultValue;
        }
        return Long.valueOf(stringValue);
    }

    public Long getLong(String key, Long defaultValue, Object... args) {
        var stringValue = getString(new MessageFormat(key).format(args));
        if (stringValue == null) {
            return defaultValue;
        }
        return Long.valueOf(stringValue);
    }

    public List<String> getStringList(String key) {
        return getListForKeyAndParser(key, identity());
    }

    public List<Integer> getIntegerList(String key) {
        return getListForKeyAndParser(key, Integer::valueOf);
    }

    public List<Double> getDoubleList(String key) {
        return getListForKeyAndParser(key, Double::valueOf);
    }

    public List<Boolean> getBooleanList(String key) {
        return getListForKeyAndParser(key, Boolean::parseBoolean);
    }

    private <T> List<T>  getListForKeyAndParser(String key, Function<String, T> parser) {
        return Optional.ofNullable(getString(key))
                       .map(s -> s.split(","))
                       .map(Arrays::stream)
                       .orElseGet(Stream::empty)
                       .map(String::trim)
                       .map(parser)
                       .collect(Collectors.toList());
    }



    private static void loadResourceAndFile(Properties properties, String filename) {
        var resource = Configuration.class.getResourceAsStream(format("/%s",filename));
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
