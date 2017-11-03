package gov.va.ascent.starter.feign.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import feign.Feign;
import feign.hystrix.HystrixFeign;
import feign.hystrix.SetterFactory;

/**
 * Created by rthota on 9/05/17.
 */

@Configuration
public class AscentFeignAutoConfiguration {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AscentFeignAutoConfiguration.class);

	private String groupKey = "defaultGroup";

	@Autowired
	public TokenFeignRequestInterceptor feignRequestInterceptor;

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}
	
    @Bean
    @ConditionalOnMissingBean
    public TokenFeignRequestInterceptor tokenFeignRequestInterceptor(){
        return new TokenFeignRequestInterceptor();
    } 
    
    @Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	@ConditionalOnProperty(name = "feign.hystrix.enabled", matchIfMissing = true)
	public Feign.Builder feignBuilder() {
	  SetterFactory commandKeyIsRequestLine = (target, method) -> {
		String commandKey =  Feign.configKey(target.type(), method);
	    LOGGER.debug("Feign Hystrix Group Key: {}", groupKey);
	    LOGGER.debug("Feign Hystrix Command Key: {}", commandKey);
	    return HystrixCommand.Setter
	      .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
	      .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey));
	  };
	  return HystrixFeign.builder().setterFactory(commandKeyIsRequestLine).requestInterceptor(feignRequestInterceptor);
	}

}    

