package control;

import model.Metadata;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;
import java.util.ArrayList;

public class MetadataStoreManager {
	private Set<Metadata> metadataSet;
	private final String filePath;

	// Constructor que recibe la ruta del archivo de metadatos
	public MetadataStoreManager(String filePath) throws IOException {
		this.filePath = filePath;
		loadDatamart();
	}

	// Método para cargar los metadatos desde el archivo o crear un nuevo archivo si no existe
	private void loadDatamart() throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			// Si el archivo existe, cargar los metadatos
			metadataSet = new HashSet<>(MetadataSerializer.deserialize(filePath));
		} else {
			// Si el archivo no existe, inicializar el conjunto de metadatos vacío y crear el archivo
			System.out.println("El archivo de metadatos no existe. Creando un nuevo archivo.");
			metadataSet = new HashSet<>();
			saveDatamart();  // Guardar el archivo vacío
		}
	}

	// Método para añadir nuevos metadatos si no existen previamente
	public void addMetadata(Metadata newMetadata) throws IOException {
		// Verificar si los metadatos ya existen utilizando el `book_id` como clave de identificación
		boolean exists = metadataSet.stream()
				.anyMatch(metadata -> metadata.getBook_id().equals(newMetadata.getBook_id()));

		if (!exists) {
			// Si no existe, añadir los metadatos
			metadataSet.add(newMetadata);
			saveDatamart();  // Guardar los cambios en el archivo
		} else {
			System.out.println("Los metadatos ya existen para el book_id: " + newMetadata.getBook_id());
		}
	}

	// Método para guardar los metadatos en el archivo
	private void saveDatamart() throws IOException {
		MetadataSerializer.serialize(new ArrayList<>(this.metadataSet), this.filePath);
	}
}
