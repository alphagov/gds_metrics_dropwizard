package engineering.reliability.gds.metrics.bundle;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import engineering.reliability.gds.metrics.config.PrometheusConfiguration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;

import java.util.SortedMap;

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
            MetricRegistry metrics = new FilteredMetricRegistryView(environment.metrics(), this::isNotJvmMetric);
            CollectorRegistry.defaultRegistry.register(new DropwizardExports(metrics));
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

    /**
     * This class wraps a MetricRegistry but only presents a subset of the metrics to the caller
     * We use it to present only certain metrics to the Prometheus CollectorRegistry using
     * filters like isNotJvmMetric above.
     */
    private static class FilteredMetricRegistryView extends MetricRegistry {
        private final MetricRegistry metrics;
        private final MetricFilter filter;

        private FilteredMetricRegistryView(MetricRegistry metrics, MetricFilter filter) {
            this.metrics = metrics;
            this.filter = filter;
        }

        @Override
        @SuppressWarnings("rawtypes")
        public SortedMap<String, Gauge> getGauges() {
            return metrics.getGauges(filter);
        }

        @Override
        public SortedMap<String, Counter> getCounters() {
            return metrics.getCounters(filter);
        }

        @Override
        public SortedMap<String, Histogram> getHistograms() {
            return metrics.getHistograms(filter);
        }

        @Override
        public SortedMap<String, Meter> getMeters() {
            return metrics.getMeters(filter);
        }

        @Override
        public SortedMap<String, Timer> getTimers() {
            return metrics.getTimers(filter);
        }
    }
}
