package com.path.variable.commons.properties;

import org.junit.Test;

import static com.path.variable.commons.properties.Configuration.getConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testStringList() {
        var list = getConfiguration().getStringList("s.list");

        assertEquals(2,list.size());
        assertTrue(list.contains("abc"));
        assertTrue(list.contains("def"));
    }

    @Test
    public void testIntegerList() {
        var list = getConfiguration().getIntegerList("i.list");

        assertEquals(4, list.size());
        assertTrue(list.contains(1));
        assertTrue(list.contains(2));
        assertTrue(list.contains(3));
        assertTrue(list.contains(4));
    }

    @Test
    public void testDoubleList() {
        var list = getConfiguration().getDoubleList("d.list");

        assertEquals(2, list.size());
        assertTrue(list.contains(1.5));
        assertTrue(list.contains(25.6));
    }

    @Test
    public void testBooleanList() {
        var list = getConfiguration().getBooleanList("b.list");

        assertEquals(4, list.size());
        assertTrue(list.contains(true));
        assertTrue(list.contains(false));
        assertTrue(list.contains(false));
        assertTrue(list.contains(true));
    }
}
