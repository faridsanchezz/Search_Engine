package control;

import model.Word;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WordStoreManager implements StoreManager<Word> {
	private final String datamartPath;
	private Set<Word> datamartSetWords;

	public WordStoreManager(String datamartPath) throws IOException {
		this.datamartPath = datamartPath;
	}

	@Override
	public Set<Word> loadDatamart() throws IOException {
		File file = new File(this.datamartPath);
		if (file.exists()) {
			return WordSerializer.deserialize(this.datamartPath);
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
		}else{
			datamartSetWords.add(new_word);
		}

		saveDatamart();
	}


	@Override
	public void saveDatamart() throws IOException {
		WordSerializer.serialize(this.datamartSetWords, this.datamartPath);
	}
}
