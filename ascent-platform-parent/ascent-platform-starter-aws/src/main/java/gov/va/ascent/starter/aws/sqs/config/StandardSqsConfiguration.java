package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;

@Configuration
@Profile(AscentCommonSpringProfiles.NOT_PROFILE_EMBEDDED_AWS)
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

  @Bean
  @Override
  public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
    return createStandardSQSConnectionFactory(sqsProperties);
  }

}
