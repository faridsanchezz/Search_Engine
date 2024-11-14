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

		String datamartDirectory = "";
		String datalakeDirectory = "";

		DirectoryManager.createDirectory(datamartDirectory);

		SerializerController<Word.WordOccurrence> wordSerializer = new WordSerializerV2();
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();

		StoreManager<Word> wordStoreManager = new WordStoreManagerV2(datamartDirectory, wordSerializer);
		StoreManager<Metadata> metadataStoreManager = new MetadataStoreManagerV2(datamartDirectory, metadataSerializer);

		WordCleaner wordCleaner = new WordCleaner1();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);

		IndexerV1 indexerV1 = new IndexerV1(wordStoreManager, metadataStoreManager, metadataExtractor, wordExtractor);

		EventsWatcher eventsWatcher = new EventsWatcher(datalakeDirectory, indexerV1);
		eventsWatcher.startMonitoring();

	}
}
