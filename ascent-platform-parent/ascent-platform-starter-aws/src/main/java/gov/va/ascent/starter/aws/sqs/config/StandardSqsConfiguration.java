package gov.va.ascent.starter.aws.sqs.config;

import javax.jms.ConnectionFactory;

public class StandardSqsConfiguration extends AbstractSqsConfiguration {

	@Override
	public ConnectionFactory connectionFactory(final SqsProperties sqsProperties) {
		return createStandardSQSConnectionFactory(sqsProperties);
	}

}
