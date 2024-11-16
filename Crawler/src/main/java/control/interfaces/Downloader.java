package control.interfaces;

import java.nio.file.Path;

public interface Downloader {
	boolean download(int bookId, String url, Path datalakePath);

}
