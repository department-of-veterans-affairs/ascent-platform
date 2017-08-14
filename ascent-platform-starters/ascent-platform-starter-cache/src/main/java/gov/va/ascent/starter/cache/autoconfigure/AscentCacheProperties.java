package gov.va.ascent.starter.cache.autoconfigure;

/**
 * Created by vgadda on 8/11/17.
 */
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix="ascent.cache")
public class AscentCacheProperties {

    private boolean enabled = false;

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

