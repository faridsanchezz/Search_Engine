package control;

import control.interfaces.SerializerController;
import control.interfaces.WordStoreManager;
import model.Word;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

public class WordStoreManagerV2 implements WordStoreManager<Word> {
	private final SerializerController<Word.WordOccurrence> wordSerializer;
	private final File datamartDirectory;

	private final Set<String> lockedDocuments = ConcurrentHashMap.newKeySet();

	public WordStoreManagerV2(String datamartDirectory, SerializerController<Word.WordOccurrence> wordSerializer) throws IOException {
		this.wordSerializer = wordSerializer;
		this.datamartDirectory = Paths.get(datamartDirectory, "words").toFile();
		DirectoryManager.createDirectory(this.datamartDirectory);
	}

	@Override
	public void update(Set<Word> newWordSet) {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		for (Word newWord : newWordSet) {
			forkJoinPool.submit(() -> {
				File datamartFilePath = new File(this.datamartDirectory, newWord.getText());
				String filePath = datamartFilePath.getAbsolutePath();

				if (!lockedDocuments.add(filePath)) {
					return;
				}

				try {
					Set<Word.WordOccurrence> datamartSetWordOccurrences = wordSerializer.deserialize(datamartFilePath);

					for (Word.WordOccurrence newOccurrence : newWord.getOccurrences()) {
						Word.WordOccurrence targetOccurrence = datamartSetWordOccurrences.stream()
								.filter(o -> o.hashCode() == newOccurrence.hashCode())
								.findFirst()
								.orElse(null);

						if (targetOccurrence == null) {
							datamartSetWordOccurrences.add(newOccurrence);
						}
					}
					wordSerializer.serialize(datamartFilePath, datamartSetWordOccurrences);

				} finally {
					lockedDocuments.remove(filePath);
				}
			});
		}
		forkJoinPool.shutdown();
	}
}
