package control;

import model.Metadata;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetadataReader {

	public static Set<Metadata> readMetadata(String filePath) {
		Set<Metadata> metadataSet = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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
