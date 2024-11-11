package control;

import model.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class WordExtractor {

	public static Set<Word> getWords(BufferedReader book, String bookID) throws IOException {
		Set<Word> indexedWordSet = new HashSet<>();

		String startPattern = "\\*\\*\\* START .* \\*\\*\\*";
		String endPattern = "\\*\\*\\* END .* \\*\\*\\*";
		boolean startProcessing = false;

		String line;
		int lineNumber = 0;

		while ((line = book.readLine()) != null) {
			System.out.println(line);
			if (!startProcessing) {
				if (Pattern.matches(startPattern, line)) {
					startProcessing = true;
				}
				continue;
			}

			if (Pattern.matches(endPattern, line)) {
				break;
			}

			processLine(line, bookID, lineNumber, indexedWordSet);

			lineNumber++;
		}

		return indexedWordSet;
	}

	private static void processLine(String line, String bookID, int lineNumber, Set<Word> indexedWordSet) {
		String[] words = line.split("\\s+");

		for (String word : words) {
			String cleanWord = WordCleaner.execute(word);
			if (cleanWord == null || cleanWord.isEmpty()) continue;

			addOrUpdateWord(cleanWord, bookID, lineNumber, indexedWordSet);
		}
	}

	private static void addOrUpdateWord(String cleanWord, String bookID, int lineNumber, Set<Word> indexedWordSet) {
		Word targetWord = indexedWordSet.stream()
				.filter(w -> w.getText().equals(cleanWord))
				.findFirst()
				.orElse(null);

		if (targetWord != null) {

			Word.WordOccurrence targetOccurrence = targetWord.getOccurrences().stream()
					.filter(occurrence -> occurrence.getBookID().equals(bookID))
					.findFirst()
					.orElse(null);

			if (targetOccurrence != null) {

				targetOccurrence.addLineOccurrence(lineNumber);
			} else {

				targetWord.addOccurrence(new Word.WordOccurrence(bookID, lineNumber));
			}
		} else {
			indexedWordSet.add(new Word(cleanWord, new Word.WordOccurrence(bookID, lineNumber)));
		}
	}
}
