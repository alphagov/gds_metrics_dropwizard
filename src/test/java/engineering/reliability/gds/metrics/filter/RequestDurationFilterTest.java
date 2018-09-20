package engineering.reliability.gds.metrics.filter;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RequestDurationFilterTest {
	private final RequestDurationFilter f = new RequestDurationFilter();

	@After
	public void clear() {
		CollectorRegistry.defaultRegistry.clear();
	}

	@Test
	public void init() throws Exception {
		FilterConfig cfg = mock(FilterConfig.class);
		when(cfg.getInitParameter(anyString())).thenReturn(null);

		String metricName = "foo";

		when(cfg.getInitParameter(RequestDurationFilter.METRIC_NAME_PARAM)).thenReturn(metricName);
		when(cfg.getInitParameter(RequestDurationFilter.PATH_COMPONENT_PARAM)).thenReturn("4");

		f.init(cfg);

		assertEquals(f.pathComponents, 4);

		HttpServletRequest req = mock(HttpServletRequest.class);

		when(req.getRequestURI()).thenReturn("/foo/bar/baz/bang/zilch/zip/nada");
		when(req.getMethod()).thenReturn(HttpMethod.GET.asString());
		when(req.getHeader("Host")).thenReturn("test-host");

		HttpServletResponse res = mock(HttpServletResponse.class);

		when(res.getStatus()).thenReturn(200);

		FilterChain c = mock(FilterChain.class);

		f.doFilter(req, res, c);

		verify(c).doFilter(req, res);

		final Double sampleValue = CollectorRegistry.defaultRegistry
				.getSampleValue(
						metricName + "_count",
						new String[]{"host", "code", "path", "method"},
						new String[]{"test-host", "200", "/foo/bar/baz/bang",
								HttpMethod.GET.asString()});
		assertNotNull(sampleValue);
		assertEquals(1, sampleValue, 0.0001);
	}

	@Test
	public void doFilter() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		final String path = "/foo/bar/baz/bang/zilch/zip/nada";

		when(req.getRequestURI()).thenReturn(path);
		when(req.getMethod()).thenReturn(HttpMethod.GET.asString());
		when(req.getHeader("Host")).thenReturn("test-host");

		HttpServletResponse res = mock(HttpServletResponse.class);

		when(res.getStatus()).thenReturn(200);

		FilterChain c = mock(FilterChain.class);

		String name = "foo";
		FilterConfig cfg = mock(FilterConfig.class);
		when(cfg.getInitParameter(RequestDurationFilter.METRIC_NAME_PARAM)).thenReturn(name);
		when(cfg.getInitParameter(RequestDurationFilter.PATH_COMPONENT_PARAM)).thenReturn("0");

		f.init(cfg);
		f.doFilter(req, res, c);

		verify(c).doFilter(req, res);

		final Double sampleValue = CollectorRegistry.defaultRegistry
				.getSampleValue(
						name + "_count",
						new String[]{"host", "code", "path", "method"},
						new String[]{"test-host", "200", path, HttpMethod.GET.asString()});
		assertNotNull(sampleValue);
		assertEquals(1, sampleValue, 0.0001);
	}

	@Test
	public void doFilterWithoutHostHeader() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		final String path = "/foo/bar/baz/bang/zilch/zip/nada";

		when(req.getRequestURI()).thenReturn(path);
		when(req.getMethod()).thenReturn(HttpMethod.GET.asString());
		when(req.getHeader("Host")).thenReturn(null);

		HttpServletResponse res = mock(HttpServletResponse.class);

		when(res.getStatus()).thenReturn(200);

		FilterChain c = mock(FilterChain.class);

		String name = "foo";
		FilterConfig cfg = mock(FilterConfig.class);
		when(cfg.getInitParameter(RequestDurationFilter.METRIC_NAME_PARAM)).thenReturn(name);
		when(cfg.getInitParameter(RequestDurationFilter.PATH_COMPONENT_PARAM)).thenReturn("0");

		f.init(cfg);
		f.doFilter(req, res, c);

		verify(c).doFilter(req, res);

		final Double sampleValue = CollectorRegistry.defaultRegistry
				.getSampleValue(
						name + "_count",
						new String[]{"host", "code", "path", "method"},
						new String[]{"", "200", path, HttpMethod.GET.asString()});
		assertNotNull(sampleValue);
		assertEquals(1, sampleValue, 0.0001);
	}

	@Test
	public void testConstructor() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		final String path = "/foo/bar/baz/bang";
		when(req.getRequestURI()).thenReturn(path);
		when(req.getMethod()).thenReturn(HttpMethod.POST.asString());
		when(req.getHeader("Host")).thenReturn("test-host");

		FilterChain c = mock(FilterChain.class);
		doAnswer((Answer<Void>) invocationOnMock -> {
			Thread.sleep(100);
			return null;
		}).when(c).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

		RequestDurationFilter constructed = new RequestDurationFilter(
				"foobar_baz_filter_duration_seconds",
				"Help for my filter",
				0,
				null
		);
		constructed.init(mock(FilterConfig.class));

		HttpServletResponse res = mock(HttpServletResponse.class);

		when(res.getStatus()).thenReturn(200);

		constructed.doFilter(req, res, c);

		final Double sum = CollectorRegistry.defaultRegistry
				.getSampleValue(
						"foobar_baz_filter_duration_seconds_sum",
						new String[]{"host", "code", "path", "method"},
						new String[]{"test-host", "200", path, HttpMethod.POST.asString()});
		assertNotNull(sum);
		assertEquals(0.1, sum, 0.01);
	}

	@Test
	public void testBucketsAndName() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		final String path = "/foo/bar/baz/bang";
		when(req.getRequestURI()).thenReturn(path);
		when(req.getMethod()).thenReturn(HttpMethod.POST.asString());
		when(req.getHeader("Host")).thenReturn("test-host");

		FilterChain c = mock(FilterChain.class);
		doAnswer((Answer<Void>) invocationOnMock -> {
			Thread.sleep(100);
			return null;
		}).when(c).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

		final String buckets = "0.01,0.05,0.1,0.15,0.25";
		FilterConfig cfg = mock(FilterConfig.class);
		when(cfg.getInitParameter(RequestDurationFilter.BUCKET_CONFIG_PARAM)).thenReturn(buckets);
		when(cfg.getInitParameter(RequestDurationFilter.METRIC_NAME_PARAM)).thenReturn("foo");

		HttpServletResponse res = mock(HttpServletResponse.class);

		when(res.getStatus()).thenReturn(200);

		f.init(cfg);

		f.doFilter(req, res, c);

		final Double sum = CollectorRegistry.defaultRegistry
				.getSampleValue(
						"foo_sum",
						new String[]{"host", "code", "path", "method"},
						new String[]{"test-host", "200", "/foo", HttpMethod.POST.asString()});
		assertEquals(0.1, sum, 0.01);

		final Double le05 = CollectorRegistry.defaultRegistry
				.getSampleValue(
						"foo_bucket",
						new String[]{"host", "code", "path", "method", "le"},
						new String[]{"test-host", "200", "/foo", HttpMethod.POST.asString(), "0.05"});
		assertNotNull(le05);
		assertEquals(0, le05, 0.01);
		final Double le15 = CollectorRegistry.defaultRegistry
				.getSampleValue(
						"foo_bucket",
						new String[]{"host", "code", "path", "method", "le"},
						new String[]{"test-host", "200", "/foo", HttpMethod.POST.asString(), "0.15"});
		assertNotNull(le15);
		assertEquals(1, le15, 0.01);


		final Enumeration<Collector.MetricFamilySamples> samples = CollectorRegistry.defaultRegistry.metricFamilySamples();
		Collector.MetricFamilySamples sample = null;
		while (samples.hasMoreElements()) {
			sample = samples.nextElement();
			if (sample.name.equals("foo")) {
				break;
			}
		}

		assertNotNull(sample);

		int count = 0;
		for (Collector.MetricFamilySamples.Sample s : sample.samples) {
			if (s.name.equals("foo_bucket")) {
				count++;
			}
		}
		// +1 because of the final le=+infinity bucket
		assertEquals(buckets.split(",").length + 1, count);
	}
}
