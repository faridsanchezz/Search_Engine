package control;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@State(Scope.Benchmark)
public class BencharkQueryEngine1 {
    private BookController bookController;

    @Setup(Level.Trial)
    public void setUp() {

        bookController = new BookController();
    }

    @Param({
            "typographical",
            "latitude",
            "good wooed",
            "stop cruelty",
            "antonio want salary",
            "perfect avais filippo",
            "pretend immenses caserne pire",
            "information going fontana highbred",
            "gold pick rome picture siffle",
            "swim preface prêts protecting woman",
            "torture poet lover fearless spoke papas",
            "roll different reproduce collin golf criticism",
            "assisté empty chapelle walker jeanne kate reformed",
            "reformed flow christmas lantern amaranté adelaide suggests",
            "rose pulpit author dirt second dominican casino paganini",
            "base monastery europe recommended going bologna year near end"
    })
    private String test_phrase;


    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    @Threads(4)
    public Map<String, Object> benchmarkSearchWords() {

        return bookController.searchWords(test_phrase);
    }
}
