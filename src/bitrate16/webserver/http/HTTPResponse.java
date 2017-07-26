package bitrate16.webserver.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class HTTPResponse {
	protected OutputStream	out;
	protected Socket		socket;
	// Locked when status line was written
	private boolean			statusWritten;
	// Locked when response body was written
	private boolean			headersWritten;
	protected String		protocol;

	protected HTTPResponse() {}

	public void writeStatus(String code) throws IOException {
		if (statusWritten)
			return;
		out.write((protocol + " " + code).getBytes());
		out.write(Constants.END_OF_LINE);
		out.flush();
		statusWritten = true;
	}

	public void writeHeader(String key, String value) throws IOException {
		if (!statusWritten)
			throw new IllegalStateException("Status Line expected to be written first");
		if (headersWritten)
			throw new IllegalStateException("Headers can't be written after response body");
		out.write((key + ": " + value).getBytes());
		out.write(Constants.END_OF_LINE);
		out.flush();
	}

	public void writeHeader(String header) throws IOException {
		if (!statusWritten)
			throw new IllegalStateException("Status Line expected to be written first");
		if (headersWritten)
			throw new IllegalStateException("Headers can't be written after response body");
		if (header.indexOf(':') == -1)
			throw new IllegalArgumentException("Header expected to have value");
		out.write(header.getBytes());
		out.write(Constants.END_OF_LINE);
		out.flush();
	}

	public void write(byte[] data) throws IOException {
		if (!statusWritten)
			throw new IllegalStateException("Status Line expected to be written first");
		if (!headersWritten)
			out.write(Constants.END_OF_LINE);
		headersWritten = true;
		out.write(data);
		out.flush();
	}

	public void write(byte[] data, int off, int len) throws IOException {
		if (!statusWritten)
			throw new IllegalStateException("Status Line expected to be written first");
		if (!headersWritten)
			out.write(Constants.END_OF_LINE);
		headersWritten = true;
		out.write(data, off, len);
		out.flush();
	}

	public void write(String data) throws IOException {
		write(data.getBytes());
	}

	public void write(Object data) throws IOException {
		write((data == null ? null : data.toString()).getBytes());
	}

	public void close() throws IOException {
		if (isClosed())
			return;
		if (!statusWritten) { /* nothing */ } else {
			out.write(Constants.END_OF_LINE);
			out.flush();
		}
	}

	public Socket getSocket() {
		return this.socket;
	}

	public boolean isClosed() {
		return socket.isClosed();
	}
}
