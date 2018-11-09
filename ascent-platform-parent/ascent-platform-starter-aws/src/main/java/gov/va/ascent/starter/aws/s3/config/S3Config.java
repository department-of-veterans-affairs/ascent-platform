package gov.va.ascent.starter.aws.s3.config;

import static com.amazonaws.services.s3.internal.Constants.MB;

import java.util.concurrent.Executors;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;

/**
 * Configuration for amazon S3 service access.
 */
@Configuration
public class S3Config {
	private final AscentLogger logger = AscentLoggerFactory.getLogger(S3Config.class);

	@Value("${ascent.aws.access_key_id}")
	private String awsId;

	@Value("${ascent.aws.secret_access_key}")
	private String awsKey;

	@Value("${ascent.s3.region}")
	private String region;

	@Value("${ascent.aws.localstack-config.s3.endpoint}")
	private String endpoint;

	@Autowired
	Environment environment;

	/**
	 * Creates a client object for accessing S3 service.
	 * <p>
	 * Side note, this does not need to be a spring bean, and should be accessed from the transferManager.
	 */
	protected AmazonS3 s3client() {
		// get the localstack implementation if running under a "embedded aws" profile
		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				final AmazonS3ClientBuilder s3ClientBuider = AmazonS3ClientBuilder.standard()
						.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)));
				s3ClientBuider.setEndpointConfiguration(new EndpointConfiguration(endpoint, region));
				s3ClientBuider.setPathStyleAccessEnabled(true);
				return s3ClientBuider.build();
			}
		}

		// otherwise, get a real client
		final BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	}

	/**
	 * Create a S3 transfer manager for handling transfers between a client and the service.
	 * Get the s3client from this object.
	 * <p>
	 * For use only in service IMPL classes and their delegates.
	 *
	 * @return TransferManager
	 */
	@Bean
	public TransferManager transferManager() {

		final TransferManager tm = TransferManagerBuilder.standard()
				.withS3Client(s3client())
				.withDisableParallelDownloads(false)
				.withMinimumUploadPartSize(Long.valueOf(5 * Long.valueOf(MB)))
				.withMultipartUploadThreshold(Long.valueOf(5 * Long.valueOf(MB)))
				.withMultipartCopyPartSize(Long.valueOf(5 * Long.valueOf(MB)))
				.withMultipartCopyThreshold(Long.valueOf(100 * Long.valueOf(MB)))
				.withExecutorFactory(() -> Executors.newFixedThreadPool(20))
				.build();
		logger.debug("TransferManager {}", ReflectionToStringBuilder.toString(tm));
		return tm;
	}
}
