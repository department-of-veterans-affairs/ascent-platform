package gov.va.ascent.starter.aws.sqs.services;

import org.springframework.http.ResponseEntity;

public interface SqsService {
	/**
     * Sends a message to SQS
     * @param request message
     * @return response entity
     */
	public ResponseEntity<String> sendMessage(String request);
    
}
