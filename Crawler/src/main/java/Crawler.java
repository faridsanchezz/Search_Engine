import java.io.BufferedWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Crawler {

    public static void execute(int numOfBooks, String datalakeDirectory) throws IOException, InterruptedException {
        // Crear directorio si no existe
        Path datalakePath = Paths.get(datalakeDirectory);
        if (!Files.exists(datalakePath)) {
            Files.createDirectories(datalakePath);
        }

        int counter = 0;
        Random random = new Random();

        while (counter < numOfBooks) {
            int i = random.nextInt(60000) + 1; // Limitar el ID a libros válidos
            String urlString = "https://www.gutenberg.org/cache/epub/" + i + "/pg" + i + ".txt";

            if (counter >= numOfBooks) {
                break;
            }

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    String book = new String(url.openStream().readAllBytes(), StandardCharsets.UTF_8);
                    if (LanguageFilter.languageFilter(book)) {
                        System.out.println("Error. Book " + i + " not in English, Spanish or French.");
                        continue;
                    }

                    // Guardar el archivo
                    Path filePath = datalakePath.resolve(i + ".txt");
                    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                        writer.write(book);
                    }

                    System.out.println("Archive downloaded and saved in 'datalake' as '" + i + ".txt'");
                    counter++;
                } else {
                    System.out.println("Error while downloading archive " + i + ". State code: " + responseCode);
                }

                // Pausa para evitar sobrecargar el servidor
                Thread.sleep(3000);

            } catch (IOException e) {
                System.out.println("Error connecting to URL " + urlString + ": " + e.getMessage());
            }
        }
    }
}
