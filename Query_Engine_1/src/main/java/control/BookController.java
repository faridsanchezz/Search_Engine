package control;



import model.Word;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class BookController {

    private static final String WORDS_DATAMART_PATH = "words.txt";
    private static final String DATALAKE_PATH = "datalake\\";
    private static final String METADATA_FILE_PATH = "metadata.txt";

    @GetMapping("/search")
    public Map<String, Object> searchWords(@RequestParam String phrase) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> wordResults = new ArrayList<>();

        // Instancia de QueryEngineOneFile para realizar las búsquedas
        QueryEngineOneFile app = new QueryEngineOneFile();

        // Divide la frase en palabras
        String[] words = phrase.split(" ");

        // Crea un ExecutorService con un número de hilos igual al número de procesadores disponibles
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        // Crea una tarea para cada palabra y envía al ExecutorService
        for (String word : words) {
            Future<Map<String, Object>> future = executorService.submit(() -> {
                Set<Word> wordsDatamart = app.readFile(WORDS_DATAMART_PATH);
                return app.printResultsAsMap(wordsDatamart, DATALAKE_PATH, METADATA_FILE_PATH, word.trim());
            });
            futures.add(future);
        }

        // Recoge los resultados de cada tarea
        for (Future<Map<String, Object>> future : futures) {
            try {
                wordResults.add(future.get());  // Obtiene el resultado de cada tarea
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("error", "Error processing word");
                errorResult.put("details", e.getMessage());
                wordResults.add(errorResult);
            }
        }

        // Cierra el ExecutorService
        executorService.shutdown();

        response.put("results", wordResults);
        return response;
    }
}