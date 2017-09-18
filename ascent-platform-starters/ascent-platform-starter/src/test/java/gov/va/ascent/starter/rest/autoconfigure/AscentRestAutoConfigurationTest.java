package gov.va.ascent.starter.rest.autoconfigure;

import gov.va.ascent.framework.rest.provider.RestProviderHttpResponseCodeAspect;
import gov.va.ascent.starter.security.autoconfigure.AscentSecurityAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.Assert.*;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
/**
 * Created by rthota on 8/24/17.
 */

public class AscentRestAutoConfigurationTest {

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
        context.register( SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class, AscentSecurityAutoConfiguration.class,
                AscentSecurityAutoConfiguration.class, AscentRestAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(RestProviderHttpResponseCodeAspect.class));
    }
     
}
