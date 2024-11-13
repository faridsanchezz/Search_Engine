package control;
import interfaces.Downloader;
import interfaces.Filter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BookDownloader implements Downloader {
	private final Filter languageFilter;
	public BookDownloader(Filter languageFilter) {
		this.languageFilter = languageFilter;
	}

	@Override
	public boolean download(int bookId, String urlString, Path datalakePath) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();
			if (responseCode == 200) {
				String content = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
				if (languageFilter.applyFilter(content)) {
					System.out.println("Error. Book " + bookId + " not in English, Spanish, or French.");
					return false;
				}
				Path filePath = datalakePath.resolve(bookId + ".txt");
				try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
					writer.write(content);
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
