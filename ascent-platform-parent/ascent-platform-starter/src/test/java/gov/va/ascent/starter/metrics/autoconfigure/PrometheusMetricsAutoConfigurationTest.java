package gov.va.ascent.starter.metrics.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Unit test for PrometheusMetricsAutoConfiguration class.
 * @author jluck
 *
 */
public class PrometheusMetricsAutoConfigurationTest {
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
        context.register(PrometheusMetricsAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(PrometheusMetricsAutoConfiguration.class));

    }

}
