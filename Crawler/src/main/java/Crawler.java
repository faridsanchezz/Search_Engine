import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler {

	public static void execute(int numOfBooks, String datalakeDirectory) throws IOException, InterruptedException {

		Path datalakePath = Paths.get(datalakeDirectory);
		ExecutorService executorService = Executors.newFixedThreadPool(5); // Pool de hilos, ajustable
		Random random = new Random();
		AtomicInteger booksDownloaded = new AtomicInteger(0); // Contador atómico de libros descargados exitosamente

		while (booksDownloaded.get() < numOfBooks) {
			int bookId = random.nextInt(99999);
			String urlString = "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId + ".txt";

			executorService.execute(() -> {
				boolean success = downloadBook(urlString, datalakePath, bookId);
				if (success) {
					booksDownloaded.incrementAndGet(); // Incrementa el contador de forma segura
				}
			});

			// Esperamos un breve momento antes de intentar el próximo libro
			Thread.sleep(500);
		}

		executorService.shutdown();
	}

	private static boolean downloadBook(String urlString, Path datalakePath, int bookId) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				String book = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
				if (LanguageFilter.languageFilter(book)) {
					System.out.println("Error. Book " + bookId + " not in English, Spanish or French.");
					return false;
				}

				// Guardar el archivo en el directorio especificado del datalake
				Path filePath = datalakePath.resolve(bookId + ".txt");
				try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
					writer.write(book);
				}

				System.out.println("Archivo descargado y guardado en 'datalake' como '" + bookId + ".txt'");
				return true;
			} else {
				System.out.println("Error al descargar el archivo " + bookId + ". Código de estado: " + responseCode);
			}
		} catch (IOException e) {
			System.out.println("Error al conectar con la URL " + urlString + ": " + e.getMessage());
		}
		return false;
	}
}

