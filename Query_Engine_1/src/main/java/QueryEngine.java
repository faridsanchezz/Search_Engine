
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class QueryEngine {

    public static Map<String, Object> searchBook(Map<String, Map<String, Object>> wordsDict, String word) {
        // cargamos los libros asociados a una palabra del datamart si esta se encuentra
        if (wordsDict.containsKey(word)) {
            return wordsDict.get(word);
        } else {
            return new HashMap<>();
        }
    }


    public static void execute(String wordsPath, String metadataPath, String datalakePath) {

        JsonController jsonController = new JsonController();

        Map<String, Map<String, Object>> wordsDatamart = jsonController.readJSON(wordsPath);
        Map<String, Map<String, Object>> metadataDatamart = jsonController.readJSON(metadataPath);

        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.print("Enter a word (or type 'exit' to finish): ");
            String entry = scanner.nextLine();

            if (entry.equalsIgnoreCase("exit")) {
                break;
            }

            List<String> words = new ArrayList<>(Arrays.asList(entry.split(" ")));

            while (!words.isEmpty()) {
                String word = words.remove(0);
                System.out.println("buscando: " + word);
                Map<String, Object> books = searchBook(wordsDatamart, word); // diccionario con todos los codigos de los libros
                printResults(books, metadataDatamart, datalakePath);
            }
        }
    }

    private static void printResults(Map<String, Object> books, Map<String, Map<String, Object>> metadataDatamart, String datalakePath) {
        for (String idBook : books.keySet()) {
            Map<String, Object> metadata = metadataDatamart.get(idBook);
            if (metadata != null) {
                for (Map.Entry<String, Object> entry : metadata.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue().toString());
                }

                Object rawBookData = books.get(idBook);
                Map<String, Object> bookData = (Map<String, Object>) rawBookData;
                
                // Casting
                Double frequency = (Double) bookData.get("frecuency");
                List<Double> rawLines = (List<Double>) bookData.get("lines");
                List<Integer> lines = rawLines.stream()
                        .map(Double::intValue) // Convertimos cada Double a Integer
                        .collect(Collectors.toList());

                System.out.println("Frecuency: " + frequency);
                System.out.println("Lines: " + lines);
                System.out.println("Preview lines:");
                previewLines(datalakePath, idBook, lines);
                System.out.println("-----------------------------------------------------------------------------------");
            }
            }
        }

    private static void previewLines(String datalakePath, String idBook, List<Integer> lines) {
        Path bookPath = Paths.get(datalakePath, idBook + ".txt");

        try (BufferedReader reader = Files.newBufferedReader(bookPath)) {
            String lineContent;
            int currentNumber = 0;
            int linesShown = 0;  // Para contar las líneas mostradas

            while ((lineContent = reader.readLine()) != null) {
                currentNumber++;
                // Mostramos solo si la línea actual está en la lista de líneas
                if (lines.contains(currentNumber)) {
                    System.out.println("Line " + currentNumber + ": " + lineContent.trim());
                    linesShown++;

                    // Solo mostramos las primeras 3 líneas
                    if (linesShown >= 3) {
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + idBook + " not found.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}