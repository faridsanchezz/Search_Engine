import control.*;
import control.interfaces.*;
import model.Metadata;
import model.Word;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IndexerBenchmarkTest {

	private static final String DATAMART_DIRECTORY = "datamart/";
	private static final String DATALAKE_DIRECTORY = "datalake/";
	private static final int[] BOOK_COUNTS = {10, 50, 100, 200};

	@BeforeEach
	void prepareEnvironment() throws IOException {
		cleanDatamart();
		DirectoryManager.createDirectory(new File(DATAMART_DIRECTORY));
		DirectoryManager.createDirectory(new File(DATALAKE_DIRECTORY));
	}

	@AfterEach
	void cleanDatamart() {
		File datamartDir = new File(DATAMART_DIRECTORY);
		if (datamartDir.exists()) {
			for (File file : datamartDir.listFiles()) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
	}

	private void deleteDirectory(File directory) {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		directory.delete();
	}

	@Test
	@Order(1)
	void benchmarkIndexerV1() throws IOException {
		System.out.println("Benchmarking Indexer V1...");
		long[] executionTimes = new long[BOOK_COUNTS.length];

		for (int i = 0; i < BOOK_COUNTS.length; i++) {
			int bookCount = BOOK_COUNTS[i];
			executionTimes[i] = measureExecutionTime(() -> {
				try {
					runIndexerV1(bookCount);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			System.out.println("Indexer V1: " + bookCount + " books -> " + (executionTimes[i] / 1_000_000) + " ms");
		}

		assertTrue(executionTimes[0] < executionTimes[1], "Time should be higher with more books.");
	}

	@Test
	@Order(2)
	void benchmarkIndexerV2() throws IOException {
		System.out.println("Benchmarking Indexer V2...");
		long[] executionTimes = new long[BOOK_COUNTS.length];

		for (int i = 0; i < BOOK_COUNTS.length; i++) {
			int bookCount = BOOK_COUNTS[i];
			executionTimes[i] = measureExecutionTime(() -> {
				try {
					runIndexerV2(bookCount);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			System.out.println("Indexer V2: " + bookCount + " books -> " + (executionTimes[i] / 1_000_000) + " ms");
		}

		assertTrue(executionTimes[0] < executionTimes[1], "Time should be higher with more books.");
	}

	private void runIndexerV1(int bookCount) throws IOException {
		WordCleaner wordCleaner = new WordCleaner1();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();
		SerializerController<Word> wordSerializerV1 = new WordSerializerV1();
		WordStoreManager<Word> wordStoreManagerV1 = new WordStoreManagerV1(DATAMART_DIRECTORY, wordSerializerV1);
		MetadataStoreManager<Metadata> metadataStoreManagerV1 = new MetadataStoreManagerV1(DATAMART_DIRECTORY, metadataSerializer);
		Indexer indexerV1 = new Indexer(wordStoreManagerV1, metadataStoreManagerV1, metadataExtractor, wordExtractor);

		processBooks(indexerV1, bookCount);
	}

	private void runIndexerV2(int bookCount) throws IOException {
		WordCleaner wordCleaner = new WordCleaner1();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);
		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();
		SerializerController<Word.WordOccurrence> wordSerializerV2 = new WordSerializerV2();
		WordStoreManager<Word> wordStoreManagerV2 = new WordStoreManagerV2(DATAMART_DIRECTORY, wordSerializerV2);
		MetadataStoreManager<Metadata> metadataStoreManagerV2 = new MetadataStoreManagerV2(DATAMART_DIRECTORY, metadataSerializer);
		Indexer indexerV2 = new Indexer(wordStoreManagerV2, metadataStoreManagerV2, metadataExtractor, wordExtractor);

		processBooks(indexerV2, bookCount);
	}

	private void processBooks(Indexer indexer, int bookCount) throws IOException {
		for (int i = 0; i < bookCount; i++) {
			Path bookPath = generateDummyBook(i);
			indexer.execute(bookPath);
		}
	}

	private Path generateDummyBook(int index) throws IOException {
		File bookFile = new File(DATALAKE_DIRECTORY + "book" + index + ".txt");
		if (!bookFile.exists()) {
			bookFile.createNewFile();
			Files.writeString(bookFile.toPath(), generateDummyContent(index));
		}
		return bookFile.toPath();
	}

	private String generateDummyContent(int index) {
		return "Title: Book " + index + "\n" +
				"Author: Author " + index + "\n" +
				"Language: English\n" +
				"Release date: 2023\n" +
				"This is a dummy content for benchmarking book " + index + ".";
	}

	private long measureExecutionTime(Runnable task) {
		long startTime = System.nanoTime();
		task.run();
		return System.nanoTime() - startTime;
	}
}
