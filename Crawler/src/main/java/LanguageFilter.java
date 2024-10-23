import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageFilter {

    public static boolean languageFilter(String book) {
        // Expresión regular ajustada para que sea más flexible con saltos de línea y espacios.
        Pattern languagePattern = Pattern.compile("(?i)Language\\s*:\\s*(\\w+)", Pattern.MULTILINE);
        Matcher matcher = languagePattern.matcher(book);

        // Comprobamos si encuentra el idioma
        if (matcher.find()) {
            String language = matcher.group(1).trim();
            System.out.println("Detected language: " + language); // Agregado para depurar

            // Verificamos si el idioma es uno de los permitidos
            return !(language.equalsIgnoreCase("Spanish") || language.equalsIgnoreCase("English") || language.equalsIgnoreCase("French"));
        } else {
            System.out.println("No language field found.");
        }

        // Si no encuentra el campo "Language", asumimos que no está en los idiomas requeridos
        return true;
    }
}
