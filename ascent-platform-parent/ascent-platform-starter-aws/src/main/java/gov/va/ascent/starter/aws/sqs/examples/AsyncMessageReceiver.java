package gov.va.ascent.starter.aws.sqs.examples;

import java.util.concurrent.TimeUnit;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.Base64;

public class AsyncMessageReceiver implements MessageListener {

	private Logger logger = LoggerFactory.getLogger(AsyncMessageReceiver.class);

	// Used to listen for message silence
	private volatile long timeOfLastMessage = System.nanoTime();

	public void waitForOneMinuteOfSilence() throws InterruptedException {
		for(;;) {
			long timeSinceLastMessage = System.nanoTime() - timeOfLastMessage;
			long remainingTillOneMinuteOfSilence = 
					TimeUnit.MINUTES.toNanos(1) - timeSinceLastMessage;
			if( remainingTillOneMinuteOfSilence < 0 ) {
				break;
			}
			TimeUnit.NANOSECONDS.sleep(remainingTillOneMinuteOfSilence);
		}
	}

	@Override
	public void onMessage(Message message) {
		try {
			handleMessage(message);
			message.acknowledge();
			logger.info( "Acknowledged message " + message.getJMSMessageID() );
			timeOfLastMessage = System.nanoTime();
		} catch (JMSException e) {
			logger.error( "Error processing message: " + e.getMessage() );
			e.printStackTrace();
		}
	}

	private void handleMessage(Message message) throws JMSException {
		logger.info( "Got message " + message.getJMSMessageID() );
		logger.info( "Content: ");
		if( message instanceof TextMessage ) {
			TextMessage txtMessage = ( TextMessage ) message;
			logger.info( "\t" + txtMessage.getText() );
		} else if( message instanceof BytesMessage ){
			BytesMessage byteMessage = ( BytesMessage ) message;
			// Assume the length fits in an int - SQS only supports sizes up to 256k so that
			// should be true
			byte[] bytes = new byte[(int)byteMessage.getBodyLength()];
			byteMessage.readBytes(bytes);
			logger.info( "\t" +  Base64.encodeAsString( bytes ) );
		} else if( message instanceof ObjectMessage ) {
			ObjectMessage objMessage = (ObjectMessage) message;
			logger.info( "\t" + objMessage.getObject() );
		}
	}
}
