package uk.gov.ida.metrics.bundle;

import com.codahale.metrics.Metric;
import uk.gov.ida.metrics.config.PrometheusConfiguration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * PrometheusBundle
 *
 * This is a Dropwizard bundle that adds prometheus instrumentation to a dropwizard app. in particular:
 * <ul>
 *  <li>it registers the dropwizard metrics (ie com.codahale.metrics metrics) with the prometheus registry
 *  <li>it registers the {@link DefaultExports} with the prometheus registry
 *  <li>it registers a prometheus {@link MetricsServlet} to serve metrics on /prometheus/metrics on the admin port
 *  <li>it filters out jvm.* dropwizard metrics (which are mostly duplicated by DefaultExports)
 * </ul>
 */
public class PrometheusBundle implements ConfiguredBundle<PrometheusConfiguration> {

    public static final String PROMETHEUS_METRICS_RESOURCE = "/prometheus/metrics";

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(PrometheusConfiguration configuration, Environment environment) {
        if (configuration.isPrometheusEnabled()) {
            DefaultExports.initialize();
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(environment.metrics(), this::isNotJvmMetric));
            environment.admin().addServlet("metrics", new MetricsServlet())
                    .addMapping(PROMETHEUS_METRICS_RESOURCE);
        }
    }

    /**
     * There are a number of dropwizard metrics that start with <code>jvm.*</code>.
     * These are confusing because, after sanitizing dots to underscores, they end up in the same namespace as
     * prometheus <code>jvm_*</code> metrics, and in most cases they duplicate something that already exists in
     * prometheus.
     * For example: the dropwizard metric <code>jvm.threads.daemon.count</code> will have its name converted to
     * <code>jvm_threads_daemon_count</code>, but it duplicates the existing prometheus metric
     * <code>jvm_threads_daemon</code>.
     *
     * Easiest is just to remove all the dropwizard <code>jvm.*</code> metrics with this filter.
     */
    private boolean isNotJvmMetric(String name, Metric metric) {
        return !name.startsWith("jvm.");
    }
}
