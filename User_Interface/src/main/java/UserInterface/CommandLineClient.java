package UserInterface;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CommandLineClient {
	private static final String SERVER_URL = "http://localhost:8080/search?phrase=";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Gson gson = new Gson();

		System.out.println("Welcome to the Book Search CLI! Type 'exit' to quit.");

		while (true) {
			try {
				// Pedir al usuario que ingrese una frase
				System.out.print("\nEnter a phrase to search: ");
				String phrase = scanner.nextLine();

				// Condición de salida
				if (phrase.equalsIgnoreCase("exit")) {
					System.out.println("Goodbye!");
					break; // Salir del ciclo
				}

				// Verificar si la frase está vacía
				if (phrase.isEmpty()) {
					System.out.println("Error: Phrase cannot be empty.");
					continue;
				}

				// Crear URL con la frase
				URL url = new URL(SERVER_URL + phrase.replace(" ", "%20"));

				// Configurar la conexión HTTP
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Accept", "application/json");

				// Leer la respuesta del servidor
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder response = new StringBuilder();
				String line;

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}

				reader.close();

				// Procesar la respuesta JSON
				JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
				JsonArray results = jsonResponse.getAsJsonArray("results");

				if (results.size() == 0) {
					System.out.println("No results found for the given phrase.");
				} else {
					System.out.println("\nResults:");
					for (int i = 0; i < results.size(); i++) {
						JsonObject result = results.get(i).getAsJsonObject();
						String word = result.get("word").getAsString();
						System.out.println("\nWord: " + word);

						JsonArray occurrences = result.getAsJsonArray("occurrences");
						for (int j = 0; j < occurrences.size(); j++) {
							JsonObject occurrence = occurrences.get(j).getAsJsonObject();
							System.out.println("  - Title: " + occurrence.get("title").getAsString());
							System.out.println("    Author: " + occurrence.get("author").getAsString());
							System.out.println("    Year: " + occurrence.get("year").getAsInt());
							System.out.println("    Language: " + occurrence.get("language").getAsString());
							System.out.println("    Frequency: " + occurrence.get("frequency").getAsInt());
							System.out.println("    Lines: " + occurrence.get("lines").getAsString());
							System.out.println("    Download Link: " + occurrence.get("download_link").getAsString());
							JsonArray previewLines = occurrence.getAsJsonArray("preview_lines");
							if (previewLines != null && previewLines.size() > 0) {
								System.out.println("    Preview Lines:");
								for (int k = 0; k < previewLines.size(); k++) {
									System.out.println("      - " + previewLines.get(k).getAsString());
								}
							}
							System.out.println("---------------------------------------------------\n");
						}
					}
				}

			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
}