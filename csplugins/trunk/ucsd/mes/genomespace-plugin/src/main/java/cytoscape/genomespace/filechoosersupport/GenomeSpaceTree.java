package cytoscape.genomespace.filechoosersupport;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public class GenomeSpaceTree extends JTree {
	public GenomeSpaceTree(final DataManagerClient dataManagerClient,
			       final List<String> acceptableExtensions)
	{
		super(createTopAndFirstTier(dataManagerClient, acceptableExtensions));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	public GenomeSpaceTree(final DataManagerClient dataManagerClient) {
		this(dataManagerClient, new ArrayList<String>());
	}

	private static DefaultMutableTreeNode createTopAndFirstTier(
						final DataManagerClient dataManagerClient,
						final List<String> acceptableExtensions)
	{
		final DefaultMutableTreeNode top =
			new DefaultMutableTreeNode("GenomeSpace Files");
		
		final Vector<GSFileMetadata> filesMetadata =
			new Vector(dataManagerClient.listDefaultDirectory().getContents());
		Collections.sort(filesMetadata, new GSFileMetadataComparator());

		final Iterator<GSFileMetadata> iter = filesMetadata.iterator();
		while (iter.hasNext()) {
			final GSFileMetadata metadata = iter.next();
			top.add(new GSFileMetadataTreeNode(metadata, dataManagerClient,
							   acceptableExtensions));
		}

		return top;
	}
}