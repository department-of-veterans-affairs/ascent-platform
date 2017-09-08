package gov.va.ascent.starter.audit.autoconfigure;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import gov.va.ascent.framework.audit.RequestResponseAspect;

import static org.junit.Assert.*;

/**
 * Created by rthota on 8/24/17.
 */
public class AscentAuditAutoConfigurationTest {

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
        context.register(AscentAuditAutoConfiguration.class);
        //context.refresh();
        assertNotNull(context);
        //assertNotNull(this.context.getBean(RequestResponseAspect.class));
    }
}
