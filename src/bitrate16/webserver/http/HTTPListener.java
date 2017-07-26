package bitrate16.webserver.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.URLDecoder;

import bitrate16.webserver.ServerConnectionListener;
import bitrate16.webserver.utils.StreamUtils;

public abstract class HTTPListener implements ServerConnectionListener {
	@SuppressWarnings("deprecation")
	@Override
	public void onConnection(Socket client) {
		try {
			// Read all headers + status-line till request body (double
			// \r\n\r\n)
			InputStream stream = client.getInputStream();

			// Wait untill data will be received
			long now = System.currentTimeMillis();
			while (true) {
				int available = 0;
				if (System.currentTimeMillis() - now >= Constants.TIMEOUT) {
					stream.close();
					throw new IllegalStateException("Request TIMEOUT");
				} else if ((available = stream.available()) == 0) {
					Thread.sleep(1);
					continue;
				} else if (available == -1)
					throw new IllegalStateException("Stream closed");
				else
					break;
			}

			// Read data
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.readTill(stream, baos, Constants.END_OF_BLOCK);

			String[] lines = baos.toString().split(Constants.END_OF_LINE_STRING);
			String[] statusLine = lines[0].split(" ");

			HTTPRequest request = new HTTPRequest();
			request.socket = client;

			request.setHeader("Method", statusLine[0]);
			request.setHeader("Query", statusLine[1]);
			request.setHeader("Protocol", statusLine[2]);
			request.setHeader("Addres", client.getRemoteSocketAddress().toString());

			request.query = HTTPQueryString.parse(statusLine[1]);

			for (int i = 1; i < lines.length; i++) {
				if (lines[i].isEmpty())
					continue;
				String[] pair = lines[i].split(":");
				// Header parameters had to be decoded
				request.setHeader(pair[0].trim(), URLDecoder.decode(pair[1].trim()));
			}
			HTTPResponse response = new HTTPResponse();
			response.out = client.getOutputStream();
			response.socket = client;
			response.protocol = statusLine[2];

			response(request, response);

			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract void response(HTTPRequest request, HTTPResponse response);
}
