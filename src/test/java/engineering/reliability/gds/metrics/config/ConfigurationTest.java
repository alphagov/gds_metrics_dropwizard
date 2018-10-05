package engineering.reliability.gds.metrics.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationTest extends AbstractConfigurationTest {

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		clearConfigurationConfiguration();
	}

	@Test
	public void setDefaultValues() {
		configuration = Configuration.getInstance();

		assertThat("ApplicationId should be null",
				configuration.getApplicationId(), nullValue());
		assertThat("PrometheusMetricsPath should be null",
				configuration.getPrometheusMetricsPath(), equalTo("/metrics"));
		assertThat("Authentication should be disabled",
				configuration.isAuthEnable(), is(false));
		assertThat("Dropwizard metrics should be disabled",
				configuration.isActiveDropwizardMetrics(), is(false));
	}

	@Test
	public void getApplicationIdFromEnv() throws NoSuchFieldException, IllegalAccessException {
		setConfigurationInEnvironment("something", null, false);
		assertThat("ApplciationId should be equal to 'something'",
				configuration.getApplicationId(), equalTo("something"));
	}

	@Test
	public void getPrometheusMetricsPathFromEnv() throws NoSuchFieldException, IllegalAccessException {
		setConfigurationInEnvironment(null, "/prometheus", false);
		assertThat("PrometheusMetricsPath should be equal to '/prometheus'",
				configuration.getPrometheusMetricsPath(), equalTo("/prometheus"));
	}

	@Test
	public void getActiveDropwizardMetricsPathFromEnv() throws NoSuchFieldException, IllegalAccessException {
		setConfigurationInEnvironment(null, null, true);
		assertThat("activeDropwizardMetrics should be equal to 'true'",
				configuration.isActiveDropwizardMetrics(), equalTo(true));
	}

	@Test
	public void checkIfAuthorizationIsEnabled() throws NoSuchFieldException, IllegalAccessException {
		setConfigurationInEnvironment("app", null, false);
		assertThat("Authorization should be enabled if application name is set",
				configuration.isAuthEnable(), is(true));
	}
}
