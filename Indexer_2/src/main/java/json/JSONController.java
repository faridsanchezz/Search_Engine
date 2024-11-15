package json;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JSONController {

	// Method to read JSON data from a file and return it as a JSONObject
	public static JSONObject readJSON(String path) {
		try {
			String content = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			return new JSONObject(content);
		} catch (IOException e) {
			// If the file doesn't exist, return an empty JSONObject
			return new JSONObject();
		}
	}

	// Method to write a JSONObject to a file
	public static void writeJSON(String path, JSONObject input) throws IOException {
		Files.write(Paths.get(path), input.toString(1).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
	}
}
