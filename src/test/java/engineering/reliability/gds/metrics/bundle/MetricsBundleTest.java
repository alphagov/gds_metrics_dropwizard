package engineering.reliability.gds.metrics.bundle;

import engineering.reliability.gds.metrics.config.AbstractConfigurationTest;
import engineering.reliability.gds.metrics.filter.AuthenticationFilter;
import engineering.reliability.gds.metrics.filter.RequestDurationFilter;
import engineering.reliability.gds.metrics.filter.RequestCountFilter;
import io.dropwizard.jetty.setup.ServletEnvironment;
import io.dropwizard.setup.Environment;
import io.prometheus.client.exporter.MetricsServlet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRegistration;
import java.util.EnumSet;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MetricsBundleTest extends AbstractConfigurationTest {

	private final ServletEnvironment servletEnvironment = mock(ServletEnvironment.class);
	private final Environment environment = mock(Environment.class);

	private String servletPath = "";

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		when(environment.servlets()).thenReturn(servletEnvironment);
		clearConfigurationConfiguration();
	}

	@Test
	public void test() {
		runBundle(new MetricsBundle());

		Assert.assertThat(servletPath, equalTo("/metrics"));
	}

	private void runBundle(final MetricsBundle bundle) {
		runBundle(bundle, "metrics");
	}

	private void runBundle(final MetricsBundle bundle, String metricName) {
		final ArgumentCaptor<MetricsServlet> servletCaptor;
		final ArgumentCaptor<String> pathCaptor;
		final ServletRegistration.Dynamic registration = mock(ServletRegistration.Dynamic.class);
		final FilterRegistration.Dynamic authFilterRegistration = mock(FilterRegistration.Dynamic.class);
		final FilterRegistration.Dynamic metricsFilterRegistration = mock(FilterRegistration.Dynamic.class);
		final FilterRegistration.Dynamic requestCountRegistration = mock(FilterRegistration.Dynamic.class);

		when(servletEnvironment.addServlet(anyString(), any(MetricsServlet.class))).thenReturn(registration);
		when(servletEnvironment.addFilter(anyString(), any(AuthenticationFilter.class))).thenReturn(authFilterRegistration);
		when(servletEnvironment.addFilter(anyString(), any(RequestDurationFilter.class))).thenReturn(metricsFilterRegistration);
		when(servletEnvironment.addFilter(anyString(), any(RequestCountFilter.class))).thenReturn(requestCountRegistration);

		bundle.run(environment);

		servletCaptor = ArgumentCaptor.forClass(MetricsServlet.class);
		pathCaptor = ArgumentCaptor.forClass(String.class);

		verify(servletEnvironment).addServlet(eq(metricName), servletCaptor.capture());
		verify(registration).addMapping(pathCaptor.capture());

		verify(authFilterRegistration).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/metrics");
		verify(metricsFilterRegistration).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
		verify(requestCountRegistration).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

		this.servletPath = pathCaptor.getValue();
	}
}
