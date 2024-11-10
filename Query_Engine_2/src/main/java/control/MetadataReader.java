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
                // Read each field based on the provided format
                String bookId = line.trim();
                String name = reader.readLine().trim();
                String author = reader.readLine().trim();
                int year = Integer.parseInt(reader.readLine().trim());
                String language = reader.readLine().trim();
                String downloadLink = reader.readLine().trim();

                // Create a Metadata object and add it to the list
                Metadata metadata = new Metadata(bookId, name, author, year, language, downloadLink);
                metadataList.add(metadata);

                // Skip the blank line between records (if there is any)
                reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error reading metadata file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing year in metadata file: " + e.getMessage());
        }

        return metadataList;
    }
}
