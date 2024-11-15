package control;

import model.Metadata;
import model.Word;

import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static control.MetadataReader.readMetadata;


public class QueryEngineFileWord implements QueryEngineManager{
    @Override
    public Word searchBook(String wordsDatamartPath, String word) {
        File wordFile = new File(wordsDatamartPath, word);

        if (!wordFile.exists()) {
            System.out.println("\n" + "No file found for the word: " + word);
            return new Word(word, new Word.WordOccurrence[0]); // Devuelve un Word vacío si el archivo no existe
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(wordFile))) {
            // Usamos Stream para procesar cada línea y mapearla a una lista de WordOccurrence
            List<Word.WordOccurrence> occurrences = reader.lines()
                    .map(line -> line.split(" ")) // Divide la línea en partes
                    .filter(parts -> parts.length > 1) // Filtra líneas válidas con al menos un ID de línea
                    .map(parts -> {
                        // El ID del libro es el primer elemento (una string terminada en .txt)
                        String bookId = parts[0];

                        // Los números de línea comienzan desde el índice 1
                        List<Integer> lineNumbers = Arrays.stream(parts, 1, parts.length)
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());

                        return new Word.WordOccurrence(bookId, lineNumbers);
                    })
                    .collect(Collectors.toList());

            return new Word(word, occurrences.toArray(new Word.WordOccurrence[0]));

        } catch (IOException e) {
            e.printStackTrace();
            return new Word(word, new Word.WordOccurrence[0]); // Devuelve un Word vacío en caso de error
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
                    lineNumber++; // Incrementa solo después de procesar la primera línea
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
