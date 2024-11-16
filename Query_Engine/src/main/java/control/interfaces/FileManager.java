package control.interfaces;

import model.Word;

import java.util.Set;

public interface FileManager {
	Set<Word> readFile(String filename);
}
