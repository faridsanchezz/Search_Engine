package eventsWatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.function.BiConsumer;

public class EventsWatcher {

	// Method to watch a directory and trigger indexing when changes occur
	public static void watchDirectory(String datalakePath, String datamartPath, BiConsumer<String, String> indexer) throws IOException, InterruptedException {
		Path path = Paths.get(datalakePath);
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);

		System.out.println("Observando el directorio: " + datalakePath);

		WatchKey key;
		while ((key = watchService.take()) != null) {
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();
				Path newPath = path.resolve((Path) event.context());
				File newFile = newPath.toFile();

				// Ignorar archivos ocultos y archivos que no son .txt
				if (newFile.isHidden() || !newFile.getName().endsWith(".txt")) {
					// Puedes quitar este println si no deseas que aparezca ningún mensaje para archivos ignorados
					continue;  // Saltar este archivo y pasar al siguiente
				}

				// Llamar al indexador si se detecta un archivo válido
				indexer.accept(newPath.toString(), datamartPath);
			}
			key.reset();
		}
	}
}
