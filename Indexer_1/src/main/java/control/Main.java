package control;

import control.interfaces.*;
import model.Metadata;
import model.Word;

import java.io.File;
import java.io.IOException;


public class Main {
	public static void main(String[] args) throws IOException {

		String datamartDirectory = "datamart\\";
		String datalakeDirectory = "datalake\\";

		DirectoryManager.createDirectory(new File(datamartDirectory));
		DirectoryManager.createDirectory(new File(datalakeDirectory));

		WordCleaner wordCleaner = new WordCleaner1();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();


		// Indexer V1
		SerializerController<Word> wordSerializerV1 = new WordSerializerV1();
		WordStoreManager<Word> wordStoreManagerV1 = new WordStoreManagerV1(datamartDirectory, wordSerializerV1);
		MetadataStoreManager<Metadata> metadataStoreManagerV1 = new MetadataStoreManagerV1(datamartDirectory, metadataSerializer);
		Indexer indexerV1 = new Indexer(wordStoreManagerV1, metadataStoreManagerV1, metadataExtractor, wordExtractor);
		EventsWatcher eventsWatcher = new EventsWatcher(datalakeDirectory, indexerV1);
		eventsWatcher.run();




/*

		// Indexer V2: Optimized
		SerializerController<Word.WordOccurrence> wordSerializerV2 = new WordSerializerV2();
		WordStoreManager<Word> wordStoreManagerV2 = new WordStoreManagerV2(datamartDirectory, wordSerializerV2);
		MetadataStoreManager<Metadata> metadataStoreManagerV2 = new MetadataStoreManagerV2(datamartDirectory, metadataSerializer);
		Indexer indexerV2 = new Indexer(wordStoreManagerV2, metadataStoreManagerV2, metadataExtractor, wordExtractor);
		EventsWatcher eventsWatcher = new EventsWatcher(datalakeDirectory, indexerV2);
		eventsWatcher.run();

 */



	}
}
