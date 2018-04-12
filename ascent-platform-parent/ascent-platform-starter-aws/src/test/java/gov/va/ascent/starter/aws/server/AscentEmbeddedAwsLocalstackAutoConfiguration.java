package gov.va.ascent.starter.aws.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.starter.aws.server.AscentAwsLocalstackProperties.Services;

/**
 * 
 * @author akulkarni
 *
 */
@Configuration
public class AscentEmbeddedAwsLocalstackAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public AscentEmbeddedAwsLocalstack ascentEmbeddedAwsServers() {
		return new AscentEmbeddedAwsLocalstack();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AscentAwsLocalstackProperties ascentAwsLocalstackProperties() {
		AscentAwsLocalstackProperties ascentAwsLocalstackProperties = new AscentAwsLocalstackProperties();
		List<Services> services = new ArrayList<>();
		
		Services service = new Services();
		service.setName("s3");
		service.setPort(4572);
		services.add(service);
		
		service = new Services();
		service.setName("sqs");
		service.setPort(4576);
		services.add(service);
		ascentAwsLocalstackProperties.setServices(services);
		
		return  ascentAwsLocalstackProperties;
	}
	
}