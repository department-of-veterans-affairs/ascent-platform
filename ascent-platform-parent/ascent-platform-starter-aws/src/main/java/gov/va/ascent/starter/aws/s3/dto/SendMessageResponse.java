package gov.va.ascent.starter.aws.s3.dto;

import gov.va.ascent.framework.service.ServiceResponse;

public class SendMessageResponse extends ServiceResponse {

	private static final long serialVersionUID = -2531179599395037607L;
	
    private String statusCode;
    
    private String messageId;

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
    
    
    
}
