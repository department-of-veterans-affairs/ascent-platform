package gov.va.ascent.starter.aws.autoconfigure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.util.IOUtils;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.TestUtils;
import cloud.localstack.docker.LocalstackDockerTestRunner;
import cloud.localstack.docker.annotation.LocalstackDockerProperties;

@Ignore
@RunWith(LocalstackDockerTestRunner.class)
@LocalstackDockerProperties(randomizePorts = true)
public class TestIntegrationMyDockerCloudAppTest {

	@Test
	public void testS3() throws Exception {
		AmazonS3 client = DockerTestUtils.getClientS3();

		client.createBucket("test-bucket");
		List<Bucket> bucketList = client.listBuckets();

		assertThat(bucketList.size(), is(1));

		File file = File.createTempFile("localstack", "s3");
		file.deleteOnExit();

		try(FileOutputStream stream = new FileOutputStream(file)) {
			String content = "HELLO WORLD!";
			stream.write(content.getBytes());
		}

		PutObjectRequest request = new PutObjectRequest("test-bucket", "testData", file);
		client.putObject(request);

		ObjectListing listing = client.listObjects("test-bucket");
		assertThat(listing.getObjectSummaries().size(), is(1));

		S3Object s3Object = client.getObject("test-bucket", "testData");
		String resultContent = IOUtils.toString(s3Object.getObjectContent());

		assertThat(resultContent, is("HELLO WORLD!"));
	}
	
	@Test
    public void testSQS() throws Exception {
        AmazonSQS client = DockerTestUtils.getClientSQS();

        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("DelaySeconds", "0");
        attributeMap.put("MaximumMessageSize", "262144");
        attributeMap.put("MessageRetentionPeriod", "1209600");
        attributeMap.put("ReceiveMessageWaitTimeSeconds", "20");
        attributeMap.put("VisibilityTimeout", "30");

        CreateQueueRequest createQueueRequest = new CreateQueueRequest("test-queue").withAttributes(attributeMap);
        client.createQueue(createQueueRequest);

        ListQueuesResult listQueuesResult = client.listQueues();
        assertThat(listQueuesResult.getQueueUrls().size(), is(1));

        SQSConnection connection = createSQSConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue queue = session.createQueue("test-queue");

        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("Hello World!");
        producer.send(message);

        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage received = (TextMessage) consumer.receive();
        assertThat(received.getText(), is ("Hello World!"));
    }


    private SQSConnection createSQSConnection() throws Exception {
    	AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
    	.withEndpointConfiguration(new EndpointConfiguration (LocalstackDockerTestRunner.getLocalstackDocker().getEndpointSQS(), "us-west-1"))
    	.withCredentials(new AWSStaticCredentialsProvider(TestUtils.TEST_CREDENTIALS)).build();
    	SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
    	        new ProviderConfiguration(),
    	        amazonSQS
    	        );
        return  connectionFactory.createConnection();
    }
}
