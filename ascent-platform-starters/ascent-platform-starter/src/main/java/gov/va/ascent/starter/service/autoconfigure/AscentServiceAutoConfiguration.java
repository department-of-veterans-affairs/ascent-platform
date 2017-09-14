package gov.va.ascent.starter.service.autoconfigure;


import gov.va.ascent.framework.service.ServiceExceptionHandlerAspect;
import gov.va.ascent.framework.service.ServiceTimerAspect;
import gov.va.ascent.framework.service.ServiceValidationToMessageAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class AscentServiceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ServiceExceptionHandlerAspect serviceExceptionHandlerAspect(){
        return new ServiceExceptionHandlerAspect();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public ServiceTimerAspect serviceTimerAspect(){
        return new ServiceTimerAspect();
    }    
    
    @Bean
    @ConditionalOnMissingBean
    public ServiceValidationToMessageAspect serviceValidationToMessageAspect(){
        return new ServiceValidationToMessageAspect();
    }   

}


