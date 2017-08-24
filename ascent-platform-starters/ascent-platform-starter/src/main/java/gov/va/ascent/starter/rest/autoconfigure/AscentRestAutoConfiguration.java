package gov.va.ascent.starter.rest.autoconfigure;


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


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class AscentRestAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestProviderHttpResponseCodeAspect restProviderHttpResponseCodeAspect(){
        return new RestProviderHttpResponseCodeAspect();
    }    
    
    @Bean
    @ConditionalOnMissingBean
    public RestProviderTimerAspect sestProviderTimerAspect(){
        return new RestProviderTimerAspect();
    }    
    
}


