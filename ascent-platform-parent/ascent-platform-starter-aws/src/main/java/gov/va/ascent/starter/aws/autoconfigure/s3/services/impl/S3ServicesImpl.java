package gov.va.ascent.starter.aws.autoconfigure.s3.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import gov.va.ascent.starter.aws.autoconfigure.s3.services.S3Services;


@Service
public class S3ServicesImpl implements S3Services {
	
	private Logger logger = LoggerFactory.getLogger(S3ServicesImpl.class);
	
	@Autowired
	private AmazonS3 s3client;
	
	@Autowired
	protected TransferManager transferManager;
	
	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${ascent.s3.bucket}")
	private String bucketName;

	/**
     * Retrieves a file from S3
     * @param key key to the file i.e. /myfolder/myfile
     * @param bucket bucket name i.e. bucket-name
     * @return response entity
     * @throws IOException
     */
	@Override
	public ResponseEntity<byte[]> downloadFile(String keyName) throws IOException {
		
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName);
        S3Object s3Object = s3client.getObject(getObjectRequest);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        String fileName = URLEncoder.encode(keyName, "UTF-8").replaceAll("\\+", "%20");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}
	
	/**
     * Upload a list of multipart files to S3
     * @param multipartFiles list of multipart files
     * @return list of PutObjectResults returned from Amazon sdk
     */
	@Override
	public ResponseEntity<List<UploadResult>> uploadMultiPart(MultipartFile[] multipartFiles) {
		List<UploadResult> putObjectResults = new ArrayList<>();

		Arrays.stream(multipartFiles)
				.filter(multipartFile -> !StringUtils.isEmpty(multipartFile.getOriginalFilename()))
				.forEach(multipartFile -> {
					try {
						putObjectResults.add(upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});

		return new ResponseEntity<>(putObjectResults, HttpStatus.OK);
	}

	/**
     * Upload a single multipart file to S3
     * @param multipartFile multipart file
     * @return PutObjectResult returned from Amazon sdk
     */
	@Override
	public ResponseEntity<UploadResult> uploadMultiPartSingle(MultipartFile multipartFile) {
		UploadResult putObjectResult = new UploadResult();

        try {
            putObjectResult = upload(multipartFile.getOriginalFilename(), multipartFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

		return new ResponseEntity<>(putObjectResult, HttpStatus.OK);
	}
	
	/**
     * Upload stream to S3
     * @param uploadKey
     * @param inputStream 
     * @return PutObjectResult returned from Amazon sdk
     */
	private UploadResult upload(String uploadKey, InputStream inputStream) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadKey, inputStream, new ObjectMetadata());

		putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
		
		Upload upload = transferManager.upload(putObjectRequest);
		
		UploadResult uploadResult = new UploadResult();
		try {
			uploadResult = upload.waitForUploadResult();
			logger.info("Upload completed, bucket={}, key={}", uploadResult.getBucketName(), uploadResult.getKey());
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			logger.info("Caught an AmazonClientException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ace.getMessage());
		} catch (InterruptedException ie) {
			logger.info("Caught an InterruptedException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ie.getMessage());
		}
		finally {
			IOUtils.closeQuietly(inputStream);
		}
		//PutObjectResult putObjectResult = s3client.putObject(putObjectRequest);
		
		return uploadResult;
	}
	
	/**
     * Upload a file to S3
     * @param keyName 
     * @param uploadFilePath 
	 * @return 
     * @return 
     */
	@Override
	public ResponseEntity<UploadResult> uploadFile(String keyName, String uploadFilePath) {
		UploadResult putObjectResult = new UploadResult();
		
		try {
			putObjectResult = upload(keyName, resourceLoader.getResource(uploadFilePath).getInputStream());
	        logger.info("===================== Upload File - Done! =====================");
	        
		} catch (AmazonServiceException ase) {
			logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.info("Error Message:    " + ase.getMessage());
			logger.info("HTTP Status Code: " + ase.getStatusCode());
			logger.info("AWS Error Code:   " + ase.getErrorCode());
			logger.info("Error Type:       " + ase.getErrorType());
			logger.info("Request ID:       " + ase.getRequestId());
        } catch (IOException ioe) {
        	 logger.info("Caught an IOException: ");
             logger.info("Error Message: " + ioe.getMessage());
		} catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
		return new ResponseEntity<>(putObjectResult, HttpStatus.OK);
	}

}
