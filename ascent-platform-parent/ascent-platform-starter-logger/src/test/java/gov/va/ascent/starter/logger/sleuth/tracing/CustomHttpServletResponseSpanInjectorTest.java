package gov.va.ascent.starter.logger.sleuth.tracing;

import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.mock.web.MockHttpServletResponse;

public class CustomHttpServletResponseSpanInjectorTest {

	CustomHttpServletResponseSpanInjector spanInjector;

	@SuppressWarnings("deprecation")
	Span span = new Span(-1, -1, "dummy", 0, Collections.<Long> emptyList(), 0, false, false, null);

	SpanTextMap spanTextMap;

	HttpServletResponse response = new MockHttpServletResponse();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		spanInjector = new CustomHttpServletResponseSpanInjector();
		assertNotNull(spanInjector);

		spanTextMap = new TestTextMap(response);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInjectSpanSpanTextMap() {
//		Map<String, String> baggageMap = new HashMap<String, String>();
//		baggageMap.put("test", "testing");
//		when(span).baggageItems().thenReturn(baggageMap.entrySet());

		spanInjector.inject(span, spanTextMap);
	}

	class TestTextMap implements SpanTextMap {

		private final HttpServletResponse delegate;

		/**
		 * The SpanTextMap must be instantiated with the existing response object.
		 *
		 * @param delegate HttpServletResponse - the existing response object
		 */
		TestTextMap(HttpServletResponse delegate) {
			this.delegate = delegate;
		}

		@Override
		public Iterator<Map.Entry<String, String>> iterator() {
			Map<String, String> map = new HashMap<>();
			for (String header : this.delegate.getHeaderNames()) {
				map.put(header, this.delegate.getHeader(header));
			}
			return map.entrySet().iterator();
		}

		@Override
		public void put(String key, String value) {
			this.delegate.addHeader(key, value);
		}
	}
}
