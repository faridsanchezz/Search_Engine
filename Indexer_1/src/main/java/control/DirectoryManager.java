package control;

import java.io.IOException;
import java.nio.file.*;

public class DirectoryManager {

	public static void createDirectory(String directory) {
		Path directoryPath = Paths.get(directory);
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

