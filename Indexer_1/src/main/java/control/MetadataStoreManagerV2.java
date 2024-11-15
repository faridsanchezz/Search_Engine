package control;

import control.interfaces.MetadataStoreManager;
import control.interfaces.SerializerController;
import model.Metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

public class MetadataStoreManagerV2 implements MetadataStoreManager<Metadata> {
	private final File datamartFilePath;
	private final SerializerController<Metadata> metadataSerializer;

	public MetadataStoreManagerV2(String datamartDirectory, SerializerController<Metadata> metadataSerializer) throws IOException {
		this.metadataSerializer = metadataSerializer;
		DirectoryManager.createDirectory(Paths.get(datamartDirectory, "metadata").toFile());
		this.datamartFilePath = Paths.get(datamartDirectory, "metadata", "metadata").toFile();
	}

	@Override
	public void update(Metadata new_metadata) throws IOException {
		Set<Metadata> metadataSetDatamart = metadataSerializer.deserialize(this.datamartFilePath);
		if (metadataSetDatamart.stream().noneMatch(m -> m.hashCode() == new_metadata.hashCode())) {
			metadataSetDatamart.add(new_metadata);
		}

		metadataSerializer.serialize(this.datamartFilePath, metadataSetDatamart);
	}
}

