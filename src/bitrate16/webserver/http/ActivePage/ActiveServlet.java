package bitrate16.webserver.http.ActivePage;

import java.io.IOException;

import bitrate16.webserver.http.HTTPRequest;
import bitrate16.webserver.http.HTTPResponse;
import bitrate16.webserver.http.ActivePage.ActiveFragment.ActiveListener;
import bitrate16.webserver.http.ServletListener.Servlet;

/**
 * Active servlet is a servlet, that supports on-request-page-building.
 * Inserting tag <%> into code will invoke method buildTag with index of this
 * tag. It must return string of code, that'll be inserted into page code.
 * 
 * @author bitrate16
 *
 */
public class ActiveServlet extends Servlet {
	private ActiveFragment fragment;

	public ActiveServlet(ActiveFragment f) {
		this.fragment = f;
	}

	public ActiveServlet(String code, ActiveListener listener) {
		fragment = new ActiveFragment(code, listener);
	}

	public ActiveServlet(String code) {
		fragment = new ActiveFragment(code);
	}

	public void setActiveListener(ActiveListener l) {
		fragment.setActiveListener(l);
	}

	@Override
	public void response(HTTPRequest request, HTTPResponse response) {
		try {
			response.writeStatus("200 OK");
			response.writeHeader("Connection", "close");
			response.writeHeader("Content-Type", "text/html");
			response.write(fragment.build(request));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
