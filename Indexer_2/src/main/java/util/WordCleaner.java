package util;

import java.util.*;
import java.util.regex.Pattern;

public class WordCleaner {

	private static final Set<String> stopwords = new HashSet<>(Arrays.asList(
			"the", "and", "a", "to", "of", "in", "on", "for", "is", "it"  // Example stopwords
	));

	public static String cleanAndEvaluateWord(String word) {
		// Remove non-alphabet characters
		word = word.replaceAll("[^A-Za-z]", "").toLowerCase();

		// Check if the word is valid (not in stopwords and not too short)
		if (!word.isEmpty() && !stopwords.contains(word) && word.length() > 1) {
			return word;
		}
		return null;
	}
}

