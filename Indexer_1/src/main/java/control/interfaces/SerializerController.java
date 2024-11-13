package control.interfaces;

import java.util.Set;

public interface SerializerController<T> {

	void serialize(Set<T> items);

	Set<T> deserialize();
}
