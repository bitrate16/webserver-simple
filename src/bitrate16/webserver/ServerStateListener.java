package bitrate16.webserver;

public interface ServerStateListener {
	/**
	 * Called after server start
	 */
	public void onStart();

	/**
	 * Called before server stop
	 */
	public void onStop();

	public void onError(Exception e);
}
