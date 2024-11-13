package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Metadata;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MetadataStoreManager implements StoreManager<Metadata> {
	private final String filePath;
	private final SerializerController<Metadata> metadataSerializer;
	private Set<Metadata> metadataSetDatamart;

	public MetadataStoreManager(String filePath, SerializerController<Metadata> metadataSerializer) throws IOException {
		this.filePath = filePath;
		this.metadataSerializer = metadataSerializer;
	}

	@Override
	public Set<Metadata> loadDatamart() throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			return metadataSerializer.deserialize();
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public void saveDatamart() throws IOException {
		metadataSerializer.serialize(this.metadataSetDatamart);
	}

	@Override
	public void update(Metadata new_metadata) throws IOException {
		this.metadataSetDatamart = loadDatamart();
		if (this.metadataSetDatamart.stream().noneMatch(m -> m.hashCode() == new_metadata.hashCode())) {
			this.metadataSetDatamart.add(new_metadata);
		}

		saveDatamart();
	}
}
