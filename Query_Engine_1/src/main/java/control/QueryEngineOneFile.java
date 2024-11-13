package control;

import model.Metadata;
import model.Word;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static control.MetadataReader.readMetadata;

public class QueryEngineOneFile implements QueryEngineManager, FileManager{

    @Override
    public  Set<Word> readFile(String filePath) {
        Set<Word> wordSet = new HashSet<>();
        Pattern linePattern = Pattern.compile("-\\s(\\d+)\\s([\\d\\s]+)");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Word currentWord = null;

            while ((line = reader.readLine()) != null) {
                // Check if the line is a word (not starting with '-')
                if (!line.startsWith("-")) {
                    if (currentWord != null) {
                        wordSet.add(currentWord);
                    }
                    currentWord = new Word(line.trim(), new ArrayList<>());
                } else if (currentWord != null) {
                    // If the line starts with '-' and we have a current word, parse occurrences
                    Matcher matcher = linePattern.matcher(line.trim());
                    if (matcher.matches()) {
                        String bookId = matcher.group(1);
                        List<Integer> lineOccurrences = new ArrayList<>();
                        for (String part : matcher.group(2).split("\\s")) {
                            lineOccurrences.add(Integer.parseInt(part));
                        }
                        Word.WordOccurrence occurrence = new Word.WordOccurrence(bookId, lineOccurrences);
                        currentWord.addOccurrence(occurrence);
                    }
                }
            }
            // Add the last word processed to the set
            if (currentWord != null) {
                wordSet.add(currentWord);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordSet;
    }

    @Override
    public Word searchBook(Set<Word> wordsDatamart, String word) {
        return wordsDatamart.stream()
                .filter(w -> w.getText().equals(word))
                .findFirst()
                .orElse(new Word(word, List.of(new Word.WordOccurrence[0])));
    }

    @Override
    public List<String> getPreviewLines(String datalakePath, String idBook, List<Integer> lines) {
        List<String> previewLines = new ArrayList<>();
        String bookPath = datalakePath + File.separator + idBook + ".txt";
        File file = new File(bookPath);

        if (!file.exists()) {
            previewLines.add("File " + idBook + " not found.");
            return previewLines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String lineContent;
            int currentLineNumber = 0;
            int printedLines = 0;

            while ((lineContent = reader.readLine()) != null) {
                currentLineNumber++;
                if (lines.contains(currentLineNumber) && printedLines < 3) {
                    previewLines.add("Line " + currentLineNumber + ": " + lineContent.trim());
                    printedLines++;
                }

                if (printedLines >= 3) {
                    break;
                }
            }
        } catch (IOException e) {
            previewLines.add("Error reading file: " + e.getMessage());
        }

        return previewLines;
    }

    @Override
    public Map<String, Object> printResultsAsMap(Set<Word> wordsDatamart, String datalakePath, String metadataFilePath, String word) {
        Map<String, Object> wordResult = new HashMap<>();
        wordResult.put("word", word);

        // Load metadata
        Map<String, Metadata> metadataDict = new HashMap<>();
        for (Metadata metadata : readMetadata(metadataFilePath)) {
            metadataDict.put(metadata.getBookId(), metadata);
        }

        // Search for the word occurrences
        Word result = searchBook(wordsDatamart, word);

        List<Map<String, Object>> occurrencesList = new ArrayList<>();
        for (Word.WordOccurrence occurrence : result.getOccurrences()) {
            String idBook = occurrence.getBook_id();
            Metadata metadata = metadataDict.get(idBook);

            // Prepare occurrence details
            Map<String, Object> occurrenceDetails = new HashMap<>();
            occurrenceDetails.put("book_id", idBook);
            occurrenceDetails.put("frequency", occurrence.getFrequency());

            // Convert lineNumbers to a string before adding to the map
            String lineNumbersStr = occurrence.getLineOccurrences().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", "));
            occurrenceDetails.put("lines", lineNumbersStr);

            if (metadata != null) {
                // Add metadata details
                occurrenceDetails.put("title", metadata.getName());
                occurrenceDetails.put("author", metadata.getAuthor());
                occurrenceDetails.put("year", metadata.getYear());
                occurrenceDetails.put("language", metadata.getLanguage());
                occurrenceDetails.put("download_link", metadata.getDownloadLink());

                // Get preview lines
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
