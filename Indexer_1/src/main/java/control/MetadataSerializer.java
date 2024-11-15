package control;

import control.interfaces.SerializerController;
import model.Metadata;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class MetadataSerializer implements SerializerController<Metadata> {

	@Override
	public void serialize(File metadataDatamartFile,Set<Metadata> metadataSet) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(metadataDatamartFile, false))) {
			for (Metadata metadata : metadataSet) {
				writer.write(metadata.getBookID());
				writer.newLine();
				writer.write(metadata.getName());
				writer.newLine();
				writer.write(metadata.getAuthor());
				writer.newLine();
				writer.write(metadata.getYear());
				writer.newLine();
				writer.write(metadata.getLanguage());
				writer.newLine();
				writer.write(metadata.getDownloadLink());
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<Metadata> deserialize(File metadataDatamartFile) {
		Set<Metadata> metadataSet = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(metadataDatamartFile))) {
			String line;
			String book_id = null, name = null, author = null, language = null, downloadLink = null, year = null;

			while ((line = reader.readLine()) != null) {
				book_id = line.trim();
				name = reader.readLine().trim();
				author = reader.readLine().trim();
				year = reader.readLine().trim();
				language = reader.readLine().trim();
				downloadLink = reader.readLine().trim();
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
