package uk.gov.reng.metrics.bundle;

import com.codahale.metrics.MetricRegistry;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.ClassLoadingExports;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;
import io.prometheus.client.hotspot.StandardExports;
import io.prometheus.client.hotspot.ThreadExports;
import io.prometheus.client.hotspot.VersionInfoExports;
import uk.gov.reng.metrics.config.Configuration;
import uk.gov.reng.metrics.filter.AuthenticationFilter;
import uk.gov.reng.metrics.filter.GdsMetricsFilter;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

public class MetricsBundle implements Bundle {

	final Configuration configuration = Configuration.getInstance();

	@Override
	public void initialize(final Bootstrap<?> bootstrap) {
		final MetricRegistry metrics;

		if (configuration.isActiveDropwizardMetrics()) {
			metrics = bootstrap.getMetricRegistry();
			CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
		}

		CollectorRegistry.defaultRegistry.register(new ClassLoadingExports());
		CollectorRegistry.defaultRegistry.register(new GarbageCollectorExports());
		CollectorRegistry.defaultRegistry.register(new MemoryPoolsExports());
		CollectorRegistry.defaultRegistry.register(new StandardExports());
		CollectorRegistry.defaultRegistry.register(new ThreadExports());
		CollectorRegistry.defaultRegistry.register(new VersionInfoExports());
	}

	@Override
	public void run(final Environment environment) {
		double[] bucket = { 0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10};

		environment.servlets().addServlet("metrics", new MetricsServlet())
				.addMapping(configuration.getPrometheusMetricsPath());

		environment.servlets()
				.addFilter("AuthenticationFilter", new AuthenticationFilter())
				.addMappingForUrlPatterns(
						EnumSet.of(DispatcherType.REQUEST),
						true,
						configuration.getPrometheusMetricsPath());

		environment.servlets()
				.addFilter("MetricsFilter", new GdsMetricsFilter(
						"requests_total",
						"Represent the total request made to the application", 0,
						bucket)
				)
				.addMappingForUrlPatterns(
						EnumSet.of(DispatcherType.REQUEST),
						true, "/*");
	}
}
