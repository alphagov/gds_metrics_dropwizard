package engineering.reliability.gds.metrics.filter;

import engineering.reliability.gds.metrics.utils.StringUtils;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleTimer;

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
 * This class is based on io.prometheus.client.filter.MetricsFilter,
 * but also adds the Host header as a label to the emitted metrics.
 *
 * Because this file is based on the Apache-licensed
 * io.prometheus.client.filter.MetricsFilter, this file is also Apache
 * licensed.
 *
 * See the original class for more documentation.
 */
public class RequestDurationFilter implements Filter {

	static final String PATH_COMPONENT_PARAM = "path-components";
	static final String HELP_PARAM = "help";
	static final String METRIC_NAME_PARAM = "metric-name";
	static final String BUCKET_CONFIG_PARAM = "buckets";

	private Histogram histogram = null;

	// Package-level for testing purposes.
	int pathComponents = 1;
	private String metricName = null;
	private String help = "The time taken fulfilling servlet requests";
	private double[] buckets = null;

	public RequestDurationFilter() {}

	public RequestDurationFilter(
			String metricName,
			String help,
			Integer pathComponents,
			double[] buckets) {
		this.metricName = metricName;
		this.buckets = buckets;
		if (help != null) {
			this.help = help;
		}
		if (pathComponents != null) {
			this.pathComponents = pathComponents;
		}
	}

	private String getComponents(String str) {
		if (str == null || pathComponents < 1) {
			return str;
		}
		int count = 0;
		int i =  -1;
		do {
			i = str.indexOf("/", i + 1);
			if (i < 0) {
				// Path is longer than specified pathComponents.
				return str;
			}
			count++;
		} while (count <= pathComponents);

		return str.substring(0, i);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		Histogram.Builder builder = Histogram.build()
				.labelNames("host", "code", "path", "method");

		if (filterConfig == null && StringUtils.isEmpty(metricName)) {
			throw new ServletException("No configuration object provided, and no metricName passed via constructor");
		}

		if (filterConfig != null) {
			if (StringUtils.isEmpty(metricName)) {
				metricName = filterConfig.getInitParameter(METRIC_NAME_PARAM);
				if (StringUtils.isEmpty(metricName)) {
					throw new ServletException("Init parameter \"" + METRIC_NAME_PARAM + "\" is required; please supply a value");
				}
			}

			if (!StringUtils.isEmpty(filterConfig.getInitParameter(HELP_PARAM))) {
				help = filterConfig.getInitParameter(HELP_PARAM);
			}

			// Allow overriding of the path "depth" to track
			if (!StringUtils.isEmpty(filterConfig.getInitParameter(PATH_COMPONENT_PARAM))) {
				pathComponents = Integer.valueOf(filterConfig.getInitParameter(PATH_COMPONENT_PARAM));
			}

			// Allow users to override the default bucket configuration
			if (!StringUtils.isEmpty(filterConfig.getInitParameter(BUCKET_CONFIG_PARAM))) {
				String[] bucketParams = filterConfig.getInitParameter(BUCKET_CONFIG_PARAM).split(",");
				buckets = new double[bucketParams.length];

				for (int i = 0; i < bucketParams.length; i++) {
					buckets[i] = Double.parseDouble(bucketParams[i]);
				}
			}
		}

		if (buckets != null) {
			builder = builder.buckets(buckets);
		}

		histogram = builder
				.help(help)
				.name(metricName)
				.register();
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest request;
		final HttpServletResponse response;
		final String path;
		final SimpleTimer simpleTimer;
		String hostName;


		if (!(servletRequest instanceof HttpServletRequest)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		request = (HttpServletRequest) servletRequest;
		path = request.getRequestURI();
		hostName = request.getHeader("Host");

		if (Objects.isNull(hostName) || hostName.isEmpty()) {
			hostName = "";
		}

		simpleTimer = new SimpleTimer();

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			response = (HttpServletResponse) servletResponse;

			histogram.labels(hostName, String.valueOf(response.getStatus()), getComponents(path), request.getMethod())
					.observe(simpleTimer.elapsedSeconds());
		}
	}

	@Override
	public void destroy() {
	}
}
