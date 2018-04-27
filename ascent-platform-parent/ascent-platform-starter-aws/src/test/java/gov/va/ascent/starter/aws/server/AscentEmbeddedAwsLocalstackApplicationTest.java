package gov.va.ascent.starter.aws.server;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author akulkarni
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AscentEmbeddedAwsLocalstackTestAutoConfiguration.class)
public class AscentEmbeddedAwsLocalstackApplicationTest {
    
	@Autowired
	AscentEmbeddedAwsLocalstackApplication ascentEmbeddedAwsServers;


	@Test
	public void testStartStack() throws Exception {
		ascentEmbeddedAwsServers.startAwsLocalStack();
	}
	
	@Test
	public void testStopStack() throws Exception {
		ascentEmbeddedAwsServers.stopAwsLocalStack();
	}
}
