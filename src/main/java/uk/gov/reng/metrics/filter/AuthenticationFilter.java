package uk.gov.reng.metrics.filter;

import uk.gov.reng.metrics.config.Configuration;

import javax.annotation.Priority;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
@Priority(500)
public class AuthenticationFilter implements Filter {

	private Configuration configuration;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		configuration = Configuration.getInstance();
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest httpRequest;
		final HttpServletResponse httpResponse;

		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
			throw new ServletException("Authentication doesn't support non-HTTP request or response.");
		}

		httpRequest = (HttpServletRequest) request;
		httpResponse = (HttpServletResponse) response;

		if (needAuthenticationCheck((HttpServletRequest) request, configuration)) {
			if (!isAllowed(httpRequest, configuration)) {
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.setContentType("text/plain");
				httpResponse.getWriter().print("Request not allowed");

				return;
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

	private boolean needAuthenticationCheck(final HttpServletRequest request, final Configuration configuration) {
		return configuration.getPrometheusMetricsPath().equals(request.getRequestURI()) && configuration.isAuthEnable();
	}

	private boolean isAllowed(final HttpServletRequest request, final Configuration configuration) {
		final String httpAuthorization = request.getHeader("Authorization");
		final Pattern pattern;
		final Matcher matcher;

		if (Objects.isNull(httpAuthorization) || httpAuthorization.isEmpty()) {
			return false;
		}

		pattern = Pattern.compile("Bearer (.*)", Pattern.CASE_INSENSITIVE);
		matcher = pattern.matcher(httpAuthorization);

		return matcher.find() &&
				Objects.nonNull(configuration.getApplicationId()) &&
				configuration.getApplicationId().equals(matcher.group(1));
	}
}
