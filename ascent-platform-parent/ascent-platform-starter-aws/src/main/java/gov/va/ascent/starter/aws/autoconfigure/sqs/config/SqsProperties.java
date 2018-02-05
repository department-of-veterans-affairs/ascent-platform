package gov.va.ascent.starter.aws.autoconfigure.sqs.config;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;



@ConfigurationProperties(prefix = "ascent.sqs")
public class SqsProperties {
	
	private Logger logger = LoggerFactory.getLogger(SqsProperties.class);

	private String region;
	private String endpoint;
	
	@Value("${ascent.aws.access_key_id}")
	private String accessKey;

	@Value("${ascent.aws.secret_access_key}")
	private String secretKey;

	private Integer numberOfMessagesToPrefetch;

	private Extended extended = new Extended();

	public Optional<Integer> getNumberOfMessagesToPrefetch() {
		return Optional.ofNullable(numberOfMessagesToPrefetch);
	}

	public String getQueueName() {
		URI endpointUri = URI.create(endpoint);
		String path = endpointUri.getPath();
		int pos = path.lastIndexOf('/');
		logger.info("path: {}", path);
		return path.substring(pos + 1);
	}

	/**
	 * @return the extended
	 */
	public Extended getExtended() {
		return extended;
	}

	/**
	 * @param extended the extended to set
	 */
	public void setExtended(Extended extended) {
		this.extended = extended;
	}

	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}

	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		logger.info("secretKey: {}", secretKey);
		this.secretKey = secretKey;
	}

	/**
	 * @return the accessKey
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * @param accessKey the accessKey to set
	 */
	public void setAccessKey(String accessKey) {
		logger.info("accessKey: {}", accessKey);
		this.accessKey = accessKey;
	}

	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}

	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}

	/**
	 * @return
	 */
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public static class Extended {

		private String s3Region;
		private String s3BucketName;

		/**
		 * @return the s3Region
		 */
		public String getS3Region() {
			return s3Region;
		}
		/**
		 * @param s3Region the s3Region to set
		 */
		public void setS3Region(String s3Region) {
			this.s3Region = s3Region;
		}
		/**
		 * @return the s3BucketName
		 */
		public String getS3BucketName() {
			return s3BucketName;
		}
		/**
		 * @param s3BucketName the s3BucketName to set
		 */
		public void setS3BucketName(String s3BucketName) {
			this.s3BucketName = s3BucketName;
		}

	}

}
