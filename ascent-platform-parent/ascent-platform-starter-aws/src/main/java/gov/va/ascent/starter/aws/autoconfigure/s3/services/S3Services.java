package gov.va.ascent.starter.aws.autoconfigure.s3.services;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.transfer.model.UploadResult;

public interface S3Services {
	/**
     * Retrieves a file from S3
     * @param key key to the file i.e. /{documentid}/{myfile}
     * @param bucket bucket name i.e. bucket-name
     * @return response entity
     * @throws IOException
     */
	public ResponseEntity<byte[]> downloadFile(String keyName) throws IOException;
	/**
     * Upload a file to S3
     * @param keyName 
     * @param uploadFilePath 
     * @return 
     */
	public ResponseEntity<UploadResult> uploadFile(String keyName, String uploadFilePath);
	/**
     * Upload a list of multipart files to S3
     * @param multipartFiles list of multipart files
     * @return ResponseEntity<List of UploadResult> returned from Amazon sdk
     */
	public ResponseEntity<List<UploadResult>> uploadMultiPart(MultipartFile[] multipartFiles);
	/**
     * Upload a single multipart file to S3
     * @param multipartFile multipart file
     * @return ResponseEntity<UploadResult> returned from Amazon sdk
     */
    public ResponseEntity<UploadResult> uploadMultiPartSingle(MultipartFile multipartFile);
}
