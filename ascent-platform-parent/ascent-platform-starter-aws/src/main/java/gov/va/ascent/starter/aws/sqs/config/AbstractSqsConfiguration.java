package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.util.StringUtils;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import cloud.localstack.docker.LocalstackDockerTestRunner;
import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.starter.aws.server.AscentEmbeddedAwsLocalstack;

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@EnableJms
public abstract class AbstractSqsConfiguration {
	@Autowired
    Environment environment;
	
	@Autowired
	AscentEmbeddedAwsLocalstack ascentEmbeddedAwsLocalstack;
	
	@Bean
	public abstract ConnectionFactory connectionFactory(SqsProperties sqsProperties);

	@Bean
	public DestinationResolver destinationResolver(SqsProperties sqsProperties) {
		return new StaticDestinationResolver(sqsProperties.getQueueName());
	}

	@Bean
	public JmsTemplate jmsTemplate(
			SqsProperties sqsProperties, ConnectionFactory connectionFactory) {

		JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
		jmsTemplate.setDefaultDestinationName(sqsProperties.getQueueName());
		jmsTemplate.setSessionAcknowledgeMode(SQSSession.UNORDERED_ACKNOWLEDGE);
		jmsTemplate.setMessageTimestampEnabled(true);
		return jmsTemplate;
	}

	protected SQSConnectionFactory createStandardSQSConnectionFactory(SqsProperties sqsProperties) {
		AmazonSQS sqsClient = createAmazonSQSClient(sqsProperties);

		ProviderConfiguration providerConfiguration = new ProviderConfiguration();
		sqsProperties.getNumberOfMessagesToPrefetch()
		.ifPresent(providerConfiguration::setNumberOfMessagesToPrefetch);

		return new SQSConnectionFactory(providerConfiguration, sqsClient);
	}

	private AmazonSQS createAmazonSQSClient(SqsProperties sqsProperties) {

	    EndpointConfiguration endpointConfiguration = getEndpointConfiguration(sqsProperties);

		AWSCredentialsProvider awsCredentialsProvider = createAwsCredentialsProvider(
				sqsProperties.getAccessKey(),
				sqsProperties.getSecretKey()
				);

		return AmazonSQSClientBuilder
				.standard()
				.withCredentials(awsCredentialsProvider)
				.withEndpointConfiguration(endpointConfiguration)
				.build();
	}

	private EndpointConfiguration getEndpointConfiguration(SqsProperties sqsProperties) {
	    boolean isEmbeddedAws = false;
	    EndpointConfiguration endpointConfiguration = null;
	    
	    Regions region = Regions.fromName(sqsProperties.getRegion());

	    for (final String profileName : environment.getActiveProfiles()) {
	      if (profileName.equals(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
	        isEmbeddedAws = true;
	      }
	    }
	    
	    if (isEmbeddedAws) {
	      endpointConfiguration = new EndpointConfiguration(
	    		  LocalstackDockerTestRunner.getLocalstackDocker().getEndpointSQS(), region.getName());
	    } else {
	      endpointConfiguration = new EndpointConfiguration(
	          sqsProperties.getEndpoint(), region.getName());
	    }
	    return endpointConfiguration;
	  }
	
	private AWSCredentialsProvider createAwsCredentialsProvider(
			String localAccessKey, String localSecretKey) {

		AWSCredentialsProvider ec2ContainerCredentialsProvider =
				new EC2ContainerCredentialsProviderWrapper();

		if (StringUtils.isEmpty(localAccessKey) || StringUtils.isEmpty(localSecretKey)) {
			return ec2ContainerCredentialsProvider;
		}

		AWSCredentialsProvider localAwsCredentialsProvider =
				new AWSStaticCredentialsProvider(
						new BasicAWSCredentials(localAccessKey, localSecretKey));

		return new AWSCredentialsProviderChain(
				localAwsCredentialsProvider, ec2ContainerCredentialsProvider);
	}

}
