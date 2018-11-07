package gov.va.ascent.starter.aws.s3.services.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

import gov.va.ascent.framework.log.AscentBanner;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.util.Defense;
import gov.va.ascent.starter.aws.exception.S3Exception;
import gov.va.ascent.starter.aws.s3.dto.UploadResultRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;
import gov.va.ascent.starter.aws.s3.services.S3Service;
import gov.va.ascent.starter.aws.transform.AbstractAwsS3Transformer;
import gov.va.ascent.starter.aws.transform.impl.UploadResultTransform;

@Service
public class S3ServiceImpl implements S3Service {

	/*
	 * TODO
	 *
	 * Delete method
	 * -------------
	 * > Need to write one
	 *
	 * Upload methods
	 * --------------
	 * re https://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html#object-metadata
	 * > S3 Put headers are limited to a total of 8 KB, and all keys/values must be US-ASCII.
	 * > The propertyMap (a "user-defined header") is limited to 2 KB total, and contributes to the total header size.
	 * Can we fit all our CORP metadata in 2 KB? Will we have to zip + base64-encode it?
	 * > S3 docs state regarding the Put request:
	 * "... user-defined metadata names must begin with "x-amz-meta-" to distinguish them from other HTTP headers."
	 * And it continues: "When you retrieve the object using the REST API, this prefix is returned."
	 * The prefix should be added and stripped "behind the scenes" in this class so that total size calculations
	 * (2 KB max) are correct.
	 * > ANY of the above restrictions that are not met should result in rejection of the request.
	 *
	 * For crude example of zip in java, see:
	 * https://www.javacodegeeks.com/2015/01/working-with-gzip-and-compressed-data.html
	 *
	 * Download method
	 * ---------------
	 * > Should be returning an Ascent POJO, not responseEntity
	 * > Need to see if there are headers that should be stripped out of the response
	 *
	 * Copy and Move methods
	 * ---------------------
	 * > copy: does the header metadata get copied with the file?
	 * > move: what purpose does this serve? Are we not going to have a JMS dead letter queue?
	 *
	 */

	private static final String REQUEST_ID_MESSAGE = "Request ID:       {}";
	private static final String ERROR_TYPE_MESSAGE = "Error Type:       {}";
	private static final String AWS_ERROR_CODE_MESSAGE = "AWS Error Code:   {}";
	private static final String HTTP_STATUS_CODE_MESSAGE = "HTTP Status Code: {}";
	private final AscentLogger logger = AscentLoggerFactory.getLogger(S3ServiceImpl.class);
	private static final String NEWLINE = System.lineSeparator();
	public static final String ERROR_MESSAGE = "Error Message: {}";
	public static final String ERROR_MSG = "Error Message: ";
	public static final String ERROR = "Error: {}";
	public static final String UPLOAD_RESULT = "UploadResult: {}";
	public static final String BUCKET_NAME_NOTNULL_MESSAGE = "Bucket Name can't be null";
	public static final String KEY_NOTNULL_MESSAGE = "Key of the object can't be null";
	public static final String MULTIPART_NOTNULL_MESSAGE = "Multipart Request can't be null";
	public static final String UPLOAD_FAILED = "Upload Failed";
	public static final String DOWNLOAD_FAILED = "Download Failed";
	public static final String COPY_FAILED = "Copy Failed";
	public static final String MOVE_FAILED = "Move Failed";

	@Autowired
	private AmazonS3 s3client;

	@Autowired
	protected TransferManager transferManager;

	@Autowired
	private ResourceLoader resourceLoader;
	
	@Autowired
	@Qualifier(UploadResultTransform.BEAN_NAME)
	AbstractAwsS3Transformer<UploadResult, UploadResultResponse> uploadResultTransform;

	/**
	 * Upload a byte array to S3
	 *
	 * @param byteData
	 * @param fileName
	 * @param propertyMap
	 * @return PutObjectResult returned from Amazon sdk
	 */
	@Override
	public UploadResultResponse uploadByteArray(UploadResultRequest uploadResultRequest) {

		Defense.notNull(uploadResultRequest.getBucketName(), BUCKET_NAME_NOTNULL_MESSAGE);
		Defense.notNull(uploadResultRequest.getByteData(), "byte[] can't be null");
		Defense.notNull(uploadResultRequest.getFileName(), "File Name can't be null");

		final UploadResultResponse putObjectResult = upload(uploadResultRequest.getBucketName(), 
				uploadResultRequest.getFileName(), new ByteArrayInputStream(uploadResultRequest.getByteData()), 
				uploadResultRequest.getPropertyMap());

		if (logger.isDebugEnabled()) {
			logger.debug(UPLOAD_RESULT, ReflectionToStringBuilder.toString(putObjectResult));
		}

		return putObjectResult;
	}


	/**
	 * Copy a file from one bucket to another bucket.
	 *
	 * @param key
	 */
	@Override
	public void copyFileFromSourceToTargetBucket(final String sourceBucketName, final String targetBucketName, final String key) {


		try {
			Defense.notNull(sourceBucketName, "Source Bucket Name can't be null");
			Defense.notNull(targetBucketName, "Target Bucket Name can't be null");
			Defense.notNull(key, KEY_NOTNULL_MESSAGE);
			
			// Copying object
			final CopyObjectRequest copyObjRequest = new CopyObjectRequest(
					sourceBucketName, key, targetBucketName, key);
			logger.info("Copying object. {}", ReflectionToStringBuilder.toString(copyObjRequest));
			s3client.copyObject(copyObjRequest);
			// Deleting object from original source bucket
			s3client.deleteObject(sourceBucketName, key);
			logger.info("Deleting object. Bucket Name: {} Key : {}", sourceBucketName, key);
		} catch (final AmazonServiceException ase) {
			String message = "Caught an AmazonServiceException, " + "which means your request made it "
					+ "to Amazon S3, but was rejected with an error response ."
					+ NEWLINE + ERROR_MESSAGE + ase.getMessage()
					+ NEWLINE + HTTP_STATUS_CODE_MESSAGE + ase.getStatusCode()
					+ NEWLINE + AWS_ERROR_CODE_MESSAGE + ase.getErrorCode()
					+ NEWLINE + ERROR_TYPE_MESSAGE + ase.getErrorType()
					+ NEWLINE + REQUEST_ID_MESSAGE + ase.getRequestId();
			logger.error(AscentBanner.newBanner(COPY_FAILED, Level.ERROR), message, ase);
			throw new S3Exception(ase);
			
		} catch (final AmazonClientException ace) {
			String message = "Caught an AmazonClientException, " + "which means the client encountered "
					+ "an internal error while trying to " + " communicate with S3, "
					+ "such as not being able to access the network.";
			logger.error(AscentBanner.newBanner(COPY_FAILED, Level.ERROR), message, ace);
            throw new S3Exception(ace);
			
		}catch (final Exception e) {
			String message = "Caught an Exception, " + "which means the client encountered "
					+ "an internal error while trying to " + " communicate with S3, "
					+ "such as not being able to access the network.";
			logger.error(AscentBanner.newBanner(COPY_FAILED, Level.ERROR), message, e);
            throw new S3Exception(e);
		}
	}

	/**
	 * Copy the DLQ Message to S3 DLQ Bucket.
	 */
	@Override
	public void moveMessageToS3(final String dlqBucketName, final String key, final String message) {
		
		try {
			Defense.notNull(dlqBucketName, BUCKET_NAME_NOTNULL_MESSAGE);
			Defense.notNull(key, KEY_NOTNULL_MESSAGE);
			Defense.notNull(message, "Message Content can't be null");

			logger.debug("Moving Message to S3. DLQ Bucket Name: {} Key: {}", dlqBucketName, key);
			s3client.putObject(dlqBucketName, key, message);

		}catch (final AmazonServiceException ase) {
			String errorMessage =
					"Caught an AmazonServiceException from PUT requests, rejected reasons:"
							+ NEWLINE + ERROR_MESSAGE + ase.getMessage()
							+ NEWLINE + HTTP_STATUS_CODE_MESSAGE + ase.getStatusCode()
							+ NEWLINE + AWS_ERROR_CODE_MESSAGE + ase.getErrorCode()
							+ NEWLINE + ERROR_TYPE_MESSAGE + ase.getErrorType()
							+ NEWLINE + REQUEST_ID_MESSAGE + ase.getRequestId();
			logger.error(AscentBanner.newBanner(MOVE_FAILED, Level.ERROR), errorMessage, ase);
			throw new S3Exception(ase);
			
		} catch (final AmazonClientException ace) {
			String errorMessage = "Caught an AmazonClientException from PUT requests, rejected reason:"
					+ NEWLINE + ERROR_MSG + ace.getMessage();
			logger.error(AscentBanner.newBanner(MOVE_FAILED, Level.ERROR), errorMessage, ace);
			throw new S3Exception(ace);
			
		} catch (final Exception ie) { // NOSONAR
			String errorMessage = "Caught an Exception from PUT requests, rejected reasons:"
					+ NEWLINE + ERROR_MSG + ie.getMessage();
			logger.error(AscentBanner.newBanner(MOVE_FAILED, Level.ERROR), errorMessage, ie);
			throw new S3Exception(ie);
			
		} 
	}

	/**
	 * Retrieves a file from S3
	 *
	 * @param key key to the file i.e. /myfolder/myfile
	 * @param bucket bucket name i.e. bucket-name
	 * @return response entity
	 * @throws IOException
	 */
	@Override
	public ResponseEntity<byte[]> downloadFile(final String bucketName, final String keyName)  {

		try {
			Defense.notNull(bucketName, BUCKET_NAME_NOTNULL_MESSAGE);
			Defense.notNull(keyName, KEY_NOTNULL_MESSAGE);

			final GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName);
			final S3Object s3Object = s3client.getObject(getObjectRequest);
			final S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

			final byte[] bytes = IOUtils.toByteArray(objectInputStream);
			final String fileName = URLEncoder.encode(keyName, "UTF-8").replaceAll("\\+", "%20");

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			httpHeaders.setContentLength(bytes == null ? 0 : bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileName);

			if (logger.isDebugEnabled()) {
				logger.debug("GetObjectRequest: {}", ReflectionToStringBuilder.toString(getObjectRequest));
				logger.debug("S3Object: {}", ReflectionToStringBuilder.toString(s3Object));
				logger.debug("File Name: {}", fileName);
				if (bytes != null) {
					logger.debug("Bytes Length: {}", bytes.length);
				}
			}

			return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
			
		}catch(Exception e) {
		
			logger.error(AscentBanner.newBanner(DOWNLOAD_FAILED, Level.ERROR), e.getMessage(), e);
			throw new S3Exception(e);
		}

	}

	/**
	 * Upload stream to S3
	 *
	 * @param uploadKey
	 * @param inputStream
	 * @return PutObjectResult returned from Amazon sdk
	 */
	private UploadResultResponse upload(final String bucketName, final String uploadKey, final InputStream inputStream,
			final Map<String, String> propertyMap) {

		final ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setUserMetadata(propertyMap);

		final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadKey, inputStream, objectMetadata);

		putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
		Upload upload = null;
		UploadResultResponse uploadResultResponse = null;
		
		try {
			upload = transferManager.upload(putObjectRequest);
			uploadResultResponse = uploadResultTransform.transformToService(upload.waitForUploadResult());
			logger.info("Upload completed, bucket={}, key={}", uploadResultResponse.getBucketName(), uploadResultResponse.getKey());
		} catch (final AmazonServiceException ase) {
			String message =
					"Caught an AmazonServiceException from PUT requests, rejected reasons:"
							+ NEWLINE + ERROR_MESSAGE + ase.getMessage()
							+ NEWLINE + HTTP_STATUS_CODE_MESSAGE + ase.getStatusCode()
							+ NEWLINE + AWS_ERROR_CODE_MESSAGE + ase.getErrorCode()
							+ NEWLINE + ERROR_TYPE_MESSAGE + ase.getErrorType()
							+ NEWLINE + REQUEST_ID_MESSAGE + ase.getRequestId();
			logger.error(AscentBanner.newBanner(UPLOAD_FAILED, Level.ERROR), message, ase);
			throw new S3Exception(ase);
			
		} catch (final AmazonClientException ace) {
			String message = "Caught an AmazonClientException from PUT requests, rejected reason:"
					+ NEWLINE + ERROR_MSG + ace.getMessage();
			logger.error(AscentBanner.newBanner(UPLOAD_FAILED, Level.ERROR), message, ace);
			throw new S3Exception(ace);
			
		} catch (final InterruptedException ie) { // NOSONAR
			String message = "Caught an InterruptedException from PUT requests, rejected reasons:"
					+ NEWLINE + ERROR_MSG + ie.getMessage();
			logger.error(AscentBanner.newBanner(UPLOAD_FAILED, Level.ERROR), message, ie);
			throw new S3Exception(ie);
			
		} finally {
			IOUtils.closeQuietly(inputStream);
		}

		return uploadResultResponse;
	}
}
