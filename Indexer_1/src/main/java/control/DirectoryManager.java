package control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectoryManager {

	public static void createDirectory(File directory) {
		Path directoryPath = directory.toPath();
		try {
			if (Files.notExists(directoryPath)) {
				Files.createDirectories(directoryPath);
				System.out.println("Directory created: " + directoryPath);
			}
		} catch (IOException e) {
			System.out.println("Error creating directory or file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}

