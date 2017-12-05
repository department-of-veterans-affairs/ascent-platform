package gov.va.ascent.starter.cache.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by vgadda on 8/11/17.
 */
@EnableConfigurationProperties(AscentCacheProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class AscentCacheAutoConfiguration {

    static final Logger LOGGER = LoggerFactory.getLogger(AscentCacheAutoConfiguration.class);

    @Autowired
    private AscentCacheProperties ascentCacheProperties;

    @Bean
    public CacheManagerCustomizers cacheManagerCustomizers(){
        return new CacheManagerCustomizers(Arrays.asList(new RedisCacheManagerCustomizer(ascentCacheProperties)));
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory)
            {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object o, Method method, Object... objects) {
                LOGGER.info("Generating cacheKey ");
                StringBuilder sb = new StringBuilder();
                sb.append(o.getClass().getName());
                sb.append(method.getName());
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
                LOGGER.info("Generated cacheKey {}", sb.toString());
                return sb.toString();
            }
        };
    }
}

class RedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager>{

    private AscentCacheProperties ascentCacheProperties;

    public RedisCacheManagerCustomizer(AscentCacheProperties ascentCacheProperties) {
        this.ascentCacheProperties = ascentCacheProperties;
    }

    @Override
    public void customize(RedisCacheManager redisCacheManager) {
        redisCacheManager.setLoadRemoteCachesOnStartup(true);
        redisCacheManager.setDefaultExpiration(ascentCacheProperties.getDefaultExpires());
        redisCacheManager.setExpires(ascentCacheProperties.getExpires());
    }
}