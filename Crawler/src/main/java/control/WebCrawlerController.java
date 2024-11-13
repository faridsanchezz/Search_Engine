package control;

import interfaces.CrawlerController;
import interfaces.Downloader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebCrawlerController implements CrawlerController {
	private final Downloader downloader;

	public WebCrawlerController(Downloader downloader) {
		this.downloader = downloader;
	}

	@Override
	public void execute(int numOfBooks, String datalakeDirectory) throws IOException, InterruptedException {
		Path datalakePath = Path.of(datalakeDirectory);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		Random random = new Random();
		AtomicInteger booksDownloaded = new AtomicInteger(0);

		while (booksDownloaded.get() < numOfBooks) {
			int bookId = random.nextInt(99999);
			String urlString = "https://www.gutenberg.org/cache/epub/" + bookId + "/pg" + bookId + ".txt";

			executorService.execute(() -> {
				boolean success = downloader.download(bookId, urlString, datalakePath);
				if (success) {
					booksDownloaded.incrementAndGet();
				}
			});

			Thread.sleep(500);
		}

		executorService.shutdown();
	}
}
