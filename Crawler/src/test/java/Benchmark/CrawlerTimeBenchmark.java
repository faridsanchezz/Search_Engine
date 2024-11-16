package Benchmark;

import control.BookDownloader;
import control.LanguageFilter;
import control.WebCrawlerController;
import control.interfaces.CrawlerController;
import control.interfaces.Downloader;
import control.interfaces.Filter;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@Fork(value = 1)
public class CrawlerTimeBenchmark {

	@Param({"5", "10", "20", "50"})
	private int numBooks;

	private CrawlerController controller;

	@Setup
	public void setUp() {
		Filter filter = new LanguageFilter();
		Downloader downloader = new BookDownloader(filter);
		controller = new WebCrawlerController(downloader);
	}

	@Benchmark
	public void measureExecutionTime() throws Exception {
		controller.execute(numBooks, "/datalake");
	}
}
