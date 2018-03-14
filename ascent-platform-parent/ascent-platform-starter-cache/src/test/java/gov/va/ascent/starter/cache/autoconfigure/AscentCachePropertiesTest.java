package gov.va.ascent.starter.cache.autoconfigure;

import org.junit.Test;

import gov.va.ascent.starter.cache.autoconfigure.AscentCacheProperties.RedisExpires;

import java.util.ArrayList;
import java.util.List;

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
        List<RedisExpires> listRedisExpires = new ArrayList<>();
        RedisExpires redisExpires = new RedisExpires();
        redisExpires.setName("methodcachename_projectname_projectversion");
        redisExpires.setTtl(86400L);
        listRedisExpires.add(0, redisExpires);
        ascentCacheProperties.setExpires(listRedisExpires);
        ascentCacheProperties.setDefaultExpires(500L);
        assertTrue(!ascentCacheProperties.getExpires().isEmpty());
        assertTrue(Long.valueOf(86400L).equals(ascentCacheProperties.getExpires().get(0).getTtl()));
        assertEquals(new Long(500L), ascentCacheProperties.getDefaultExpires());
    }
}
