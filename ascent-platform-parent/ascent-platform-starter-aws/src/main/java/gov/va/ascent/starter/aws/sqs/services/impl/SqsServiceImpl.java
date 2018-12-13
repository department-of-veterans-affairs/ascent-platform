package gov.va.ascent.starter.aws.sqs.services.impl;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.util.Defense;
import gov.va.ascent.starter.aws.exception.SqsException;
import gov.va.ascent.starter.aws.s3.dto.SendMessageResponse;
import gov.va.ascent.starter.aws.sqs.services.SqsService;

/**
 * 
 * @author srikanthvanapalli
 * 
 * How to set the Queue attributes programatically ?? 
 * 
 * https://docs.aws.amazon.com/AWSSimpleQueueService/latest/APIReference/API_SetQueueAttributes.html
 * We need to send a request using the queue URL with action=SetQueueAttributes
 * and specifying the attributes and their values that need to be set. 
 * 
 * Currently we do an implementation for that action and as per the link above
 * we need to write an API to modify queue attributes. This is not something
 * we do with every queue message that is sent out.
 * 
 * Example request: 
 * https://sqs.us-east-2.amazonaws.com/123456789012/MyQueue/
		  	?Action=SetQueueAttributes
			&DelaySeconds=45
			&Expires=2020-12-20T22%3A52%3A43PST
			&Version=2012-11-05
			&AUTHPARAMS
 * 
 * 
 *
 */
@Service
public class SqsServiceImpl implements SqsService {

	private static final String ERROR_MESSAGE = "Error Message: {}";

	private AscentLogger logger = AscentLoggerFactory.getLogger(SqsServiceImpl.class);

	public static final String MESSAGE_TRANSFER_FAILED = "Message transfer Failed";

	public static final String MESSAGE_CREATE_FAILED = "Message creation Failed";

	private static final String SQS_EXCEPTION_MSG = "SQS Exception: ";

	private static final String SQS_JMS_EXCEPTION_MSG = "SQS JMS Exception: ";

	@Resource
	JmsOperations jmsOperations;

	@Autowired
	ConnectionFactory connectionFactory;

	/**
	 * Sends the message to the main queue.
	 */
	@Override
	@ManagedOperation
	public SendMessageResponse sendMessage(Message message) {

		String messageId = null;
		SendMessageResponse sendMessageResponse = new SendMessageResponse();
		try {
			Defense.notNull(message, "Message can't be null");
			messageId = jmsOperations.execute(new ProducerCallback<String>() {
				@Override
				public String doInJms(Session session, MessageProducer producer) throws JMSException {
					message.setJMSTimestamp(System.currentTimeMillis());
					producer.send(message);
					logger.info("Sent JMS message with payload='{}', id: '{}'", message, message.getJMSMessageID());
					return message.getJMSMessageID();
				}
			});

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(e.getMessage());
			else
				throw new SqsException(SQS_EXCEPTION_MSG + MESSAGE_TRANSFER_FAILED);

		}
		if (messageId == null) {
			logger.error("Error Message: Message ID cannot be null after message has been sent");
			throw new SqsException(SQS_EXCEPTION_MSG + MESSAGE_TRANSFER_FAILED + " - Message ID cannot be null");
		}
		sendMessageResponse.setMessageId(messageId);
		sendMessageResponse.setStatusCode(HttpStatus.OK.toString());
		return sendMessageResponse;
	}

	/**
	 * Creates a TextMessage
	 */
	@Override
	public TextMessage createTextMessage(String message) {

		try {

			Defense.notNull(message, "Message can't be null");
			return connectionFactory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE)
					.createTextMessage(message);

		} catch (JMSException e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(SQS_JMS_EXCEPTION_MSG + e.getMessage());
			else
				throw new SqsException(SQS_JMS_EXCEPTION_MSG + MESSAGE_CREATE_FAILED);

		} catch (Exception e) {
			logger.error(ERROR_MESSAGE, e);
			if (e.getMessage() != null)
				throw new SqsException(e.getMessage());
			else
				throw new SqsException(SQS_EXCEPTION_MSG + MESSAGE_CREATE_FAILED);

		}

	}
}
