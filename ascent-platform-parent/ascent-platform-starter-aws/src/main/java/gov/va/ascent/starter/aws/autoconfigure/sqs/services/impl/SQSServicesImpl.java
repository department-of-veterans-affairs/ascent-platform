package gov.va.ascent.starter.aws.autoconfigure.sqs.services.impl;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import gov.va.ascent.starter.aws.autoconfigure.sqs.services.SQSServices;


@Service
public class SQSServicesImpl implements SQSServices {
	
	private Logger logger = LoggerFactory.getLogger(SQSServicesImpl.class);
	
	@Resource
	JmsOperations jmsOperations;
	

	@Override
	@ManagedOperation
	public ResponseEntity<String> sendMessage(String request) {
			logger.info("Handling request: '{}'", request);
			
		    final String messageId = jmsOperations.execute(new ProducerCallback<String>() {
				@Override
				public String doInJms(Session session, MessageProducer producer) throws JMSException {
					 final TextMessage message = session.createTextMessage(request);
			            producer.send(message);
			            logger.debug("Sent JMS message with payload='{}', id: '{}'", request, message.getJMSMessageID());
			            return message.getJMSMessageID();
				}
		    });
		    
		    return new ResponseEntity<>(messageId, HttpStatus.OK);
	}
	
    /* @JmsListener(destination = "")
    public void receiveMessage(@Payload String message) {
      logger.info("Received message {}.", message);
      //jmsOperations
    } */
}
