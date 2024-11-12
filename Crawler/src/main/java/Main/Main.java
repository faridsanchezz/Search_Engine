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
		String datalakePath = "/Users/nestoruniversidad/Desktop/test";
		int numBooks = 5;

		Downloader downloader = new BookDownloader();
		Filter filter = new LanguageFilter();
		CrawlerController controller = new WebCrawlerController(downloader, filter);

		try {
			controller.execute(numBooks, datalakePath);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}


