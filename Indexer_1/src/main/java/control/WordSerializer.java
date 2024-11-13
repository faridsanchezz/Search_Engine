package control;

import control.interfaces.SerializerController;
import model.Word;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class WordSerializer implements SerializerController<Word> {
	private final String datamartPath;

	public WordSerializer(String datamartPath) {
		this.datamartPath = datamartPath;
	}

	@Override
	public void serialize(Set<Word> words) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.datamartPath, false))) {
			for (Word word : words) {

				writer.write(word.getText());
				writer.newLine();

				for (Word.WordOccurrence occurrence : word.getOccurrences()) {
					writer.write("- " + occurrence.getBookID() + " ");
					writer.write(String.join(" ", occurrence.getLineOccurrences().stream()
							.map(String::valueOf)
							.toArray(String[]::new)));
					writer.newLine();
				}
				writer.newLine();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<Word> deserialize() {
		Set<Word> words = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(this.datamartPath))) {
			String line;
			String wordText = null;
			Set<Word.WordOccurrence> wordOccurrences = new HashSet<>();

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				if (!line.startsWith("-")) {
					if (wordText != null && !wordOccurrences.isEmpty()) {
						Word word = new Word(wordText, wordOccurrences);
						words.add(word);
						wordOccurrences.clear();
					}

					wordText = line;
				} else {

					String[] parts = line.substring(2).split(" ");
					String bookID = parts[0];
					Set<Integer> lineOccurrences = new HashSet<>();
					for (int i = 1; i < parts.length; i++) {
						lineOccurrences.add(Integer.parseInt(parts[i]));
					}


					Word.WordOccurrence wordOccurrence = new Word.WordOccurrence(bookID, lineOccurrences);
					wordOccurrences.add(wordOccurrence);
				}
			}

			if (wordText != null && !wordOccurrences.isEmpty()) {
				Word word = new Word(wordText, new HashSet<>(wordOccurrences));
				words.add(word);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return words;
	}
}
