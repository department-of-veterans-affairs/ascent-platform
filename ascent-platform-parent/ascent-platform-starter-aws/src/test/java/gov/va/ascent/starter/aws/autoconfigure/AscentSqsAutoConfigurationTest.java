package gov.va.ascent.starter.aws.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;



/**
 * Created by akulkarni on 2/1/18.
 */

public class AscentSqsAutoConfigurationTest {
	
	@Autowired
	AscentSqsAutoConfiguration ascentSqsAutoConfiguration;
	
	@Before
	public void setUp() throws Exception {
		ascentSqsAutoConfiguration = new AscentSqsAutoConfiguration();
	}

	@Test
	public void testSqsService(){
		assertNotNull(ascentSqsAutoConfiguration.sqsService());
	}
	
}


