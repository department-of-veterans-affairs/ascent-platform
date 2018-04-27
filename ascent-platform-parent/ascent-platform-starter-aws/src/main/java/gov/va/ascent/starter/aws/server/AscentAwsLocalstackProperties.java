package gov.va.ascent.starter.aws.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@ConfigurationProperties(prefix="ascent.aws.localstack-config")
@Configuration
public class AscentAwsLocalstackProperties {

	static final Logger LOGGER = LoggerFactory.getLogger(AscentAwsLocalstackProperties.class);
	
	private List<Services> services;

		
	public void setServices(List<Services> services) {
		this.services = services;
	}
	
	public List<Services> getServices() {
		return this.services;
	}

	/** Inner class with Services specific config properties */
	public static class Services {

		/** AWS Service name*/
		private String name;

		/** AWS service port */
		private int port;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}
	}
}

