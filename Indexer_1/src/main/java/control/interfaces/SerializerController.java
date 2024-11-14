package control.interfaces;

import java.util.Set;

public interface SerializerController<T> {

	void serialize(String filePath, Set<T> items);

	Set<T> deserialize(String filePath);
}
