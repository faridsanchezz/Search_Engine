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
		Pattern releaseDatePattern = Pattern.compile("Release date:\\s*(.*?)(?=\\n|$)");

		Matcher titleMatcher = titlePattern.matcher(inputString);
		Matcher authorMatcher = authorPattern.matcher(inputString);
		Matcher languageMatcher = languagePattern.matcher(inputString);
		Matcher releaseDateMatcher = releaseDatePattern.matcher(inputString);

		String title = titleMatcher.find() ? titleMatcher.group(1).trim() : "UNKNOWN";
		String author = authorMatcher.find() ? authorMatcher.group(1).trim() : "UNKNOWN";
		String language = languageMatcher.find() ? languageMatcher.group(1).trim() : "UNKNOWN";
		String releaseDate = releaseDateMatcher.find() ? releaseDateMatcher.group(1).trim() : "UNKNOWN"; //TODO: add to metadata class
		String downloadLink = "https://www.gutenberg.org/cache/epub/" + bookID + "/pg" + bookID + ".txt";
		String year = "2024";

		Metadata metadata = new Metadata(bookID, title, author, year, language, downloadLink);

		Set<Metadata> metadataSet = new HashSet<>();
		metadataSet.add(metadata);

		return metadataSet;
	}
}
