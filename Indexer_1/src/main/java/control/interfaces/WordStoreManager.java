package control.interfaces;

import java.io.IOException;
import java.util.Set;

public interface WordStoreManager<T> {

	void update(Set<T> items) throws IOException;

}
