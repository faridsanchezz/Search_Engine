package control.interfaces;

import java.io.IOException;

public interface CrawlerController {
	void execute(int numOfBooks, String datalakeDirectory) throws IOException, InterruptedException;
}
