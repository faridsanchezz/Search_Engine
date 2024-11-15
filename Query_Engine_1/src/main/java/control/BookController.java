package control;

import model.Word;

import java.util.*;
import java.util.stream.Collectors;

public class BookController {

	private static final String WORDS_DATAMART_PATH = "datamart/words";
	private static final String DATALAKE_PATH = "datalake/";
	private static final String METADATA_FILE_PATH = "datamart/metadata";

	private final QueryEngineOneFile app;

	public BookController() {
		this.app = new QueryEngineOneFile(); // Inicializar el motor de búsqueda
	}

	public Map<String, Object> searchWords(String phrase) {
		Map<String, Object> response = new HashMap<>();

		// Dividir la frase en palabras y procesarlas en paralelo
		List<Map<String, Object>> wordResults = Arrays.stream(phrase.split(" "))
				.parallel() // Procesar cada palabra en paralelo
				.map(word -> {
					try {
						return app.printResultsAsMap(WORDS_DATAMART_PATH, DATALAKE_PATH, METADATA_FILE_PATH, word.trim());
					} catch (Exception e) {
						// En caso de error, devolver un mapa con el mensaje de error
						Map<String, Object> errorResult = new HashMap<>();
						errorResult.put("error", "Error processing word");
						errorResult.put("details", e.getMessage());
						return errorResult;
					}
				})
				.collect(Collectors.toList()); // Recolectar los resultados en una lista

		// Agregar los resultados al objeto de respuesta
		response.put("results", wordResults);
		return response;
	}
}