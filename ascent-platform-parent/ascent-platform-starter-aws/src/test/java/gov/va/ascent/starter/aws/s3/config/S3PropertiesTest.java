/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.s3.config;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

/**
 *
 * @author vanapalliv
 */
public class S3PropertiesTest {
	

    @Test
    public void testGetMaxconcurrentrequestsh() {
    	Integer max = 8;
        S3Properties instance = new S3Properties();
        instance.setMaxconcurrentrequests(max);

        assertEquals(instance.getMaxconcurrentrequests(), max);
    }
    
    @Test
    public void testGetMaxqueuesize() {
    	Integer max = 8;
        S3Properties instance = new S3Properties();
        instance.setMaxqueuesize(max);

        assertEquals(instance.getMaxqueuesize(), max);
    }
    
    @Test
    public void testMultipartthreshold() {
    	Integer max = 8;
        S3Properties instance = new S3Properties();
        instance.setMultipartthreshold(max);

        assertEquals(instance.getMultipartthreshold(), max);
    }
    
    @Test
    public void testGetMultipartchunksize() {
    	Integer max = 8;
        S3Properties instance = new S3Properties();
        instance.setMultipartchunksize(max);

        assertEquals(instance.getMultipartchunksize(), max);
    }
    
    @Test
    public void testGetUseaccelerateendpoint() {
    	Boolean useaccelerateendpoint = true;
        S3Properties instance = new S3Properties();
        instance.setUseaccelerateendpoint(useaccelerateendpoint);

        assertEquals(instance.getUseaccelerateendpoint(), useaccelerateendpoint);
    }

    @Test
    public void testGetPayloadsigningenabled() {
    	
    	Boolean payloadsigningenabled = true;
        S3Properties instance = new S3Properties();
        instance.setPayloadsigningenabled(payloadsigningenabled);

        assertEquals(instance.getPayloadsigningenabled(), payloadsigningenabled);
    }
    
    @Test
    public void testGetAddressingStyle() {
    	String add = "st";
        S3Properties instance = new S3Properties();
        instance.setAddressingstyle(add);

        assertEquals(instance.getAddressingstyle(), add);
    }

 }
