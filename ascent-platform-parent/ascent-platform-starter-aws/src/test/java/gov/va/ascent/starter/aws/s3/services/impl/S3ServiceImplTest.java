package gov.va.ascent.starter.aws.s3.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import gov.va.ascent.starter.aws.s3.services.S3Service;


@RunWith(MockitoJUnitRunner.class)
public class S3ServiceImplTest {
	
	private static final String TEST_REGION = "test-region";
	private static final String TEST_BUCKET_NAME = "test-bucket";
	private static final String TEST_TARGET_BUCKET = "test-target-bucket";
	private static final String TEST_DLQ_BUCKET = "test-dlq-bucket";
	
	@Autowired
	@InjectMocks
	private S3Service s3Service = new S3ServiceImpl();
	
	@Mock
	private AmazonS3 mockS3Client;
	
	@Mock
	S3Object mockS3Object;
	
	@Mock
	protected TransferManager transferManager;
	
	@Mock
	private ResourceLoader resourceLoader;

	
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET_NAME);
		ReflectionTestUtils.setField(s3Service, "targetBucketName", TEST_TARGET_BUCKET);
		ReflectionTestUtils.setField(s3Service, "dlqBucketName", TEST_DLQ_BUCKET);
        final Logger logger = (Logger) LoggerFactory.getLogger(S3ServiceImpl.class);
        logger.setLevel(Level.DEBUG);
	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(mockS3Client, FieldUtils.readField(s3Service, "s3client", true));
		Assert.assertEquals(TEST_BUCKET_NAME, FieldUtils.readField(s3Service, "bucketName", true));
		Assert.assertEquals(TEST_TARGET_BUCKET, FieldUtils.readField(s3Service, "targetBucketName", true));
		Assert.assertEquals(TEST_DLQ_BUCKET, FieldUtils.readField(s3Service, "dlqBucketName", true));
	}
	
	@Test
	public void testDownloadFile() throws Exception {
		List<Bucket> bucketList = prepareBucketList();

		prepareS3Mock(bucketList);
		ResponseEntity<byte[]> bytesArray = s3Service.downloadFile("TEST-KEY");
		assertNotNull(bytesArray);
	}

	private List<Bucket> prepareBucketList() {
		List<Bucket> bucketList = new ArrayList<Bucket>();
		bucketList.add(new Bucket(TEST_BUCKET_NAME));
		bucketList.add(new Bucket(TEST_TARGET_BUCKET));
		bucketList.add(new Bucket(TEST_DLQ_BUCKET));
		return bucketList;
	}

	private void prepareS3Mock(List<Bucket> bucketList) throws Exception {
		Mockito.when(mockS3Client.listBuckets()).thenReturn(bucketList);
		Mockito.when(mockS3Client.getRegionName()).thenReturn(TEST_REGION);
		Mockito.when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
		when(mockS3Object.getObjectContent())
				.thenReturn(new S3ObjectInputStream(new ByteArrayInputStream("testString".getBytes()), null));
	}

	
	@Test
	public void testUploadMultiPart() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		
		MockMultipartFile[] multipartFiles = new MockMultipartFile[2];
		MockMultipartFile mockFileOne = new MockMultipartFile("data", "filename.txt", "text/plain", "one xml".getBytes());
		MockMultipartFile mockFileTwo = new MockMultipartFile("data", "filename.txt", "text/plain", "two xml".getBytes());

		multipartFiles[0] = mockFileOne;
		multipartFiles[1] = mockFileTwo;

		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		ResponseEntity<List<UploadResult>> bytesArray = s3Service.uploadMultiPart(multipartFiles);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	
	@Test
	public void testUploadMultiPartSingle() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadMultiPartSingle(mockFile, propertyMap);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	
	@Test
	public void testUploadByteArray() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		Map<String, String> propertyMap = new HashMap<>();
		propertyMap.put("documentName", "Sample Upload File");
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadByteArray("some xml".getBytes(), "filename.txt", propertyMap);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}
	
	@Test
	public void testUploadMultiPartSingle_AmazonServiceException() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenThrow(new AmazonServiceException("Error occurred"));
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadMultiPartSingle(mockFile, propertyMap);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	@Test
	public void testUploadMultiPartSingle_AmazonClientException() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenThrow(new AmazonClientException("Error occurred"));
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadMultiPartSingle(mockFile, propertyMap);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	@Test
	public void testUploadMultiPartSingle_IOException() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		Map<String, String> propertyMap = new HashMap<String, String>();
		propertyMap.put("documentName", "Sample Upload File");
		MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenThrow(new InterruptedException());
		// doThrow(IOException.class).when(upload).waitForUploadResult();
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadMultiPartSingle(mockFile, propertyMap);
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	@Test
	public void testuploadFile() throws Exception {
		Upload upload = mock(Upload.class);
		when(transferManager.upload(any())).thenReturn(upload);
		when(upload.waitForUploadResult()).thenReturn(new UploadResult());
		when(resourceLoader.getResource(anyString())).thenReturn(mock(Resource.class));
		ResponseEntity<UploadResult> bytesArray = s3Service.uploadFile("testFile.txt", "testFile.txt");
		assertEquals(200, bytesArray.getStatusCodeValue());
	}

	
	@Test
	public void testCopyFileFromSourceToTargetBucket() throws Exception {
		List<Bucket> bucketList = prepareBucketList();
		prepareS3Mock(bucketList);
		when(mockS3Client.copyObject(anyObject())).thenReturn(mock(CopyObjectResult.class));
		s3Service.copyFileFromSourceToTargetBucket("testFile.txt");
	}
	
	@Test
	public void testCopyFileFromSourceToTargetBucket_AmazonServiceException() {
		when(mockS3Client.copyObject(anyObject())).thenThrow(new AmazonServiceException("Error occurred"));
		s3Service.copyFileFromSourceToTargetBucket("testFile.txt");
	}
	
	@Test
	public void testCopyFileFromSourceToTargetBucket_AmazonClientException() {
		when(mockS3Client.copyObject(anyObject())).thenThrow(new AmazonClientException("Error occurred"));
		s3Service.copyFileFromSourceToTargetBucket("testFile.txt");
	}
	
	@Test
	public void testMoveMessageToS3() {
		s3Service.moveMessageToS3("key", "messageBody");
	}
}
