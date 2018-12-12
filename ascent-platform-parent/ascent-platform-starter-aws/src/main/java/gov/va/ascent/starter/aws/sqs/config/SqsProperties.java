package gov.va.ascent.starter.aws.sqs.config;

import java.net.URI;
import java.util.Optional;
import org.springframework.boot.context.properties.ConfigurationProperties;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.config.ConfigConstants;

@ConfigurationProperties(prefix = "aws.sqs")
public class SqsProperties {

	private AscentLogger logger = AscentLoggerFactory.getLogger(SqsProperties.class);

	private String region;
	private String queue;
	private String deadletterqueue;
	private int retries;
	private Integer prefetch;
	private Boolean queuetype;
	private Boolean contentbasedduplication;
	private Integer delay;
	private String maxmessagesize;
	private String messageretentionperiod;
    private Integer waittime;
    private Integer visibilitytimeout;
	
	
	private String accessKey = ConfigConstants.AWS_LOCALSTACK_ID; 
	private String secretKey = ConfigConstants.AWS_LOCALSTACK_KEY; 
	
	/**
	 * @return the queuetype
	 */
	public Boolean getQueuetype() {
		return queuetype;
	}

	/**
	 * @param queuetype the queuetype to set
	 */
	public void setQueuetype(Boolean queuetype) {
		this.queuetype = queuetype;
	}

	/**
	 * @return the contentbasedduplication
	 */
	public Boolean getContentbasedduplication() {
		return contentbasedduplication;
	}

	/**
	 * @param contentbasedduplication the contentbasedduplication to set
	 */
	public void setContentbasedduplication(Boolean contentbasedduplication) {
		this.contentbasedduplication = contentbasedduplication;
	}

	/**
	 * @return the delay
	 */
	public Integer getDelay() {
		return delay;
	}

	/**
	 * @param delay the delay to set
	 */
	public void setDelay(Integer delay) {
		this.delay = delay;
	}

	/**
	 * @return the maxmessagesize
	 */
	public String getMaxmessagesize() {
		return maxmessagesize;
	}

	/**
	 * @param maxmessagesize the maxmessagesize to set
	 */
	public void setMaxmessagesize(String maxmessagesize) {
		this.maxmessagesize = maxmessagesize;
	}

	/**
	 * @return the messageretentionperiod
	 */
	public String getMessageretentionperiod() {
		return messageretentionperiod;
	}

	/**
	 * @param messageretentionperiod the messageretentionperiod to set
	 */
	public void setMessageretentionperiod(String messageretentionperiod) {
		this.messageretentionperiod = messageretentionperiod;
	}

	/**
	 * @return the waittime
	 */
	public Integer getWaittime() {
		return waittime;
	}

	/**
	 * @param waittime the waittime to set
	 */
	public void setWaittime(Integer waittime) {
		this.waittime = waittime;
	}

	/**
	 * @return the visibilitytimeout
	 */
	public Integer getVisibilitytimeout() {
		return visibilitytimeout;
	}

	/**
	 * @param visibilitytimeout the visibilitytimeout to set
	 */
	public void setVisibilitytimeout(Integer visibilitytimeout) {
		this.visibilitytimeout = visibilitytimeout;
	}
	
	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public String getDeadletterqueue() {
		return deadletterqueue;
	}

	public void setDeadletterqueue(String deadletterqueue) {
		this.deadletterqueue = deadletterqueue;
	}

	public void setPrefetch(Integer prefetch) {
		this.prefetch = prefetch;
	}


	public Optional<Integer> getPrefetch() {
		return Optional.ofNullable(prefetch);
	}

	public String getQueueName() {
		return parseQueueName(queue);
	}

	public String getDLQQueueName() {
		return parseQueueName(deadletterqueue);
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
		return queue;
	}

	public void setEndpoint(String endpoint) {
		this.queue = endpoint;
	}

	/**
	 *
	 * @return
	 */
	public String getDlqendpoint() {
		return deadletterqueue;
	}

	/**
	 *
	 * @param dlqendpoint
	 */
	public void setDlqendpoint(String dlqendpoint) {
		this.deadletterqueue = dlqendpoint;
	}

	/**
	 *
	 * @return
	 */
	public int getRetries() {
		return retries;
	}

	/**
	 *
	 * @param dlqRetriesCount
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

}
