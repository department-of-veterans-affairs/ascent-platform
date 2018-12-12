/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author rajuthota
 */
public class SqsPropertiesTest {

    /**
     * Test of getNumberOfMessagesToPrefetch method, of class SqsProperties.
     */
/*    @Test
    public void testGetNumberOfMessagesToPrefetch() {
        SqsProperties instance = new SqsProperties();
        Optional<Integer> result = instance.getNumberOfMessagesToPrefetch();
		ReflectionTestUtils.setField(instance, "numberOfMessagesToPrefetch", 1);

        assertEquals(1, result);
    }*/

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
    }
    
}
