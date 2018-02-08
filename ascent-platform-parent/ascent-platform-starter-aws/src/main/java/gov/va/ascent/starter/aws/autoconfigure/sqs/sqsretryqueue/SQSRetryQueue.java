package gov.va.ascent.starter.aws.autoconfigure.sqs.sqsretryqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSRetryQueue {

	private AmazonSQSClient sqs;
	private String sqsQueueName;
	private String queueUrl;
	private Regions region;
	private int visibilityTimeout = 10;
	private int messageReceiverThreads = 1;
	private volatile boolean listening = true;
	private Object monitor = new Object();
	private Consumer<String> messageConsumer;

	public String getSqsQueueName() {
		return sqsQueueName;
	}

	public void setSqsQueueName(String sqsQueueName) {
		this.sqsQueueName = sqsQueueName;
	}

	@PostConstruct
	public void init() {
		initQueue();
		startMessageReceivers();

	}

	private void initQueue() {
		this.sqs = new AmazonSQSClient(); // Do we need to use new
		// ClientConfiguration().withMaxConnections(256)
		// ?
		this.sqs.configureRegion(region);
		try {
			// Check to see if queue exists
			GetQueueUrlResult queueUrlResult = this.sqs.getQueueUrl(getSqsQueueName());
			this.queueUrl = queueUrlResult.getQueueUrl();
		} catch (QueueDoesNotExistException queueDoesNotExist) {
			// Queue does not exist, need to create one
			CreateQueueRequest createQueueRequest = new CreateQueueRequest();
			createQueueRequest.setQueueName(getSqsQueueName());
			createQueueRequest.addAttributesEntry("VisibilityTimeout", "" + getVisibilityTimeout());
			CreateQueueResult createQueueResult = this.sqs.createQueue(createQueueRequest);
			this.queueUrl = createQueueResult.getQueueUrl();
		}
	}

	private void startMessageReceivers() {
		for (int i = 0; i < this.messageReceiverThreads; i++) {
			String threadName = sqsQueueName + "#" + i;
			Thread receiverThread = new Thread() {
				public void run() {
					Thread.currentThread().setName(threadName);
					doReceive();
				}
			};
			receiverThread.setDaemon(true);
			receiverThread.start();
		}

	}

	protected void doReceive() {
		// This is where the interesting stuff happens
		while (isListening()) {
			synchronized (this.monitor) {
				try {
					this.monitor.wait(this.getVisibilityTimeout() * 1000);
				} catch (InterruptedException e) {

				}
			}
			boolean messagesReceived = false;
			do {
				ReceiveMessageRequest request = new ReceiveMessageRequest().withQueueUrl(this.queueUrl)
						.withWaitTimeSeconds(1).withMaxNumberOfMessages(10);
				ReceiveMessageResult result = sqs.receiveMessage(request);
				List<Message> messages = result.getMessages();
				messagesReceived = messages.size() > 0;
				if (!messagesReceived) {
					break;
				}
				List<DeleteMessageBatchRequestEntry> deletes = new ArrayList<DeleteMessageBatchRequestEntry>();
				for (Message message : messages) {
					String messageBody = message.getBody();
					try {
						this.messageConsumer.accept(messageBody);
						DeleteMessageBatchRequestEntry entry = new DeleteMessageBatchRequestEntry(
								UUID.randomUUID().toString(), message.getReceiptHandle());
						deletes.add(entry);
					} catch (Throwable exp) {
						Logger.getLogger(getSqsQueueName()).log(Level.WARNING,
								"Could not process message: " + messageBody, exp);
					}
				}
				if (!deletes.isEmpty()) {
					DeleteMessageBatchRequest deleteBatch = new DeleteMessageBatchRequest(this.queueUrl, deletes);
					sqs.deleteMessageBatch(deleteBatch);
				}
			} while (messagesReceived);
		}

	}

	public void sendMessage(String messageBody) {
		SendMessageRequest smr = new SendMessageRequest().withQueueUrl(this.queueUrl).withMessageBody(messageBody);
		this.sqs.sendMessage(smr);

		synchronized (monitor) {
			monitor.notify();
		}
	}

	public Regions getRegion() {
		return region;
	}

	public void setRegion(Regions region) {
		this.region = region;
	}

	public int getVisibilityTimeout() {
		return visibilityTimeout;
	}

	public void setVisibilityTimeout(int visibilityTimeout) {
		this.visibilityTimeout = visibilityTimeout;
	}

	public int getMessageReceiverThreads() {
		return messageReceiverThreads;
	}

	public void setMessageReceiverThreads(int messageReceiverThreads) {
		this.messageReceiverThreads = messageReceiverThreads;
	}

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	public void startListening() {
		this.setListening(true);
	}

	public void stopListening() {
		this.setListening(false);
	}

	public Consumer<String> getMessageConsumer() {
		return messageConsumer;
	}

	public void setMessageConsumer(Consumer<String> consumer) {
		this.messageConsumer = consumer;
	}
}
