package control;

import control.interfaces.SerializerController;
import model.Metadata;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class MetadataSerializer implements SerializerController<Metadata> {

	@Override
	public void serialize(String metadataDatamartFile,Set<Metadata> metadataSet) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataDatamartFile, false))) {
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
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<Metadata> deserialize(String metadataDatamartFile) {
		Set<Metadata> metadataSet = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(metadataDatamartFile))) {
			String line;
			String book_id = null, name = null, author = null, language = null, downloadLink = null, year = null;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() && book_id != null) {
					metadataSet.add(new Metadata(book_id, name, author, year, language, downloadLink));
					book_id = name = author = language = downloadLink = year = null;
				} else if (book_id == null) {
					book_id = line;
				} else if (name == null) {
					name = line;
				} else if (author == null) {
					author = line;
				} else if (year == null) {
					year = line;
				} else if (language == null) {
					language = line;
				} else if (downloadLink == null) {
					downloadLink = line;
				}
			}

			if (book_id != null) {
				metadataSet.add(new Metadata(book_id, name, author, year, language, downloadLink));
			}
		} catch (FileNotFoundException e) {
			return metadataSet;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return metadataSet;
	}
}
