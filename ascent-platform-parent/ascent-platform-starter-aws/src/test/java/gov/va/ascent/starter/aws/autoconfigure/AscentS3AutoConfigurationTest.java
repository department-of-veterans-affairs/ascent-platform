package gov.va.ascent.starter.aws.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Created by akulkarni on 2/1/18.
 */
@RunWith(MockitoJUnitRunner.class)
public class AscentS3AutoConfigurationTest {
	
	@Autowired
	AscentS3AutoConfiguration ascentS3AutoConfiguration;
	
	@Before
	public void setUp() throws Exception {
		ascentS3AutoConfiguration = new AscentS3AutoConfiguration();
	}

	@Test
	public void testS3Services(){
		assertNotNull(ascentS3AutoConfiguration.s3Service());
	}

}


