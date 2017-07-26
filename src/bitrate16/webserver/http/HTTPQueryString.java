package bitrate16.webserver.http;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Query String parser. Produces array of parsed raw tokens.
 * 
 * @author bitrate16
 *
 */
public class HTTPQueryString {
	// Map of REQUEST parameters aka /a?foo=bar&too=tar
	private HashMap<String, String>	parameters	= new HashMap<String, String>();
	// Query PATH string line
	private String					queryLine	= "/";
	// HOST string (if was passed to parser)
	private String					host;

	/**
	 * Parses HTTP QUery from DECODED string
	 * 
	 * @param query
	 */
	private HTTPQueryString() {}

	/**
	 * Returns parameter with given name
	 */
	public String getParameter(String name) {
		return parameters.get(name);
	}

	/**
	 * Returns true if exists parameter with given name
	 */
	public boolean containsParameter(String name) {
		return parameters.containsKey(name);
	}

	/**
	 * Sets parameter with given name
	 */
	public void setParameter(String name, String value) {
		parameters.put(name, value);
	}

	/**
	 * Removes parameter with given name
	 */
	public void removeParameter(String name) {
		parameters.remove(name);
	}

	/**
	 * Returns KeyList of parameters
	 */
	public Set<String> keys() {
		return parameters.keySet();
	}

	/**
	 * Returns parameters Map
	 */
	public HashMap<String, String> getParameters() {
		return parameters;
	}

	/**
	 * Returns HOSTNAME if it was passed into constructor [aka
	 * http://mysite.com]
	 */
	public String getHostName() {
		return host;
	}

	/**
	 * Sets HOSTNAME
	 */
	public void setHostName(String host) {
		this.host = host;
	}

	/**
	 * Returns QUERY PATH line
	 */
	public String getQueryPath() {
		return queryLine;
	}

	/**
	 * Sets QUERY PATH
	 * 
	 * @return
	 */
	public void setQueryPath(String queryLine) {
		this.queryLine = queryLine;
	}

	@SuppressWarnings("deprecation")
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (host != null)
			sb.append(host);
		if (queryLine != null)
			sb.append(queryLine); // XXX: Encode QueryLine
		// else if (parameters.size() != 0)
		// sb.append('/');

		if (parameters.size() == 0)
			return sb.toString();
		sb.append('?');
		ArrayList<String> keys = new ArrayList<String>(keys());
		for (int i = 0; i < keys.size(); i++) {
			String k = keys.get(i);
			sb.append(URLEncoder.encode(k) + "=" + URLEncoder.encode(getParameter(k)));
			if (i < keys.size() - 1)
				sb.append('&');
		}
		return sb.toString();
	}

	// Parses [http://website.com/a/b/d/c/foo] block
	@SuppressWarnings("deprecation")
	private static String preParse(HTTPQueryString qr, String query) {
		if (query.startsWith("http://") || query.startsWith("https://")) {
			// Expected host name
			int breakout = -1;
			int slash = 0;
			for (int i = 0; i < query.length(); i++) {
				if (query.charAt(i) == '/')
					slash++;
				if (slash == 3) { // <-- match end of URL
					breakout = i;
					break;
				}
				// \/-- invalid URL, deal with it
				if (query.charAt(i) == '?' || query.charAt(i) == '&') {
					breakout = i;
					break;
				}
			}
			if (breakout == -1) {
				qr.host = query;
				query = "/";
			} else {
				qr.host = query.substring(0, breakout);
				query = query.substring(breakout);
			}
		} else if (!query.startsWith("/")) {
			// Expected web site name
			int breakout = -1;
			int slash = 0;
			for (int i = 0; i < query.length(); i++) {
				if (query.charAt(i) == '/')
					slash++;
				if (slash == 1) { // <-- match end of URL
					breakout = i;
					break;
				}
				// \/-- invalid URL, deal with it
				if (query.charAt(i) == '&' || query.charAt(i) == '?') {
					breakout = i;
					break;
				}
			}
			if (breakout == -1) {
				qr.host = "http://" + query;
				query = "/";
			} else {
				qr.host = "http://" + query.substring(0, breakout);
				query = query.substring(breakout);
			}
		}
		if (qr.host != null)
			qr.host = URLDecoder.decode(qr.host);

		// Read String untill match ? or &
		int breakout = -1;
		for (int i = 0; i < query.length(); i++) {
			if (query.charAt(i) == '&' || query.charAt(i) == '?') {
				breakout = i;
				break;
			}
		}
		if (breakout == -1) {
			// XXX: Add regex pattern checker and hard parser
			// Assuming string is correct
			qr.queryLine = query;
			return null;
		}
		qr.queryLine = query.substring(0, breakout);
		query = query.substring(breakout);

		return query;
	}

	// Post parse of [/a?foo/bar&doo=tah] block. Parameters are encoded
	@SuppressWarnings("deprecation")
	private static void postParse(HTTPQueryString qr, String query) {
		// Parse parameters
		String[] pairs = query.split("[?&]");
		for (String p : pairs) {
			String[] pair = p.split("=", 2);
			if (pair.length == 0 || pair.length == 1)
				continue;
			pair[0] = URLDecoder.decode(pair[0]);
			pair[1] = URLDecoder.decode(pair[1]);
			qr.parameters.put(pair[0], pair[1]);
		}
	}

	/**
	 * Produces HTTPQusery parsed String from URLENCODED string aka
	 * [http://mywebsite.com]/page/new/a?foo=%20bar%20is%20foo
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static HTTPQueryString parse(String query) {
		HTTPQueryString qr = new HTTPQueryString();

		if (query == null)
			return qr;

		query = preParse(qr, query);
		// Decode URL body
		qr.queryLine = URLDecoder.decode(qr.queryLine);
		if (query == null)
			return qr;

		postParse(qr, query);

		return qr;
	}

	/**
	 * Produces HTTPQusery parsed String from RAW string aka
	 * [http://mywebsite.com]/page/new/a?foo= bar is foo
	 * 
	 * @param query
	 * @return
	 */
	// public static HTTPQueryString raw(String query) {
	// return new HTTPQueryString(query);
	// }
}
