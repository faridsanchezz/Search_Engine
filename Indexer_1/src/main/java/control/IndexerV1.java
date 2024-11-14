package control;

import control.interfaces.ExtractorController;
import control.interfaces.StoreManager;
import model.Metadata;
import model.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;


public class IndexerV1 {

	private final StoreManager<Word> wordStoreManager;
	private final StoreManager<Metadata> metadataStoreManager;
	private final ExtractorController<Metadata> metadataExtractor;
	private final ExtractorController<Word> wordExtractor;


	public IndexerV1(StoreManager<Word> wordStoreManager, StoreManager<Metadata> metadataStoreManager, ExtractorController<Metadata> metadataExtractor, ExtractorController<Word> wordExtractor) throws IOException {

		this.wordStoreManager = wordStoreManager;
		this.metadataStoreManager = metadataStoreManager;
		this.metadataExtractor = metadataExtractor;
		this.wordExtractor = wordExtractor;
	}

	public void execute(Path bookDatalakePath) throws IOException {
		Set<Metadata> metadataBook;
		Set<Word> wordSet;

		try (BufferedReader book = new BufferedReader(new FileReader(bookDatalakePath.toFile()))) {
			book.mark(5 * 1024 * 1024);
			metadataBook = metadataExtractor.get(book, bookDatalakePath.getFileName().toString());
			metadataStoreManager.update(metadataBook.iterator().next());
			book.reset();
			wordSet = wordExtractor.get(book, metadataBook.iterator().next().getBookID());
			for (Word word : wordSet) {
				wordStoreManager.update(word);
			}
			System.out.println("finish with exit");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}




