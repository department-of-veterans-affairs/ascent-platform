package gov.va.ascent.starter.aws.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import gov.va.ascent.starter.aws.s3.config.S3LocalStackConfig;
import gov.va.ascent.starter.aws.s3.config.S3Config;
import gov.va.ascent.starter.aws.s3.services.S3Service;
import gov.va.ascent.starter.aws.s3.services.impl.S3ServiceImpl;



/**
 * Created by akulkarni on 2/1/18.
 */

@Configuration
@Import({S3LocalStackConfig.class, S3Config.class})
public class AscentS3AutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public S3Service s3Service(){
		return new S3ServiceImpl();
	}

}


