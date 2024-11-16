package control;

import control.interfaces.Downloader;
import control.interfaces.Filter;

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

				Path filePath = datalakePath.resolve(String.valueOf(bookId));

				try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
					writer.write(content);
				}
				System.out.println("Book downloaded and saved in 'datalake' as '" + bookId);
				return true;
			} else {
				System.out.println("Error downloading book " + bookId + ". Code state: " + responseCode);
			}
		} catch (IOException e) {
			System.out.println("Error connecting with URL " + urlString + ": " + e.getMessage());
		}
		return false;
	}
}
