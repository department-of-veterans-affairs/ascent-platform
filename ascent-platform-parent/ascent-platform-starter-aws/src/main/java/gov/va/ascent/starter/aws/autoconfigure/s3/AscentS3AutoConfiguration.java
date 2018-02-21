package gov.va.ascent.starter.aws.autoconfigure.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import gov.va.ascent.starter.aws.autoconfigure.s3.config.S3Config;
import gov.va.ascent.starter.aws.autoconfigure.s3.services.S3Services;
import gov.va.ascent.starter.aws.autoconfigure.s3.services.impl.S3ServicesImpl;



/**
 * Created by akulkarni on 2/1/18.
 */

@Configuration
@Import(S3Config.class)
public class AscentS3AutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public S3Services s3Services(){
		return new S3ServicesImpl();
	}

}


