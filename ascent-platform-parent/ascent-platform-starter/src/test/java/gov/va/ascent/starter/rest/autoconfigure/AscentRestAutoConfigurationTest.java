package gov.va.ascent.starter.rest.autoconfigure;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ascent.framework.rest.provider.RestProviderHttpResponseCodeAspect;
import gov.va.ascent.starter.audit.autoconfigure.AscentAuditAutoConfiguration;
import gov.va.ascent.starter.security.autoconfigure.AscentSecurityAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AscentRestAutoConfigurationTest {

	private static final String CONNECTION_TIMEOUT = "20000";

	private AscentRestAutoConfiguration ascentRestAutoConfiguration;

	private AnnotationConfigWebApplicationContext context;

	@Before
	public void setup() {
		context = new AnnotationConfigWebApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "feign.hystrix.enabled=true");
		EnvironmentTestUtils.addEnvironment(context, "ascent.rest.client.connection-timeout=" + CONNECTION_TIMEOUT);
		context.register(JacksonAutoConfiguration.class, SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class,
				AscentSecurityAutoConfiguration.class,
				AscentAuditAutoConfiguration.class, AscentRestAutoConfiguration.class,
				RestProviderHttpResponseCodeAspect.class);

		context.refresh();
		assertNotNull(context);

		// test configuration and give ascentRestAutoConfiguration a value for other tests
		ascentRestAutoConfiguration = context.getBean(AscentRestAutoConfiguration.class);
		assertNotNull(ascentRestAutoConfiguration);
	}

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testConfiguration_Broken() {
		EnvironmentTestUtils.addEnvironment(context, "ascent.rest.client.connection-timeout=BLAHBLAH");

		try {
			context.refresh();
			ascentRestAutoConfiguration.restClientTemplate();
			fail("AscentRestAutoConfiguration should have thrown IllegalStateException or BeansException");
		} catch (Exception e) {
			assertTrue(BeansException.class.isAssignableFrom(e.getClass()));
		} finally {
			EnvironmentTestUtils.addEnvironment(context, "ascent.rest.client.connection-timeout=" + CONNECTION_TIMEOUT);
			context.refresh();
			ascentRestAutoConfiguration = context.getBean(AscentRestAutoConfiguration.class);
			assertNotNull(ascentRestAutoConfiguration);
		}
	}

	@Test
	public void testWebConfiguration() throws Exception {
		assertNotNull(ascentRestAutoConfiguration.restProviderHttpResponseCodeAspect());
		assertNotNull(ascentRestAutoConfiguration.restProviderTimerAspect());
		assertNotNull(ascentRestAutoConfiguration.restClientTemplate());
		assertNotNull(ascentRestAutoConfiguration.restClientTokenTemplate());
		assertNotNull(ascentRestAutoConfiguration.tokenClientHttpRequestInterceptor());
	}

}
