package control;

import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {

		String wordsDatamart = "words.txt";
		String metadataDatamart = "metadata.txt";
		String datalake = "";

		WordStoreManager wordStoreManager = new WordStoreManager(wordsDatamart);
		MetadataStoreManager metadataStoreManager = new MetadataStoreManager(metadataDatamart);

		try {
			IndexerV1 indexerV1 = new IndexerV1(datalake, wordStoreManager, metadataStoreManager);
			indexerV1.execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


	}
}
