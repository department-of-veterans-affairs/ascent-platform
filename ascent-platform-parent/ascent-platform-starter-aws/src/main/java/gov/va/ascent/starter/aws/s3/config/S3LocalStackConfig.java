package gov.va.ascent.starter.aws.s3.config;

import static com.amazonaws.services.s3.internal.Constants.MB;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.starter.aws.server.AscentEmbeddedAwsLocalstack;

/**
 * Created by akulkarni on 2/1/18.
 */
@Configuration
@Profile(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)
public class S3LocalStackConfig {

	private Logger logger = LoggerFactory.getLogger(S3LocalStackConfig.class);

	@Value("${ascent.aws.localstack-config.s3.region}")
	private String region;
	
	@Value("${ascent.aws.localstack-config.s3.s3endpoint}")
	private String s3endpoint;
	
	@Value("${ascent.aws.localstack-config.s3.bucket}")
	private String sourceBucket;
	
	@Value("${ascent.aws.localstack-config.s3.target.bucket}")
	private String targetBucket;

	/**
	 * Embedded Redis bean to make sure embedded redis is started before redis cache is created
	 */
	@SuppressWarnings("unused")
	@Autowired(required = false)
	private AscentEmbeddedAwsLocalstack ascentEmbeddedAwsLocalstack;
	
	@Bean
	public AmazonS3 s3client() {
		AmazonS3ClientBuilder s3ClientBuider = AmazonS3ClientBuilder.standard()
		    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("foo", "foo")));
		
		s3ClientBuider.setEndpointConfiguration(new EndpointConfiguration(s3endpoint, region));
		s3ClientBuider.setPathStyleAccessEnabled(true);
		AmazonS3 s3 = s3ClientBuider.build();
		s3.createBucket(sourceBucket);
		s3.createBucket(targetBucket);

		return s3;
	}
	
	@Bean
	public TransferManager transferManager(){
		
		TransferManager tm = TransferManagerBuilder.standard()
									.withS3Client(s3client())
									.withDisableParallelDownloads(false)
									.withMinimumUploadPartSize(Long.valueOf(5 * Long.valueOf(MB)))
									.withMultipartUploadThreshold(Long.valueOf(16 * Long.valueOf(MB)))
									.withMultipartCopyPartSize(Long.valueOf(5 * Long.valueOf(MB)))
									.withMultipartCopyThreshold(Long.valueOf(100 * Long.valueOf(MB)))
									.withExecutorFactory(()->createExecutorService(20))
									.build();
		
		int oneDay = 1000 * 60 * 60 * 24;
		Date oneDayAgo = new Date(System.currentTimeMillis() - oneDay);
		
		try {
			logger.info("bucketName: {}", sourceBucket);
			tm.abortMultipartUploads(sourceBucket, oneDayAgo);
			
		} catch (AmazonClientException e) {
			logger.error("Unable to upload file, upload was aborted, reason: {}" + e);
		}
		
		return tm;
	}
	
	private ThreadPoolExecutor createExecutorService(int threadNumber) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 1;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ascent-amazon-s3-transfer-manager-worker-" + threadCount++);
                return thread;
            }
        };
        return (ThreadPoolExecutor)Executors.newFixedThreadPool(threadNumber, threadFactory);
    }
}


