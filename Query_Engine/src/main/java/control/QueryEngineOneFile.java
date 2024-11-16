package control;

import control.interfaces.FileManager;
import control.interfaces.QueryEngineManager;
import model.Metadata;
import model.Word;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static control.MetadataReader.readMetadata;

public class QueryEngineOneFile implements QueryEngineManager, FileManager {

	@Override
	public Set<Word> readFile(String filePath) {
		Set<Word> wordSet = new HashSet<>();
		Pattern linePattern = Pattern.compile("-\\s(\\d+)\\s((\\d+\\s?)+)");

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			Word currentWord = null;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (!line.startsWith("-")) {
					if (currentWord != null) {
						wordSet.add(currentWord);
					}
					currentWord = new Word(line, new Word.WordOccurrence[0]);
				} else if (currentWord != null) {
					Matcher matcher = linePattern.matcher(line);
					if (matcher.matches()) {
						String bookId = matcher.group(1);
						List<Integer> lineOccurrences = new ArrayList<>();
						for (String part : matcher.group(2).trim().split("\\s+")) {
							lineOccurrences.add(Integer.parseInt(part));
						}
						Word.WordOccurrence occurrence = new Word.WordOccurrence(bookId, lineOccurrences);
						currentWord.addOccurrence(occurrence);
					} else {
						System.out.println("Line doesn't match expected format: " + line);
					}
				}
			}

			if (currentWord != null) {
				wordSet.add(currentWord);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return wordSet;
	}


	@Override
	public Word searchBook(String filePath, String word) {
		Set<Word> wordsDatamart = readFile(filePath);
		return wordsDatamart.stream()
				.filter(w -> w.getText().equals(word))
				.findFirst()
				.orElse(new Word(word, new Word.WordOccurrence[0]));
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
	public Map<String, Object> printResultsAsMap(String filePath, String datalakePath, String metadataFilePath, String word) {
		Map<String, Object> wordResult = new HashMap<>();
		wordResult.put("word", word);

		Set<Metadata> metadataSet = readMetadata(metadataFilePath);

		Word result = searchBook(filePath, word);

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
