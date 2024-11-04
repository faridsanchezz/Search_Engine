package Main;

import Control.Word;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class JsonController implements JsonManager {

    private final Gson gson;

    public JsonController() {
        this.gson = new Gson();
    }

    @Override
    public Word readJSON(String filePath) {
        HashMap<String, Map<String, Object>> content = new HashMap<>();

        try (Reader reader = new FileReader(filePath)) {
            content = gson.fromJson(reader, HashMap.class);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }
        return content;
    }
}
