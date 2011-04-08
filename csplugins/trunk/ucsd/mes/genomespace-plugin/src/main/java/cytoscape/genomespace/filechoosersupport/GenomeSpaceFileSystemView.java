package cytoscape.genomespace.filechoosersupport;


import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public final class GenomeSpaceFileSystemView extends FileSystemView {
	private final DataManagerClient dataManagerClient;

	public GenomeSpaceFileSystemView(final DataManagerClient dataManagerClient) {
		this.dataManagerClient = dataManagerClient;
	}

	@Override
	public File createNewFolder(final File containingDir) throws IOException {
		final GSFileMetadata fileMetadata =
			dataManagerClient.createDirectory(containingDir.getPath(), "New Folder");
		return new GenomeSpaceFile(fileMetadata, dataManagerClient);
	}

	@Override
	public File getDefaultDirectory() {
		return new GenomeSpaceFile(dataManagerClient.listDefaultDirectory().getDirectory(),
					   dataManagerClient);
	}
}
