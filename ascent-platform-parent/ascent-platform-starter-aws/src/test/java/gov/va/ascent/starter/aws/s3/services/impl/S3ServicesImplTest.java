package gov.va.ascent.starter.aws.s3.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;

import gov.va.ascent.starter.aws.s3.services.S3Service;


@RunWith(MockitoJUnitRunner.class)
public class S3ServicesImplTest {
	
	private static final String TEST_REGION = "test-region";
	private static final String TEST_BUCKET_NAME = "test-bucket";
	private static final String TEST_TARGET_BUCKET = "test-target-bucket";
	private static final String TEST_DLQ_BUCKET = "test-dlq-bucket";
	
	@Autowired
	private S3Service s3Service;
	
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
		s3Service = new S3ServiceImpl();
		ReflectionTestUtils.setField(s3Service, "s3client", mockS3Client);
		ReflectionTestUtils.setField(s3Service, "bucketName", TEST_BUCKET_NAME);
		ReflectionTestUtils.setField(s3Service, "targetBucketName", TEST_TARGET_BUCKET);
		ReflectionTestUtils.setField(s3Service, "dlqBucketName", TEST_DLQ_BUCKET);
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
		List<Bucket> bucketList = new ArrayList<Bucket>();
		bucketList.add(new Bucket(TEST_BUCKET_NAME));
		bucketList.add(new Bucket(TEST_TARGET_BUCKET));
		bucketList.add(new Bucket(TEST_DLQ_BUCKET));
		
		prepareS3Mock(bucketList);
		ResponseEntity<byte[]> bytesArray = s3Service.downloadFile("TEST-KEY");
		assertNotNull(bytesArray);

	}
	
	private void prepareS3Mock(List<Bucket> bucketList) throws Exception {
		S3Object mockS3Object = mock(S3Object.class);
		Mockito.when(mockS3Client.listBuckets()).thenReturn(bucketList);
		Mockito.when(mockS3Client.getRegionName()).thenReturn(TEST_REGION);
		Mockito.when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockS3Object);
		when(mockS3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(
	            new ByteArrayInputStream("testString".getBytes()), null));
	}
	
}
