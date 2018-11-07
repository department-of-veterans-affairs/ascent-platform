package gov.va.ascent.starter.aws.s3.services;

import java.io.IOException;
import org.springframework.http.ResponseEntity;
import gov.va.ascent.starter.aws.s3.dto.UploadResultRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;

public interface S3Service {

	/**
	 * Upload a byte array to S3
	 * @param byteData
	 * @param fileName
	 * @param propertyMap
	 * @return
	 */
	public UploadResultResponse uploadByteArray(UploadResultRequest uploadResultRequest);

	/**
	 * Copy a file from one bucket to another bucket.
	 * @param key
	 */
	public void copyFileFromSourceToTargetBucket(String sourceBucketName, String targetBucketName, String key);

	/**
	 * Copy the DLQ Message to S3 DLQ Bucket.
	 * @param key
	 * @param message
	 */
	public void moveMessageToS3(String dlqBucketName, String key, String message);

	/**
	 * Retrieves a file from S3
	 * @param key key to the file i.e. /{documentid}/{myfile}
	 * @param bucket bucket name i.e. bucket-name
	 * @return response entity
	 * @throws IOException
	 */
	public ResponseEntity<byte[]> downloadFile(String bucketName, String keyName) throws IOException;
}
