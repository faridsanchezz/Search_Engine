package Main;
import control.BookDownloader;
import control.LanguageFilter;
import control.WebCrawlerController;
import interfaces.CrawlerController;
import interfaces.Downloader;
import interfaces.Filter;
import java.io.IOException;
public class Main {

	public static void main(String[] args) {
		String datalakePath = "datalake\\";
		int numBooks = 2;
		Filter filter = new LanguageFilter();
		Downloader downloader = new BookDownloader(filter);
		CrawlerController controller = new WebCrawlerController(downloader);

		try {
			controller.execute(numBooks, datalakePath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}


