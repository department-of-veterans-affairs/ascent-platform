package gov.va.ascent.starter.rest.autoconfigure;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import gov.va.ascent.security.jwt.JwtTokenService;

public class TokenClientHttpRequestInterceptor implements ClientHttpRequestInterceptor{

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenClientHttpRequestInterceptor.class);

    @Autowired
    private JwtTokenService tokenService;

    @Override
    public final ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                              final ClientHttpRequestExecution execution)
            throws IOException {

        Map<String, String> tokenMap =  tokenService.getTokenFromRequest();
        for(String token: tokenMap.keySet()){
            LOGGER.info("Adding Token Header {} {}", token, tokenMap.get(token));
            request.getHeaders().add(token, tokenMap.get(token));
        }
        return execution.execute(request, body);
    }
}