package gov.va.ascent.starter.audit.autoconfigure;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.framework.rest.provider.RestProviderHttpResponseCodeAspect;
import gov.va.ascent.framework.rest.provider.RestProviderTimerAspect;
import gov.va.ascent.framework.service.ServiceExceptionHandlerAspect;
import gov.va.ascent.framework.service.ServiceTimerAspect;
import gov.va.ascent.framework.service.ServiceValidationToMessageAspect;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class AscentAuditAutoConfiguration {

	/*    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "ascent.security.jwt", name = "enabled", matchIfMissing = true)
    public TokenResource tokenResource(){
        return new TokenResource();
    }*/ 
}


