package bitrate16.webserver.http;

import java.util.HashMap;

import bitrate16.webserver.ServerStateListener;

/**
 * Listener that allows binding different listeners on different URL paths
 * 
 * @author bitrate16
 *
 */
public class ServletListener extends HTTPListener implements ServerStateListener {

	private HashMap<String, Servlet> servlets = new HashMap<String, Servlet>();

	public ServletListener() {

	}

	@Override
	public void onStart() {
		for (Servlet s : servlets.values())
			if (s != null) {
				try {
					s.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	@Override
	public void onStop() {
		for (Servlet s : servlets.values())
			if (s != null) {
				try {
					s.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	}

	@Override
	public void onError(Exception e) {}

	@Override
	public void response(HTTPRequest request, HTTPResponse response) {
		Servlet s = servlets.get(request.getQuery().getQueryPath());
		if (s == null)
			return;
		s.response(request, response);
	}

	/**
	 * Adds servlet listening on specified path. Path = first part of the url
	 * http://[path]?param=value
	 * 
	 * @param requestPath
	 * @param s
	 */
	public void addServlet(String requestPath, Servlet s) {
		if (!requestPath.startsWith("/"))
			requestPath = "/" + requestPath;
		servlets.put(requestPath, s);
	}

	/**
	 * Removes servlet listening on specified path path = first part of the url
	 * http://[path]?param=value
	 * 
	 * @param requestPath
	 * @param s
	 */
	public void removeServlet(String requestPath) {
		servlets.remove(requestPath);
	}

	public static abstract class Servlet {
		public void start() {}

		public abstract void response(HTTPRequest request, HTTPResponse response);

		public void stop() {}
	}
}
