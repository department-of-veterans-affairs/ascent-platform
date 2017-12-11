package gov.va.ascent.starter.audit.autoconfigure;


import gov.va.ascent.framework.audit.RequestResponseLogSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
@EnableAsync
public class AscentAuditAutoConfiguration {
	
    @Bean
    @ConditionalOnMissingBean
    public RequestResponseLogSerializer requestResponseAsyncLogging() {
        return new RequestResponseLogSerializer();
    }
}


