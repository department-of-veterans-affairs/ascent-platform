package gov.va.ascent.starter.feign.autoconfigure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.netflix.hystrix.HystrixCommand;

import feign.Feign;
import feign.Target;
import feign.hystrix.SetterFactory;
import gov.va.ascent.starter.security.autoconfigure.AscentSecurityAutoConfiguration;

/**
 * Created by rthota on 8/24/17.
 */
public class AscentFeignAutoConfigurationTest {

	private AnnotationConfigWebApplicationContext context;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testWebConfiguration() throws Exception {
		context = new AnnotationConfigWebApplicationContext();
		context.register(SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class,
				AscentSecurityAutoConfiguration.class,
				TokenFeignRequestInterceptor.class, AscentFeignAutoConfiguration.class);
		context.refresh();
		assertNotNull(context);
		final TokenFeignRequestInterceptor tokenFeignRequestInterceptor = this.context.getBean(TokenFeignRequestInterceptor.class);
		assertNotNull(tokenFeignRequestInterceptor);

	}

	@Test
	public void testGetterSettingAscentFiegnConfig() throws Exception {
		final AscentFeignAutoConfiguration ascentFeignAutoConfiguration = new AscentFeignAutoConfiguration();
		assertEquals("defaultGroup", ascentFeignAutoConfiguration.getGroupKey());
		ascentFeignAutoConfiguration.setGroupKey("NewGroupKey");
		assertEquals("NewGroupKey", ascentFeignAutoConfiguration.getGroupKey());
	}

	/**
	 * Test of feignBuilder method, of class AscentFeignAutoConfiguration.
	 */
	@Test
	public void testFeignBuilder() {
		final AscentFeignAutoConfiguration instance = new AscentFeignAutoConfiguration();
		final Feign.Builder result = instance.feignBuilder();
		assertNotNull(result);
	}

	@Test
	public void testSetterFactory() {
		final AscentFeignAutoConfiguration instance = new AscentFeignAutoConfiguration();
		final Feign.Builder result = instance.feignBuilder();

		try {
			final Field setterFactoryField = result.getClass().getDeclaredField("setterFactory");
			setterFactoryField.setAccessible(true);
			final SetterFactory factory = (SetterFactory) setterFactoryField.get(result);
			final Target target = new TestTarget(this.getClass(), "testFeignBuilder");
			final HystrixCommand.Setter setter = factory.create(target, this.getClass().getMethod("testFeignBuilder"));
			assertNotNull(setter);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| NoSuchMethodException e) {
			e.printStackTrace();
			fail("Should not throw exception here.");
		}
	}

	@SuppressWarnings("rawtypes")
	class TestTarget extends Target.HardCodedTarget {

		@SuppressWarnings("unchecked")
		public TestTarget(final Class type, final String url) {
			super(type, url);
		}

	}
}
