package control;

import control.interfaces.SerializerController;
import control.interfaces.WordStoreManager;
import model.Word;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class WordStoreManagerV1 implements WordStoreManager<Word> {
	private final SerializerController<Word> wordSerializer;
	private final File datamartFilePath;


	public WordStoreManagerV1(String datamartDirectory, SerializerController<Word> wordSerializer) throws IOException {
		this.wordSerializer = wordSerializer;
		this.datamartFilePath = new File(datamartDirectory, "words");
	}

	@Override
	public void update(Set<Word> new_word_set) throws IOException {
		Set<Word> datamartSetWords = wordSerializer.deserialize(this.datamartFilePath);

		for (Word new_word : new_word_set) {
			Word targetWord = datamartSetWords.stream()
					.filter(w -> w.hashCode() == new_word.hashCode())
					.findFirst()
					.orElse(null);
			if (targetWord != null) {
				targetWord.addOccurrence(new_word.getOccurrences());
			} else {
				datamartSetWords.add(new_word);
			}
		}
		wordSerializer.serialize(this.datamartFilePath, datamartSetWords);
	}
}
