package gov.va.ascent.starter.aws.s3.services;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import gov.va.ascent.starter.aws.s3.dto.CopyFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DeleteFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DownloadFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DownloadFileResponse;
import gov.va.ascent.starter.aws.s3.dto.MoveMessageRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;

public interface S3Service {

	/**
	 * Upload a byte array to S3
	 * 
	 * @param byteData
	 * @param fileName
	 * @param propertyMap
	 * @return
	 */
	public UploadResultResponse uploadByteArray(UploadResultRequest uploadResultRequest);

	/**
	 * Copy a file from one bucket to another bucket.
	 * 
	 * @param key
	 */
	public void copyFileFromSourceToTargetBucket(CopyFileRequest copyFileRequest);

	/**
	 * Copy the DLQ Message to S3 DLQ Bucket.
	 * 
	 * @param key
	 * @param message
	 */
	public void moveMessageToS3(MoveMessageRequest moveResultRequest);

	/**
	 * Retrieves a file from S3
	 * 
	 * @param key key to the file i.e. /{documentid}/{myfile}
	 * @param bucket bucket name i.e. bucket-name
	 * @return response entity
	 * @throws IOException
	 */
	public DownloadFileResponse downloadFile(DownloadFileRequest downloadResultRequest) throws IOException;

	/**
	 * Deletes a file from S3
	 * 
	 * @param deleteFileRequest
	 * @return void
	 * @throws IOException
	 */
	public void deleteFile(DeleteFileRequest deleteFileRequest);
	
	/**
	 * 
	 * @param bucketName
	 * @param multipartFile
	 * @param propertyMap
	 * @return
	 */
	public ResponseEntity<UploadResultResponse> uploadMultiPartFile(final String bucketName, final MultipartFile multipartFile,
			final Map<String, String> propertyMap);
	
}
