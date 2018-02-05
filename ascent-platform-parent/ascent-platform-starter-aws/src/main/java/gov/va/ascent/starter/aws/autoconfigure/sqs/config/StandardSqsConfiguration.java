package gov.va.ascent.starter.aws.autoconfigure.sqs.config;

import javax.jms.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lcsontos on 8/4/17.
 */
@Configuration
public class StandardSqsConfiguration extends AbstractSqsConfiguration {

  @Bean
  @Override
  public ConnectionFactory connectionFactory(SqsProperties sqsProperties) {
    return createStandardSQSConnectionFactory(sqsProperties);
  }

}
