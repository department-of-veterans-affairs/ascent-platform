package gov.va.ascent.starter.rest.autoconfigure;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.security.jwt.JwtTokenService;

/**
 * An implementation of {@link ClientHttpRequestInterceptor} that adds the JWT token
 * from the originating request, and adds it to the outgoing request. No changes are
 * made to the response.
 * <p>
 * Use this class when making inter-=service REST calls that require PersonTraits.
 */
public class TokenClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(TokenClientHttpRequestInterceptor.class);

	@Autowired
	private JwtTokenService tokenService;

	/**
	 * Add token header from the originating request to the outgoing request.
	 * No changes made to the response.
	 * 
	 * @see org.springframework.http.client.ClientHttpRequestInterceptor#intercept(org.springframework.http.HttpRequest, byte[],
	 *      org.springframework.http.client.ClientHttpRequestExecution)
	 */
	@Override
	public final ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution)
			throws IOException {

		Map<String, String> tokenMap = tokenService.getTokenFromRequest();
		for (Map.Entry<String, String> token : tokenMap.entrySet()) {
			LOGGER.info("Adding Token Header {} {}", token.getKey(), token.getValue());
			request.getHeaders().add(token.getKey(), token.getValue());
		}
		return execution.execute(request, body);
	}
}