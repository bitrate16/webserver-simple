package bitrate16.webserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import bitrate16.webserver.http.Constants;

public class StreamUtils {
	/**
	 * Read stream to out till the sequence of bytes (they're ignored)
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @param keepTimeout
	 *            true if server have to wait client for send data
	 * @throws IOException
	 */
	public static void readTill(InputStream in, OutputStream out, byte[] delimiter) throws IOException {
		readTill(in, out, delimiter, false);
	}

	/**
	 * Read stream to out till the sequence of bytes (they're ignored)
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @param keepTimeout
	 *            true if server have to wait client for send data
	 * @throws IOException
	 */
	public static void readTill(InputStream in, OutputStream out, byte[] delimiter, boolean keepTimeout)
			throws IOException {
		int delimiterIndex = 0;

		while (in.available() > 0) {
			while (in.available() > 0) {
				byte b = (byte) in.read();

				if (delimiterIndex >= delimiter.length - 1)
					return;

				if (b != delimiter[delimiterIndex]) {
					out.write(delimiter, 0, delimiterIndex);
					delimiterIndex = 0;
				}
				if (b == delimiter[delimiterIndex])
					delimiterIndex++;
				else
					out.write(b);
			}

			if (keepTimeout) {
				try {
					Thread.sleep(Constants.INPUTSTREAM_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		if (in.available() <= 0 && delimiterIndex != 0)
			out.write(delimiter, 0, delimiterIndex);
	}

	/**
	 * Read stream to NULL till the sequence of bytes (they're ignored)
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @throws IOException
	 */
	public static void emptyTill(InputStream in, byte[] delimiter) throws IOException {
		emptyTill(in, delimiter, false);
	}

	/**
	 * Read stream to NULL till the sequence of bytes (they're ignored)
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @param keepTimeout
	 *            true if server have to wait client for send data
	 * @throws IOException
	 */
	public static void emptyTill(InputStream in, byte[] delimiter, boolean keepTimeout) throws IOException {
		int delimiterIndex = 0;

		while (in.available() > 0) {
			while (in.available() > 0) {
				byte b = (byte) in.read();

				if (delimiterIndex >= delimiter.length - 1)
					return;

				if (b != delimiter[delimiterIndex])
					delimiterIndex = 0;

				if (b == delimiter[delimiterIndex])
					delimiterIndex++;
			}

			if (keepTimeout) {
				try {
					Thread.sleep(Constants.INPUTSTREAM_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Read full stream to out
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @throws IOException
	 */
	public static void dump(InputStream in, OutputStream out) throws IOException {
		dump(in, out, false);
	}

	/**
	 * Read full stream to out
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @param keepTimeout
	 *            true if server have to wait client for send data
	 * @throws IOException
	 */
	public static void dump(InputStream in, OutputStream out, boolean keepTimeout) throws IOException {
		byte[] buffer = new byte[Constants.BUFFER_SIZE];
		int read;

		// Read, wait, read if new data has arrived
		while (in.available() > 0) {
			while (in.available() > 0 && (read = in.read(buffer)) > 0)
				out.write(buffer, 0, read);

			if (keepTimeout) {
				try {
					Thread.sleep(Constants.INPUTSTREAM_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Read full stream to NULL
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @throws IOException
	 */
	public static void empty(InputStream in) throws IOException {
		empty(in, false);
	}

	/**
	 * Read full stream to NULL
	 * 
	 * @param in
	 * @param out
	 * @param delimiter
	 * @param keepTimeout
	 *            true if server have to wait client for send data
	 * @throws IOException
	 */
	public static void empty(InputStream in, boolean keepTimeout) throws IOException {
		while (in.available() > 0) {
			while (in.available() > 0)
				in.skip(in.available());

			if (keepTimeout) {
				try {
					Thread.sleep(Constants.INPUTSTREAM_TIMEOUT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
