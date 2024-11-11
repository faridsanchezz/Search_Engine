package control;

import java.io.IOException;
import java.util.Set;

public interface StoreManager<T> {

	Set<T> loadDatamart() throws IOException;

	void saveDatamart() throws IOException;

	void update(T item) throws IOException;

}
