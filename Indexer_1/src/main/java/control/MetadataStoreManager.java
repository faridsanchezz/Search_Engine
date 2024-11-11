package control;

import model.Metadata;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MetadataStoreManager implements StoreManager<Metadata>{
	private final String filePath;
	private Set<Metadata> metadataSetDatamart;

	public MetadataStoreManager(String filePath) throws IOException {
		this.filePath = filePath;
	}

	@Override
	public Set<Metadata> loadDatamart() throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			return new HashSet<>(MetadataSerializer.deserialize(filePath));
		} else {
			return new HashSet<>();
		}
	}

	@Override
	public void saveDatamart() throws IOException {
		MetadataSerializer.serialize(this.metadataSetDatamart, this.filePath);
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
