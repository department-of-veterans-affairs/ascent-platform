package gov.va.ascent.starter.aws.sqs.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.starter.aws.exception.SqsException;
import gov.va.ascent.starter.aws.s3.dto.SendMessageResponse;
import gov.va.ascent.starter.aws.sqs.services.SqsService;

@RunWith(MockitoJUnitRunner.class)
public class SqsServiceImplTest {

	@Mock
	JmsOperations jmsOperations;

	@Mock
	ConnectionFactory connectionFactory;

	@Mock
	Connection connection;

	@Autowired
	@InjectMocks
	private SqsService sqsService = new SqsServiceImpl();

	TextMessage mockTextMessage;

	@Before
	public void setUp() throws Exception {
		prepareSqsMock();
		final AscentLogger logger = AscentLoggerFactory.getLogger(SqsServiceImpl.class);
		logger.setLevel(Level.DEBUG);
	}

	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(jmsOperations, FieldUtils.readField(sqsService, "jmsOperations", true));
	}

	@Test
	public void testSendMessageWithMessageObject() throws Exception {
		SendMessageResponse response = sqsService.sendMessage(mockTextMessage);
		assertNotNull(response);
		assertNotNull(response.getStatusCode());

	}
	
	@Test(expected=SqsException.class)
	public void testSendMessageWithMessageIDNullException() throws Exception {
		when(mockTextMessage.getJMSMessageID()).thenReturn(null);
		sqsService.sendMessage(mockTextMessage);
	}
	
	@Test(expected=SqsException.class)
	public void testSendMessageWithMessageNull() throws Exception {
		sqsService.sendMessage(null);
	}

	@Test
	public void testCreateTextMessage() throws JMSException {
		TextMessage response = sqsService.createTextMessage("Test-Message");
		assertNotNull(response);
		assertEquals("Test-Message", response.getText());
	}

	@SuppressWarnings("unchecked")
	private void prepareSqsMock() throws Exception {
		Session mockSession = mock(Session.class);
		TemporaryQueue mockTemporaryQueue = mock(TemporaryQueue.class);
		mockTextMessage = mock(TextMessage.class);
		MessageProducer mockMessageProducer = mock(MessageProducer.class);

		String content = "Test-Message";
		String messageId = "Test-Message-ID";

		/**
		 * Boiler plate mock code to inject mock session and messageProducer into
		 * Generic Spring ProducerCallback class which has our internal code
		 * To test
		 **/
		when(jmsOperations.execute((ProducerCallback<String>) anyObject())).thenAnswer(
				new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						Object[] args = invocation.getArguments();
						ProducerCallback<String> pc = (ProducerCallback<String>) args[0];
						return pc.doInJms(mockSession, mockMessageProducer);
					}
				});

		when(mockSession.createTemporaryQueue()).thenReturn(mockTemporaryQueue);
		when(mockSession.createTextMessage(content)).thenReturn(mockTextMessage);
		when(mockTextMessage.getText()).thenReturn(content);
		when(mockTextMessage.getJMSMessageID()).thenReturn(messageId);

		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenReturn(mockTextMessage);
	}

	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithJmsException() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(JMSException.class);
		sqsService.createTextMessage("Test-Message");
	
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithNullMessage() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(JMSException.class);
		sqsService.createTextMessage(null);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithException() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(Exception.class);
		sqsService.createTextMessage("Test-Message");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithSqsException() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(new SqsException());
		sqsService.createTextMessage("Test-Message");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithSqsExceptionWithMessage() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(new SqsException("Test Sqs Exception"));
		sqsService.createTextMessage("Test-Message");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithSqsExceptionWithMessageThrowable() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(new SqsException("Test Sqs Exception", new Throwable()));
		sqsService.createTextMessage("Test-Message");
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=SqsException.class)
	public void testCreateTextMessageWithSqsExceptionWithThrowable() throws JMSException {
		Session mockSession = mock(Session.class);
		when(connectionFactory.createConnection()).thenReturn(connection);
		when(connection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
		when(mockSession.createTextMessage(anyString())).thenThrow(new SqsException(new Throwable()));
		sqsService.createTextMessage("Test-Message");
	}
	
}
