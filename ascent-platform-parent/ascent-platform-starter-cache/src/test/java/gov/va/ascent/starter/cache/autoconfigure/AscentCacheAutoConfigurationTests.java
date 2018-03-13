package gov.va.ascent.starter.cache.autoconfigure;

import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import gov.va.ascent.starter.cache.autoconfigure.AscentCacheProperties.RedisExpires;

/**
 * Created by vgadda on 8/11/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class AscentCacheAutoConfigurationTests {

	private AnnotationConfigApplicationContext context;

	@Mock
	CacheManager cacheManager;

	@Mock
	Cache mockCache;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testAscentCacheConfiguration() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		CacheManager cacheManager = context.getBean(CacheManager.class);
		assertNotNull(cacheManager);
	}

	@Test
	public void testAscentCacheConfigurationKeyGenerator() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		KeyGenerator keyGenerator = context.getBean(KeyGenerator.class);
		String key = (String)keyGenerator.generate(new Object(),myMethod(),new Object());
		assertNotNull(key);
	}

	@Test
	public void testCacheGetError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		
		AscentCacheAutoConfiguration ascentCacheAutoConfiguration = context.getBean(AscentCacheAutoConfiguration.class);
		ascentCacheAutoConfiguration.errorHandler().handleCacheGetError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}
	
	@Test
	public void testCachePutError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		
		AscentCacheAutoConfiguration ascentCacheAutoConfiguration = context.getBean(AscentCacheAutoConfiguration.class);
		ascentCacheAutoConfiguration.errorHandler().handleCachePutError(new RuntimeException("Test Message"), mockCache, "TestKey", "TestValue");
	}
	
	@Test
	public void testCacheEvictError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		
		AscentCacheAutoConfiguration ascentCacheAutoConfiguration = context.getBean(AscentCacheAutoConfiguration.class);
		ascentCacheAutoConfiguration.errorHandler().handleCacheEvictError(new RuntimeException("Test Message"), mockCache, "TestKey");
	}
	
	@Test
	public void testCacheClearError() throws Exception {
		context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis");
		context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
		context.refresh();
		assertNotNull(context);
		
		AscentCacheAutoConfiguration ascentCacheAutoConfiguration = context.getBean(AscentCacheAutoConfiguration.class);
		ascentCacheAutoConfiguration.errorHandler().handleCacheClearError(new RuntimeException("Test Message"), mockCache);
	}

	public Method myMethod() throws NoSuchMethodException{
		return getClass().getDeclaredMethod("someMethod");
	}

	public void someMethod(){
		//do nothing
	}
}

/**
 * Copy of Spring Boot RedisCacheConfiguration. Couldn't load as it is not public.
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(RedisTemplate.class)
@ConditionalOnMissingBean(CacheManager.class)
@EnableConfigurationProperties(CacheProperties.class)
class RedisCacheConfiguration {

	private final AscentCacheProperties cacheProperties;

	private final CacheManagerCustomizers customizerInvoker;

	RedisCacheConfiguration(AscentCacheProperties cacheProperties,
			CacheManagerCustomizers customizerInvoker) {
		this.cacheProperties = cacheProperties;
		this.customizerInvoker = customizerInvoker;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
		RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
		cacheManager.setUsePrefix(true);
	    List<RedisExpires> listRedisExpires = new ArrayList<>();
	    RedisExpires redisExpires = new RedisExpires();
	    redisExpires.setName("methodcachename_projectname_projectversion");
	    redisExpires.setTtl(86400L);
	    listRedisExpires.add(0, redisExpires);
	    cacheProperties.setExpires(listRedisExpires);
	    cacheProperties.setDefaultExpires(500L);
	    
		return this.customizerInvoker.customize(cacheManager);
	}

}
