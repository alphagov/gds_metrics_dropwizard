package engineering.reliability.gds.metrics.filter;

import io.prometheus.client.CollectorRegistry;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestCountFilterTest {
    private final RequestCountFilter f = new RequestCountFilter(
        "http_server_requests_total", "The count of http requests"
    );

    @Test
    public void countsNumberOfRequests() throws Exception {
        FilterConfig cfg = mock(FilterConfig.class);
        when(cfg.getInitParameter(anyString())).thenReturn(null);

        f.init(cfg);

        HttpServletRequest req = mock(HttpServletRequest.class);

        when(req.getRequestURI()).thenReturn("/some/path");
        when(req.getMethod()).thenReturn(HttpMethod.GET.asString());
        when(req.getHeader("Host")).thenReturn("example.com");

        HttpServletResponse res = mock(HttpServletResponse.class);

        when(res.getStatus()).thenReturn(200);

        FilterChain c = mock(FilterChain.class);

        f.doFilter(req, res, c);
        f.doFilter(req, res, c);
        f.doFilter(req, res, c);

        verify(c, times(3)).doFilter(req, res);

        final Double sampleValue = CollectorRegistry.defaultRegistry
            .getSampleValue(
                "http_server_requests_total",
                new String[]{"host", "code", "path", "method"},
                new String[]{"example.com", "200", "/some/path", HttpMethod.GET.asString()}
            );

        assertNotNull(sampleValue);
        assertEquals(3, sampleValue, 0.0001);
    }
}
