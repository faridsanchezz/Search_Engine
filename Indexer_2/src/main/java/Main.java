import eventsWatcher.EventsWatcher;
import indexer.IndexerV2;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		String datalakePath = "/Users/nestoruniversidad/Desktop/test/datalake";
		String datamartPath = "/Users/nestoruniversidad/Desktop/test/datamart";

		// Call the EventsWatcher to watch the directory and index files
		try {
			EventsWatcher.watchDirectory(datalakePath, datamartPath, (datalake, datamart) -> {
				try {
					IndexerV2.invertedIndex(datalake, datamart);
				} catch (IOException e) {
					System.err.println("Error processing file: " + e.getMessage());
					e.printStackTrace();  // Log stack trace for detailed debugging
				}
			});
		} catch (IOException | InterruptedException e) {
			System.err.println("Error while watching the directory: " + e.getMessage());
		}
	}
}




