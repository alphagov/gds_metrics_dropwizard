package engineering.reliability.gds.metrics.bundle;

import com.codahale.metrics.MetricRegistry;
import engineering.reliability.gds.metrics.config.Configuration;
import engineering.reliability.gds.metrics.filter.AuthenticationFilter;
import engineering.reliability.gds.metrics.filter.RequestCountFilter;
import engineering.reliability.gds.metrics.filter.RequestDurationFilter;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

/**
 * MetricsBundle
 *
 * This is a Dropwizard bundle that adds prometheus instrumentation to a dropwizard app. in particular:
 * <ul>
 *  <li> it registers the dropwizard metrics (ie com.codahale.metrics metrics) with the prometheus registry
 *  <li> it registers the {@link DefaultExports} with the prometheus registry
 *  <li> it adds {@link RequestCountFilter} to record a count of requests by code, path, host, and method
 *  <li> it adds {@link RequestDurationFilter} to record a histogram of request durations by code, path, host, and method
 *  <li> it registers a prometheus {@link MetricsServlet} to serve metrics on /metrics (or wherever configured) on the app port
 *    <li> it adds {@link AuthenticationFilter} to ensure requests to the metrics endpoint based on the application_id
 *      in the VCAP_APPLICATION environment variable
 * </ul>
 * @deprecated this bundle will be removed in future -> migrate to {@link engineering.reliability.gds.metrics.bundle.PrometheusBundle}
 */
@Deprecated
public class MetricsBundle implements Bundle {

	private final Configuration configuration = Configuration.getInstance();

	@Override
	public void initialize(final Bootstrap<?> bootstrap) {
		final MetricRegistry metrics;

		if (configuration.isActiveDropwizardMetrics()) {
			metrics = bootstrap.getMetricRegistry();
			CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
		}

		DefaultExports.initialize();
	}

	@Override
	public void run(final Environment environment) {
		final double[] bucket = {0.005, 0.01, 0.025, 0.05, 0.075, 0.1, 0.25, 0.5, 0.75, 1, 2.5, 5, 7.5, 10};
		final RequestDurationFilter requestDurationFilter;
		final RequestCountFilter requestCountFilter;

		environment.servlets().addServlet("metrics", new MetricsServlet())
				.addMapping(configuration.getPrometheusMetricsPath());

		this.addFilter(environment, "AuthenticationFilter", new AuthenticationFilter(), configuration.getPrometheusMetricsPath());

		requestDurationFilter = new RequestDurationFilter(
				"http_server_request_duration_seconds",
				"Represent the total request made to the application",
				0,
				bucket);
		this.addFilter(environment, "MetricsFilter", requestDurationFilter, "/*");

		requestCountFilter = new RequestCountFilter(
				"http_server_requests_total",
				"The number of http requests made to the application");
		this.addFilter(environment, "RequestCountFilter", requestCountFilter, "/*");
	}

	private void addFilter(final Environment environment, final String name, final Filter filter, final String path) {
		environment.servlets()
				.addFilter(name, filter)
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, path);

	}
}
