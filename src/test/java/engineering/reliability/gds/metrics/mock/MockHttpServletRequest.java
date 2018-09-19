package engineering.reliability.gds.metrics.mock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public class MockHttpServletRequest implements HttpServletRequest {

	private final Map<String, List<String>> headers = new HashMap<>();
	private String requestURI;

	@Override
	public String getAuthType() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Cookie[] getCookies() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public long getDateHeader(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getHeader(final String s) {
		final List<String> list = headers.get(s);

		if (Objects.nonNull(list)) {
			return list.stream().findFirst().orElse(null);
		}

		return null;
	}

	@Override
	public Enumeration<String> getHeaders(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getIntHeader(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getMethod() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getPathInfo() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getPathTranslated() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getContextPath() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getQueryString() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteUser() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isUserInRole(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Principal getUserPrincipal() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRequestedSessionId() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getServletPath() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HttpSession getSession(final boolean b) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public HttpSession getSession() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String changeSessionId() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	@Deprecated
	public boolean isRequestedSessionIdFromUrl() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean authenticate(final HttpServletResponse httpServletResponse) throws IOException, ServletException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void login(final String s, final String s1) throws ServletException {

	}

	@Override
	public void logout() throws ServletException {

	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Part getPart(final String s) throws IOException, ServletException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public <T extends HttpUpgradeHandler> T upgrade(final Class<T> aClass) throws IOException, ServletException {
		throw new RuntimeException("Not implemented");
	}

	public void setRequestURI(final String uri) {
		requestURI = uri;
	}

	@Override
	public Object getAttribute(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setCharacterEncoding(final String s) throws UnsupportedEncodingException {

	}

	@Override
	public int getContentLength() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public long getContentLengthLong() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getContentType() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getParameter(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Enumeration<String> getParameterNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String[] getParameterValues(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getProtocol() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getScheme() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getServerName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getServerPort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public BufferedReader getReader() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteAddr() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getRemoteHost() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setAttribute(final String s, final Object o) {

	}

	@Override
	public void removeAttribute(final String s) {

	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Enumeration<Locale> getLocales() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isSecure() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public RequestDispatcher getRequestDispatcher(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	@Deprecated
	public String getRealPath(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getRemotePort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getLocalName() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String getLocalAddr() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public int getLocalPort() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ServletContext getServletContext() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) throws IllegalStateException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isAsyncStarted() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isAsyncSupported() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public AsyncContext getAsyncContext() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public DispatcherType getDispatcherType() {
		throw new RuntimeException("Not implemented");
	}

	public void setHeader(final String name, final String value) {
		final List<String> values = new ArrayList<>();
		values.add(value);
		headers.put(name, values);
	}
}
