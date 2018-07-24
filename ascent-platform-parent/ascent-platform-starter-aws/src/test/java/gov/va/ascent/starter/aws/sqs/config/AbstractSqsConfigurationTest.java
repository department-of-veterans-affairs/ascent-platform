/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.aws.sqs.config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.jms.ConnectionFactory;

import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.mock.env.MockEnvironment;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;

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
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final DestinationResolver result = instance.destinationResolver(sqsProperties);
		assertNotNull(result);

	}

	/**
	 * Test of jmsTemplate method, of class AbstractSqsConfiguration.
	 */
	@Test
	public void testJmsTemplate() {
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final JmsTemplate result = instance.jmsTemplate(sqsProperties, mock(ConnectionFactory.class));
		assertNotNull(result);
	}

	@Test
	public void testCreateStandardSQSConnectionFactory() {
		final SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("kttp://localhost:8080/endpoint");
		sqsProperties.setRegion("us-west-2");

		final AbstractSqsConfiguration instance = new AbstractSqsConfigurationImpl();
		final Environment env = new MockEnvironment();
		instance.environment = env;

		/*
		 * localstack-config:
		 * s3:
		 * endpoint: http://localhost:4572/
		 * services:
		 * - name: s3
		 * port: 4572
		 * - name: sqs
		 * port: 4576
		 * access_key_id: ${AWS_ACCESS_KEY:test-key}
		 * secret_access_key: ${AWS_SECRET_ACCESS_KEY:test-secret}
		 * s3:
		 * bucket: ${AWS_S3_BUCKET_NAME:test-bucket}
		 * region: ${AWS_REGION:us-west-2}
		 * target:
		 * bucket: ${AWS_S3_TARGET_BUCKET_NAME:test-s3-target-bucket}
		 * dlq:
		 * bucket: ${AWS_S3_DLQ_BUCKET_NAME:test-s3-dlq-bucket}
		 * sqs:
		 * region: ${AWS_SQS_REGION:us-west-2}
		 * endpoint: ${AWS_SQS_ENDPOINT:http://localhost:4576}
		 * dlqendpoint: ${AWS_SQS_DLQ_ENDPOINT:test-sqs-dlq-bucket}
		 * dlqRetriesCount: 2
		 * numberOfMessagesToPrefetch: ${AMAZON_SQS_NUMBER_OF_MESSAGES_TO_PREFETCH:5}
		 */
		final SQSConnectionFactory factory = instance.createStandardSQSConnectionFactory(sqsProperties);
		assertNotNull(factory);
	}

	public class AbstractSqsConfigurationImpl extends AbstractSqsConfiguration {

		@Override
		public ConnectionFactory connectionFactory(final SqsProperties sqsProperties) {
			return null;
		}
	}

}
