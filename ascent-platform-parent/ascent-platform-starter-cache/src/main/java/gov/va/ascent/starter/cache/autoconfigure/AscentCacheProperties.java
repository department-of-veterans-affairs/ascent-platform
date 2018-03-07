package gov.va.ascent.starter.cache.autoconfigure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix="ascent.cache")
@Configuration
public class AscentCacheProperties {

	static final Logger LOGGER = LoggerFactory.getLogger(AscentCacheProperties.class);

	private List<RedisExpires> expires;

	private Long defaultExpires = 86400L;

	/** redis config properties */
	private RedisConfig redisConfig;

	public void setExpires(List<RedisExpires> expires) {
		this.expires = expires;
	}

	public void setDefaultExpires(Long defaultExpires) {
		this.defaultExpires = defaultExpires;
	}

	public void setRedisConfig(RedisConfig redisConfig) {
		this.redisConfig = redisConfig;
	}

	public List<RedisExpires> getExpires() {
		return this.expires;
	}

	public Long getDefaultExpires() {
		return this.defaultExpires;
	}
	
	public RedisConfig getRedisConfig() {
		return this.redisConfig;
	}

	/** Inner class with Redis specific config properties */
	public static class RedisExpires {

		/** Redis Host */
		private String name;

		/** Redis port */
		private Long ttl;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Long getTtl() {
			return ttl;
		}

		public void setTtl(Long ttl) {
			this.ttl = ttl;
		}
	}

	/** Inner class with Redis specific config properties */
	public static class RedisConfig {

		/** Redis Host */
		private String host;

		/** Redis port */
		private Integer port;

		public void setHost(String host) {
			this.host = host;
		}

		public String getHost() {
			return host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

	}

}

