package gov.va.ascent.starter.aws.sqs.services;

import javax.jms.Message;
import javax.jms.TextMessage;
import gov.va.ascent.starter.aws.s3.dto.SendMessageResponse;

public interface SqsService {
	
	/**
	 * Send a Message
	 * @param message
	 * @return returns a JMS ID
	 */
	public SendMessageResponse sendMessage(Message message);
    
	/**
	 * Create a TextMessage
	 * @param message
	 * @return returns a TextMessage 
	 */
	public TextMessage createTextMessage(String message);
}
