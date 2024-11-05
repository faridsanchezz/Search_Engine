package control;

import model.Word;
import java.io.*;
import java.util.*;

public class WordSerializer {

	// Custom serialization method for a collection of Word objects
	public static void serialize(Collection<Word> words, String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
			for (Word word : words) {
				// Write the word text
				writer.write(word.getText() + "\n");

				// Serialize each WordOccurrence
				for (Word.WordOccurrence occurrence : word.getOccurrences()) {
					writer.write("- " + occurrence.getBook_id() + " ");
					writer.write(String.join(" ", occurrence.getLineOccurrences().stream()
							.map(String::valueOf)
							.toArray(String[]::new)));
					writer.newLine();
				}
				writer.newLine(); // Blank line to separate words
			}
		}
	}

	// Custom deserialization method to read multiple Word objects
	public static Set<Word> deserialize(String filePath) throws IOException {
		Set<Word> words = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			String wordText = null;
			List<Word.WordOccurrence> wordOccurrences = new ArrayList<>();

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				// Detect a new word if line does not start with "-"
				if (!line.startsWith("-")) {
					// If we're switching to a new word, save the current one
					if (wordText != null && !wordOccurrences.isEmpty()) {
						Word word = new Word(wordText, wordOccurrences.toArray(new Word.WordOccurrence[0]));
						words.add(word);
					}

					// Start a new word
					wordText = line;
					wordOccurrences = new ArrayList<>();
				} else {
					// Process WordOccurrence line
					String[] parts = line.substring(2).split(" ");
					String book_id = parts[0];
					List<Integer> lineOccurrences = new ArrayList<>();
					for (int i = 1; i < parts.length; i++) {
						lineOccurrences.add(Integer.parseInt(parts[i]));
					}

					Word.WordOccurrence wordOccurrence = new Word.WordOccurrence(book_id, lineOccurrences);
					wordOccurrences.add(wordOccurrence);
				}
			}

			// Add the last word if it exists
			if (wordText != null && !wordOccurrences.isEmpty()) {
				Word word = new Word(wordText, wordOccurrences.toArray(new Word.WordOccurrence[0]));
				words.add(word);
			}
		}

		return words;
	}
}
