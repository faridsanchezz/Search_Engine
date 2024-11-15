package control;

import static spark.Spark.*;

import com.google.gson.Gson;

import java.util.Map;

public class ApiServer {

	private static final Gson gson = new Gson();

	public static void main(String[] args) {

		// Configurar puerto
		port(8080);

		// Habilitar CORS
		enableCORS("*", "GET,POST,OPTIONS", "Content-Type,Authorization");

		// Definir rutas de datos
		String WORDS_DATAMART_PATH_FileWord = "/app/datamart/words/";
		String DATALAKE_PATH_FileWord = "/app/datalake/";
		String METADATA_FILE_PATH_FileWord = "/app/datamart/metadata/metadata";

		String WORDS_DATAMART_PATH_OneFile = "/app/datamart/words";
		String DATALAKE_PATH_OneFile = "/app/datalake/";
		String METADATA_FILE_PATH_OneFile = "/app/datamart/metadata";

		// Instanciar QueryEngine y BookController
		QueryEngineFileWord queryEngineFileWord = new QueryEngineFileWord();
		QueryEngineOneFile queryEngineOneFile = new QueryEngineOneFile();

		// Cambiar entre FileWord y OneFile según sea necesario
		//BookController bookController = new BookController(WORDS_DATAMART_PATH_OneFile, DATALAKE_PATH_OneFile, METADATA_FILE_PATH_OneFile, queryEngineOneFile);
		BookController bookController = new BookController(WORDS_DATAMART_PATH_FileWord, DATALAKE_PATH_FileWord, METADATA_FILE_PATH_FileWord, queryEngineFileWord);

		// Configurar rutas
		configureRoutes(bookController);
	}

	/**
	 * Método para configurar las rutas de la API.
	 */
	private static void configureRoutes(BookController bookController) {
		// Ruta principal de búsqueda
		get("/search", (req, res) -> {
			String phrase = req.queryParams("phrase"); // Obtener el parámetro 'phrase'

			if (phrase == null || phrase.isEmpty()) {
				res.status(400);
				return gson.toJson(Map.of("error", "El parámetro de búsqueda 'phrase' es requerido"));
			}

			try {
				// Llamar a la función searchWords en BookController
				Map<String, Object> response = bookController.searchWords(phrase);

				res.type("application/json");
				return gson.toJson(response);
			} catch (Exception e) {
				res.status(500);
				return gson.toJson(Map.of("error", "Error interno del servidor", "details", e.getMessage()));
			}
		});

		// Ruta para manejar errores 404
		notFound((req, res) -> {
			res.type("application/json");
			return gson.toJson(Map.of("status", 404, "message", "Ruta no encontrada"));
		});
	}

	/**
	 * Middleware para habilitar CORS.
	 */
	private static void enableCORS(final String origin, final String methods, final String headers) {
		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", origin);
			response.header("Access-Control-Allow-Methods", methods);
			response.header("Access-Control-Allow-Headers", headers);
		});

		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});
	}
}