package gov.va.ascent.starter.aws.transform.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.transfer.model.UploadResult;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;
import gov.va.ascent.starter.aws.transform.AbstractAwsS3Transformer;


/**
 * <p>
 * Implementation of the AwsTransformer interface
 * for transforming Request objects being sent/received from/to Aws Services
 * This object is for the UploadResultTransform operation response object
 *
 * @Vanapalliv
 */
@Service(value = UploadResultTransform.BEAN_NAME)
@Qualifier(UploadResultTransform.BEAN_NAME)
public class UploadResultTransform extends AbstractAwsS3Transformer<UploadResult, UploadResultResponse> {

	/** Constant for the logger for this class */
	public static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(UploadResultTransform.class);

	/** Spring bean name constant */
	public static final String BEAN_NAME = "uploadResultTransform";

	@Override
	public UploadResultResponse transformToService(UploadResult toTransform) {
		
		UploadResultResponse uploadResultResponse = new UploadResultResponse();
		uploadResultResponse.setBucketName(toTransform.getBucketName());
		uploadResultResponse.seteTag(toTransform.getETag());
        uploadResultResponse.setKey(toTransform.getKey());
        uploadResultResponse.setVersionId(toTransform.getVersionId());
		
		return uploadResultResponse;
	}

	@Override
	public UploadResult transformToAwsS3(UploadResultResponse toTransform) {
		throw new IllegalAccessError(
				"Method not impelemented: UploadResult transformToAwsS3(UploadResultResponse toTransform)");
	}

	
}
