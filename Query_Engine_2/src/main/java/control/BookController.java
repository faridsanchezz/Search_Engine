package control;

import java.util.*;
import java.util.stream.Collectors;

public class BookController {

	// TODO: RENOMBRAR TODOS LAS VARIABLES Y CLASES
	private static final String WORDS_DATAMART_PATH = "datamart/words/";
	private static final String DATALAKE_PATH = "datalake/";
	private static final String METADATA_FILE_PATH = "datamart/metadata/metadata";

	private final QueryEngineFileWord app;

	public BookController() {
		this.app = new QueryEngineFileWord();
	}

	public Map<String, Object> searchWords(String phrase) {
		Map<String, Object> response = new HashMap<>();

		List<Map<String, Object>> wordResults = Arrays.stream(phrase.split(" "))
				.parallel()
				.map(word -> {
					try {
						return app.printResultsAsMap(WORDS_DATAMART_PATH, DATALAKE_PATH, METADATA_FILE_PATH, word.trim());
					} catch (Exception e) {
						Map<String, Object> errorResult = new HashMap<>();
						errorResult.put("error", "Error processing word");
						errorResult.put("details", e.getMessage());
						return errorResult;
					}
				})
				.collect(Collectors.toList());

		response.put("results", wordResults);
		return response;
	}
}
