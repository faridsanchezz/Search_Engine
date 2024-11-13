package control;

import control.interfaces.ExtractorController;
import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import control.interfaces.WordCleaner;
import model.Metadata;
import model.Word;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {

		String wordsDatamart = "words.txt";
		String metadataDatamart = "metadata.txt";
		String datalake = "/Users/faridsanchez/Desktop/datalakeV1";

		SerializerController<Word> wordSerializer = new WordSerializer(wordsDatamart);
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer(metadataDatamart);

		StoreManager<Word> wordStoreManager = new WordStoreManager(wordsDatamart, wordSerializer);
		StoreManager<Metadata> metadataStoreManager = new MetadataStoreManager(metadataDatamart, metadataSerializer);

		WordCleaner wordCleaner = new WordCleaner1();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);

		IndexerV1 indexerV1 = new IndexerV1(wordStoreManager, metadataStoreManager, metadataExtractor, wordExtractor);

		EventsWatcher eventsWatcher = new EventsWatcher(datalake, indexerV1);
		eventsWatcher.startMonitoring();

	}
}
