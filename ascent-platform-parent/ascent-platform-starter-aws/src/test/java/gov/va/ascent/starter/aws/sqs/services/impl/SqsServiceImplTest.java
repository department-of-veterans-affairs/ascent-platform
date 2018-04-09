package gov.va.ascent.starter.aws.sqs.services.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.test.util.ReflectionTestUtils;

import gov.va.ascent.starter.aws.sqs.services.SqsService;


@RunWith(MockitoJUnitRunner.class)
public class SqsServiceImplTest {

	private Logger logger = LoggerFactory.getLogger(SqsServiceImplTest.class);

	@Mock
	JmsOperations jmsOperations;

	@Autowired
	private SqsService sqsService;


	@Before
	public void setUp() throws Exception {
		sqsService = new SqsServiceImpl();
		prepareSqsMock();
		ReflectionTestUtils.setField(sqsService, "jmsOperations", jmsOperations);
	}
	
	@Test
	public void testFields() throws Exception {
		Assert.assertEquals(jmsOperations, FieldUtils.readField(sqsService, "jmsOperations", true));
	}
	
	@Test
	public void testSendMessage() throws Exception {
		ResponseEntity<String> response = sqsService.sendMessage("Test-Message");
		assertNotNull(response);
		assertNotNull(response.getStatusCode());
		assertNotNull(response.getStatusCodeValue());
		logger.info("response.getStatusCode(): {}", response.getStatusCode());
		logger.info("response: {}", response);
	}
	
	private void prepareSqsMock() throws Exception {
		Session mockSession = mock(Session.class);
		TemporaryQueue mockTemporaryQueue = mock(TemporaryQueue.class);
		TextMessage mockTextMessage = mock(TextMessage.class);
		MessageProducer mockMessageProducer = mock(MessageProducer.class);
		
		String content = "Test-Message";
		String messageId = "Test-Message-ID";
		
		/** Boiler plate mock code to inject mock session and messageProducer into
	    *  Generic Spring ProducerCallback class which has our internal code
	    *  To test
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
	     
	}
}
