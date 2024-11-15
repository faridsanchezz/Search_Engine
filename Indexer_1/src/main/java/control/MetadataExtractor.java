package control;

import control.interfaces.ExtractorController;
import model.Metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataExtractor implements ExtractorController<Metadata> {

	@Override
	public Set<Metadata> get(BufferedReader book, String bookID) throws IOException {
		StringBuilder inputString = new StringBuilder();

		for (int i = 0; i < 30; i++) {
			String line = book.readLine();
			if (line == null) break;
			inputString.append(line).append("\n");
		}

		Pattern titlePattern = Pattern.compile("Title:\\s*(.*?)(?=\\n|$)");
		Pattern authorPattern = Pattern.compile("Author:\\s*(.*?)(?=\\n|$)");
		Pattern languagePattern = Pattern.compile("Language:\\s*(.*?)(?=\\n|$)");
		Pattern yearPattern = Pattern.compile("Release date:.*?(\\b\\d{4}\\b)");

		Matcher titleMatcher = titlePattern.matcher(inputString);
		Matcher authorMatcher = authorPattern.matcher(inputString);
		Matcher languageMatcher = languagePattern.matcher(inputString);
		Matcher yearMatcher = yearPattern.matcher(inputString);

		String title = titleMatcher.find() ? titleMatcher.group(1).trim() : "UNKNOWN";
		String author = authorMatcher.find() ? authorMatcher.group(1).trim() : "UNKNOWN";
		String language = languageMatcher.find() ? languageMatcher.group(1).trim() : "UNKNOWN";
		String year = yearMatcher.find() ? yearMatcher.group(1).trim() : "UNKNOWN";
		String downloadLink = "https://www.gutenberg.org/cache/epub/" + bookID + "/pg" + bookID + ".txt";

		Metadata metadata = new Metadata(bookID, title, author, year, language, downloadLink);

		Set<Metadata> metadataSet = new HashSet<>();
		metadataSet.add(metadata);

		return metadataSet;
	}
}
