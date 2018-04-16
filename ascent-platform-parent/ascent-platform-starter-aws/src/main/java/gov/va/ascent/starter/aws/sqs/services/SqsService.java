package gov.va.ascent.starter.aws.sqs.services;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.http.ResponseEntity;

public interface SqsService {
	
	/**
	 * Send a Message
	 * @param message
	 * @return returns a JMS ID
	 */
	public ResponseEntity<String> sendMessage(Message message);
    
	/**
	 * Create a TextMessage
	 * @param message
	 * @return returns a TextMessage 
	 */
	public TextMessage createTextMessage(String message);
}
