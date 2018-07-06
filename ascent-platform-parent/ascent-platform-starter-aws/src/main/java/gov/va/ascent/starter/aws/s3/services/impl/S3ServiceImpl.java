package gov.va.ascent.starter.aws.s3.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import gov.va.ascent.framework.log.LogUtil;
import gov.va.ascent.framework.util.Defense;
import gov.va.ascent.starter.aws.s3.services.S3Service;


@Service
public class S3ServiceImpl implements S3Service {

	private Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

	public static final String ERROR_MESSAGE = "Error Message: {}"; 
	public static final String ERROR = "Error: {}"; 
	public static final String UPLOAD_RESULT = "UploadResult: {}";
	public static final String BUCKET_NAME_NOTNULL_MESSAGE = "Bucket Name can't be null";
	public static final String KEY_NOTNULL_MESSAGE = "Key of the object can't be null";
	public static final String MULTIPART_NOTNULL_MESSAGE = "Multipart Request can't be null";
	public static final String UPLOAD_FAILED = "Upload Failed";
	public static final String COPY_FAILED = "Copy Failed";

	@Autowired
	private AmazonS3 s3client;

	@Autowired
	protected TransferManager transferManager;

	@Autowired
	private ResourceLoader resourceLoader;

	/**
	 * Upload a byte array to S3
	 * @param byteData
	 * @param fileName
	 * @param propertyMap
	 * @return PutObjectResult returned from Amazon sdk
	 */
	@Override
	public ResponseEntity<UploadResult> uploadByteArray(String bucketName, byte[] byteData, String fileName,
			Map<String, String> propertyMap) {

		Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(byteData, "byte[] can't be null");
		Defense.notNull(fileName, "File Name can't be null");

		UploadResult putObjectResult = upload(bucketName, fileName, new ByteArrayInputStream(byteData), propertyMap);

		if (logger.isDebugEnabled()) {
			logger.debug(UPLOAD_RESULT, ReflectionToStringBuilder.toString(putObjectResult));
		}

		return new ResponseEntity<>(putObjectResult, HttpStatus.OK);
	}

	/**
	 * Upload a single multipart file to S3
	 * @param multipartFile multipart file
	 * @return PutObjectResult returned from Amazon sdk
	 */
	@Override
	public ResponseEntity<UploadResult> uploadMultiPartFile(String bucketName, MultipartFile multipartFile, Map<String, String> propertyMap) {
		Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(multipartFile, MULTIPART_NOTNULL_MESSAGE);

		UploadResult putObjectResult = null;

		try {
			putObjectResult = upload(bucketName, multipartFile.getOriginalFilename(), multipartFile.getInputStream(), propertyMap);
		} catch (IOException e) {
			logger.error(ERROR_MESSAGE, e);
		}

		if (logger.isDebugEnabled()) {
			logger.debug(UPLOAD_RESULT, putObjectResult == null ? "" : ReflectionToStringBuilder.toString(putObjectResult));
		}

		return new ResponseEntity<>(putObjectResult, HttpStatus.OK);
	}


	/**
	 * Upload a list of multipart files to S3
	 * @param multipartFiles list of multipart files
	 * @return list of PutObjectResults returned from Amazon sdk
	 */
	@Override
	public ResponseEntity<List<UploadResult>> uploadMultiPartFiles(String bucketName, MultipartFile[] multipartFiles) { 

		Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(multipartFiles, MULTIPART_NOTNULL_MESSAGE);

		List<UploadResult> putObjectResults = new ArrayList<>();

		Arrays.stream(multipartFiles).filter(multipartFile -> !StringUtils.isEmpty(multipartFile.getOriginalFilename()))
		.forEach(multipartFile -> {
			try {
				// Passing null temporarily. When this method is used, null needs to be replaced
				// with document metadata.
				putObjectResults
				.add(upload(bucketName, multipartFile.getOriginalFilename(), multipartFile.getInputStream(), null));
			} catch (IOException e) {
				logger.error(ERROR_MESSAGE, e);
			}
		});

		if (logger.isDebugEnabled()) {
			logger.debug(UPLOAD_RESULT, ReflectionToStringBuilder.toString(putObjectResults));
		}

		return new ResponseEntity<>(putObjectResults, HttpStatus.OK);
	}

	/**
	 * Upload a file to S3
	 * @param keyName 
	 * @param uploadFilePath 
	 * @return 
	 * @return 
	 */
	@Override
	public ResponseEntity<UploadResult> uploadFile(String bucketName, String keyName, String uploadFilePath) {
		Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(keyName, KEY_NOTNULL_MESSAGE);
		Defense.notNull(uploadFilePath, "Upload File Path can't be null");

		UploadResult putObjectResult = new UploadResult();

		Map<String, String> propertyMap = new HashMap<>();

		try {
			putObjectResult = upload(bucketName, keyName, resourceLoader.getResource(uploadFilePath).getInputStream(), propertyMap);

			if (logger.isDebugEnabled()) {
				logger.debug("===================== Upload File - Done! =====================");
				logger.debug("UploadResult:    {}", putObjectResult);
			}

		} catch (IOException ioe) {
			logger.error("Caught an IOException: ");
			logger.error(ERROR_MESSAGE, ioe);
		}
		return new ResponseEntity<>(putObjectResult, HttpStatus.OK);
	}


	/**
	 * Copy a file from one bucket to another bucket.
	 * @param key
	 */
	@Override
	public void copyFileFromSourceToTargetBucket(String sourceBucketName, String targetBucketName, String key) {
		Defense.notNull(sourceBucketName, "Source Bucket Name can't be null");
		Defense.notNull(targetBucketName, "Target Bucket Name can't be null");
		Defense.notNull(key, KEY_NOTNULL_MESSAGE);

		try {
			// Copying object
			CopyObjectRequest copyObjRequest = new CopyObjectRequest(
					sourceBucketName, key, targetBucketName, key);
			logger.info("Copying object. {}", ReflectionToStringBuilder.toString(copyObjRequest));
			s3client.copyObject(copyObjRequest);
			// Deleting object from original source bucket
			s3client.deleteObject(sourceBucketName, key);
			logger.info("Deleting object. Bucket Name: {} Key : {}", sourceBucketName, key);
		} catch (AmazonServiceException ase) {
			LogUtil.logErrorWithBanner(logger, COPY_FAILED, ase.getMessage());
			logger.error(ERROR, ase);
			logger.error("Caught an AmazonServiceException, " + "which means your request made it "
					+ "to Amazon S3, but was rejected with an error " + "response for some reason.");
			logger.error("Error Message:    {}", ase.getMessage());
			logger.error("HTTP Status Code: {}", ase.getStatusCode());
			logger.error("AWS Error Code:   {}", ase.getErrorCode());
			logger.error("Error Type:       {}", ase.getErrorType());
			logger.error("Request ID:       {}", ase.getRequestId());
		} catch (AmazonClientException ace) {
			LogUtil.logErrorWithBanner(logger, COPY_FAILED, ace.getMessage());
			logger.error(ERROR, ace);
			logger.error("Caught an AmazonClientException, " + "which means the client encountered "
					+ "an internal error while trying to " + " communicate with S3, "
					+ "such as not being able to access the network.");
			logger.error(ERROR_MESSAGE, ace.getMessage());
		}
	}

	/**
	 * Copy the DLQ Message to S3 DLQ Bucket.
	 */
	@Override
	public void moveMessageToS3(String dlqBucketName, String key, String message) {
		Defense.notNull(dlqBucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(key, KEY_NOTNULL_MESSAGE);
		Defense.notNull(message, "Message Content can't be null");

		logger.debug("Moving Message to S3. DLQ Bucket Name: {} Key: {}", dlqBucketName, key);
		s3client.putObject(dlqBucketName, key, message);
	}
	
	/**
	 * Retrieves a file from S3
	 * @param key key to the file i.e. /myfolder/myfile
	 * @param bucket bucket name i.e. bucket-name
	 * @return response entity
	 * @throws IOException
	 */
	@Override
	public ResponseEntity<byte[]> downloadFile(String bucketName, String keyName) throws IOException {

		Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(keyName, KEY_NOTNULL_MESSAGE);

		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName);
		S3Object s3Object = s3client.getObject(getObjectRequest);
		S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

		byte[] bytes = IOUtils.toByteArray(objectInputStream);
		String fileName = URLEncoder.encode(keyName, "UTF-8").replaceAll("\\+", "%20");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentLength(bytes.length);
		httpHeaders.setContentDispositionFormData("attachment", fileName);

		if (logger.isDebugEnabled()) {
			logger.debug("GetObjectRequest: {}", ReflectionToStringBuilder.toString(getObjectRequest));
			logger.debug("S3Object: {}", ReflectionToStringBuilder.toString(s3Object));
			logger.debug("File Name: {}", fileName);
			if (bytes !=null) {
				logger.debug("Bytes Length: {}", bytes.length);
			}
		}

		return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}

	/**
	 * Upload stream to S3
	 * @param uploadKey
	 * @param inputStream 
	 * @return PutObjectResult returned from Amazon sdk
	 */
	private UploadResult upload(String bucketName, String uploadKey, InputStream inputStream, Map<String, String> propertyMap) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setUserMetadata(propertyMap);

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadKey, inputStream, objectMetadata);

		putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
		Upload upload = transferManager.upload(putObjectRequest);

		UploadResult uploadResult = null;
		try {
			uploadResult = upload.waitForUploadResult();
			logger.info("Upload completed, bucket={}, key={}", uploadResult.getBucketName(), uploadResult.getKey());
		} catch (AmazonServiceException ase) {
			LogUtil.logErrorWithBanner(logger, UPLOAD_FAILED, ase.getMessage());
			logger.error(ERROR, ase);
			logger.error("Caught an AmazonServiceException from PUT requests, rejected reasons:");
			logger.error("Error Message:    {}", ase.getMessage());
			logger.error("HTTP Status Code: {}", ase.getStatusCode());
			logger.error("AWS Error Code:   {}", ase.getErrorCode());
			logger.error("Error Type:       {}", ase.getErrorType());
			logger.error("Request ID:       {}", ase.getRequestId());
		} catch (AmazonClientException ace) {
			LogUtil.logErrorWithBanner(logger, UPLOAD_FAILED, ace.getMessage());
			logger.error(ERROR, ace);
			logger.error("Caught an AmazonClientException from PUT requests, rejected reasons:");
			logger.error("Error Message:    " + ace.getMessage());
		} catch (InterruptedException ie) { //NOSONAR
			LogUtil.logErrorWithBanner(logger, UPLOAD_FAILED, ie.getMessage());
			logger.error("Caught an InterruptedException from PUT requests, rejected reasons:");
			logger.error("Error Message:    " + ie.getMessage());
		}
		finally {
			IOUtils.closeQuietly(inputStream);
		}

		return uploadResult;
	}
}
