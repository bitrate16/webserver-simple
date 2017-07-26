package bitrate16.webserver.http.ActivePage;

import java.io.IOException;

import bitrate16.webserver.http.HTTPRequest;
import bitrate16.webserver.http.HTTPResponse;

/**
 * Active page, splitten by <%> tags. Each tag is being called in ActiveListener
 * for result page part string. Invoking build will build the entire page.
 * 
 * @author bitrate16
 *
 */
public class ActiveFragment {
	private String[]		parts;
	private ActiveListener	listener;

	public ActiveFragment(String code, ActiveListener listener) {
		this.parts = code.split("(<%>.*?<\\/%>)|(<%>)");
		this.listener = listener;
	}

	public ActiveFragment(String code) {
		this.parts = code.split("<%>");
	}

	public void setActiveListener(ActiveListener l) {
		this.listener = l;
	}

	public void response(HTTPRequest request, HTTPResponse response) throws IOException {
		for (int i = 0; i < parts.length; i++) {
			response.write(parts[i]);

			if (listener != null && i != parts.length - 1) {
				String res = listener.buildTag(i, request);
				response.write(res == null ? "" : res);
			}
		}
	}

	public String build(HTTPRequest request) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			sb.append(parts[i]);

			if (listener != null && i != parts.length - 1) {
				String res = listener.buildTag(i, request);
				sb.append(res == null ? "" : res);
			}
		}
		return sb.toString();
	}

	public interface ActiveListener {
		public String buildTag(int index, HTTPRequest request);
	}
}
