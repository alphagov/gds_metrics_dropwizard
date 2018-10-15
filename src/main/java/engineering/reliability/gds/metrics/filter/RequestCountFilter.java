package engineering.reliability.gds.metrics.filter;

import engineering.reliability.gds.metrics.utils.StringUtils;
import io.prometheus.client.Counter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @deprecated this is used with {@link engineering.reliability.gds.metrics.bundle.MetricsBundle}, which will be removed in future -> migrate to using {@link engineering.reliability.gds.metrics.bundle.PrometheusBundle}
 */
@Deprecated
public class RequestCountFilter implements Filter {
	private Counter counter;
	private final String metricName;
	private String help;

	public RequestCountFilter(String metricName, String help) {
		this.metricName = metricName;
		if (Objects.nonNull(help)) {
			this.help = help;
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		final Counter.Builder builder = Counter.build()
				.labelNames("host", "code", "path", "method");

		if (StringUtils.isEmpty(metricName)) {
			throw new ServletException("No metricName passed via constructor");
		}

		counter = builder
				.help(help)
				.name(metricName)
				.register();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest;
		HttpServletResponse httpResponse;
		String path;
		String hostName;

		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		httpRequest = (HttpServletRequest) request;
		path = httpRequest.getRequestURI();
		hostName = httpRequest.getHeader("Host");

		if (Objects.isNull(hostName) || hostName.isEmpty()) {
			hostName = "";
		}

		try {
			chain.doFilter(request, response);
		} finally {
			httpResponse = (HttpServletResponse) response;

			counter.labels(hostName, String.valueOf(httpResponse.getStatus()), path, httpRequest.getMethod()).inc();
		}
	}

	@Override
	public void destroy() {
	}
}
