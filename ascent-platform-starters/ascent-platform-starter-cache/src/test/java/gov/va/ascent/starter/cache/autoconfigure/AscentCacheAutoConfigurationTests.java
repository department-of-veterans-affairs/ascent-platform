package gov.va.ascent.starter.cache.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;


import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by vgadda on 8/11/17.
 */
public class AscentCacheAutoConfigurationTests {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void testAscentCacheConfiguration() throws Exception {
        context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis", "ascent.cache.enabled=true");
        context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
        context.refresh();
        assertNotNull(context);
        CacheManager cacheManager = context.getBean(CacheManager.class);
        assertNotNull(cacheManager);
    }

    @Test
    public void testAscentCacheConfigurationKeyGenerator() throws Exception {
        context = new AnnotationConfigApplicationContext();
        EnvironmentTestUtils.addEnvironment(context, "spring.cache.type=redis", "ascent.cache.enabled=true");
        context.register(RedisAutoConfiguration.class, AscentCacheAutoConfiguration.class, RedisCacheConfiguration.class);
        context.refresh();
        assertNotNull(context);
        KeyGenerator keyGenerator = context.getBean(KeyGenerator.class);
        String key = (String)keyGenerator.generate(new Object(),myMethod(),new Object());
        assertNotNull(key);
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

    private final CacheProperties cacheProperties;

    private final CacheManagerCustomizers customizerInvoker;

    RedisCacheConfiguration(CacheProperties cacheProperties,
                            CacheManagerCustomizers customizerInvoker) {
        this.cacheProperties = cacheProperties;
        this.customizerInvoker = customizerInvoker;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
        cacheManager.setUsePrefix(true);
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        return this.customizerInvoker.customize(cacheManager);
    }

}
