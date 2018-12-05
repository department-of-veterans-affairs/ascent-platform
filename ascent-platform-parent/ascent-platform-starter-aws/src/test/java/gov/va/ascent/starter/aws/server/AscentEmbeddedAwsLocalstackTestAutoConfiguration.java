package gov.va.ascent.starter.aws.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.starter.aws.server.AscentAwsLocalstackProperties.Services;
import gov.va.ascent.starter.aws.sqs.config.SqsProperties;

/**
 * 
 * @author akulkarni
 *
 */
@Configuration
public class AscentEmbeddedAwsLocalstackTestAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AscentEmbeddedAwsLocalstackApplication ascentEmbeddedAwsServers() {
		return new AscentEmbeddedAwsLocalstackApplication();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SqsProperties sqsProperties() {
		SqsProperties sqsProperties = new SqsProperties();
		sqsProperties.setEndpoint("https://localhost/180197991925/evssstandardqueue");
		sqsProperties.setDlqendpoint("https://localhost/180197991925/evssdeadletterqueue");
		return sqsProperties;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AscentAwsLocalstackProperties ascentAwsLocalstackProperties() {
		AscentAwsLocalstackProperties ascentAwsLocalstackProperties = new AscentAwsLocalstackProperties();
		List<Services> services = new ArrayList<>();
		
		AscentAwsLocalstackProperties.Services service = new AscentAwsLocalstackProperties().new Services();
		service.setName("s3");
		service.setPort(4572);
		services.add(service);
		
		service = new AscentAwsLocalstackProperties().new Services();
		service.setName("sqs");
		service.setPort(4576);
		services.add(service);
		ascentAwsLocalstackProperties.setServices(services);
		
		return  ascentAwsLocalstackProperties;
	}
	
}