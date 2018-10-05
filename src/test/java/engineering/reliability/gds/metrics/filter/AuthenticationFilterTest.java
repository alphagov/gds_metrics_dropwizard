package engineering.reliability.gds.metrics.filter;

import engineering.reliability.gds.metrics.config.AbstractConfigurationTest;
import engineering.reliability.gds.metrics.mock.MockHttpServletRequest;
import engineering.reliability.gds.metrics.mock.MockHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest extends AbstractConfigurationTest {

	@InjectMocks
	private AuthenticationFilter authenticationFilter;

	@Mock
	private FilterChain chain;

	@Mock
	private FilterConfig config;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		clearConfigurationConfiguration();
	}

	@Test
	public void notRelevantPath() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
		request.setRequestURI("/index");
		setConfigurationInEnvironment(null, "/metrics", true);

		testFilter();

		assertThat(response.getStatus(), equalTo(200));
	}

	@Test
	public void relevantPathAndAuthDisabled() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
		request.setRequestURI("/metrics");
		setConfigurationInEnvironment(null, "/metrics", true);

		testFilter();

		assertThat(response.getStatus(), equalTo(200));
	}

	@Test
	public void relevantPathAndAuthEnabledWithWrongToken() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
		request.setRequestURI("/metrics");
		String correctBearerToken = UUID.randomUUID().toString();
		String wrongBearerToken = UUID.randomUUID().toString();
		request.setHeader("Authorization", "Bearer "+correctBearerToken);
		setConfigurationInEnvironment(wrongBearerToken, "/metrics", true);

		testFilter();

		assertThat(response.getStatus(), equalTo(401));
	}

	@Test
	public void relevantPathAndAuthEnabledWithCorrectToken() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
		request.setRequestURI("/metrics");
		String correctBearerToken = UUID.randomUUID().toString();
		request.setHeader("Authorization", "Bearer "+correctBearerToken);
		setConfigurationInEnvironment(correctBearerToken, null, true);

		testFilter();

		assertThat(response.getStatus(), equalTo(200));
	}

	private void testFilter() throws ServletException, IOException {
		authenticationFilter.init(config);
		authenticationFilter.doFilter(request, response, chain);
		authenticationFilter.destroy();
	}
}
