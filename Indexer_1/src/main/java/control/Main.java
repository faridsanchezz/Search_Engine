package control;

import model.Metadata;
import model.Word;
import model.Word.WordOccurrence;

import java.io.IOException;
import java.util.*;

public class Main {
	public static void main(String[] args) {
		try {
			// Define las rutas de los archivos para Metadata y Word
			String metadataFilePath = "metadata.txt";
			String wordFilePath = "words.txt";


			// Crear una instancia de MetadataStoreManager
			MetadataStoreManager metadataStoreManager = new MetadataStoreManager(metadataFilePath);

			// Crear metadatos de prueba
			Metadata metadata1 = new Metadata("00002", "Title 7", "Author 1", 2021, "English", "http://link1.com");

			// Agregar los metadatos
			metadataStoreManager.addMetadata(metadata1);

			System.out.println("Almacenamiento de metadatos completado.");


			// Crear una instancia de WordStoreManager
			WordStoreManager wordStoreManager = new WordStoreManager(wordFilePath);

			// Conjunto de palabras nuevas para actualizar
			Set<Word> newWords = new HashSet<>();

			// Crear otra palabra para añadir
			List<Integer> lines3 = Arrays.asList(400, 407);
			WordOccurrence occurrence3 = new WordOccurrence("00002", lines3);
			Word word2 = new Word("casa", new WordOccurrence[]{occurrence3});
			newWords.add(word2);

			// Llamar a update para actualizar el datamart con las nuevas palabras
			wordStoreManager.update(newWords);

		} catch (IOException e) {
			System.err.println("Error al procesar el almacenamiento: " + e.getMessage());
		}
	}
}
