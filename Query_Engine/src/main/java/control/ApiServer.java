package control;

import static spark.Spark.*;

import com.google.gson.Gson;

import java.util.Map;

public class ApiServer {

	private static final Gson gson = new Gson();

	public static void main(String[] args) {

		port(8080);
		enableCORS("*", "GET,POST,OPTIONS", "Content-Type,Authorization");

		// Paths for Docker
		String WORDS_DATAMART_PATH_FilePerWord = "/app/datamart/words/";
		String DATALAKE_PATH_FilePerWord = "/app/datalake/";
		String METADATA_FILE_PATH_FilePerWord = "/app/datamart/metadata/metadata";

		String WORDS_DATAMART_PATH_OneFile = "/app/datamart/words";
		String DATALAKE_PATH_OneFile = "/app/datalake/";
		String METADATA_FILE_PATH_OneFile = "/app/datamart/metadata";


		QueryEngineFilePerWord queryEngineFilePerWord = new QueryEngineFilePerWord();
		QueryEngineOneFile queryEngineOneFile = new QueryEngineOneFile();

		BookController bookController = new BookController(WORDS_DATAMART_PATH_OneFile, DATALAKE_PATH_OneFile, METADATA_FILE_PATH_OneFile, queryEngineOneFile);
		//BookController bookController = new BookController(WORDS_DATAMART_PATH_FilePerWord, DATALAKE_PATH_FilePerWord, METADATA_FILE_PATH_FilePerWord, queryEngineFilePerWord);

		configureRoutes(bookController);
	}

	private static void configureRoutes(BookController bookController) {
		get("/search", (req, res) -> {
			String phrase = req.queryParams("phrase");

			if (phrase == null || phrase.isEmpty()) {
				res.status(400);
				return gson.toJson(Map.of("error", "The search parameter 'phrase' is required"));
			}

			try {
				Map<String, Object> response = bookController.searchWords(phrase);

				res.type("application/json");
				return gson.toJson(response);
			} catch (Exception e) {
				res.status(500);
				return gson.toJson(Map.of("error", "Intern error of the server", "details", e.getMessage()));
			}
		});

		notFound((req, res) -> {
			res.type("application/json");
			return gson.toJson(Map.of("status", 404, "message", "Path not found"));
		});
	}

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