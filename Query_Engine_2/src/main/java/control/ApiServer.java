package control;

import static spark.Spark.*;

import com.google.gson.Gson;

import java.util.Map;

public class ApiServer {

	private static final Gson gson = new Gson();

	public static void main(String[] args) {
		// Configurar el puerto del servidor
		port(8080);

		// Instancia de BookController
		BookController bookController = new BookController();

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