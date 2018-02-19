package engineering.reliability.gds.metrics.filter;

import io.prometheus.client.Counter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestCountFilter implements Filter {
    private Counter counter;
    private String metricName, help;

    public RequestCountFilter(String metricName, String help) {
        this.metricName = metricName;
        this.help = help;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        counter = Counter.build()
            .name(metricName)
            .labelNames("host", "path", "method")
            .help(help).register();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String path = httpRequest.getRequestURI();

        String hostName = httpRequest.getHeader("Host");

        if (hostName == null || hostName.isEmpty()) {
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
