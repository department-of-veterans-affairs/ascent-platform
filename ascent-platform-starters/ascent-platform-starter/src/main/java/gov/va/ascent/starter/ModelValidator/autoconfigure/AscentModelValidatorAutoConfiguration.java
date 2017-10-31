package gov.va.ascent.starter.ModelValidator.autoconfigure;

import gov.va.ascent.framework.validation.ModelValidator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by rthota on 8/24/17.
 */

@Configuration
public class AscentModelValidatorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ModelValidator modelValidator(){
        return new ModelValidator();
    }

}


