package gov.va.ascent.starter.aws.sqs.services;

import javax.jms.TextMessage;

import org.springframework.http.ResponseEntity;

public interface SqsService {
	/**
     * Sends a message to SQS
     * @param request message
     * @return response entity
     */
	public ResponseEntity<String> sendMessage(String request);

	/**
	 * Send a TextMessage
	 * @param message
	 * @return
	 */
	public ResponseEntity<String> sendMessage(TextMessage message);
    
	/**
	 * Create a TextMessage
	 * @param message
	 * @return
	 */
	public TextMessage createTextMessage(String message);
}
