package gov.va.ascent.starter.rest.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by rthota on 8/24/17.
 */
public class AscentServiceAutoConfigurationTest {

    private AnnotationConfigWebApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

/*    @Test
    public void testWebConfiguration() throws Exception {
        context = new AnnotationConfigWebApplicationContext();
        context.register(SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class, AscentAuditAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertEquals(this.context.getBean(FilterChainProxy.class).getFilterChains().size(), 11);

    }*/
}
