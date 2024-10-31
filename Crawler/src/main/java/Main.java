import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String datalakePath = "pathDataLake";
        int num_Books = 5;
        try {
            Crawler.execute(num_Books, datalakePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

