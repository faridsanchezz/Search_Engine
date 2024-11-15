package control;

import control.interfaces.SerializerController;
import model.Word;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class WordSerializerV1 implements SerializerController<Word> {

	public WordSerializerV1() {

	}

	@Override
	public void serialize(File datamartFile, Set<Word> new_words) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(datamartFile, false))) {
			for (Word new_word : new_words) {
				writer.write(new_word.getText());
				writer.newLine();
				for (Word.WordOccurrence occurrence : new_word.getOccurrences()) {
					writer.write("- " + occurrence.getBookID() + " ");
					writer.write(String.join(" ", occurrence.getLineOccurrences().stream()
							.map(String::valueOf)
							.toArray(String[]::new)));
					writer.newLine();
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<Word> deserialize(File datamartFile) {
		Set<Word> words = new HashSet<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(datamartFile))) {
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
			return words;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return words;
	}
}
