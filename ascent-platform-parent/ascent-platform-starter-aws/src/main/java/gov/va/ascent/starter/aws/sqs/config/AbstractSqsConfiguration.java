package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.util.StringUtils;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@EnableJms
public abstract class AbstractSqsConfiguration {

	@Bean
	public abstract ConnectionFactory connectionFactory(SqsProperties sqsProperties);

	@Bean
	public DestinationResolver destinationResolver(SqsProperties sqsProperties) {
		return new StaticDestinationResolver(sqsProperties.getQueueName());
	}

	@Bean
	public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
			ConnectionFactory connectionFactory, DestinationResolver destinationResolver) {

		DefaultJmsListenerContainerFactory jmsListenerContainerFactory =
				new DefaultJmsListenerContainerFactory();
		jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
		jmsListenerContainerFactory.setDestinationResolver(destinationResolver);
		jmsListenerContainerFactory.setSessionTransacted(false);
		jmsListenerContainerFactory.setSessionAcknowledgeMode(SQSSession.UNORDERED_ACKNOWLEDGE);

		return jmsListenerContainerFactory;
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

	protected SQSConnectionFactory createExtendedSQSConnectionFactory(SqsProperties sqsProperties) {
		AmazonS3 s3Client = createAmazonS3Client(sqsProperties);

		// Set the SQS extended client configuration with large payload support enabled.
		ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
				.withLargePayloadSupportEnabled(s3Client, sqsProperties.getExtended().getS3BucketName());

		ProviderConfiguration providerConfiguration = new ProviderConfiguration();
		sqsProperties.getNumberOfMessagesToPrefetch()
		.ifPresent(providerConfiguration::setNumberOfMessagesToPrefetch);

		AmazonSQS sqsClient = createAmazonSQSClient(sqsProperties);

		return new SQSConnectionFactory(
				providerConfiguration,
				new AmazonSQSExtendedClient(sqsClient, extendedClientConfig)
				);
	}

	private AmazonS3 createAmazonS3Client(SqsProperties sqsProperties) {
		AWSCredentialsProvider awsCredentialsProvider = createAwsCredentialsProvider(
				sqsProperties.getAccessKey(),
				sqsProperties.getSecretKey()
				);

		Regions region = Regions.fromName(sqsProperties.getRegion());

		AmazonS3 amazonS3Client = AmazonS3ClientBuilder
				.standard()
				.withCredentials(awsCredentialsProvider)
				.withRegion(region)
				.build();

		String s3BucketName = sqsProperties.getExtended().getS3BucketName();

		if (!amazonS3Client.doesBucketExist(s3BucketName)) {
			amazonS3Client.createBucket(s3BucketName);

			// Set the Amazon S3 bucket name, and set a lifecycle rule on the bucket to
			// permanently delete objects a certain number of days after each object's creation date.
			// Next, create the bucket, and enable message objects to be stored in the bucket.
			BucketLifecycleConfiguration.Rule expirationRule =
					new BucketLifecycleConfiguration.Rule()
					.withExpirationInDays(14).withStatus("Enabled");

			BucketLifecycleConfiguration lifecycleConfig =
					new BucketLifecycleConfiguration().withRules(expirationRule);

			amazonS3Client.setBucketLifecycleConfiguration(s3BucketName, lifecycleConfig);
		}

		return amazonS3Client;
	}

	private AmazonSQS createAmazonSQSClient(SqsProperties sqsProperties) {
		Regions region = Regions.fromName(sqsProperties.getRegion());

		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(
				sqsProperties.getEndpoint(), region.getName());

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
