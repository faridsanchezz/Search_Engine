package control.interfaces;

import java.io.IOException;
import java.util.Set;

public interface StoreManager<T> {

	void update(T item) throws IOException;

}
