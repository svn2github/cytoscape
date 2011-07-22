package cytoscape.genomespace.filechoosersupport;


import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeModel;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.datamanager.core.GSDirectoryListing;


public class GenomeSpaceTree extends JTree {
	public GenomeSpaceTree(final DataManagerClient dataManagerClient,
			       final List<String> acceptableExtensions)
	{
		super(createTopAndFirstTier(dataManagerClient, acceptableExtensions));
		setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new MyTreeCellRenderer());
	}

	public GenomeSpaceTree(final DataManagerClient dataManagerClient) {
		this(dataManagerClient, new ArrayList<String>());
	}

	private static TreeModel createTopAndFirstTier(
						final DataManagerClient dataManagerClient,
						final List<String> acceptableExtensions)
	{
		final GSDirectoryListing dirListing = dataManagerClient.listDefaultDirectory();
		final Vector<GSFileMetadata> filesMetadata = new Vector(dirListing.getContents());
		Collections.sort(filesMetadata, new GSFileMetadataComparator());

		final RootTreeNode top =
			new RootTreeNode(dirListing.getDirectory(), dataManagerClient);

		final Iterator<GSFileMetadata> iter = filesMetadata.iterator();
		while (iter.hasNext()) {
			final GSFileMetadata metadata = iter.next();
			top.add(new GSFileMetadataTreeNode(metadata, dataManagerClient,
							   acceptableExtensions));
		}

		return new DefaultTreeModel(top);
	}

	static final class MyTreeCellRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(final JTree tree, final Object value,
							      final boolean sel,
							      final boolean expanded,
							      final boolean leaf, final int row,
							      final boolean hasFocus)
		{
			if (value instanceof GSFileMetadataTreeNode) {
				final GSFileMetadataTreeNode fileMetaTreeNode =
					(GSFileMetadataTreeNode)value;
				if (fileMetaTreeNode.isEnabled()) {
					setTextSelectionColor(Color.BLACK);
					setTextNonSelectionColor(Color.BLACK);
				} else {
					setTextSelectionColor(Color.GRAY);
					setTextNonSelectionColor(Color.GRAY);
				}
			}
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			return this;
		}
	}
}


class RootTreeNode extends GSFileMetadataTreeNode {
	public RootTreeNode(final GSFileMetadata fileMetadata,
			    final DataManagerClient dataManagerClient)
	{
		super(fileMetadata, dataManagerClient, new ArrayList<String>());
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public String toString() {
		return "GenomeSpace Files";
	}

	@Override
	public boolean childrenHaveBeenInitialised() {
		return true;
	}
}

