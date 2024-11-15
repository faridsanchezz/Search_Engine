package control.interfaces;

import java.io.IOException;

public interface MetadataStoreManager<T> {
	void update(T item) throws IOException;
}
