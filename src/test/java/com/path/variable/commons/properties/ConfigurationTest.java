package com.path.variable.commons.properties;

import org.junit.Test;

import static com.path.variable.commons.properties.Configuration.getConfiguration;
import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

    @Test
    public void readsAndOverridesSuccessfully() {
        var first = getConfiguration().getString("1");
        var second = getConfiguration().getString("2");
        var third = getConfiguration().getString("3");
        var fourth = getConfiguration().getString("4");

        assertEquals("external_dev", first);
        assertEquals("base_dev", second);
        assertEquals("external", third);
        assertEquals("base", fourth);
    }
}
