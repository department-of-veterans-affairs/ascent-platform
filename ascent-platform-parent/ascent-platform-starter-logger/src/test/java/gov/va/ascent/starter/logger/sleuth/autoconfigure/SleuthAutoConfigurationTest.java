package gov.va.ascent.starter.logger.sleuth.autoconfigure;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.cloud.sleuth.trace.DefaultTracer;

import gov.va.ascent.starter.logger.sleuth.tracing.HttpResponseInjectingTraceFilter;

public class SleuthAutoConfigurationTest {

	SleuthAutoConfiguration sleuthAutoConfiguration;

	@Mock
	DefaultTracer tracer;

	@Before
	public void setUp() throws Exception {
		sleuthAutoConfiguration = new SleuthAutoConfiguration();
		assertNotNull(sleuthAutoConfiguration);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testSleuthAutoConfiguration() {
		// this one call tests both beans
		HttpResponseInjectingTraceFilter traceFilter = sleuthAutoConfiguration.responseInjectingTraceFilter(tracer);
		assertNotNull(traceFilter);
	}

}
