package gov.va.ascent.starter.aws.s3.config;

import static com.amazonaws.services.s3.internal.Constants.MB;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;

@Configuration
@Profile(AscentCommonSpringProfiles.NOT_PROFILE_EMBEDDED_AWS)
public class S3Config {
	private Logger logger = LoggerFactory.getLogger(S3Config.class);
	
	@Value("${ascent.aws.access_key_id}")
	private String awsId;

	@Value("${ascent.aws.secret_access_key}")
	private String awsKey;
	
	@Value("${ascent.s3.region}")
	private String region;
	
	@Value("${ascent.s3.bucket}")
	private String bucketName;

	@Bean
	public AmazonS3 s3client() {

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);
		return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
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
			logger.info("bucketName: {}", bucketName);
			tm.abortMultipartUploads(bucketName, oneDayAgo);
			
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
