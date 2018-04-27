/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;


import javax.jms.ConnectionFactory;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;

/**
 *
 * @author rajuthota
 */
public class AbstractSqsConfigurationTest {

    /**
     * Test of destinationResolver method, of class AbstractSqsConfiguration.
     */
    @Test
    public void testDestinationResolver() {
        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
        AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
        DestinationResolver result = instance.destinationResolver(sqsProperties);
        assertNotNull(result);
    }

    /**
     * Test of jmsTemplate method, of class AbstractSqsConfiguration.
     */
    @Test
    public void testJmsTemplate() {
        SqsProperties sqsProperties = new SqsProperties();
        sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
        AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
        JmsTemplate result = instance.jmsTemplate(sqsProperties, mock(ConnectionFactory.class));
        assertNotNull(result);
    }

    public class AbstractSqsConfigurationImpl extends AbstractSqsConfiguration {

        public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
            return null;
        }
    }
    
}
