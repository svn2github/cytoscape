package cytoscape.genomespace.filechoosersupport;


import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public final class GSFileMetadataTreeNode extends DefaultMutableTreeNode {
	private static int UNINITIALISED = -1;
	private final GSFileMetadata fileMetadata;
	private final DataManagerClient dataManagerClient;
	private int childCount;

	GSFileMetadataTreeNode(final GSFileMetadata fileMetadata,
			       final DataManagerClient dataManagerClient)
	{
		super(fileMetadata, fileMetadata.isDirectory());
		this.fileMetadata = fileMetadata;
		this.dataManagerClient = dataManagerClient;
		this.childCount = fileMetadata.isDirectory() ? UNINITIALISED : 0;
	}

	@Override
	public boolean isLeaf() {
		return !fileMetadata.isDirectory();
	}

	@Override
	public String toString() {
		return fileMetadata.getName();
	}

	@Override
	public int getChildCount() {
		if (childCount != UNINITIALISED)
			return childCount;

		initChildCount();
		return childCount;
	}

	private void initChildCount() {
		final List<GSFileMetadata> filesMetadata =
			dataManagerClient.list(fileMetadata).getContents();
		childCount = 0; // Warning: The way "childCount" is being updated piecemeal
		// in this method is essential!  Do not change it!!
		Collections.sort(filesMetadata, new GSFileMetadataComparator());
		for (final GSFileMetadata metadata : filesMetadata) {
			add(new GSFileMetadataTreeNode(metadata, dataManagerClient));
			++childCount;
		}
	}

	public GSFileMetadata getFileMetadata() {
		return fileMetadata;
	}
}
