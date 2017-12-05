package gov.va.ascent.starter.cache.autoconfigure;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ascent.cache")
public class AscentCacheProperties {

    private Map<String, Long> expires;

    private Long defaultExpires = 86400L;
    
    /** redis config properties */
    private RedisConfig redisConfig;

    public Map<String, Long> getExpires() {
        return this.expires;
    }

    public Long getDefaultExpires() {
        return this.defaultExpires;
    }

    public void setExpires(Map<String, Long> expires) {
        this.expires = expires;
    }

    public void setDefaultExpires(Long defaultExpires) {
        this.defaultExpires = defaultExpires;
    }
    
    public RedisConfig getRedisConfig() {
        return redisConfig;
    }

    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }
    
    /** Inner class with Redis specific config properties */
    public static class RedisConfig {
        
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = -8570546403206572670L;

        /** Redis Host */
        private String host;

        /** Redis port */
        private Integer port;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

    }
}

