package control;

import control.interfaces.SerializerController;
import control.interfaces.StoreManager;
import model.Metadata;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

public class MetadataStoreManagerV1 implements StoreManager<Metadata> {
	private final String datamartFilePath;
	private final SerializerController<Metadata> metadataSerializer;
	private Set<Metadata> metadataSetDatamart;

	public MetadataStoreManagerV1(String datamartDirectory, SerializerController<Metadata> metadataSerializer) throws IOException {
		this.datamartFilePath = Paths.get(datamartDirectory, "metadata.txt").toString();
		this.metadataSerializer = metadataSerializer;
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
