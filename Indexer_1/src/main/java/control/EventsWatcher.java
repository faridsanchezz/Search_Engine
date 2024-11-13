package control;

import java.io.IOException;
import java.nio.file.*;

public class EventsWatcher {

	private final Path directoryPath;
	private final WatchService watchService;
	private final IndexerV1 indexerV1;

	public EventsWatcher(String directoryPath, IndexerV1 indexerV1) throws IOException {
		this.directoryPath = Paths.get(directoryPath);
		this.indexerV1 = indexerV1;

		// Ensure the directory exists; create it if it doesn't
		if (!Files.exists(this.directoryPath)) {
			Files.createDirectories(this.directoryPath);
			System.out.println("Directory created: " + this.directoryPath);
		} else {
			System.out.println("Directory already exists: " + this.directoryPath);
		}

		this.watchService = FileSystems.getDefault().newWatchService();
		this.directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
	}

	public void startMonitoring() {
		System.out.println("Monitoring directory: " + directoryPath);

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
				Path filePath = directoryPath.resolve(fileName);

				if (kind == StandardWatchEventKinds.ENTRY_CREATE && fileName.toString().endsWith(".txt")) {
					System.out.println("New .txt file detected: " + filePath);
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
		System.out.println("Processing the new .txt file: " + filePath);
		try {
			indexerV1.execute(String.valueOf(filePath));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
