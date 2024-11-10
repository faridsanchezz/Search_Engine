package control;

import model.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface QueryEngineManager {

    Word searchBook(String wordsDatamartPath, String word);

    public List<String > getPreviewLines(String datalakePath, String idBook, List<Integer> lines);

    public Map<String, Object> printResultsAsMap(String wordsDatamartPath, String datalakePath, String metadataFilePath, String word);

}