package control;

import model.Word;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface QueryEngineManager {

	Word searchBook(Set<Word> wordsDatamart, String word);

	public List<String> getPreviewLines(String datalakePath, String idBook, List<Integer> lines);

	public Map<String, Object> printResultsAsMap(Set<Word> wordsDatamart, String datalakePath, String metadataFilePath, String word);

}
