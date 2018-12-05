package gov.va.ascent.starter.aws.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.QueueAttributeName;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.LocalstackDocker;
import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.server.AscentAwsLocalstackProperties.Services;
import gov.va.ascent.starter.aws.sqs.config.SqsProperties;

/**
 * This class will start and stop AWS localstack services, to be used for local envs. The profile embedded-aws needs to be added in
 * order for this bean to be created The class is renamed to end with Application so that it could be disabled for test coverage
 * violation.
 * 
 * @author akulkarnis
 */
@Configuration
@Profile(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)
@EnableConfigurationProperties({ AscentAwsLocalstackProperties.class, SqsProperties.class })
public class AscentEmbeddedAwsLocalstackApplication {

	/** The Constant LOGGER. */
	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(AscentEmbeddedAwsLocalstackApplication.class);

	private static LocalstackDocker localstackDocker = LocalstackDocker.getLocalstackDocker();

	private static String externalHostName = "localhost";
	private static boolean pullNewImage = true;
	private static boolean randomizePorts = false;
	private static Map<String, String> environmentVariables = new HashMap<>();

	/** 
	 * Localstack Properties Bean
	 */
	@Autowired
	private AscentAwsLocalstackProperties ascentAwsLocalstackProperties;

	@Autowired
	private SqsProperties sqsProperties;

	@Value("${ascent.s3.bucket:sourcebucket}")
	private String sourcebucket;

	@Value("${ascent.s3.target.bucket:targetbucket}")
	private String targetbucket;

	public LocalstackDocker getLocalstackDocker() {
		return localstackDocker;
	}

	/**
	 * Start embedded AWS servers on context load
	 *
	 * @throws IOException
	 */
	@PostConstruct
	public void startAwsLocalStack() {
		if ((localstackDocker != null) && (localstackDocker.getLocalStackContainer() != null)) {
			LOGGER.info("AWS localstack already running, not trying to re-start: {} ", localstackDocker.getLocalStackContainer());
			return;
		} else if (localstackDocker != null) {
			// clean the localstack
			cleanAwsLocalStack();
			localstackDocker.setExternalHostName(externalHostName);
			localstackDocker.setPullNewImage(pullNewImage);
			localstackDocker.setRandomizePorts(randomizePorts);

			List<Services> listServices = ascentAwsLocalstackProperties.getServices();

			if (!CollectionUtils.isEmpty(listServices)) {
				LOGGER.info("Services List: {}", ReflectionToStringBuilder.toString(listServices));
				StringBuilder builder = new StringBuilder();
				for (Services service : listServices) {
					builder.append(service.getName());
					builder.append(":");
					builder.append(service.getPort());
					builder.append(",");
				}
				// Remove last delimiter with setLength.
				builder.setLength(builder.length() - 1);
				String services = String.join(",", builder.toString());
				if (StringUtils.isNotEmpty(services)) {
					LOGGER.info("Services to be started: {}", services);
					environmentVariables.put("SERVICES", services);
				}
				localstackDocker.setEnvironmentVariables(environmentVariables);
				localstackDocker.setRandomizePorts(false);
			}
			// create and start S3, SQS API mock
			LOGGER.info("starting localstack: {} ", ReflectionToStringBuilder.toString(localstackDocker));
			localstackDocker.startup();

			createBuckets();
			createQueues();
		}
	}

	private void createBuckets() {
		AmazonS3 amazonS3Client = DockerTestUtils.getClientS3();

		amazonS3Client.createBucket(sourcebucket);
		amazonS3Client.createBucket(targetbucket);
	}

	private void createQueues() {
		AmazonSQS client = DockerTestUtils.getClientSQS();

		String deadletterQueueUrl = client.createQueue(sqsProperties.getDLQQueueName()).getQueueUrl();

		GetQueueAttributesRequest getAttributesRequest =
				new GetQueueAttributesRequest(deadletterQueueUrl).withAttributeNames(QueueAttributeName.QueueArn);
		GetQueueAttributesResult queueAttributesResult = client.getQueueAttributes(getAttributesRequest);

		String redrivePolicy = "{\"maxReceiveCount\":\"1\", \"deadLetterTargetArn\":\""
				+ queueAttributesResult.getAttributes().get(QueueAttributeName.QueueArn.name()) + "\"}";

		Map<String, String> attributeMap = new HashMap<>();
		attributeMap.put("DelaySeconds", "0");
		attributeMap.put("MaximumMessageSize", "262144");
		attributeMap.put("MessageRetentionPeriod", "1209600");
		attributeMap.put("ReceiveMessageWaitTimeSeconds", "20");
		attributeMap.put("VisibilityTimeout", "30");
		attributeMap.put(QueueAttributeName.RedrivePolicy.name(), redrivePolicy);
		client.createQueue(new CreateQueueRequest(sqsProperties.getQueueName()).withAttributes(attributeMap));
	}

	/**
	 * stop embedded AWS servers on context destroy
	 */
	@PreDestroy
	public void stopAwsLocalStack() {
		// stop the localstack
		if ((localstackDocker != null) && (localstackDocker.getLocalStackContainer() != null)) {
			LOGGER.info("stopping localstack: {} ", localstackDocker.getLocalStackContainer());
			localstackDocker.stop();
			LOGGER.info("stopped localstack");
		}
		// clean the localstack
		cleanAwsLocalStack();
	}

	/**
	 * clean AWS Localstack containers
	 */
	private void cleanAwsLocalStack() {
		// clean up docker containers
		DockerExe newDockerExe = new DockerExe();
		String listContainerIds =
				newDockerExe.execute(Arrays.asList("ps", "--no-trunc", "-aq", "--filter", "ancestor=localstack/localstack"));
		LOGGER.info("containers to be cleaned: {} ", listContainerIds);
		if (StringUtils.isNotEmpty(listContainerIds)) {
			try {
				String[] splitArray = listContainerIds.split("\\s+");
				for (String containerId : splitArray) {
					String output = newDockerExe.execute(Arrays.asList("rm", "-f", containerId));
					LOGGER.info("docker remove command output: {} ", output);
				}
			} catch (PatternSyntaxException ex) {
				LOGGER.warn("PatternSyntaxException During Splitting: {}", ex);
			}
		}
	}
}
