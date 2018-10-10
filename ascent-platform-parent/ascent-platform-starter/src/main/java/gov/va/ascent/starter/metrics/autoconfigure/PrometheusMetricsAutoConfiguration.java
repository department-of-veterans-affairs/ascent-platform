package gov.va.ascent.starter.metrics.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.metrics.export.prometheus.EnablePrometheusMetrics;

/**
 * Spring AutoConfiguration class to enable a Prometheus metrics endpoint
 * 
 * @author jluck
 * @since 0.0.11
 *
 */
@EnablePrometheusMetrics
@Configuration
public class PrometheusMetricsAutoConfiguration {

}
