package gov.va.ascent.starter.logger.sleuth.tracing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.cloud.sleuth.trace.DefaultTracer;
import org.springframework.mock.web.MockHttpServletResponse;

import gov.va.ascent.starter.logger.sleuth.tracing.HttpResponseInjectingTraceFilter.HttpServletResponseTextMap;

public class HttpResponseInjectingTraceFilterTest {

	@Mock
	DefaultTracer tracer;

	@Mock
	HttpSpanInjector spanInjector;

	@Mock
	ServletRequest servletRequest;

	@Mock
	FilterChain chain;

	@Mock
	Span span;

	@InjectMocks
	HttpResponseInjectingTraceFilter traceFilter;

	HttpServletResponse servletResponse = new MockHttpServletResponse();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		traceFilter = new HttpResponseInjectingTraceFilter(tracer, spanInjector);
		assertNotNull(traceFilter);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testDoFilter() throws IOException, ServletException {
		when(tracer.getCurrentSpan()).thenReturn(span);

		traceFilter.doFilter(servletRequest, servletResponse, chain);
	}

	@Test
	public final void testHttpServletResponseTextMap() {
		HttpServletResponseTextMap map = traceFilter.new HttpServletResponseTextMap(servletResponse);
		map.put("iterTest", "itertesting");
		Iterator<Entry<String, String>> iterator = map.iterator();

		assertNotNull(iterator);
		assertTrue(iterator.hasNext());
		Entry<String, String> entry = iterator.next();
		assertNotNull(entry);
		assertTrue("iterTest".equals(entry.getKey()));
	}

}
