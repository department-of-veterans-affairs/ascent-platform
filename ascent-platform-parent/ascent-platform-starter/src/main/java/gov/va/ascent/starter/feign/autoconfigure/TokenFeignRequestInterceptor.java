package gov.va.ascent.starter.feign.autoconfigure;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.security.jwt.JwtTokenService;

public class TokenFeignRequestInterceptor implements RequestInterceptor {

	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(TokenFeignRequestInterceptor.class);

	@Autowired
	private JwtTokenService tokenService;

	@Override
	public void apply(RequestTemplate template) {
		Map<String, String> tokenMap = tokenService.getTokenFromRequest();
		for (Map.Entry<String, String> token : tokenMap.entrySet()) {
			LOGGER.info("Adding Token Header {} {}", token.getKey(), token.getValue());
			template.header(token.getKey(), token.getValue());
		}
	}
}