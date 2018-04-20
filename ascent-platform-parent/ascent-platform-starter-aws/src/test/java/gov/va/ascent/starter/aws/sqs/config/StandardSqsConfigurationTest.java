/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 *
 * @author rajuthota
 */
@RunWith(MockitoJUnitRunner.class)
public class StandardSqsConfigurationTest {
	
    @InjectMocks
    StandardSqsConfiguration standardSqsConfiguration = new StandardSqsConfiguration();
	
    @Mock
    private Environment environment;
    
	@Before
	public void setUp() throws Exception {
        String[] profiles = { "local-int" };
		when(environment.getActiveProfiles()).thenReturn(profiles);
	}

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
        ConnectionFactory result = standardSqsConfiguration.connectionFactory(sqsProperties);
        assertNotNull(result);
    }
}
