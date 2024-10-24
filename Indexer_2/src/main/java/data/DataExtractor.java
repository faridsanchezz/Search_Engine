package data;

import util.WordCleaner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class DataExtractor {

	// Method to extract the title from the content using regex
	public static String extractTitle(String content) {
		Pattern titlePattern = Pattern.compile("(?i)Title:\\s*(.*)");
		Matcher matcher = titlePattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;  // Return null if no title is found
	}

	// Method to extract the author from the content using regex
	public static String extractAuthor(String content) {
		Pattern authorPattern = Pattern.compile("(?i)Author:\\s*(.*)");
		Matcher matcher = authorPattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;  // Return null if no author is found
	}

	// Method to extract the language from the content using regex
	public static String extractLanguage(String content) {
		Pattern languagePattern = Pattern.compile("(?i)Language:\\s*(.*)");
		Matcher matcher = languagePattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(1).trim();
		}
		return null;  // Return null if no language is found
	}

	public static Map<String, Object> getMetadata(String content, String filePath) {
		Map<String, Object> metadata = new HashMap<>();

		// Extract book_id using regex from the file name (assuming the format is "<book_id>.txt")
		Pattern pattern = Pattern.compile("(\\d+)\\.txt$");
		Matcher matcher = pattern.matcher(filePath);

		String book_id = null;
		if (matcher.find()) {
			book_id = matcher.group(1);  // Extract the book ID
		}

		if (book_id == null || book_id.isEmpty()) {
			System.err.println("Error: Book ID is null or empty for file: " + filePath);
			return null;  // Return null if book_id is invalid
		} else {
			metadata.put("book_id", book_id);
		}

		// Extract other metadata (title, author, etc.)
		String title = extractTitle(content);
		String author = extractAuthor(content);
		String language = extractLanguage(content);

		// Add non-null values to the metadata map
		if (title != null) metadata.put("title", title);
		if (author != null) metadata.put("author", author);
		if (language != null) metadata.put("language", language);

		// Generate the download link only if book_id is not null
		metadata.put("download_link", "https://www.gutenberg.org/cache/epub/" + book_id + "/pg" + book_id + ".txt");

		return metadata;
	}


	private static String extractMetadata(String input, Pattern pattern) {
		Matcher matcher = pattern.matcher(input);
		return matcher.find() ? matcher.group(1).trim() : null;
	}

	// Método para extraer palabras de un libro y sus números de línea correspondientes
	public static Map<String, List<Integer>> getWords(String book) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(book));  // Convierte el String en BufferedReader
		Map<String, List<Integer>> wordDict = new HashMap<>();
		String line;
		int lineNumber = 1;

		// Leer el contenido línea por línea
		while ((line = reader.readLine()) != null) {
			String[] words = line.split("\\s+");

			for (String word : words) {
				String cleanWord = WordCleaner.cleanAndEvaluateWord(word);  // Usando la función de limpieza
				if (cleanWord != null) {
					wordDict.computeIfAbsent(cleanWord, k -> new ArrayList<>()).add(lineNumber);
				}
			}
			lineNumber++;
		}

		return wordDict;
	}
}

