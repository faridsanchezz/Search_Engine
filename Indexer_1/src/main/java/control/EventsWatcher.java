package control;

import java.io.IOException;
import java.nio.file.*;

public class EventsWatcher implements Runnable {

	private final Path datalakeDirectory;
	private final WatchService watchService;
	private final Indexer indexer;

	public EventsWatcher(String datalakeDirectory, Indexer indexer) throws IOException {
		this.datalakeDirectory = Paths.get(datalakeDirectory);
		this.indexer = indexer;
		this.watchService = FileSystems.getDefault().newWatchService();
		this.datalakeDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
	}

	public void startMonitoring() {
		System.out.println("Monitoring directory: " + datalakeDirectory);

		while (true) {
			WatchKey key;
			try {
				key = watchService.take();
			} catch (InterruptedException e) {
				System.out.println("Directory monitoring interrupted");
				Thread.currentThread().interrupt();
				break;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path fileName = ev.context();
				Path filePath = datalakeDirectory.resolve(fileName);

				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					System.out.println("New book detected: " + filePath);
					onTxtFileDetected(filePath);
				}
			}

			boolean valid = key.reset();
			if (!valid) {
				System.out.println("WatchKey no longer valid, stopping monitoring");
				break;
			}
		}
	}

	private void onTxtFileDetected(Path filePath) {
		System.out.println("Processing new book: " + filePath);
		try {
			indexer.execute(filePath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void run() {
		startMonitoring();
	}
}
