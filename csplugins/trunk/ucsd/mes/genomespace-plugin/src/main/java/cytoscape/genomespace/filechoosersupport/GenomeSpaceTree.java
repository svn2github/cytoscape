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
import javax.swing.tree.TreeSelectionModel;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


public class GenomeSpaceTree extends JTree {
	public GenomeSpaceTree(final DataManagerClient dataManagerClient,
			       final List<String> acceptableExtensions)
	{
		super(createTopAndFirstTier(dataManagerClient, acceptableExtensions));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setCellRenderer(new MyTreeCellRenderer());
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