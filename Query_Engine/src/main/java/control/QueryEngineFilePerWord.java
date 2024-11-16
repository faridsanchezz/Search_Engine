package control;

import control.interfaces.QueryEngineManager;
import model.Metadata;
import model.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static control.MetadataReader.readMetadata;


public class QueryEngineFilePerWord implements QueryEngineManager {
	@Override
	public Word searchBook(String wordsDatamartPath, String word) {
		File wordFile = new File(wordsDatamartPath, word);

		if (!wordFile.exists()) {
			System.out.println("\n" + "No file found for the word: " + word);
			return new Word(word, new Word.WordOccurrence[0]);
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(wordFile))) {
			List<Word.WordOccurrence> occurrences = reader.lines()
					.map(line -> line.split(" "))
					.filter(parts -> parts.length > 1)
					.map(parts -> {
						String bookId = parts[0];
						List<Integer> lineNumbers = Arrays.stream(parts, 1, parts.length)
								.map(Integer::parseInt)
								.collect(Collectors.toList());

						return new Word.WordOccurrence(bookId, lineNumbers);
					})
					.collect(Collectors.toList());

			return new Word(word, occurrences.toArray(new Word.WordOccurrence[0]));

		} catch (IOException e) {
			e.printStackTrace();
			return new Word(word, new Word.WordOccurrence[0]);
		}
	}


	@Override
	public List<String> getPreviewLines(String datalakePath, String idBook, List<Integer> lines) {
		List<String> previewLines = new ArrayList<>();
		String bookPath = datalakePath + File.separator + idBook;
		File file = new File(bookPath);
		String startPattern = "\\*\\*\\* START .* \\*\\*\\*";
		boolean startReading = false;

		if (!file.exists()) {
			previewLines.add("File " + idBook + " not found.");
			return previewLines;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int lineNumber = 0;
			int printedLines = 0;

			while ((line = reader.readLine()) != null) {
				if (!startReading) {
					if (Pattern.matches(startPattern, line)) {
						startReading = true;
					}
					continue;
				}

				if (startReading) {
					if (lines.contains(lineNumber)) {
						previewLines.add("Line " + lineNumber + ": " + line.trim());
						printedLines++;

						if (printedLines >= 3 || printedLines >= lines.size()) {
							break;
						}
					}
					lineNumber++;
				}
			}
		} catch (IOException e) {
			previewLines.add("Error reading file: " + e.getMessage());
		}
		return previewLines;
	}

	@Override
	public Map<String, Object> printResultsAsMap(String wordsDatamartPath, String datalakePath, String metadataFilePath, String word) {
		Map<String, Object> wordResult = new HashMap<>();
		wordResult.put("word", word);

		Set<Metadata> metadataSet = readMetadata(metadataFilePath);

		Word result = searchBook(wordsDatamartPath, word);

		List<Map<String, Object>> occurrencesList = new ArrayList<>();
		for (Word.WordOccurrence occurrence : result.getOccurrences()) {
			String idBook = occurrence.getBook_id();

			Metadata metadata = metadataSet.stream()
					.filter(m -> Objects.equals(m.getBookID(), idBook))
					.findFirst()
					.orElse(null);

			Map<String, Object> occurrenceDetails = new HashMap<>();
			occurrenceDetails.put("book_id", idBook);
			occurrenceDetails.put("frequency", occurrence.getFrequency());

			String lineNumbersStr = occurrence.getLineOccurrences().stream()
					.map(String::valueOf)
					.collect(Collectors.joining(", "));
			occurrenceDetails.put("lines", lineNumbersStr);

			if (metadata != null) {
				occurrenceDetails.put("title", metadata.getName());
				occurrenceDetails.put("author", metadata.getAuthor());
				occurrenceDetails.put("year", metadata.getYear());
				occurrenceDetails.put("language", metadata.getLanguage());
				occurrenceDetails.put("download_link", metadata.getDownloadLink());

				List<String> previewLines = getPreviewLines(datalakePath, idBook, occurrence.getLineOccurrences());
				occurrenceDetails.put("preview_lines", previewLines);
			} else {
				occurrenceDetails.put("error", "Metadata not found for book ID " + idBook);
			}

			occurrencesList.add(occurrenceDetails);
		}

		wordResult.put("occurrences", occurrencesList);
		return wordResult;
	}


}
