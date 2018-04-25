package gov.va.ascent.starter.aws.s3.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.transfer.model.UploadResult;

public interface S3Service {
	/**
     * Retrieves a file from S3
     * @param key key to the file i.e. /{documentid}/{myfile}
     * @param bucket bucket name i.e. bucket-name
     * @return response entity
     * @throws IOException
     */
	public ResponseEntity<byte[]> downloadFile(String bucketName, String keyName) throws IOException;
	/**
     * Upload a file to S3
     * @param keyName 
     * @param uploadFilePath 
     * @return 
     */
	public ResponseEntity<UploadResult> uploadFile(String bucketName, String keyName, String uploadFilePath);
	/**
     * Upload a list of multipart files to S3
     * @param multipartFiles list of multipart files
     * @return ResponseEntity<List of UploadResult> returned from Amazon sdk
     */
	public ResponseEntity<List<UploadResult>> uploadMultiPart(String bucketName, MultipartFile[] multipartFiles);
	/**
     * Upload a single multipart file to S3
     * @param multipartFile multipart file
     * @return ResponseEntity<UploadResult> returned from Amazon sdk
     */
    public ResponseEntity<UploadResult> uploadMultiPartSingle(String bucketName, MultipartFile multipartFile, Map<String, String> propertyMap);
    
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
	 * Upload a byte array to S3
	 * @param byteData
	 * @param fileName
	 * @param propertyMap
	 * @return
	 */
    public ResponseEntity<UploadResult> uploadByteArray(String bucketName, byte[] byteData, String fileName, Map<String, String> propertyMap);
}
