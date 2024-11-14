package control;

import control.interfaces.SerializerController;
import model.Word;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class WordSerializerV2 implements SerializerController<Word.WordOccurrence> {

	@Override
	public void serialize(String datamartFile, Set<Word.WordOccurrence> occurrences) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(datamartFile, false))) {
			for (Word.WordOccurrence occurrence : occurrences) {
				writer.write(occurrence.getBookID() + " ");
				writer.write(String.join(" ", occurrence.getLineOccurrences().stream()
						.map(String::valueOf)
						.toArray(String[]::new)));
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error serializing occurrences", e);
		}
	}

	@Override
	public Set<Word.WordOccurrence> deserialize(String datamartFile) {
		Set<Word.WordOccurrence> occurrences = new HashSet<>();
		File file = new File(datamartFile);

		// If the file doesn't exist, return an empty set
		if (!file.exists()) {
			return occurrences;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				String[] parts = line.substring(1).split(" ");
				String bookID = parts[0];
				Set<Integer> lineOccurrences = new HashSet<>();
				for (int i = 1; i < parts.length; i++) {
					lineOccurrences.add(Integer.parseInt(parts[i]));
				}
				occurrences.add(new Word.WordOccurrence(bookID, lineOccurrences));

			}
		} catch (IOException e) {
			throw new RuntimeException("Error deserializing occurrences", e);
		}

		return occurrences;
	}
}
