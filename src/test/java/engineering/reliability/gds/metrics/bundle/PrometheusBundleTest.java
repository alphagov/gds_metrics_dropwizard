package engineering.reliability.gds.metrics.bundle;

import engineering.reliability.gds.metrics.support.TestApplication;
import engineering.reliability.gds.metrics.support.TestConfiguration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static engineering.reliability.gds.metrics.bundle.PrometheusBundle.PROMETHEUS_METRICS_RESOURCE;
import static org.assertj.core.api.Assertions.assertThat;

public class PrometheusBundleTest {

    @ClassRule
    public static DropwizardAppRule<TestConfiguration> appRuleWithMetrics = new DropwizardAppRule<>(TestApplication.class, null,
            ConfigOverride.config("server.applicationConnectors[0].port", "0"),
            ConfigOverride.config("server.adminConnectors[0].port", "0"),
            ConfigOverride.config("prometheusEnabled", "true"));

    @ClassRule
    public static DropwizardAppRule<TestConfiguration> appRuleWithoutMetrics = new DropwizardAppRule<>(TestApplication.class, null,
            ConfigOverride.config("server.applicationConnectors[0].port", "0"),
            ConfigOverride.config("server.adminConnectors[0].port", "0"),
            ConfigOverride.config("prometheusEnabled", "false"));

    private final Client client = new JerseyClientBuilder().build();

    @Test
    public void aMetricIsLogged() {
        final Response response = client.target("http://localhost:" + appRuleWithMetrics.getAdminPort() + PROMETHEUS_METRICS_RESOURCE)
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // should add a better check here but I don't know what is worth adding
        assertThat(response.readEntity(String.class)).contains("engineering_reliability_gds_metrics_support_TestResource_get_count");
    }

    @Test
    public void noJvmMetricsAreLogged() {
        final Response response = client.target("http://localhost:" + appRuleWithMetrics.getAdminPort() + PROMETHEUS_METRICS_RESOURCE)
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        // there are jvm metrics in the output, so unsure here what are the values that should be excluded
        assertThat(response.readEntity(String.class)).doesNotContain("jvm");
    }

    @Test
    public void metricsAreNotPresentWhenMetricsAreDisabled() {
        final Response response = client.target("http://localhost:" + appRuleWithoutMetrics.getAdminPort() + PROMETHEUS_METRICS_RESOURCE)
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(404);
    }

}
