package gov.va.ascent.starter.cache.server;

import java.util.HashMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.starter.cache.autoconfigure.AscentCacheProperties;
import gov.va.ascent.starter.cache.autoconfigure.AscentCacheProperties.RedisConfig;

/**
 * 
 * @author rthota
 *
 */
@Configuration
public class AscentEmbeddedRedisServerAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AscentEmbeddedRedisServer ascentEmbeddedRedisServer() {
		return new AscentEmbeddedRedisServer();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AscentCacheProperties ascentCacheProperties() {
		AscentCacheProperties ascentCacheProperties = new AscentCacheProperties();
		ascentCacheProperties.setRedisConfig(new RedisConfig());
		ascentCacheProperties.getRedisConfig().setHost("localhost");
		//ascentCacheProperties.getRedisConfig().setPort(6379);
		ascentCacheProperties.setExpires(new HashMap<>());
		ascentCacheProperties.setDefaultExpires(500L);
		return  ascentCacheProperties;
	}
}