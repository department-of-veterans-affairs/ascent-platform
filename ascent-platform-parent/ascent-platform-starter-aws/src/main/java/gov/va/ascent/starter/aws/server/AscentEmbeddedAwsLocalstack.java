package gov.va.ascent.starter.aws.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import cloud.localstack.docker.DockerExe;
import cloud.localstack.docker.LocalstackDocker;
import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.starter.aws.server.AscentAwsLocalstackProperties.Services;


/**
 * this class will start AWS localstack services, to be used for local envs. The profile embedded-aws needs to be added in order for
 * this bean to be created
 *
 * @author akulkarni
 */
@Profile(AscentCommonSpringProfiles.PROFILE_EMBEDDED_AWS)
public class AscentEmbeddedAwsLocalstack {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AscentEmbeddedAwsLocalstack.class);

	private static LocalstackDocker localstackDocker = LocalstackDocker.getLocalstackDocker();
	private static String externalHostName = "localhost";
	private static boolean pullNewImage = true;
	private static boolean randomizePorts = true;
	private static Map<String, String> environmentVariables = new HashMap<>();
	
	/**
     * Cache Properties Bean
     */
    @Autowired
    private AscentAwsLocalstackProperties localstackProperties;


	public LocalstackDocker getLocalstackDocker() {
		return localstackDocker;
	}

	/**
	 * Start embedded AWS servers on context load
	 * 
	 * @throws IOException
	 */
	@PostConstruct
	public void startAwsLocalStack() throws IOException {
		
		if(localstackDocker !=null && localstackDocker.getLocalStackContainer() !=null) {
			LOGGER.info("AWS localstack already running, not trying to re-start: {} ", localstackDocker.getLocalStackContainer());
			return;
		} else {
			localstackDocker.setExternalHostName(externalHostName);
			localstackDocker.setPullNewImage(pullNewImage);
			localstackDocker.setRandomizePorts(randomizePorts);
			
			List<Services> listServices = localstackProperties.getServices();
			
			if (!CollectionUtils.isEmpty(listServices)) {
				LOGGER.info("Services List: {}", ReflectionToStringBuilder.toString(listServices));
				StringBuilder builder = new StringBuilder();
				for (Services service : listServices) {
		            builder.append(service.getName());
		            builder.append(":");
		            builder.append(service.getPort());
		            builder.append(",");
		        }
				// Remove last delimiter with setLength.
		        builder.setLength(builder.length() - 1);
				String services = String.join(",", builder.toString());
		        if(StringUtils.isNotEmpty(services)) {
		        	LOGGER.info("Services to be started: {}", services);
		            environmentVariables.put("SERVICES", services);
		        }
				localstackDocker.setEnvironmentVariables(environmentVariables);
				localstackDocker.setRandomizePorts(false);
			}
			// create and start S3, SQS API mock
			LOGGER.info("starting localstack: {} ", ReflectionToStringBuilder.toString(localstackDocker));
			localstackDocker.startup();
		}

	}

	/**
	 * stop embedded AWS servers on context destroy
	 */
	@PreDestroy
	public void stopAwsLocalStack() {
		// stop the localstack
		if (localstackDocker !=null && localstackDocker.getLocalStackContainer() !=null) {
			LOGGER.info("stopping localstack: {} ", localstackDocker.getLocalStackContainer());
			localstackDocker.stop();
			LOGGER.info("stopped localstack");

			// clean up docker containers
			DockerExe newDockerExe = new DockerExe();
			String listContainerIds = newDockerExe.execute(Arrays.asList("ps","--no-trunc","-aq","--filter","ancestor=localstack/localstack"));
			LOGGER.info("containers to be cleaned: {} ", listContainerIds);
			if (StringUtils.isNotEmpty(listContainerIds)) {
				try {
					String[] splitArray = listContainerIds.split("\\s+");
					for (String containerId : splitArray) {
						String output = newDockerExe.execute(Arrays.asList("rm","-f", containerId));
						LOGGER.info("docker remove command output: {} ", output);
					}
				} catch (PatternSyntaxException ex) {
					LOGGER.warn("PatternSyntaxException During Splitting: {}", ex);
				}
			}
		}
	}
}
