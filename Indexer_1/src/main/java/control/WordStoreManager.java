package control;

import model.Word;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

public class WordStoreManager {
	private Set<Word> datamartWords;
	private final String filePath;

	// Constructor que recibe la ruta del archivo
	public WordStoreManager(String filePath) throws IOException {
		this.filePath = filePath;
		loadDatamart();
	}

	// Método para cargar palabras desde el archivo o crear un nuevo archivo si no existe
	private void loadDatamart() throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			// Si el archivo existe, cargar palabras
			datamartWords = new HashSet<>(WordSerializer.deserialize(filePath));
		} else {
			// Si el archivo no existe, inicializar el datamart como un conjunto vacío
			System.out.println("El archivo no existe. Creando un nuevo archivo 'words'.");
			datamartWords = new HashSet<>();
			saveDatamart();  // Guardar el archivo vacío para crear "words"
		}
	}

	// Método para actualizar el datamart con nuevas palabras
	public void update(Set<Word> newWords) throws IOException {
		for (Word newWord : newWords) {
			Optional<Word> existingWordOpt = datamartWords.stream()
					.filter(word -> word.equals(newWord))
					.findFirst();

			if (existingWordOpt.isPresent()) {
				Word existingWord = existingWordOpt.get();
				for (Word.WordOccurrence newOccurrence : newWord.getOccurrences()) {
					// Convertir el array de ocurrencias a una lista para poder usar stream
					Optional<Word.WordOccurrence> existingOccurrenceOpt = Arrays.asList(existingWord.getOccurrences()).stream()
							.filter(occurrence -> occurrence.getBook_id().equals(newOccurrence.getBook_id()))
							.findFirst();

					if (existingOccurrenceOpt.isPresent()) {
						// Si el libro ya está en las ocurrencias, agregar las nuevas líneas sin duplicados
						Word.WordOccurrence existingOccurrence = existingOccurrenceOpt.get();
						newOccurrence.getLineOccurrences().forEach(line -> {
							if (!existingOccurrence.getLineOccurrences().contains(line)) {
								existingOccurrence.getLineOccurrences().add(line);
							}
						});
					} else {
						// Si el libro no está en las ocurrencias, agregar la nueva ocurrencia
						existingWord.addOccurrence(newOccurrence);
					}
				}
			} else {
				// Si la palabra no existe en el datamart, añadirla como una nueva palabra
				datamartWords.add(newWord);
			}
		}

		saveDatamart();
	}

	// Método para guardar las palabras en el archivo
	private void saveDatamart() throws IOException {
		WordSerializer.serialize(new ArrayList<>(this.datamartWords), this.filePath);
	}
}
