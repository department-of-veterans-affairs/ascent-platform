package gov.va.ascent.starter.rest.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import gov.va.ascent.framework.rest.client.resttemplate.RestClientTemplate;
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
    public RestProviderTimerAspect restProviderTimerAspect(){
        return new RestProviderTimerAspect();
    }    
    
    @Bean
    @ConditionalOnMissingBean
    public RestClientTemplate restClientTemplate(){
        return new RestClientTemplate();
    }    
        
	@Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplateBuilder().interceptors(tokenClientHttpRequestInterceptor()).build();
	}
	
    @Bean
    @ConditionalOnMissingBean
    public TokenClientHttpRequestInterceptor tokenClientHttpRequestInterceptor(){
        return new TokenClientHttpRequestInterceptor();
    }    
}


