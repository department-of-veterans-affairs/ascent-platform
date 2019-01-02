package gov.va.ascent.starter.logger.sleuth.tracing;

import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.SpanTextMap;
import org.springframework.cloud.sleuth.instrument.web.ZipkinHttpSpanInjector;

/**
 * Implementation for configured bean.
 * <p>
 * Use this class to physically inject values into the header of the Ascent HttpResponse.
 *
 * @author aburkholder
 */
public class CustomHttpServletResponseSpanInjector extends ZipkinHttpSpanInjector {

	@Override
	public void inject(Span span, SpanTextMap carrier) {
		super.inject(span, carrier);

	}
}
