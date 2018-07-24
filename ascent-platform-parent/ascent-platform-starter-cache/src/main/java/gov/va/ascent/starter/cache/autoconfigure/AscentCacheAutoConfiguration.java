package gov.va.ascent.starter.cache.autoconfigure;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.util.CollectionUtils;

import gov.va.ascent.framework.log.LogUtil;
import gov.va.ascent.starter.cache.autoconfigure.AscentCacheProperties.RedisExpires;
import gov.va.ascent.starter.cache.server.AscentEmbeddedRedisServer;

/**
 * Created by vgadda on 8/11/17.
 */
@Configuration
@EnableConfigurationProperties(AscentCacheProperties.class)
@AutoConfigureAfter(CacheAutoConfiguration.class)
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class AscentCacheAutoConfiguration extends CachingConfigurerSupport {

	static final Logger LOGGER = LoggerFactory.getLogger(AscentCacheAutoConfiguration.class);

	/**
	 * Cache properties
	 */
	@Autowired
	private AscentCacheProperties ascentCacheProperties;

	/**
	 * Embedded Redis bean to make sure embedded redis is started before redis cache is created
	 */
	@SuppressWarnings("unused")
	@Autowired(required = false)
	private AscentEmbeddedRedisServer ascentServerRedisEmbedded;

	@Bean
	public CacheManagerCustomizers cacheManagerCustomizers() {
		return new CacheManagerCustomizers(Arrays.asList(new RedisCacheManagerCustomizer(ascentCacheProperties)));
	}

	/**
	 * Redis template
	 *
	 * @param redisConnectionFactory redis connection factory
	 * @return Redis template
	 */
	@Bean
	public RedisTemplate<Object, Object> redisTemplate(
			final RedisConnectionFactory redisConnectionFactory) {
		final RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return new KeyGenerator() { // NOSONAR lambda expressions do not accept optional params
			@Override
			public Object generate(final Object o, final Method method, final Object... objects) {
				LOGGER.debug("Generating cacheKey");
				final StringBuilder sb = new StringBuilder();
				sb.append(o.getClass().getName()).append(ClassUtils.PACKAGE_SEPARATOR_CHAR);
				sb.append(method.getName());
				for (final Object obj : objects) {
					sb.append(ClassUtils.PACKAGE_SEPARATOR_CHAR).append(obj.toString());
				}
				LOGGER.debug("Generated cacheKey: {}", sb.toString());
				return sb.toString();
			}
		};
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new RedisCacheErrorHandler();
	}

	public static class RedisCacheErrorHandler implements CacheErrorHandler {

		@Override
		public void handleCacheGetError(final RuntimeException exception, final Cache cache, final Object key) {
			LogUtil.logErrorWithBanner(LOGGER, "Unable to get from cache " + cache.getName(), exception.getMessage());
		}

		@Override
		public void handleCachePutError(final RuntimeException exception, final Cache cache, final Object key, final Object value) {
			LogUtil.logErrorWithBanner(LOGGER, "Unable to put into cache " + cache.getName(), exception.getMessage());
		}

		@Override
		public void handleCacheEvictError(final RuntimeException exception, final Cache cache, final Object key) {
			LogUtil.logErrorWithBanner(LOGGER, "Unable to evict from cache " + cache.getName(), exception.getMessage());
		}

		@Override
		public void handleCacheClearError(final RuntimeException exception, final Cache cache) {
			LogUtil.logErrorWithBanner(LOGGER, "Unable to clean cache " + cache.getName(), exception.getMessage());
		}
	}
}

class RedisCacheManagerCustomizer implements CacheManagerCustomizer<RedisCacheManager> {

	static final Logger LOGGER = LoggerFactory.getLogger(RedisCacheManagerCustomizer.class);

	private final AscentCacheProperties ascentCacheProperties;

	public RedisCacheManagerCustomizer(final AscentCacheProperties ascentCacheProperties) {
		this.ascentCacheProperties = ascentCacheProperties;
	}

	@Override
	public void customize(final RedisCacheManager redisCacheManager) {
		redisCacheManager.setLoadRemoteCachesOnStartup(true);
		redisCacheManager.setDefaultExpiration(ascentCacheProperties.getDefaultExpires());
		if (!CollectionUtils.isEmpty(ascentCacheProperties.getExpires())) {
			// key = name, value - TTL
			final Map<String, Long> resultExpires = ascentCacheProperties.getExpires()
					.stream()
					.filter(o -> o.getName() != null)
					.filter(o -> o.getTtl() != null)
					.collect(Collectors.toMap(RedisExpires::getName, RedisExpires::getTtl));
			redisCacheManager.setExpires(resultExpires);

			LOGGER.info("Cache Expires: {}", resultExpires);
		}
	}
}