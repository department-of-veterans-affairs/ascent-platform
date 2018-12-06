package gov.va.ascent.starter.aws.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConfigConstantsTest {


	private static final String TEST_AWS_ID = "test-key";
	private static final String TEST_AWS_KEY = "test-secret";
	private static final String TEST_AWS_LOCALHOST_ENDPOINT = "http://localhost:4572/";

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(TEST_AWS_ID, ConfigConstants.AWS_LOCALSTACK_ID);
		Assert.assertEquals(TEST_AWS_KEY, ConfigConstants.AWS_LOCALSTACK_KEY);
		Assert.assertEquals(TEST_AWS_LOCALHOST_ENDPOINT, ConfigConstants.AWS_LOCALHOST_ENDPOINT);
	}

}
