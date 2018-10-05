package gov.va.ascent.starter.aws.server;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;

@ConfigurationProperties(prefix = "ascent.aws.localstack-config")
@Configuration
public class AscentAwsLocalstackProperties {

	static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(AscentAwsLocalstackProperties.class);

	private List<Services> services;

	public void setServices(List<Services> services) {
		this.services = services;
	}

	public List<Services> getServices() {
		return this.services;
	}

	/** Inner class with Services specific config properties */
	public static class Services {

		/** AWS Service name */
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
