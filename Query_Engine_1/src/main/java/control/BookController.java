package control;



import model.Word;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class BookController {

    private static final String WORDS_DATAMART_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datamart\\Words";
    private static final String DATALAKE_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datalake";
    private static final String METADATA_FILE_PATH = "C:\\Users\\lucia\\IdeaProjects\\Search_Engine\\Query_Engine_2\\Datamart\\Metadatos\\metada";

    @GetMapping("/search")
    public Map<String, Object> searchWords(@RequestParam String phrase) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> wordResults = new ArrayList<>();

        // Create an instance of BookSearchApp
        QueryEngineOneFile app = new QueryEngineOneFile();

        // Split phrase into individual words
        String[] words = phrase.split(" ");

        // Search each word and collect results as maps
        for (String word : words) {
            Set<Word> wordsDatamart = app.readFile(WORDS_DATAMART_PATH);
            Map<String, Object> wordResult = app.printResultsAsMap(wordsDatamart, DATALAKE_PATH, METADATA_FILE_PATH, word.trim());
            wordResults.add(wordResult);
        }

        response.put("results", wordResults);
        return response;
    }
}