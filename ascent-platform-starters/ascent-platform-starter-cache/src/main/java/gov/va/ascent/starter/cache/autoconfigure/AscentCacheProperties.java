package gov.va.ascent.starter.cache.autoconfigure;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ascent.cache")
public class AscentCacheProperties {

    private Map<String, Long> expires;

    private Long defaultExpires = 86400L;

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
}

