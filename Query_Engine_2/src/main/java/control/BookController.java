package control;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class BookController {

    private static final String WORDS_DATAMART_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datamart\\Words";
    private static final String DATALAKE_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datalake";
    private static final String METADATA_FILE_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datamart\\Metadatos\\metada";


    @GetMapping("/search")
    public Map<String, Object> searchWords(@RequestParam String phrase) {
        Map<String, Object> response = new HashMap<>();
        QueryEngineFileWord app = new QueryEngineFileWord();
        String[] words = phrase.split(" ");

        // Usamos un ExecutorService para gestionar la concurrencia
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (String word : words) {
            // Creamos una tarea para cada palabra
            Future<Map<String, Object>> future = executorService.submit(() ->
                    app.printResultsAsMap(WORDS_DATAMART_PATH, DATALAKE_PATH, METADATA_FILE_PATH, word.trim()));
            futures.add(future);
        }

        List<Map<String, Object>> wordResults = new ArrayList<>();
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

        executorService.shutdown();  // Cerramos el ExecutorService
        response.put("results", wordResults);
        return response;
    }
}