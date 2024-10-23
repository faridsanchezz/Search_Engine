import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String datalakePath = args[0];
        try {
            Crawler.execute(Integer.parseInt(args[1]), datalakePath);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
