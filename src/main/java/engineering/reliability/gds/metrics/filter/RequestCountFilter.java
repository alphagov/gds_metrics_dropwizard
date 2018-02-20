package engineering.reliability.gds.metrics.filter;

import engineering.reliability.gds.metrics.utils.StringUtils;
import io.prometheus.client.Counter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

public class RequestCountFilter implements Filter {
    private Counter counter;
    private String metricName, help;

    public RequestCountFilter(String metricName, String help) {
        this.metricName = metricName;
        if (Objects.nonNull(help)) {
            this.help = help;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        final Counter.Builder builder = Counter.build()
                .labelNames("host", "path", "method");

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

        counter
            .labels(hostName, path, httpRequest.getMethod())
            .inc();

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
