package engineering.reliability.gds.metrics.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import engineering.reliability.gds.metrics.config.PrometheusConfiguration;
import io.dropwizard.Configuration;

import javax.validation.constraints.NotNull;

public class TestConfiguration extends Configuration implements PrometheusConfiguration {

    @JsonProperty
    @NotNull
    private boolean prometheusEnabled = true;

    public TestConfiguration() {
    }

    @Override
    public boolean isPrometheusEnabled() {
        return prometheusEnabled;
    }
}
