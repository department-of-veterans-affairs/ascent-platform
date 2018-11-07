package gov.va.ascent.starter.aws.s3.dto;

import gov.va.ascent.framework.service.ServiceResponse;

public class UploadResultResponse extends ServiceResponse {

	private static final long serialVersionUID = -2531179599395037607L;
	
	private String bucketName;
    private String	eTag;
    private String	key;
    private String	versionId;
	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return bucketName;
	}
	/**
	 * @param bucketName the bucketName to set
	 */
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	/**
	 * @return the eTag
	 */
	public String geteTag() {
		return eTag;
	}
	/**
	 * @param eTag the eTag to set
	 */
	public void seteTag(String eTag) {
		this.eTag = eTag;
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
	 * @return the versionId
	 */
	public String getVersionId() {
		return versionId;
	}
	/**
	 * @param versionId the versionId to set
	 */
	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

    


}
