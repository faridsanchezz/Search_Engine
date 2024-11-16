package indexerBenchmark;

import control.*;
import control.WordCleaner;
import control.interfaces.*;
import model.*;
import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)

public class IndexerBenchmarkTest {

	@State(Scope.Benchmark)
	public static class BenchmarkState {

		@Param({"", "", "", ""})
		private String bookDirectory;
		private List<Path> bookPathList;

		// indexerOneFile
		private String datamartIndexerOneFile = "";

		SerializerController<Metadata> metadataSerializer = new MetadataSerializer();
		SerializerController<Word> wordSerializerOneFile = new WordSerializerOneFile();
		WordCleaner wordCleaner = new WordCleaner();
		ExtractorController<Metadata> metadataExtractor = new MetadataExtractor();
		ExtractorController<Word> wordExtractor = new WordExtractor(wordCleaner);
		WordStoreManager<Word> wordStoreManagerOneFile;
		MetadataStoreManager<Metadata> metadataStoreManagerOneFile;

		// indeverFilePerWord
		private String getDatamartIndexerFilePerWord = "";
		SerializerController<Word.WordOccurrence> wordSerializerFilePerWord = new WordSerializerFilePerWord();
		WordStoreManager<Word> wordStoreManagerFilePerWord;
		MetadataStoreManager<Metadata> metadataStoreManagerFilePerWord;

		{
			try {
				wordStoreManagerOneFile = new WordStoreManagerOneFile(datamartIndexerOneFile, wordSerializerOneFile);
				metadataStoreManagerOneFile = new MetadataStoreManagerOneFile(datamartIndexerOneFile, metadataSerializer);
				wordStoreManagerFilePerWord = new WordStoreManagerFilePerWord(getDatamartIndexerFilePerWord, wordSerializerFilePerWord);
				metadataStoreManagerFilePerWord = new MetadataStoreManagerFilePerWord(getDatamartIndexerFilePerWord, metadataSerializer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private static List<Path> getFilePaths(String folderPath) {
			List<Path> filePaths = new ArrayList<>();
			File folder = new File(folderPath);

			if (folder.exists() && folder.isDirectory()) {
				File[] files = folder.listFiles();

				if (files != null) {
					for (File file : files) {
						if (file.isFile()) {
							filePaths.add(file.toPath());
						}
					}
				}
			} else {
				System.out.println("The specified path is not valid or not a directory.");
			}

			return filePaths;
		}

		@Setup(Level.Trial)
		public void setup() {
			bookPathList = getFilePaths(bookDirectory);
		}

		@Benchmark
		public void IndexerOneFile(BenchmarkState state) throws IOException {
			Indexer indexerOneFile = new Indexer(state.wordStoreManagerFilePerWord, state.metadataStoreManagerOneFile, state.metadataExtractor, state.wordExtractor);
			for (Path bookPath : state.bookPathList) {
				indexerOneFile.execute(bookPath);
				return;
			}
		}

		@Benchmark
		public void IndexerFilePerWord(BenchmarkState state) throws IOException {
			Indexer indexerFilePerWord = new Indexer(state.wordStoreManagerFilePerWord, state.metadataStoreManagerFilePerWord, state.metadataExtractor, state.wordExtractor);
			for (Path bookPath : state.bookPathList) {
				indexerFilePerWord.execute(bookPath);
				return;
			}
		}
	}
}