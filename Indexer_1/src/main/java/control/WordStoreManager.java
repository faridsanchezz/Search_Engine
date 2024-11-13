package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Word;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class WordStoreManager implements StoreManager<Word> {
	private final String datamartPath;
	private final SerializerController<Word> wordSerializer;
	private Set<Word> datamartSetWords;


	public WordStoreManager(String datamartPath, SerializerController<Word> wordSerializer) throws IOException {
		this.datamartPath = datamartPath;
		this.wordSerializer = wordSerializer;
	}

	@Override
	public Set<Word> loadDatamart() throws IOException {
		File file = new File(this.datamartPath);
		if (file.exists()) {
			return wordSerializer.deserialize();
		} else {
			System.out.println("Creating Words Datamart");
			return new HashSet<>();
		}
	}

	@Override
	public void update(Word new_word) throws IOException {
		this.datamartSetWords = loadDatamart();
		Word targetWord = datamartSetWords.stream()
				.filter(w -> w.hashCode() == new_word.hashCode())
				.findFirst()
				.orElse(null);
		if (targetWord != null) {
			targetWord.addOccurrence(new_word.getOccurrences());
		} else {
			datamartSetWords.add(new_word);
		}

		saveDatamart();
	}


	@Override
	public void saveDatamart() throws IOException {
		wordSerializer.serialize(this.datamartSetWords);
	}
}
