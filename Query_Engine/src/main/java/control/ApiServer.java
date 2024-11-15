package control;

import static spark.Spark.*;

import com.google.gson.Gson;

import java.util.Map;

public class ApiServer {

	private static final Gson gson = new Gson();

	public static void main(String[] args) {

		port(8080);

		String WORDS_DATAMART_PATH_FileWord = "datamart/words/";
		String DATALAKE_PATH_FileWord = "datalake/";
		String METADATA_FILE_PATH_FileWord = "datamart/metadata/metadata";

		String WORDS_DATAMART_PATH_OneFile = "datamart/words";
		String DATALAKE_PATH_OneFile = "datalake/";
		String METADATA_FILE_PATH_OneFile = "datamart/metadata";

		QueryEngineFileWord queryEngineFileWord = new QueryEngineFileWord();
		QueryEngineOneFile queryEngineOneFile = new QueryEngineOneFile();


		//BookController bookController = new BookController(WORDS_DATAMART_PATH_FileWord,DATALAKE_PATH_FileWord,  METADATA_FILE_PATH_FileWord,  queryEngineFileWord);
		BookController bookController = new BookController(WORDS_DATAMART_PATH_OneFile,DATALAKE_PATH_OneFile,  METADATA_FILE_PATH_OneFile,  queryEngineOneFile);

		// Definir la ruta de búsqueda GET
		get("/search", (req, res) -> {
			String phrase = req.queryParams("phrase"); // Obtener el parámetro 'phrase'

			if (phrase == null || phrase.isEmpty()) {
				res.status(400);
				return gson.toJson(Map.of("error", "El parámetro de búsqueda 'phrase' es requerido"));
			}

			// Llamar a la función searchBook en BookController
			Map<String, Object> response = bookController.searchWords(phrase);

			res.type("application/json");
			return gson.toJson(response);
		});

		// Ruta para manejar errores 404
		notFound((req, res) -> {
			res.type("application/json");
			return gson.toJson(Map.of("status", 404, "message", "Ruta no encontrada"));
		});
	}
}