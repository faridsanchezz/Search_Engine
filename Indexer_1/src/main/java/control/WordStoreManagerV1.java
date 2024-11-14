package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Word;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class WordStoreManagerV1 implements StoreManager<Word> {
	private final SerializerController<Word> wordSerializer;
	private Set<Word> datamartSetWords;
	private final String datamartFilePath;


	public WordStoreManagerV1(String datamartDirectory, SerializerController<Word> wordSerializer) throws IOException {
		this.wordSerializer = wordSerializer;
		this.datamartFilePath = Paths.get(datamartDirectory, "words.txt").toString();
	}

	@Override
	public void update(Word new_word) throws IOException {
		this.datamartSetWords = wordSerializer.deserialize(this.datamartFilePath);
		Word targetWord = datamartSetWords.stream()
				.filter(w -> w.hashCode() == new_word.hashCode())
				.findFirst()
				.orElse(null);
		if (targetWord != null) {
			targetWord.addOccurrence(new_word.getOccurrences());
		} else {
			datamartSetWords.add(new_word);
		}

		wordSerializer.serialize(this.datamartFilePath, this.datamartSetWords);
	}
}
