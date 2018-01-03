package gov.va.ascent.starter.rest.autoconfigure;

import gov.va.ascent.security.jwt.JwtTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration
public class TokenClientHttpRequestInterceptorTest {

    @Mock
    JwtTokenService tokenService;

    ClientHttpRequestExecution execution1 = new ClientHttpRequestExecution() {
        @Override
        public ClientHttpResponse execute(HttpRequest httpRequest, byte[] bytes) throws IOException {
            MockClientHttpResponse response = new MockClientHttpResponse(bytes, HttpStatus.OK);
            assertEquals("TestToken", httpRequest.getHeaders().get("TestHeader").get(0));
            return response;
        }
    };

    ClientHttpRequestExecution execution2 = new ClientHttpRequestExecution() {
        @Override
        public ClientHttpResponse execute(HttpRequest httpRequest, byte[] bytes) throws IOException {
            MockClientHttpResponse response = new MockClientHttpResponse(bytes, HttpStatus.OK);
            assertTrue(httpRequest.getHeaders().isEmpty());
            return response;
        }
    };

    @InjectMocks
    private TokenClientHttpRequestInterceptor tokenClientHttpRequestInterceptor;

    @Test
    public void interceptTest() throws Exception {
        MockClientHttpRequest request = new MockClientHttpRequest();

        Map<String, String> map = new HashMap<>();
        map.put("TestHeader", "TestToken");
        when(tokenService.getTokenFromRequest()).thenReturn(map);
        ClientHttpResponse response = tokenClientHttpRequestInterceptor.intercept(request, new byte[]{},execution1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void interceptTestNoHeaders() throws Exception {
        MockClientHttpRequest request = new MockClientHttpRequest();

        Map<String, String> map = new HashMap<>();
        when(tokenService.getTokenFromRequest()).thenReturn(map);
        ClientHttpResponse response = tokenClientHttpRequestInterceptor.intercept(request, new byte[]{},execution2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
