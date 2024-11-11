package control;

import model.Metadata;
import model.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class IndexerV1 {

	private final String datalakePath;
	private final StoreManager<Word> wordStoreManager;
	private final StoreManager<Metadata> metadataStoreManager;




	public IndexerV1(String datalakePath, StoreManager<Word> wordStoreManager, StoreManager<Metadata> metadataStoreManager) throws IOException {

		this.datalakePath = datalakePath;
		this.wordStoreManager = wordStoreManager;
		this.metadataStoreManager = metadataStoreManager;
	}

	public void execute() throws IOException {
		Metadata metadataBook;
		Set<Word> wordSet;

		try (BufferedReader book = new BufferedReader(new FileReader(this.datalakePath))) {
			book.mark(5 * 1024 * 1024);
			metadataBook = MetadataExtractor.getMetadata(book, this.datalakePath);
			book.reset();
			wordSet = WordExtractor.getWords(book, metadataBook.getBookID());
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		metadataStoreManager.update(metadataBook);

		for (Word word : wordSet) {
			wordStoreManager.update(word);
		}



	}

}




