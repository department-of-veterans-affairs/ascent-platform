package gov.va.ascent.starter.aws.transform.impl;

import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.amazonaws.services.s3.transfer.model.UploadResult;

import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;
import gov.va.ascent.starter.aws.transform.AbstractAwsS3Transformer;


public class UploadResultTransformTest{

	AbstractAwsS3Transformer<UploadResult, UploadResultResponse> uploadResultTransform;

	@Before
	public void setup() throws Exception {
		uploadResultTransform = new UploadResultTransform();
	}

	@After
	public void tearDown() throws Exception {

	}
	
	@Test
	public final void testTransformToService() {
		UploadResult uploadResult = new UploadResult();
		uploadResult.setBucketName("bktName");
		uploadResult.setETag("eTag");
		uploadResult.setKey("keyy");
		uploadResult.setVersionId("versionid");
		UploadResultResponse uploadResultResponse = uploadResultTransform.transformToService(uploadResult);
	    assertEquals(uploadResult.getBucketName(),uploadResultResponse.getBucketName());
	}

	@Test(expected = IllegalAccessError.class)
	public final void testTransformToAwsS3() {
		uploadResultTransform.transformToAwsS3(new UploadResultResponse());

	}

	
}
