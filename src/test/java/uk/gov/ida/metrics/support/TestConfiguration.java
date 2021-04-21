package uk.gov.ida.metrics.support;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.ida.metrics.config.PrometheusConfiguration;
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
