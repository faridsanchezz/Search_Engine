package control;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@State(Scope.Benchmark)
public class BencharkQueryEngine1 {
    private BookController bookController;

    @Setup(Level.Trial)
    public void setUp() {
        // Inicializar manualmente el controlador
        bookController = new BookController();
    }

    // Varias frases de prueba para simular distintas consultas
    //@Param({
    //        "simple search query",
    //        "complex phrase with multiple words",
    //        "testing search with special characters",
    //        "another example search query",
    //        "short query"
    //})
    //private String test_phrase;

    private static final String TEST_PHRASE = "reëntering"; // Frase de prueba

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    @Threads(4) // Número de hilos para simular peticiones concurrentes
    public Map<String, Object> benchmarkSearchWords() {
        // Llamada directa al método searchWords del controlador
        return bookController.searchWords(TEST_PHRASE);
    }
}
