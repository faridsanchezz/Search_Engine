package control;

import model.Metadata;
import model.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static control.MetadataReader.readMetadata;


public class QueryEngineFileWord implements QueryEngineManager{
    @Override
    public Word searchBook(String wordsDatamartPath, String word) {
        File wordFile = new File(wordsDatamartPath, word);
        List<Word.WordOccurrence> occurrences = new ArrayList<>();

        if (!wordFile.exists()) {
            System.out.println("No file found for the word: " + word);
            return new Word(word, new Word.WordOccurrence[0]);  // Devuelve un Word vacío si el archivo no existe
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(wordFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    String bookId = String.valueOf(Integer.parseInt(parts[0])); // Primer elemento es el BookID
                    List<Integer> lineNumbers = new ArrayList<>();

                    // Agregar cada número de línea a la lista
                    for (int i = 1; i < parts.length; i++) {
                        lineNumbers.add(Integer.parseInt(parts[i]));
                    }

                    // Crear un WordOccurrence y agregarlo a la lista de ocurrencias
                    occurrences.add(new Word.WordOccurrence(bookId, lineNumbers));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Word(word, occurrences.toArray(new Word.WordOccurrence[0]));
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
    public Map<String, Object> printResultsAsMap(String wordsDatamartPath, String datalakePath, String metadataFilePath, String word) {
        Map<String, Object> wordResult = new HashMap<>();
        wordResult.put("word", word);

        // Load metadata
        Map<String, Metadata> metadataDict = new HashMap<>();
        for (Metadata metadata : readMetadata(metadataFilePath)) {
            metadataDict.put(metadata.getBook_id(), metadata);
        }

        // Search for the word occurrences
        Word result = searchBook(wordsDatamartPath, word);

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
