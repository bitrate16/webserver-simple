package bitrate16.webserver.utils;

import java.util.ArrayList;

/**
 * Invokes listeners backup every interval
 * 
 * @author bitrate16
 *
 */
public class BackupService {
	private Thread						backupThread;
	private long						backupInterval;
	private ArrayList<BackupListener>	listeners;
	private boolean						log;
	private boolean						running;
	private int							attempt;

	public BackupService(long backupInterval) {
		this.backupInterval = backupInterval;
		listeners = new ArrayList<BackupListener>();
	}

	public void start() {
		if (backupThread != null)
			return;
		running = true;
		backupThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!running) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						try {
							Thread.sleep(backupInterval);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (log)
							Logger.log("BackupService", "Backup #" + attempt);
						synchronized (listeners) {
							for (BackupListener l : listeners)
								if (l != null) {
									try {
										l.backup(attempt);
									} catch (Exception e) {}
								}
						}
						attempt++;
					}
				}
			}
		});
		backupThread.start();
	}

	public void pause() {
		this.running = false;
	}

	public void resume() {
		this.running = true;
	}

	public void changeInterval(long backupInterval) {
		this.backupInterval = backupInterval;
	}

	public void log(boolean log) {
		this.log = log;
	}

	public void addListener(BackupListener l) {
		this.listeners.add(l);
	}

	public void backup() {
		if (log)
			System.out.println("Backup #" + attempt);
		synchronized (listeners) {
			for (BackupListener l : listeners)
				if (l != null) {
					try {
						l.backup(attempt);
					} catch (Exception e) {}
				}
		}
		attempt++;
	}

	/**
	 * Listener for backup. backup() is invoked every time backupInterval period
	 * ends
	 * 
	 * @author bitrate16
	 *
	 */
	public static interface BackupListener {
		/**
		 * 
		 * @param attempt
		 *            is number of backups was called
		 */
		public void backup(int attempt);
	}
}
