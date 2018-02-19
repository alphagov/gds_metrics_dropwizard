package engineering.reliability.gds.metrics.bundle;

import com.codahale.metrics.MetricRegistry;
import engineering.reliability.gds.metrics.config.Configuration;
import engineering.reliability.gds.metrics.filter.AuthenticationFilter;
import engineering.reliability.gds.metrics.filter.GdsMetricsFilter;
import engineering.reliability.gds.metrics.filter.RequestCountFilter;
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
		double[] bucket = {0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10};

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
						"http_server_request_duration_seconds",
						"Represent the total request made to the application", 0,
						bucket))
				.addMappingForUrlPatterns(
						EnumSet.of(DispatcherType.REQUEST),
						true, "/*");

		environment.servlets()
				.addFilter("RequestCountFilter", new RequestCountFilter(
						"http_server_requests_total",
						"The number of http requests made to the application"))
				.addMappingForUrlPatterns(
						EnumSet.of(DispatcherType.REQUEST),
						true, "/*");
	}
}
