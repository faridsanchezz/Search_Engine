package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class MetadataStoreManagerV2 implements StoreManager<Metadata> {
	private final String datamartFilePath;
	private final SerializerController<Metadata> metadataSerializer;
	private Set<Metadata> metadataSetDatamart;

	public MetadataStoreManagerV2(String datamartDirectory, SerializerController<Metadata> metadataSerializer) throws IOException {
		this.metadataSerializer = metadataSerializer;
		DirectoryManager.createDirectory(Paths.get(datamartDirectory, "metadata").toString());
		this.datamartFilePath = Paths.get(datamartDirectory, "metadata", "metadatos.txt").toString();
	}

	@Override
	public void update(Metadata new_metadata) throws IOException {
		this.metadataSetDatamart = metadataSerializer.deserialize(this.datamartFilePath);
		if (this.metadataSetDatamart.stream().noneMatch(m -> m.hashCode() == new_metadata.hashCode())) {
			this.metadataSetDatamart.add(new_metadata);
		}

		metadataSerializer.serialize(this.datamartFilePath, metadataSetDatamart);
	}
}

