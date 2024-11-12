package control;

import model.Metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetadataExtractor {

	public static Metadata getMetadata(BufferedReader book, String bookPath) throws IOException {
		StringBuilder inputString = new StringBuilder();

		for (int i = 0; i < 30; i++) {
			String line = book.readLine();
			if (line == null) break;
			inputString.append(line).append("\n");
		}

		Pattern idPattern = Pattern.compile("(\\d+)\\.txt$");
		Matcher idMatcher = idPattern.matcher(bookPath);
		String bookID = idMatcher.find() ? idMatcher.group(1) : null;

		Pattern titlePattern = Pattern.compile("Title:\\s*(.*?)(?=\\n|$)");
		Pattern authorPattern = Pattern.compile("Author:\\s*(.*?)(?=\\n|$)");
		Pattern languagePattern = Pattern.compile("Language:\\s*(.*?)(?=\\n|$)");
		Pattern releaseDatePattern = Pattern.compile("Release date:\\s*(.*?)(?=\\n|$)");

		Matcher titleMatcher = titlePattern.matcher(inputString);
		Matcher authorMatcher = authorPattern.matcher(inputString);
		Matcher languageMatcher = languagePattern.matcher(inputString);
		Matcher releaseDateMatcher = releaseDatePattern.matcher(inputString);

		String title = titleMatcher.find() ? titleMatcher.group(1).trim() : null;
		String author = authorMatcher.find() ? authorMatcher.group(1).trim() : null;
		String language = languageMatcher.find() ? languageMatcher.group(1).trim() : null;
		String releaseDate = releaseDateMatcher.find() ? releaseDateMatcher.group(1).trim() : null; //TODO: añadir este caracteristica a METADATA
		String downloadLink = "https://www.gutenberg.org/cache/epub/" + bookID + "/pg" + bookID + ".txt";
		int year = 0;

		return new Metadata(bookID, title, author, year, language, downloadLink);
	}

}