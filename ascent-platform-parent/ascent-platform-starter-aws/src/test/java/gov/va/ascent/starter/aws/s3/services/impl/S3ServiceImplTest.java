package gov.va.ascent.starter.aws.s3.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.exception.S3Exception;
import gov.va.ascent.starter.aws.s3.dto.BaseDto;
import gov.va.ascent.starter.aws.s3.dto.CopyFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DeleteFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DownloadFileRequest;
import gov.va.ascent.starter.aws.s3.dto.DownloadFileResponse;
import gov.va.ascent.starter.aws.s3.dto.MoveMessageRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultRequest;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;
import gov.va.ascent.starter.aws.s3.services.S3Service;
import gov.va.ascent.starter.aws.transform.AbstractAwsS3Transformer;

@RunWith(MockitoJUnitRunner.class)
public class S3ServiceImplTest {

	private static final String TEST_REGION = "test-region";
	private static final String TEST_BUCKET_NAME = "test-bucket";
	private static final String TEST_TARGET_BUCKET = "test-target-bucket";
	private static final String TEST_DLQ_BUCKET = "test-dlq-bucket";

	@Autowired
	@InjectMocks
	private S3Service s3Service = new S3ServiceImpl();

//	@Mock
//	private AmazonS3 mockS3Client;

	@Mock
	S3Object mockS3Object;

	@Mock
	protected TransferManager transferManager;

	@Mock
	private ResourceLoader resourceLoader;

	@Mock
	AbstractAwsS3Transformer<UploadResult, UploadResultResponse> uploadResultTransform;

	@Before
	public void setUp() throws Exception {
		final AscentLogger logger = AscentLoggerFactory.getLogger(S3ServiceImpl.class);
		logger.setLevel(Level.DEBUG);

		System.setProperty("aws.accessKeyId", "anyValue");
		System.setProperty("aws.secretKey", "anyValue");

		MockitoAnnotations.initMocks(this);
	}

//	private AmazonS3 recreateS3client() {
//		// mock the impl returned from S3Config, because cannot mock the AmazonS3 interface
//		S3Config conf = new S3Config();
//		AmazonS3 mockS3Client = Mockito.mock(ReflectionTestUtils.invokeMethod(conf, "s3client"));
//		when(transferManager.getAmazonS3Client()).thenReturn(mockS3Client);
//		return mockS3Client;
//	}
	

	@Test(expected = S3Exception.class)
	public void testUploadMultiPartSingle() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		final Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		final MockMultipartFile mockFile = spy(new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes()));
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName(TEST_BUCKET_NAME);
		uploadResult.setETag("TEST-TAG");
		uploadResult.setKey("TEST-KEY");
		uploadResult.setVersionId("1222");
		UploadResultResponse uploadResultResponse = new UploadResultResponse();
		uploadResultResponse.setBucketName(TEST_BUCKET_NAME);
		uploadResultResponse.setKey("TEST-KEY");
		uploadResultResponse.setVersionId("1222");
		when(upload.waitForUploadResult()).thenReturn(uploadResult);
		when(uploadResultTransform.transformToService(uploadResult)).thenReturn(uploadResultResponse);
		ResponseEntity<UploadResultResponse> response = s3Service.uploadMultiPartFile(TEST_BUCKET_NAME, mockFile, propertyMap);
		assertEquals(200, response.getStatusCodeValue());

		doThrow(new IOException("Testing")).when(mockFile).getInputStream();
		response = s3Service.uploadMultiPartFile(TEST_BUCKET_NAME, mockFile, propertyMap);
	}
	
	@Test(expected = S3Exception.class)
	public void testUploadMultiPartSingleS3ExceptionNull() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		final Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		final MockMultipartFile mockFile = spy(new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes()));
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		ResponseEntity<UploadResultResponse> response = s3Service.uploadMultiPartFile(TEST_BUCKET_NAME, mockFile, propertyMap);
		assertEquals(200, response.getStatusCodeValue());
		doThrow(new IOException()).when(mockFile).getInputStream();
		response = s3Service.uploadMultiPartFile(TEST_BUCKET_NAME, mockFile, propertyMap);
	}
	

	@Test
	public void testUploadByteArray() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		final Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put("documentName", "Sample Upload File");
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName(TEST_BUCKET_NAME);
		uploadResult.setETag("eTag");
		uploadResult.setKey("Key");
		uploadResult.setVersionId("versionid");
		UploadResultResponse uploadResultResponse = new UploadResultResponse();
		uploadResultResponse.setBucketName(TEST_BUCKET_NAME);
		uploadResultResponse.seteTag("eTag");
		uploadResultResponse.setKey("Key");
		uploadResultResponse.setVersionId("versionid");
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		when(uploadResultTransform.transformToService(anyObject())).thenReturn(
				uploadResultResponse);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName(TEST_BUCKET_NAME);
		uploadResultRequest.setByteData("some xml".getBytes());
		uploadResultRequest.setFileName("filename.txt");
		uploadResultRequest.setPropertyMap(propertyMap);
		uploadResultResponse =
				s3Service.uploadByteArray(uploadResultRequest);
		assertNotNull(uploadResultResponse);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileAmazonClientException() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(
				new AmazonClientException("AmazonClientException"));
		when(mockResource.getInputStream()).thenThrow(
				new AmazonClientException("AmazonClientException"));
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileAmazonServiceException() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(
				new AmazonServiceException("AmazonServiceException"));
		when(mockResource.getInputStream()).thenThrow(
				new AmazonServiceException("AmazonServiceException"));
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileInterruptedException() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenThrow(
				new InterruptedException());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(
				new S3Exception());
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileS3ExceptionThrowable() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(new S3Exception(new Throwable()));
		when(mockResource.getInputStream()).thenThrow(new S3Exception(new Throwable()));
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileS3ExceptionMsg() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(new S3Exception("msg"));
		when(mockResource.getInputStream()).thenThrow(new S3Exception("msg"));
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test(expected = S3Exception.class)
	public void testuploadFileS3ExceptionThrowableMsg() throws Exception {
		final Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		final Resource mockResource = mock(Resource.class);
		when(resourceLoader.getResource(anyString())).thenReturn(mockResource);
		UploadResultRequest uploadResultRequest = new UploadResultRequest();
		uploadResultRequest.setBucketName("bktName");
		uploadResultRequest.setByteData(new byte[5]);
		uploadResultRequest.setFileName(TEST_BUCKET_NAME);
		uploadResultRequest.setPropertyMap(new HashMap<>());
		when(uploadResultTransform.transformToService(anyObject())).thenThrow(new S3Exception("Message",
				new Throwable()));
		when(mockResource.getInputStream()).thenThrow(new S3Exception("Message",
				new Throwable()));
		s3Service.uploadByteArray(uploadResultRequest);
	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(transferManager, FieldUtils.readField(s3Service, "transferManager", true));
	}

	@Test
	public void testDownloadFile() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();

		prepareS3Mock(bucketList);
		BaseDto downloadResultRequest = new DownloadFileRequest();
		downloadResultRequest.setBucketName(TEST_BUCKET_NAME);
		downloadResultRequest.setKeyName("TEST-KEY");
		final DownloadFileResponse response = s3Service.downloadFile(downloadResultRequest);
		assertNotNull(response);
	}

	@Test
	public void testDownloadFileNullBytes() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();

		prepareS3Mock(bucketList);
		when(mockS3Object.getObjectContent())
				.thenReturn(new S3ObjectInputStream(new ByteArrayInputStream("".getBytes()), null));
		BaseDto downloadResultRequest = new DownloadFileRequest();
		downloadResultRequest.setBucketName(TEST_BUCKET_NAME);
		downloadResultRequest.setKeyName("TEST-KEY");
		final DownloadFileResponse response = s3Service.downloadFile(downloadResultRequest);
		assertNotNull(response);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = S3Exception.class)
	public void testDownloadFile_Exception() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();
		AmazonS3 mockS3Client = prepareS3Mock(bucketList);
		when(mockS3Client.getObject(any(GetObjectRequest.class))).thenThrow(Exception.class);
		BaseDto downloadResultRequest = new DownloadFileRequest();
		downloadResultRequest.setBucketName(TEST_BUCKET_NAME);
		downloadResultRequest.setKeyName("TEST-KEY");
		s3Service.downloadFile(downloadResultRequest);

	}

	private List<Bucket> prepareBucketList() {
		final List<Bucket> bucketList = new ArrayList<Bucket>();
		bucketList.add(new Bucket(TEST_BUCKET_NAME));
		bucketList.add(new Bucket(TEST_TARGET_BUCKET));
		bucketList.add(new Bucket(TEST_DLQ_BUCKET));
		return bucketList;
	}

	private AmazonS3 prepareS3Mock(List<Bucket> bucketList) throws Exception {
		// mock the impl returned from S3Config, because cannot mock the AmazonS3 interface
//		AmazonS3 mockS3Client = Mockito.mock(AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(TEST_REGION)).build());
		AmazonS3Client mockS3Client = Mockito.mock(AmazonS3Client.class);

		// set client getters
		when(mockS3Client.listBuckets()).thenReturn(bucketList);
		when(mockS3Client.getRegionName()).thenReturn(TEST_REGION);
		when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
		when(mockS3Client.putObject(any(String.class), any(String.class), any(String.class))).thenReturn(new PutObjectResult());
		when(mockS3Object.getObjectContent())
				.thenReturn(new S3ObjectInputStream(new ByteArrayInputStream("testString".getBytes()), null));

		when(transferManager.getAmazonS3Client()).thenReturn(mockS3Client);
		return mockS3Client;
	}

	@Test
	public void testCopyFileFromSourceToTargetBucket() throws Exception {
		final List<Bucket> bucketList = prepareBucketList();
		AmazonS3 mockS3Client = prepareS3Mock(bucketList);
		when(mockS3Client.copyObject(anyObject())).thenReturn(mock(CopyObjectResult.class));
		CopyFileRequest copyFileRequest = new CopyFileRequest();
		copyFileRequest.setKey("testFile.txt");
		copyFileRequest.setTargetBucketName(TEST_TARGET_BUCKET);
		copyFileRequest.setSourceBucketName(TEST_BUCKET_NAME);
		s3Service.copyFileFromSourceToTargetBucket(copyFileRequest);
	}

	@Test(expected = S3Exception.class)
	public void testCopyFileFromSourceToTargetBucket_AmazonServiceException() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		CopyFileRequest copyFileRequest = new CopyFileRequest();
		copyFileRequest.setKey("testFile.txt");
		copyFileRequest.setTargetBucketName(TEST_TARGET_BUCKET);
		copyFileRequest.setSourceBucketName(TEST_BUCKET_NAME);
		when(mockS3Client.copyObject(anyObject())).thenThrow(new AmazonServiceException("Error occurred"));
		s3Service.copyFileFromSourceToTargetBucket(copyFileRequest);
	}

	@Test(expected = S3Exception.class)
	public void testCopyFileFromSourceToTargetBucket_AmazonClientException() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		CopyFileRequest copyFileRequest = new CopyFileRequest();
		copyFileRequest.setKey("testFile.txt");
		copyFileRequest.setTargetBucketName(TEST_TARGET_BUCKET);
		copyFileRequest.setSourceBucketName(TEST_BUCKET_NAME);
		when(mockS3Client.copyObject(anyObject())).thenThrow(new AmazonClientException("Error occurred"));
		s3Service.copyFileFromSourceToTargetBucket(copyFileRequest);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = S3Exception.class)
	public void testCopyFileFromSourceToTargetBucket_Exception() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		CopyFileRequest copyFileRequest = new CopyFileRequest();
		copyFileRequest.setKey("testFile.txt");
		copyFileRequest.setTargetBucketName(TEST_TARGET_BUCKET);
		copyFileRequest.setSourceBucketName(TEST_BUCKET_NAME);
		when(mockS3Client.copyObject(anyObject())).thenThrow(Exception.class);
		s3Service.copyFileFromSourceToTargetBucket(copyFileRequest);
	}

	@Test
	public void testMoveMessageToS3() throws Exception {
		prepareS3Mock(prepareBucketList());
		MoveMessageRequest moveMessageRequest = new MoveMessageRequest();
		moveMessageRequest.setDlqBucketName(TEST_BUCKET_NAME);
		moveMessageRequest.setKey("key");
		moveMessageRequest.setMessage("messageBody");
		s3Service.moveMessageToS3(moveMessageRequest);
	}

	@Test(expected = S3Exception.class)
	public void testMoveMessageToS3_AmazonServiceException() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		MoveMessageRequest moveMessageRequest = new MoveMessageRequest();
		moveMessageRequest.setDlqBucketName(TEST_BUCKET_NAME);
		moveMessageRequest.setKey("key");
		moveMessageRequest.setMessage("messageBody");
		when(mockS3Client.putObject(anyString(), anyString(), anyString())).thenThrow(new AmazonServiceException("Error occurred"));
		s3Service.moveMessageToS3(moveMessageRequest);
	}

	@Test(expected = S3Exception.class)
	public void testMoveMessageToS3_AmazonClientException() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		MoveMessageRequest moveMessageRequest = new MoveMessageRequest();
		moveMessageRequest.setDlqBucketName(TEST_BUCKET_NAME);
		moveMessageRequest.setKey("key");
		moveMessageRequest.setMessage("messageBody");
		when(mockS3Client.putObject(anyString(), anyString(), anyString())).thenThrow(new AmazonClientException("Error occurred"));
		s3Service.moveMessageToS3(moveMessageRequest);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = S3Exception.class)
	public void testMoveMessageToS3_Exception() throws Exception {
		AmazonS3 mockS3Client = prepareS3Mock(prepareBucketList());
		MoveMessageRequest moveMessageRequest = new MoveMessageRequest();
		moveMessageRequest.setDlqBucketName(TEST_BUCKET_NAME);
		moveMessageRequest.setKey("key");
		moveMessageRequest.setMessage("messageBody");
		when(mockS3Client.putObject(anyString(), anyString(), anyString())).thenThrow(Exception.class);
		s3Service.moveMessageToS3(moveMessageRequest);
	}
	
	@Test
	public void testDeleteFile() throws Exception {
		
		prepareS3Mock(prepareBucketList());
		DeleteFileRequest deleteFileRequest = new DeleteFileRequest();
		deleteFileRequest.setBucketName(TEST_BUCKET_NAME);
		deleteFileRequest.setKeyName("TEST-KEY");
		s3Service.deleteFile(deleteFileRequest);
	}
	
	@Test(expected=S3Exception.class)
	public void testDeleteFile_Exception() throws Exception {
				
		prepareS3Mock(prepareBucketList());
		DeleteFileRequest deleteFileRequest = new DeleteFileRequest();
		deleteFileRequest.setBucketName(null);
		deleteFileRequest.setKeyName("TEST-KEY");
		s3Service.deleteFile(deleteFileRequest);
	}

	
}
