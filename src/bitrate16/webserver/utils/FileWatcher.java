package bitrate16.webserver.utils;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

public class FileWatcher {
	private WatcherListener listener;

	public void setListener(WatcherListener l) {
		this.listener = l;
	}

	public static FileWatcher addWatcher(String filepath) {
		return addWatcher(new File(filepath));
	}

	public static FileWatcher addWatcher(final File folder) {
		final FileWatcher watcher = new FileWatcher();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Path path = FileSystems.getDefault().getPath(folder.getPath());

				try (final WatchService watchService = FileSystems.getDefault().newWatchService()) {
					path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

					while (true) {
						final WatchKey wk = watchService.take();
						for (WatchEvent<?> event : wk.pollEvents()) {
							final Path changed = (Path) event.context();
							if (watcher.listener != null)
								watcher.listener.changed(changed.toFile());
						}
						wk.reset();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		return watcher;
	}

	public static interface WatcherListener {
		public void changed(File file);
	}

	public static abstract class SingleWatcherListener implements WatcherListener {
		private String file;

		public SingleWatcherListener(String file) {
			this.file = file;
		}

		public SingleWatcherListener() {
			this(null);
		}

		public void setFIle(String filename) {
			this.file = filename;
		}

		@Override
		public void changed(File file) {
			String filename = file.getName();
			if (filename.equals(this.file))
				changed(file, filename);
		}

		public abstract void changed(File file, String filename);
	}

	public static abstract class FilteredWatcherListener implements WatcherListener {
		private ArrayList<String> files;

		public FilteredWatcherListener(ArrayList<String> files) {
			this.files = files;
		}

		public FilteredWatcherListener() {
			this(new ArrayList<String>());
		}

		public void addFile(String filename) {
			this.files.add(filename);
		}

		public void removeFile(String filename) {
			this.files.remove(filename);
		}

		@Override
		public void changed(File file) {
			String filename = file.getName();
			for (String f : files)
				if (filename.equals(f))
					changed(file, f);
		}

		public abstract void changed(File file, String filename);
	}
}
