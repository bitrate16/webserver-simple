package bitrate16.webserver;

import java.net.Socket;

public interface ServerConnectionListener {
	public void onConnection(Socket client);
}
