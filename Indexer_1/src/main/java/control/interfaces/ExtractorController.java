package control.interfaces;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

public interface ExtractorController<T> {
	Set<T> get(BufferedReader source, String identifier) throws IOException;
}
