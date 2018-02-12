package uk.gov.reng.metrics.config;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static java.lang.System.getenv;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class})
public class ConfigurationTest {

	private Configuration configuration;

	@Before
	public void setUp() {
		configuration = Configuration.getInstance();
	}

	@Test
	public void setDefaultValues() {
		assertThat("ApplicationId should be null",
				configuration.getApplicationId(), nullValue());
		assertThat("PrometheusMetricsPath should be null",
				configuration.getPrometheusMetricsPath(), IsEqual.equalTo("/metrics"));
		assertThat("Authentication should be disabled",
				configuration.isAuthEnable(), is(false));
	}

	@Test
	public void getApplicationIdFromEnv() {
		prepareEnvMocks();

		configuration.populateProperties();

		assertThat("ApplciationId should be equalt to 'something'",
				configuration.getApplicationId(), equalTo("something"));
	}

	private void prepareEnvMocks() {
		mockStatic(System.class);
		when(getenv(eq("VCAP_APPLICATION"))).thenReturn("{ \"application_id\" => \"something\" }");
		when(getenv(eq("PROMETHEUS_METRICS_PATH"))).thenReturn("/prometheus");
	}

	@Test
	public void getPrometheusMetricsPathFromEnv() {
		prepareEnvMocks();

		configuration.populateProperties();

		assertThat("PrometheusMetricsPath should be equalt to '/prometheus'",
				configuration.getPrometheusMetricsPath(), equalTo("/prometheus"));
	}

	@Test
	public void checkIfAuthorizationIsEnabled() {
		prepareEnvMocks();

		configuration.populateProperties();

		assertThat("Authorization should be enables",
				configuration.isAuthEnable(), is(true));
	}
}
