package control.interfaces;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Set;

public interface SerializerController<T> {

	void serialize(File filePath, Set<T> items);

	Set<T> deserialize(File filePath);
}
