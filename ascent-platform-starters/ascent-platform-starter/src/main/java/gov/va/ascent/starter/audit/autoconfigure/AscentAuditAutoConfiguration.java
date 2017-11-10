package gov.va.ascent.starter.audit.autoconfigure;


import gov.va.ascent.framework.audit.RequestResponseLogSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.framework.audit.RequestResponseAspect;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
@EnableAsync
public class AscentAuditAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestResponseAspect requestResponseAspect(){
        return new RequestResponseAspect();
    }

    @Bean
    public RequestResponseLogSerializer requestResponseAsyncLogging() {
        return new RequestResponseLogSerializer();
    }
}


