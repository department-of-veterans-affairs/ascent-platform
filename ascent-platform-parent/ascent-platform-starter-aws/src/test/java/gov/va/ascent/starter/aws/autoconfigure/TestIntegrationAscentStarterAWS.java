package gov.va.ascent.starter.aws.autoconfigure;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.s3.AmazonS3;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.s3.config.S3Config;
import gov.va.ascent.starter.aws.s3.services.S3Service;

/**
 * Created by akulkarni
 */
@Ignore
@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true)
public class TestIntegrationAscentStarterAWS {

	private static AscentLogger logger = AscentLoggerFactory.getLogger(TestIntegrationAscentStarterAWS.class);
	private static final String TEST_BUCKET_NAME = "test-bucket";

	private AnnotationConfigApplicationContext context;

	private boolean mockInitialized = false;

	private AmazonS3 amazonS3Client;

	@Mock
	private S3Service s3Service;

	@Before
	public void setUp() {
		String output = null;
		try {
			output = new DockerExe().execute(Arrays.asList("-v"));
			logger.error("Docker Execute Command Output [{}]", output);
		} catch (Exception e) {
			logger.error("Skipping the Test. Error [{}]", e.getMessage());
			org.junit.Assume.assumeTrue(false);
		}
		org.junit.Assume.assumeTrue(StringUtils.isNotEmpty(output));
		String profilesFromConsole = System.getProperty("spring.profiles.active", "");
		org.junit.Assume.assumeFalse(profilesFromConsole.contains("jenkins"));

		// rest of setup.
		if (!mockInitialized) {
			MockitoAnnotations.initMocks(this);
			mockInitialized = true;
		}

		amazonS3Client = DockerTestUtils.getClientS3();
		amazonS3Client.createBucket("test-bucket");
	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testAscentS3AutoConfiguration() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "ascent.aws.access_key_id=test");
		EnvironmentTestUtils.addEnvironment(context, "ascent.aws.secret_access_key=test");
		EnvironmentTestUtils.addEnvironment(context, "ascent.s3.region=us-east-1");
		EnvironmentTestUtils.addEnvironment(context, "ascent.s3.bucket=test-bucket");
		context.register(AscentS3AutoConfiguration.class, S3Config.class, amazonS3Client.getClass());
		context.refresh();
		assertNotNull(context);

		File file = File.createTempFile("localstack", "s3");
		file.deleteOnExit();

		try (FileOutputStream stream = new FileOutputStream(file)) {
			String content = "HELLO WORLD!";
			stream.write(content.getBytes());
		}

	}

}
