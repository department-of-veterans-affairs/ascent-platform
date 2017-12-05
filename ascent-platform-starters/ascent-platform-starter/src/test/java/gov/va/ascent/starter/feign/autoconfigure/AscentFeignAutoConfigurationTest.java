package gov.va.ascent.starter.feign.autoconfigure;

import feign.Feign;
import gov.va.ascent.starter.security.autoconfigure.AscentSecurityAutoConfiguration;
import org.junit.After;
import org.junit.Test;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerPropertiesAutoConfiguration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by rthota on 8/24/17.
 */
public class AscentFeignAutoConfigurationTest {

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
        context.register(SecurityAutoConfiguration.class, ServerPropertiesAutoConfiguration.class, AscentSecurityAutoConfiguration.class,
        		TokenFeignRequestInterceptor.class, AscentFeignAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        TokenFeignRequestInterceptor tokenFeignRequestInterceptor = this.context.getBean(TokenFeignRequestInterceptor.class);
        assertNotNull(tokenFeignRequestInterceptor);

    }

    @Test
    public void testGetterSettingAscentFiegnConfig() throws Exception {
        AscentFeignAutoConfiguration ascentFeignAutoConfiguration = new AscentFeignAutoConfiguration();
        assertEquals("defaultGroup", ascentFeignAutoConfiguration.getGroupKey());
        ascentFeignAutoConfiguration.setGroupKey("NewGroupKey");
        assertEquals("NewGroupKey", ascentFeignAutoConfiguration.getGroupKey());
    } 

    /**
     * Test of feignBuilder method, of class AscentFeignAutoConfiguration.
     */
    @Test
    public void testFeignBuilder() {
        AscentFeignAutoConfiguration instance = new AscentFeignAutoConfiguration();
        Feign.Builder result = instance.feignBuilder();
        assertNotNull(result);
    }
}
