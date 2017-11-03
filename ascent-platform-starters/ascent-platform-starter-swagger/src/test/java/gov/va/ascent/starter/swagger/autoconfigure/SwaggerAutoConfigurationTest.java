package gov.va.ascent.starter.swagger.autoconfigure;

import com.fasterxml.classmate.TypeResolver;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class SwaggerAutoConfigurationTest {


    @Mock
    private SwaggerProperties swaggerProperties = new SwaggerProperties();

    @Mock
    private TypeResolver typeResolver;

    @InjectMocks
    SwaggerAutoConfiguration swaggerAutoConfiguration;

    @Test
    public void swaggerAutoConfigurationTest() throws Exception {
        Docket docket = swaggerAutoConfiguration.categoryApi();
        assertEquals("default", docket.getGroupName());
        DocumentationType documentationType = docket.getDocumentationType();
        assertEquals("swagger", documentationType.getName());
        assertEquals("2.0", documentationType.getVersion());
        assertEquals("application", documentationType.getMediaType().getType());
        assertEquals("json", documentationType.getMediaType().getSubtype());
        assertEquals(0, documentationType.getMediaType().getParameters().size());
        assertNotNull(swaggerAutoConfiguration.categoryApi());
    }



}
