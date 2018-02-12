package gov.va.ascent.starter.aws.autoconfigure.sqs.services;

import org.springframework.http.ResponseEntity;

public interface SQSServices {
	/**
     * Send a message to SQS
     * @param request String message
     * @return ResponseEntity<String> JMS Message ID
     */
    public ResponseEntity<String> sendMessage(final String request);
    public void startJmsConnection();
}
