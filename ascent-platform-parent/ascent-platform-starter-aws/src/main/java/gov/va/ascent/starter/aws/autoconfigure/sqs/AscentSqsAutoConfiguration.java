package gov.va.ascent.starter.aws.autoconfigure.sqs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import gov.va.ascent.starter.aws.autoconfigure.sqs.config.AbstractSqsConfiguration;
import gov.va.ascent.starter.aws.autoconfigure.sqs.config.ExtendedSqsConfiguration;
import gov.va.ascent.starter.aws.autoconfigure.sqs.config.SqsProperties;
import gov.va.ascent.starter.aws.autoconfigure.sqs.config.StandardSqsConfiguration;
import gov.va.ascent.starter.aws.autoconfigure.sqs.services.SQSServices;
import gov.va.ascent.starter.aws.autoconfigure.sqs.services.impl.SQSServicesImpl;



/**
 * Created by akulkarni on 2/1/18.
 */

@Configuration
@EnableConfigurationProperties(SqsProperties.class)
@Import({AbstractSqsConfiguration.class, StandardSqsConfiguration.class, ExtendedSqsConfiguration.class})
public class AscentSqsAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public SQSServices sqsServices(){
		return new SQSServicesImpl();
	}
	
}


