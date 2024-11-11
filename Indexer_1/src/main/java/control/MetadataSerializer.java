package control;

import model.Metadata;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MetadataSerializer {

	public static void serialize(Collection<Metadata> metadataSet, String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {  // Sobrescribir archivo
			for (Metadata metadata : metadataSet) {
				writer.write(metadata.getBookID());
				writer.newLine();
				writer.write("  " + metadata.getName());
				writer.newLine();
				writer.write("  " + metadata.getAuthor());
				writer.newLine();
				writer.write("  " + metadata.getYear());
				writer.newLine();
				writer.write("  " + metadata.getLanguage());
				writer.newLine();
				writer.write("  " + metadata.getDownloadLink());
				writer.newLine();
				writer.newLine();
			}
		}
	}

	public static Collection<Metadata> deserialize(String filePath) throws IOException {
		Set<Metadata> metadataSet = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			String book_id = null, name = null, author = null, language = null, downloadLink = null;
			int year = 0;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() && book_id != null) {
					metadataSet.add(new Metadata(book_id, name, author, year, language, downloadLink));
					book_id = name = author = language = downloadLink = null;
					year = 0;
				} else if (book_id == null) {
					book_id = line;
				} else if (name == null) {
					name = line;
				} else if (author == null) {
					author = line;
				} else if (year == 0) {
					year = Integer.parseInt(line.trim());
				} else if (language == null) {
					language = line;
				} else if (downloadLink == null) {
					downloadLink = line;
				}
			}

			if (book_id != null) {
				metadataSet.add(new Metadata(book_id, name, author, year, language, downloadLink));
			}
		}

		return metadataSet;
	}
}
