package gov.va.ascent.starter.logger.sleuth.autoconfigure;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.instrument.web.HttpSpanInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import gov.va.ascent.starter.logger.sleuth.tracing.CustomHttpServletResponseSpanInjector;
import gov.va.ascent.starter.logger.sleuth.tracing.HttpResponseInjectingTraceFilter;

/**
 * Configures Sleuth to inject span information into http responses.
 * Intent is to allow REST consumers to quote traceId and any other relevant info from the http headers.
 * 
 * @author aburkholder
 */
@Configuration
public class SleuthAutoConfiguration {

	/**
	 * Spring bean to make {@link CustomHttpServletResponseSpanInjector}
	 * available for injecting key/value pairs into the response header.
	 * <p>
	 * This bean is used by Sleuth via the {@link SleuthAutoConfiguration#responseInjectingTraceFilter(Tracer)} bean.
	 * 
	 * @return HttpSpanInjector
	 */
	@Bean
	HttpSpanInjector customHttpServletResponseSpanInjector() {
		return new CustomHttpServletResponseSpanInjector();
	}

	/**
	 * Spring bean to add {@link HttpResponseInjectingTraceFilter} to the Jersey filter chain.
	 * <p>
	 * This bean inserts span injection into the execution path, and adds custom values to the http response via
	 * the {@link SleuthAutoConfiguration#customHttpServletResponseSpanInjector()} bean.
	 * 
	 * @param tracer Tracer
	 * @return HttpResponseInjectingTraceFilter
	 */
	@Bean
	HttpResponseInjectingTraceFilter responseInjectingTraceFilter(Tracer tracer) {
		return new HttpResponseInjectingTraceFilter(tracer, customHttpServletResponseSpanInjector());
	}
	
}
