package indexer;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.DataExtractor.getMetadata;
import static data.DataExtractor.getWords;
import static json.JSONController.readJSON;
import static json.JSONController.writeJSON;

public class IndexerV2 {

	// Method to save metadata to a file
	public static void saveMetadata(String metadataDirectory, Map<String, Object> metadataDict) throws IOException {
		String metadataPath = metadataDirectory + File.separator + "metadatos";

		JSONObject bookInfo = readJSON(metadataPath);

		// Update the book info with the new metadata
		for (Map.Entry<String, Object> entry : metadataDict.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			// Check if the key or value is null to avoid NullPointerException
			if (key != null && value != null) {
				bookInfo.put(key, value);  // Only put non-null key-value pairs
			} else {
				System.err.println("Warning: Null key or value found in metadata. Key: " + key + ", Value: " + value);
			}
		}

		// Save the updated metadata back to the file
		writeJSON(metadataPath, bookInfo);
	}


	// Method to save words and their frequencies to a file
	public static void saveWords(String datamartDirectory, Map<String, List<Integer>> wordDict, String bookID) throws IOException {
		String wordsDatamartPath = datamartDirectory + File.separator + "words";

		// Check if bookID is null, and skip if it is
		if (bookID == null || bookID.isEmpty()) {
			System.err.println("Error: bookID is null or empty. Cannot save words.");
			return;
		}

		// Load existing words data from the file
		JSONObject wordsDatamart = readJSON(wordsDatamartPath);

		for (Map.Entry<String, List<Integer>> entry : wordDict.entrySet()) {
			String word = entry.getKey();
			List<Integer> lineNumbers = entry.getValue();

			// Skip null keys or values
			if (word == null || lineNumbers == null) {
				System.err.println("Warning: Null key or value found in word dictionary. Skipping entry.");
				continue;
			}

			// Prepare auxiliary data
			Map<String, Object> auxiliar = new HashMap<>();
			auxiliar.put(bookID, Map.of("frequency", lineNumbers.size(), "lines", lineNumbers));

			// Update or insert new words into the dictionary
			if (wordsDatamart.isNull(word)) {
				wordsDatamart.put(word, auxiliar);
			} else {
				JSONObject existingWord = wordsDatamart.getJSONObject(word);
				existingWord.put(bookID, auxiliar.get(bookID));
				wordsDatamart.put(word, existingWord);
			}
		}

		// Save the updated words data back to the file
		writeJSON(wordsDatamartPath, wordsDatamart);
	}

	// Method to save metadata and words to the datamart directory
	public static void saveToDatamart(Map<String, List<Integer>> wordDict, Map<String, Object> metadata, String datamartDirectory) throws IOException {
		// Create the datamart directory if it does not exist
		File directory = new File(datamartDirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		// Save metadata and words to the datamart
		saveMetadata(datamartDirectory, metadata);
		saveWords(datamartDirectory, wordDict, metadata.keySet().iterator().next());
	}

	// Main method to process the book and create an inverted index
	public static void invertedIndex(String datalakePath, String datamartPath) throws IOException {
		// Extract metadata from the book
		String bookContent = new String(Files.readAllBytes(Paths.get(datalakePath)));
		Map<String, Object> metadataBook = getMetadata(bookContent, datalakePath);

		// If metadataBook is null, log a warning and skip processing this book
		if (metadataBook == null) {
			System.err.println("Error: Metadata extraction failed for file: " + datalakePath);
			return;
		}

		// Extract words from the book
		Map<String, List<Integer>> wordDict = getWords(bookContent);

		// Save the metadata and words to the datamart
		saveToDatamart(wordDict, metadataBook, datamartPath);
	}
}
