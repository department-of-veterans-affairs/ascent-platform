/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ascent.starter.modelvalidator.autoconfigure;

import gov.va.ascent.framework.validation.ModelValidator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 *
 * @author rthota
 */
public class AscentModelValidatorAutoConfigurationTest {
    
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
        context.register(AscentModelValidatorAutoConfiguration.class);
        context.refresh();
        assertNotNull(context);
        assertNotNull(this.context.getBean(AscentModelValidatorAutoConfiguration.class));

    }
    
}
