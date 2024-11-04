package Main;

import java.util.Map;

public interface JsonManager {
    Map<String, Map<String, Object>> readJSON(String words_data);
}
