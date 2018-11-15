package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

	@Bean
	@Override
	public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
		return createStandardSQSConnectionFactory(sqsProperties);
	}

}
