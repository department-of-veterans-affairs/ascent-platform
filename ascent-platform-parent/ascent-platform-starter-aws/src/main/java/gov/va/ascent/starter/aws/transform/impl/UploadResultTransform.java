package gov.va.ascent.starter.aws.transform.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.s3.dto.UploadResultResponse;
import gov.va.ascent.starter.aws.transform.AbstractAwsS3Transformer;
import gov.va.ascent.starter.aws.util.HystrixCommandConstants;


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
@Scope("prototype")
@RefreshScope
@DefaultProperties(groupKey = HystrixCommandConstants.AWS_SERVICE_GROUP_KEY)
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
