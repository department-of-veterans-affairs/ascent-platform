package gov.va.ascent.starter.cache.autoconfigure;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AscentCachePropertiesTest {

    @Test
    public void testGetters(){
        AscentCacheProperties ascentCacheProperties = new AscentCacheProperties();
        assertNull(ascentCacheProperties.getExpires());
        assertEquals(new Long(86400L), ascentCacheProperties.getDefaultExpires());
    }

    @Test
    public void testSetters(){
        AscentCacheProperties ascentCacheProperties = new AscentCacheProperties();
        ascentCacheProperties.setExpires(new HashMap<>());
        ascentCacheProperties.setDefaultExpires(500L);
        assertTrue(ascentCacheProperties.getExpires().isEmpty());
        assertEquals(new Long(500L), ascentCacheProperties.getDefaultExpires());
    }
}
