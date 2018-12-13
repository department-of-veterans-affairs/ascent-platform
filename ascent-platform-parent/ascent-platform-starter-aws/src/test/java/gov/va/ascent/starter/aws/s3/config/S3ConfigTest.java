package gov.va.ascent.starter.aws.s3.config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.event.Level;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class S3ConfigTest {

	private static final String TEST_AWS_REGION = "us-east-1";

	@InjectMocks
	S3Config s3Config = new S3Config();

	@Mock
	private Environment environment;

	@Before
	public void setUp() throws Exception {

		ReflectionTestUtils.setField(s3Config, "region", TEST_AWS_REGION);

		final AscentLogger logger = AscentLoggerFactory.getLogger(S3Config.class);
		logger.setLevel(Level.DEBUG);
	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(TEST_AWS_REGION, FieldUtils.readField(s3Config, "region", true));
	}

	@Test
	public void testS3Client() throws Exception {
		String[] profiles = { AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS };
		when(environment.getActiveProfiles()).thenReturn(profiles);
		AmazonS3 amazonS3 = s3Config.s3client();
		assertNotNull(amazonS3);
	}

//	@Test(expected = Exception.class)
//	public void testS3NonEmbeddedAWSClient() throws Exception {
//		String[] profiles = { AscentCommonSpringProfiles.PROFILE_ENV_LOCAL_INT };
//		when(environment.getActiveProfiles()).thenReturn(profiles);
//		AmazonS3 amazonS3;
//		try {
//			amazonS3 = s3Config.s3client();
//		} catch (Throwable e) {
//			System.out.print("Rrethrowing caught exception " + e.getClass().getName() + ": " + e.getMessage());
//			throw e;
//		}
//		fail("Should have thrown exception on s3Config.s3client(). Instead it returned "
//				+ ReflectionToStringBuilder.toString(amazonS3));
////		assertNotNull("Got AmazonS3 instance when one was not expected: " + ReflectionToStringBuilder.toString(amazonS3), amazonS3);
//	}

	@Test
	public void testS3TransferManager() throws Exception {
		String[] profiles = { AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS };
		when(environment.getActiveProfiles()).thenReturn(profiles);
		TransferManager amazonS3TransferManager = s3Config.transferManager();
		assertNotNull(amazonS3TransferManager);
	}
}
