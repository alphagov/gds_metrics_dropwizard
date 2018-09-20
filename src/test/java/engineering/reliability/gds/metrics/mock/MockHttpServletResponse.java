package engineering.reliability.gds.metrics.mock;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class MockHttpServletResponse implements HttpServletResponse {
	private final List<String> headerNames = new ArrayList<>();
	private final List<String> headerValues = new ArrayList<>();
	private PrintWriter pw;
	private int status;

	@Override
	public void addCookie(final Cookie cookie) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean containsHeader(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeURL(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public String encodeRedirectURL(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	@Deprecated
	public String encodeUrl(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	@Deprecated
	public String encodeRedirectUrl(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void sendError(final int i, final String s) throws IOException {

	}

	@Override
	public void sendError(final int i) throws IOException {

	}

	@Override
	public void sendRedirect(final String s) throws IOException {

	}

	@Override
	public void setDateHeader(final String s, final long l) {

	}

	@Override
	public void addDateHeader(final String s, final long l) {

	}

	@Override
	public void setHeader(final String s, final String s1) {

	}

	@Override
	public void addHeader(final String s, final String s1) {

	}

	@Override
	public void setIntHeader(final String s, final int i) {

	}

	@Override
	public void addIntHeader(final String s, final int i) {

	}

	@Override
	public String getCharacterEncoding() {
		throw new RuntimeException("Not implemented");
	}

	@Override

	public String getContentType() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (pw == null) {
			pw = new PrintWriter(new StringWriter());
		}

		return pw;
	}

	@Override
	public void setContentType(final String s) {

	}

	@Override
	public void setCharacterEncoding(final String s) {

	}

	@Override
	public void setContentLength(final int i) {

	}

	@Override
	public void setContentLengthLong(final long l) {

	}

	@Override
	public void setStatus(final int i) {
		status = i;
	}

	@Override
	@Deprecated
	public void setStatus(final int i, final String s) {

	}


	@Override
	public int getStatus() {
		return status == 0 ? 200 : status;
	}


	@Override
	public String getHeader(final String s) {
		final int index = headerNames.indexOf(s);

		if (index != -1) {
			return headerValues.get(index);
		}

		return null;
	}

	@Override
	public Collection<String> getHeaders(final String s) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Collection<String> getHeaderNames() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void setBufferSize(final int i) {

	}

	@Override
	public int getBufferSize() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void flushBuffer() throws IOException {

	}

	@Override
	public void resetBuffer() {

	}

	@Override
	public boolean isCommitted() {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void reset() {

	}

	@Override
	public void setLocale(final Locale locale) {

	}

	@Override
	public Locale getLocale() {
		throw new RuntimeException("Not implemented");
	}
}
