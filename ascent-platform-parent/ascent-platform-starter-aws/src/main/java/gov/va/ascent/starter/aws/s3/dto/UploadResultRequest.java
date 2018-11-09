package gov.va.ascent.starter.aws.s3.dto;

import java.util.Map;

/**
 * ;poulp;u./l
 * 
 * @author srikanthvanapalli
 *
 */
public class UploadResultRequest {

	private String bucketName;
	private byte[] byteData;
	private String fileName;
	private Map<String, String> propertyMap;

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
	 * @return the byteData
	 */
	public byte[] getByteData() {
		return byteData;
	}

	/**
	 * @param byteData the byteData to set
	 */
	public void setByteData(byte[] byteData) {
		this.byteData = byteData;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the propertyMap
	 */
	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	/**
	 * @param propertyMap the propertyMap to set
	 */
	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

}
