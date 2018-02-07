package gov.va.ascent.starter.aws.autoconfigure.sqs.services;

import org.springframework.http.ResponseEntity;

public interface SQSServices {
	/**
     * Send a message to SQS
     * @param multipartFile multipart file
     * @return ResponseEntity<UploadResult> returned from Amazon sdk
     */
    public ResponseEntity<String> sendMessage(final String request);
}
