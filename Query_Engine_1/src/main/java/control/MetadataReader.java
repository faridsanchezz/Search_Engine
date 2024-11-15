package control;
import model.Metadata;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MetadataReader {

    public static List<Metadata> readMetadata(String filePath) {
        List<Metadata> metadataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line ends with ".txt" to identify the start of a new record
                if (line.trim().endsWith(".txt")) {
                    String bookId = line.trim(); // Book ID
                    String name = reader.readLine().trim(); // Book name
                    String author = reader.readLine().trim(); // Author
                    int year = Integer.parseInt(reader.readLine().trim()); // Year
                    String language = reader.readLine().trim(); // Language
                    String downloadLink = reader.readLine().trim(); // Download link

                    // Create a Metadata object and add it to the list
                    Metadata metadata = new Metadata(bookId, name, author, year, language, downloadLink);
                    metadataList.add(metadata);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading metadata file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing year in metadata file: " + e.getMessage());
        }

        return metadataList;
    }
}
