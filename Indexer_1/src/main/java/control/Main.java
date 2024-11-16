package control;

import control.interfaces.*;
import model.Metadata;
import model.Word;

import java.io.File;
import java.io.IOException;


public class Main {
	public static void main(String[] args) throws IOException {

		// Paths for Docker
		String datamartDirectory = "/app/datamart/";
		String datalakeDirectory = "/app/datalake/";

		DirectoryManager.createDirectory(new File(datamartDirectory));
		DirectoryManager.createDirectory(new File(datalakeDirectory));

		control.interfaces.WordCleaner wordCleaner = new WordCleaner();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();
/*
		// Indexer One File
		SerializerController<Word> wordSerializerOneFile = new WordSerializerOneFile();
		WordStoreManager<Word> wordStoreManagerOneFile = new WordStoreManagerOneFile(datamartDirectory, wordSerializerOneFile);
		MetadataStoreManager<Metadata> metadataStoreManagerOneFile = new MetadataStoreManagerOneFile(datamartDirectory, metadataSerializer);
		Indexer indexerOneFile = new Indexer(wordStoreManagerOneFile, metadataStoreManagerOneFile, metadataExtractor, wordExtractor);
		EventsWatcher eventsWatcher = new EventsWatcher(datalakeDirectory, indexerOneFile);
		eventsWatcher.run();

 */


		// Indexer File Per Word
		SerializerController<Word.WordOccurrence> wordSerializerFilePerWord = new WordSerializerFilePerWord();
		WordStoreManager<Word> wordStoreManagerFilePerWord = new WordStoreManagerFilePerWord(datamartDirectory, wordSerializerFilePerWord);
		MetadataStoreManager<Metadata> metadataStoreManagerFilePerWord = new MetadataStoreManagerFilePerWord(datamartDirectory, metadataSerializer);
		Indexer indexerFilePerWord = new Indexer(wordStoreManagerFilePerWord, metadataStoreManagerFilePerWord, metadataExtractor, wordExtractor);
		EventsWatcher eventsWatcher = new EventsWatcher(datalakeDirectory, indexerFilePerWord);
		eventsWatcher.run();



	}
}
