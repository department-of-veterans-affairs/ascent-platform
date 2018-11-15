package gov.va.ascent.starter.aws.s3.dto;

import gov.va.ascent.framework.service.ServiceResponse;

public class DownloadFileResponse extends ServiceResponse {

	private static final long serialVersionUID = -2531179599395037607L;

	private byte[] fileByteArray;

	private String contentType;

	private String fileName;

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
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
	 * @return the fileByteArray
	 */
	public byte[] getFileByteArray() {
		return fileByteArray;
	}

	/**
	 * @param fileByteArray the fileByteArray to set
	 */
	public void setFileByteArray(byte[] fileByteArray) {
		this.fileByteArray = fileByteArray;
	}

}
