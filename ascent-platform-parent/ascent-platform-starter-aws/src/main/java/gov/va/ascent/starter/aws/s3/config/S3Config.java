package gov.va.ascent.starter.aws.s3.config;

import static com.amazonaws.services.s3.internal.Constants.MB;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;

@Configuration
public class S3Config {
	private Logger logger = LoggerFactory.getLogger(S3Config.class);
	
	@Value("${ascent.aws.access_key_id}")
	private String awsId;

	@Value("${ascent.aws.secret_access_key}")
	private String awsKey;
	
	@Value("${ascent.s3.region}")
	private String region;
	
	@Value("${ascent.aws.localstack-config.s3.endpoint}")
	private String endpoint;
	
	@Autowired
    Environment environment;

	@Bean
	public AmazonS3 s3client() {
		for (final String profileName : environment.getActiveProfiles()) {
			if (profileName.equals(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)) {
				AmazonS3ClientBuilder s3ClientBuider = AmazonS3ClientBuilder.standard()
						.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsId, awsKey)));
				s3ClientBuider.setEndpointConfiguration(new EndpointConfiguration(endpoint, region));
				s3ClientBuider.setPathStyleAccessEnabled(true);
				return s3ClientBuider.build();
			}
		}

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
									.withMultipartUploadThreshold(Long.valueOf(5 * Long.valueOf(MB)))
									.withMultipartCopyPartSize(Long.valueOf(5 * Long.valueOf(MB)))
									.withMultipartCopyThreshold(Long.valueOf(100 * Long.valueOf(MB)))
									.withExecutorFactory(()->createExecutorService(20))
									.build();
		logger.debug("TransferManager {}", ReflectionToStringBuilder.toString(tm));
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
