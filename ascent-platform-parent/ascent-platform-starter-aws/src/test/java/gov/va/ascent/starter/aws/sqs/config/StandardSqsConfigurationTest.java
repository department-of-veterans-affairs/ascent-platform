/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rajuthota
 */
public class StandardSqsConfigurationTest {

    /**
     * Test of connectionFactory method, of class StandardSqsConfiguration.
     */
    @Test
    public void testConnectionFactory() {
        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setAccessKey("sampleAccesskey");
        sqsProperties.setSecretKey("sampleSecrectKey");
        sqsProperties.setRegion("us-west-2");
        sqsProperties.setEndpoint("http://localhost:8080/endpoint");
        StandardSqsConfiguration instance = new StandardSqsConfiguration();
        ConnectionFactory result = instance.connectionFactory(sqsProperties);
        assertNotNull(result);
    }
    
}
