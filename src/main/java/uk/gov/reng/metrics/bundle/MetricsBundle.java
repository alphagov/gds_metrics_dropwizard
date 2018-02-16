package uk.gov.reng.metrics.bundle;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import uk.gov.reng.metrics.config.Configuration;
import uk.gov.reng.metrics.filter.AuthenticationFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class MetricsBundle implements Bundle {

	@Override
	public void initialize(final Bootstrap<?> bootstrap) {
		final MetricRegistry metrics = bootstrap.getMetricRegistry();

		CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
	}

	@Override
	public void run(final Environment environment) {
		final Configuration configuration = Configuration.getInstance();

		environment.servlets().addServlet("metrics", new MetricsServlet())
				.addMapping(configuration.getPrometheusMetricsPath());

		environment.servlets()
				.addFilter("AuthenticationFilter", new AuthenticationFilter())
				.addMappingForUrlPatterns(
						EnumSet.of(DispatcherType.REQUEST),
						true,
						configuration.getPrometheusMetricsPath());
	}
}
