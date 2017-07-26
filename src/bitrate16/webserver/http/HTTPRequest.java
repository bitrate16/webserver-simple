package bitrate16.webserver.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import bitrate16.webserver.utils.StreamUtils;

public class HTTPRequest {
	protected HashMap<String, String>	headings	= new HashMap<String, String>();
	protected HTTPQueryString			query;
	protected Socket					socket;

	private InputStream					cacheStream;
	private boolean						cached;
	// Last redden entity
	private HTTPEntity					lastEntity;

	protected HTTPRequest() {}

	public void setHeader(String key, String value) {
		this.headings.put(key, value);
	}

	public String getHeader(String key) {
		return this.headings.get(key);
	}

	public boolean containsHeader(String key) {
		return this.headings.containsKey(key);
	}

	public void setParameter(String key, String value) {
		this.query.setParameter(key, value);
	}

	public String getParameter(String key) {
		return this.query.getParameter(key);
	}

	public boolean containsParameter(String key) {
		return this.query.containsParameter(key);
	}

	public Set<String> headerKeys() {
		return headings.keySet();
	}

	public Set<String> parameterKeys() {
		return this.query.keys();
	}

	public HTTPQueryString getQuery() {
		return this.query;
	}

	public InputStream getStream() throws IOException {
		if (isCached())
			return cacheStream;
		return socket.getInputStream();
	}

	public Socket getSocket() {
		return this.socket;
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	/**
	 * Will cache request body into byte array
	 * 
	 * @throws IOException
	 */
	public void cache() throws IOException {
		if (cached)
			return;
		InputStream is = socket.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[Constants.BUFFER_SIZE];
		int read;
		while (!isClosed() && (read = is.read(buffer)) > 0)
			baos.write(buffer, 0, read);
		cached = true;
		cacheStream = new CachedStream(baos.toByteArray());
	}

	public byte[] getCache() {
		if (cached)
			return ((CachedStream) cacheStream).getCache();
		return null;
	}

	public boolean isCached() {
		return cached;
	}

	protected int available() throws IOException {
		if (!isCached())
			return socket.getInputStream().available();
		else
			return cacheStream.available();
	}

	/**
	 * Returns already aprsed Entity
	 * 
	 * @return
	 * @throws IOException
	 */
	public HTTPEntity getEntity() throws IOException {
		return lastEntity;
	}

	/*
	 * Returns POST body Entity. If using multipart/form-data, first and last
	 * entities are empty
	 */
	public HTTPEntity nextEntity() throws IOException {
		if (lastEntity != null && !lastEntity.isRedden())
			throw new IllegalStateException("Previous Entity expected to be redden");

		if (available() > 0)
			return new HTTPEntity(this);

		return null;
	}

	/**
	 * Custom cached InputStream for reading data
	 */
	private class CachedStream extends InputStream {
		private byte[]	cache;
		private int		cachePos;

		public CachedStream(byte[] cache) {
			this.cache = cache;
		}

		@Override
		public int read() throws IOException {
			if (cachePos < cache.length)
				return cache[cachePos++];
			else
				return -1;
		}

		@Override
		public int available() {
			return cache.length - 1 - cachePos;
		}

		public byte[] getCache() {
			return cache;
		}
	}

	/**
	 * HTTP body entity. Used in POST requests
	 * 
	 * @author bitrate16
	 *
	 */
	public class HTTPEntity {
		private boolean					redden;
		private byte[]					data;
		private HashMap<String, String>	headers	= new HashMap<String, String>();
		private HTTPRequest				request;
		protected byte[]				delimiter;
		private String[]				type;

		protected HTTPEntity(HTTPRequest response) throws IOException {
			this.request = response;
			this.type = response.getHeader("Content-Type").split(";");

			if (this.type[0].startsWith("multipart"))
				delimiter = (Constants.END_OF_LINE_STRING + "--" + this.type[1].split("=")[1]).getBytes();
			// or application/x-www-form-encoded with no delimiter

			readHeaders();
		}

		private void readHeaders() throws IOException {
			if (!this.type[0].startsWith("multipart"))
				return;
			// Read data
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StreamUtils.readTill(request.getStream(), baos, Constants.END_OF_BLOCK);

			String[] lines = baos.toString().split(Constants.END_OF_LINE_STRING);

			for (int i = 0; i < lines.length; i++) {
				if (lines[i].isEmpty())
					continue;
				String[] pair = lines[i].split(":");
				if (pair.length != 2)
					continue;
				setHeader(pair[0].trim(), pair[1].trim());
			}
		}

		/**
		 * Stream read into OutputStream
		 * 
		 * @return
		 * @throws IOException
		 */
		public void read(OutputStream os) throws IOException {
			if (redden)
				throw new IllegalStateException("Entity already redden");
			if (this.type[0].startsWith("multipart"))
				StreamUtils.readTill(request.getStream(), os, delimiter);
			else
				StreamUtils.dump(request.getStream(), os, true);
		}

		/**
		 * Stream redden into byte array, array stored in this class
		 * 
		 * @return
		 * @throws IOException
		 */
		public byte[] read() throws IOException {
			if (redden)
				throw new IllegalStateException("Entity already redden");
			if (data != null)
				return data;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			read(baos);

			return data = baos.toByteArray();
		}

		/**
		 * Reads stream till delimiter, do not save data
		 * 
		 * @throws IOException
		 */
		public void empty() throws IOException {
			if (redden)
				throw new IllegalStateException("Entity already redden");

			if (this.type[0].startsWith("multipart"))
				StreamUtils.emptyTill(request.getStream(), delimiter);
			else
				StreamUtils.empty(request.getStream());
		}

		/**
		 * Returns redden data
		 * 
		 * @return
		 */
		public byte[] getData() {
			return data;
		}

		public void setHeader(String key, String value) {
			this.headers.put(key, value);
		}

		public String getHeader(String key) {
			return this.headers.get(key);
		}

		public boolean isRedden() {
			return redden;
		}
	}
}
