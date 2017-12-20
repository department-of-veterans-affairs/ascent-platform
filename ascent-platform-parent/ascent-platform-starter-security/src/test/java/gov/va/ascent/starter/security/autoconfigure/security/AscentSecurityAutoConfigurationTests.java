package gov.va.ascent.starter.security.autoconfigure.security;

import gov.va.ascent.starter.security.autoconfigure.AscentSecurityAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by vgadda on 7/31/17.
 */
public class AscentSecurityAutoConfigurationTests {

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
        context.register(SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class, AscentSecurityAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(12, this.context.getBean(FilterChainProxy.class).getFilterChains().size());

    }
}
