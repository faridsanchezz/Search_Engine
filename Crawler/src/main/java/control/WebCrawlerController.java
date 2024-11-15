package control;

import interfaces.CrawlerController;
import interfaces.Downloader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public class WebCrawlerController implements CrawlerController {
	private final Downloader downloader;

	public WebCrawlerController(Downloader downloader) {
		this.downloader = downloader;
	}

	@Override
	public void execute(int numOfBooks, String datalakeDirectory) throws IOException, InterruptedException {
		Path datalakePath = Path.of(datalakeDirectory);
		Random random = new Random();
		int booksDownloaded = 0;

		while (booksDownloaded < numOfBooks) {
			int bookId = random.nextInt(99999);
			String urlString = "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId + ".txt";

			boolean success = downloader.download(bookId, urlString, datalakePath);
			if (success) {
				booksDownloaded++;
				System.out.println("Libros descargados: " + booksDownloaded);
			}
			Thread.sleep(500);
		}
	}

}
