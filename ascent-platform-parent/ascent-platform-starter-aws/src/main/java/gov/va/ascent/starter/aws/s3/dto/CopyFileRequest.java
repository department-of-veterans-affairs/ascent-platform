package gov.va.ascent.starter.aws.s3.dto;

/**
 * @author srikanthvanapalli
 */
public class CopyFileRequest {

	private String sourceBucketName;

	private String targetBucketName;

	private String key;

	/**
	 * @return the sourceBucketName
	 */
	public String getSourceBucketName() {
		return sourceBucketName;
	}

	/**
	 * @param sourceBucketName the sourceBucketName to set
	 */
	public void setSourceBucketName(String sourceBucketName) {
		this.sourceBucketName = sourceBucketName;
	}

	/**
	 * @return the targetBucketName
	 */
	public String getTargetBucketName() {
		return targetBucketName;
	}

	/**
	 * @param targetBucketName the targetBucketName to set
	 */
	public void setTargetBucketName(String targetBucketName) {
		this.targetBucketName = targetBucketName;
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

}
