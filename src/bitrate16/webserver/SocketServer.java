package bitrate16.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP layer of this server. Creates socket server on given port and listens to
 * all connections/disconnections, redirects handle of new connection to
 * listener in a new Thread.
 * 
 * @author bitrate16
 *
 */
@SuppressWarnings({ "deprecation" })
public final class SocketServer implements Runnable {
	private ServerSocket				socket;
	private ServerConnectionListener	connectionListener;
	private ServerStateListener			stateListener;
	private int							port	= -1;
	private Thread						listener;
	private boolean						running;
	private boolean						listening;
	private SocketServer				instance;

	public SocketServer() {
		this.instance = this;
		this.listener = new Thread(instance);
	}

	public SocketServer(int port) {
		this.port = port;
		this.instance = this;
		this.listener = new Thread(instance);
	}

	public void start() throws IOException {
		if (running)
			throw new IllegalStateException("Server already running");
		if (port == -1)
			throw new IllegalStateException("Port is not set");

		socket = new ServerSocket(port);
		running = true;
		listening = true;

		if (stateListener != null)
			stateListener.onStart();

		listener.start();
	}

	public void stop() {
		if (running = false)
			return;

		if (stateListener != null)
			stateListener.onStop();

		this.listening = false;
		this.running = false;
		try {
			// Let daemon Thread to release
			Thread.sleep(1000);
		} catch (InterruptedException e1) {}
		try {
			listener.stop();
		} catch (Exception e) {}
		try {
			socket.close();
		} catch (Exception e) {}
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setConnectionListener(ServerConnectionListener l) {
		this.connectionListener = l;
	}

	public void setStateListener(ServerStateListener l) {
		this.stateListener = l;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

	@Override
	public void run() {
		while (running) {
			if (listening) {
				Socket s = null;
				try {
					s = socket.accept();
				} catch (IOException e) {
					if (stateListener != null)
						stateListener.onError(e);
				}

				if (s != null) {
					try {
						final Socket c = s;
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (connectionListener != null)
									connectionListener.onConnection(c);
							}
						}).start();
					} catch (Exception e) {
						if (stateListener != null)
							stateListener.onError(e);
					}
				}

			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
	}
}
