package gov.va.ascent.starter.aws.sqs.services.impl;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;

import gov.va.ascent.framework.util.Defense;
import gov.va.ascent.starter.aws.sqs.services.SqsService;

@Service
public class SqsServiceImpl implements SqsService {

	private Logger logger = LoggerFactory.getLogger(SqsServiceImpl.class);

	@Resource
	JmsOperations jmsOperations;

	@Autowired
	ConnectionFactory connectionFactory;


	/**
	 * Sends the message to the main queue.
	 */
	@Override
	@ManagedOperation
	public ResponseEntity<String> sendMessage(Message message) {
		Defense.notNull(message, "Message can't be null");
		
		final String messageId = jmsOperations.execute(new ProducerCallback<String>() {
			@Override
			public String doInJms(Session session, MessageProducer producer) throws JMSException {
				message.setJMSTimestamp(System.currentTimeMillis());
				producer.send(message);
				logger.info("Sent JMS message with payload='{}', id: '{}'", message, message.getJMSMessageID());
				return message.getJMSMessageID();
			}
		});

		return new ResponseEntity<>(messageId, HttpStatus.OK);
	}

	/**
	 * Creates a TextMessage
	 */
	public TextMessage createTextMessage(String message) {
		Defense.notNull(message, "Message can't be null");
		try {
			return connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
					.createTextMessage(message);
		} catch (JMSException e) {
			logger.error("Error Message: {}", e);
		}
		return null;
	}
}
