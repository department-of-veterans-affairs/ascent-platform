package gov.va.ascent.starter.aws.sqs.config;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import gov.va.ascent.framework.config.AscentCommonSpringProfiles;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@EnableJms
@Profile(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)
public class SqsLocalStackConfiguration {

	@Value("${ascent.aws.localstack-config.sqs.region}")
	private String region;
	
	@Value("${ascent.aws.localstack-config.sqs.standardqueuename}")
	private String standardqueuename;
	
	@Value("${ascent.aws.localstack-config.sqs.deadletterqueuename}")
	private String deadletterqueuename;
	
	private Logger logger = LoggerFactory.getLogger(SqsLocalStackConfiguration.class);

	@Bean
	public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
		try {
			return createStandardSQSConnectionFactory(sqsProperties);
		} catch (JMSException e) {
			logger.error("Error occurred while creating SQS Connection Factory: " + e.getStackTrace());
		}
		return null;
	}

	@Bean
	public DestinationResolver destinationResolver(SqsProperties sqsProperties) {
		return new StaticDestinationResolver(standardqueuename);
	}

	@Bean
	public JmsTemplate jmsTemplate(SqsProperties sqsProperties, ConnectionFactory connectionFactory) {

		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setDefaultDestinationName(standardqueuename);
		jmsTemplate.setSessionAcknowledgeMode(SQSSession.UNORDERED_ACKNOWLEDGE);
		jmsTemplate.setMessageTimestampEnabled(true);
		return jmsTemplate;
	}

	protected SQSConnectionFactory createStandardSQSConnectionFactory(SqsProperties sqsProperties) throws JMSException {

		AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration(
						LocalstackDockerTestRunner.getLocalstackDocker().getEndpointSQS(), region))
				.withCredentials(new AWSStaticCredentialsProvider(TestUtils.TEST_CREDENTIALS)).build();
		SQSConnectionFactory connectionFactory = new SQSConnectionFactory(new ProviderConfiguration(), amazonSQS);

		AmazonSQS client = DockerTestUtils.getClientSQS();

		Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put("DelaySeconds", "0");
		attributeMap.put("MaximumMessageSize", "262144");
		attributeMap.put("MessageRetentionPeriod", "1209600");
		attributeMap.put("ReceiveMessageWaitTimeSeconds", "20");
		attributeMap.put("VisibilityTimeout", "30");

		String deadletterQueueUrl = client.createQueue(deadletterqueuename).getQueueUrl();

		GetQueueAttributesRequest getAttributesRequest = new GetQueueAttributesRequest(deadletterQueueUrl)
				.withAttributeNames(QueueAttributeName.QueueArn);
		GetQueueAttributesResult queueAttributesResult = client.getQueueAttributes(getAttributesRequest);

		String redrivePolicy = "{\"maxReceiveCount\":\"1\", \"deadLetterTargetArn\":\""
				+ queueAttributesResult.getAttributes().get(QueueAttributeName.QueueArn.name()) + "\"}";
		Map<String, String> attributes = new HashMap<>();
		attributes.put(QueueAttributeName.RedrivePolicy.name(), redrivePolicy);
		client.createQueue(new CreateQueueRequest(standardqueuename).withAttributes(attributes));

		return connectionFactory;
	}

}
