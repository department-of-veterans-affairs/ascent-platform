package gov.va.ascent.starter.audit.autoconfigure;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.framework.audit.RequestResponseAspect;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class AscentAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestResponseAspect requestResponseAspect(){
        return new RequestResponseAspect();
    } 
}


