package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Word;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

public class WordStoreManagerV2 implements StoreManager<Word> {
	private final SerializerController<Word.WordOccurrence> wordSerializer;
	private final String datamartDirectory;

	public WordStoreManagerV2(String datamartDirectory, SerializerController<Word.WordOccurrence> wordSerializer) throws IOException {
		this.wordSerializer = wordSerializer;
		this.datamartDirectory = Paths.get(datamartDirectory, "words").toString();
		DirectoryManager.createDirectory(this.datamartDirectory);
	}

	@Override
	public void update(Word newWord) {
		String datamartFilePath = Paths.get(this.datamartDirectory, newWord.getText() + ".txt").toString();

		// Load existing occurrences from file
		Set<Word.WordOccurrence> datamartSetWordOccurrences = wordSerializer.deserialize(datamartFilePath);

		// Update or add occurrences
		for (Word.WordOccurrence newOccurrence : newWord.getOccurrences()) {
			Word.WordOccurrence targetOcurrence = datamartSetWordOccurrences.stream()
					.filter(o -> o.hashCode() == newOccurrence.hashCode())
					.findFirst()
					.orElse(null);

			if (targetOcurrence == null) {
				datamartSetWordOccurrences.add(newOccurrence);
			}
		}

		wordSerializer.serialize(datamartFilePath, datamartSetWordOccurrences);
	}
}
