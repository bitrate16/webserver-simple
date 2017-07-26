package bitrate16.webserver.utils;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;

public abstract class ContextListener {
	private static ArrayList<ContextListener> listeners;
	{
		listeners = new ArrayList<ContextListener>();
		final ArrayList<ContextListener> const_listeners = listeners;

		Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				for (ContextListener l : const_listeners)
					if (l != null)
						try {
							l.exception(t, e);
						} catch (Exception ex) {}
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("stop");
				for (ContextListener l : const_listeners)
					if (l != null)
						l.shutdown();
			}
		}));
	}

	public static void addListener(ContextListener l) {
		if (l != null)
			listeners.add(l);
	}

	public abstract void exception(Thread t, Throwable e);

	public abstract void shutdown();
}
