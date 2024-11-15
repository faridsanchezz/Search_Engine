package control;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class BenchmarkQueryEngine {
    private BookController bookController;
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
    String WORDS_DATAMART_PATH_FileWord = "datamart/words/";
    String DATALAKE_PATH_FileWord = "datalake/";
    String METADATA_FILE_PATH_FileWord = "datamart/metadata/metadata";

    String WORDS_DATAMART_PATH_OneFile = "datamart/words";
    String DATALAKE_PATH_OneFile = "datalake/";
    String METADATA_FILE_PATH_OneFile = "datamart/metadata";

    QueryEngineFileWord queryEngineFileWord = new QueryEngineFileWord();
    QueryEngineOneFile queryEngineOneFile = new QueryEngineOneFile();




    @Setup(Level.Trial)
    public void setUp() {

        //bookController = new BookController(WORDS_DATAMART_PATH_FileWord,DATALAKE_PATH_FileWord,  METADATA_FILE_PATH_FileWord,  queryEngineFileWord);

        bookController = new BookController(WORDS_DATAMART_PATH_OneFile,DATALAKE_PATH_OneFile,  METADATA_FILE_PATH_OneFile,  queryEngineOneFile);
    }

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