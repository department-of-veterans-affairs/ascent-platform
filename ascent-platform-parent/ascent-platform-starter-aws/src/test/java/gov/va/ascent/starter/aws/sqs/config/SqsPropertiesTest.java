/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

/**
 *
 * @author rajuthota
 */
public class SqsPropertiesTest {

    /**
     * Test of getNumberOfMessagesToPrefetch method, of class SqsProperties.
     */
    @Test
    public void testGetNumberOfMessagesToPrefetch() {
    	Integer preFetch = 8;
        SqsProperties instance = new SqsProperties();
        instance.setPrefetch(preFetch);


        assertEquals(instance.getPrefetch(), Optional.ofNullable(preFetch));
    }

    /**
     * Test of getQueueName method, of class SqsProperties.
     */
    @Test
    public void testGetQueueName() {
        SqsProperties instance = new SqsProperties();
        instance.setEndpoint("http://localhost:8080/queuename");
        assertEquals("queuename", instance.getQueueName());
    }

    /**
     * Test of getDLQQueueName method, of class SqsProperties.
     */
    @Test
    public void testGetDLQQueueName() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqendpoint("http://localhost:8080/queuename");
        assertEquals("queuename", instance.getDLQQueueName());
    }

    /**
     * Test of setSecretKey method, of class SqsProperties.
     */
    @Test
    public void testSetSecretKey() {
        SqsProperties instance = new SqsProperties();
        instance.setSecretKey("secretKey");
        assertEquals("secretKey", instance.getSecretKey());
    }

    /**
     * Test of setAccessKey method, of class SqsProperties.
     */
    @Test
    public void testSetAccessKey() {
        SqsProperties instance = new SqsProperties();
        instance.setAccessKey("accessKey");
        assertEquals("accessKey", instance.getAccessKey());

    }

    /**
     * Test of setRegion method, of class SqsProperties.
     */
    @Test
    public void testSetRegion() {
        SqsProperties instance = new SqsProperties();
        instance.setRegion("region");
        assertEquals("region", instance.getRegion());
    }

    /**
     * Test of setEndpoint method, of class SqsProperties.
     */
    @Test
    public void testSetEndpoint() {
        SqsProperties instance = new SqsProperties();
        instance.setEndpoint("http://localhost:8080/queuename");
        assertEquals("http://localhost:8080/queuename", instance.getEndpoint());
    }


    /**
     * Test of setDlqendpoint method, of class SqsProperties.
     */
    @Test
    public void testSetDlqendpoint() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqendpoint("http://localhost:8080/queuename");
        assertEquals("http://localhost:8080/queuename", instance.getDlqendpoint());
    }


    /**
     * Test of setDlqRetriesCount method, of class SqsProperties.
     */
    @Test
    public void testSetDlqRetriesCount() {
        int dlqRetriesCount = 0;
        SqsProperties instance = new SqsProperties();
        instance.setRetries(dlqRetriesCount);
        assertEquals(dlqRetriesCount, instance.getRetries());
    }
    
    /**
     * Test of queueType method, of class SqsProperties.
     */
    @Test
    public void testSetQueueType() {
        boolean queueType = false;
        SqsProperties instance = new SqsProperties();
        instance.setQueuetype(queueType);
        assertEquals(queueType, instance.getQueuetype());
    }
    
    /**
     * Test of ContentBasedDuplication method, of class SqsProperties.
     */
    @Test
    public void testSetContentBasedDuplication() {
        boolean contentBased = false;
        SqsProperties instance = new SqsProperties();
        instance.setContentbasedduplication(contentBased);
        assertEquals(contentBased, instance.getContentbasedduplication());
    }
    
    /**
     * Test of Delay method, of class SqsProperties.
     */
    @Test
    public void testSetDelay() {
        Integer delay = 5;
        SqsProperties instance = new SqsProperties();
        instance.setDelay(delay);
        assertEquals(delay, instance.getDelay());
    }
    
    /**
     * Test of Max method, of class SqsProperties.
     */
    @Test
    public void testSetMaxmessagesize() {
        String max = "5";
        SqsProperties instance = new SqsProperties();
        instance.setMaxmessagesize(max);
        assertEquals(max, instance.getMaxmessagesize());
    }
    
    /**
     * Test of MessageRet method, of class SqsProperties.
     */
    @Test
    public void testSetMessageretentionperiod() {
        String ret = "5";
        SqsProperties instance = new SqsProperties();
        instance.setMessageretentionperiod(ret);
        assertEquals(ret, instance.getMessageretentionperiod());
    }
    
    /**
     * Test of Waittime method, of class SqsProperties.
     */
    @Test
    public void testSetWaittime() {
        Integer waittime = 5;
        SqsProperties instance = new SqsProperties();
        instance.setWaittime(waittime);
        assertEquals(waittime, instance.getWaittime());
    }
    
    /**
     * Test of Visibility method, of class SqsProperties.
     */
    @Test
    public void testSetVisibility() {
        Integer visibility = 5;
        SqsProperties instance = new SqsProperties();
        instance.setVisibilitytimeout(visibility);
        assertEquals(visibility, instance.getVisibilitytimeout());
    }
    
}
