package gov.va.ascent.starter.aws.s3.dto;

/** 
 * @author srikanthvanapalli
 */
public class MoveMessageRequest {
	
	private String dlqBucketName;
	
	private String key; 
	
	private String message;

	/**
	 * @return the dlqBucketName
	 */
	public String getDlqBucketName() {
		return dlqBucketName;
	}

	/**
	 * @param dlqBucketName the dlqBucketName to set
	 */
	public void setDlqBucketName(String dlqBucketName) {
		this.dlqBucketName = dlqBucketName;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
