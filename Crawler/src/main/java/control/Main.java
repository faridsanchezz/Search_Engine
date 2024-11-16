package control;

import control.interfaces.CrawlerController;
import control.interfaces.Downloader;
import control.interfaces.Filter;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {

		// Paths for Docker
		String datalakePath = "/app/datalake/";

		int numBooks = 10;
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


