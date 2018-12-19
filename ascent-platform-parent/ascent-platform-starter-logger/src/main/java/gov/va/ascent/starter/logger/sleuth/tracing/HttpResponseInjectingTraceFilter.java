package gov.va.ascent.starter.logger.sleuth.tracing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.web.filter.GenericFilterBean;

import gov.va.ascent.starter.logger.sleuth.autoconfigure.SleuthAutoConfiguration;

/**
 * Implementation for the configured bean.
 * <p>
 * Inserts a span injection filter into the servlet filter chain.
 * Sleuth's span injector calls {@link CustomHttpServletResponseSpanInjector} using the SpanTextMap, as configured in
 * {@link SleuthAutoConfiguration}
 *
 * @author aburkholder
 */
public class HttpResponseInjectingTraceFilter extends GenericFilterBean {

	private final Tracer tracer;
	private final HttpSpanInjector spanInjector;

	/**
	 * Construct the filter with the existing tracer and span injector.
	 *
	 * @param tracer Tracer
	 * @param spanInjector HttpSpanInjector
	 */
	public HttpResponseInjectingTraceFilter(Tracer tracer, HttpSpanInjector spanInjector) {
		this.tracer = tracer;
		this.spanInjector = spanInjector;
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		Span currentSpan = this.tracer.getCurrentSpan();
		this.spanInjector.inject(currentSpan, new HttpServletResponseTextMap(httpResponse));
		chain.doFilter(servletRequest, httpResponse);
	}

	/**
	 * Slueth construct for manipulating key/value pairs included in the trace.
	 *
	 * @author aburkholder
	 */
	class HttpServletResponseTextMap implements SpanTextMap {

		private final HttpServletResponse delegate;

		/**
		 * The SpanTextMap must be instantiated with the existing response object.
		 *
		 * @param delegate HttpServletResponse - the existing response object
		 */
		HttpServletResponseTextMap(HttpServletResponse delegate) {
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
