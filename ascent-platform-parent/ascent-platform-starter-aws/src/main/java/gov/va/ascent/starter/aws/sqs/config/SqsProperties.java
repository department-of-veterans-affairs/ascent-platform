package gov.va.ascent.starter.aws.sqs.config;

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
	private String dlqendpoint;
	private int dlqRetriesCount;

	@Value("${ascent.aws.access_key_id}")
	private String accessKey;

	@Value("${ascent.aws.secret_access_key}")
	private String secretKey;

	private Integer numberOfMessagesToPrefetch;

	public Optional<Integer> getNumberOfMessagesToPrefetch() {
		return Optional.ofNullable(numberOfMessagesToPrefetch);
	}

	public String getQueueName() {
		return parseQueueName(endpoint);
	}
	
	public String getDLQQueueName() {
		return parseQueueName(dlqendpoint);
	}
	
	private String parseQueueName(String endpoint) {
		URI endpointUri = URI.create(endpoint);
		String path = endpointUri.getPath();
		int pos = path.lastIndexOf('/');
		logger.info("path: {}", path);
		return path.substring(pos + 1);
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

	
	/**
	 * 
	 * @return
	 */
	public String getDlqendpoint() {
		return dlqendpoint;
	}

	/**
	 * 
	 * @param dlqendpoint
	 */
	public void setDlqendpoint(String dlqendpoint) {
		this.dlqendpoint = dlqendpoint;
	}

	/**
	 * 
	 * @return
	 */
	public int getDlqRetriesCount() {
		return dlqRetriesCount;
	}

	/**
	 * 
	 * @param dlqRetriesCount
	 */
	public void setDlqRetriesCount(int dlqRetriesCount) {
		this.dlqRetriesCount = dlqRetriesCount;
	}

}
