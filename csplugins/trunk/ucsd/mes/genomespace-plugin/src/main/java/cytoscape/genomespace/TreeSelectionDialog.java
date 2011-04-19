package cytoscape.genomespace;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import cytoscape.genomespace.filechoosersupport.GenomeSpaceTree;
import cytoscape.genomespace.filechoosersupport.GSFileMetadataTreeNode;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


final class TreeSelectionDialog extends JDialog implements TreeSelectionListener {
	private final List<String> acceptableExtensions;
	private final GenomeSpaceTree tree;
	private final JButton selectButton;
	private final JButton cancelButton;
	private GSFileMetadata selectedFileMetadata;

	TreeSelectionDialog(final Frame owner, final DataManagerClient dataManagerClient,
			    final List<String> acceptableExtensions)
	{
		super(owner, /* modal = */ true);
		this.acceptableExtensions = acceptableExtensions;
		this.selectedFileMetadata = null;

		tree = new GenomeSpaceTree(dataManagerClient, acceptableExtensions);
		tree.addTreeSelectionListener(this);
		final JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(450, 300));
		final JPanel treePane = new JPanel();
		treePane.add(treeScrollPane);
		getContentPane().add(treePane);

		final JPanel buttonPane = new JPanel();
		selectButton = new JButton("Select");
		selectButton.setEnabled(false);
		selectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					TreeSelectionDialog.this.dispose();
				}
			});
		buttonPane.add(selectButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					TreeSelectionDialog.this.selectedFileMetadata = null;
					TreeSelectionDialog.this.dispose();
				}
			});
		buttonPane.add(cancelButton);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	TreeSelectionDialog(final Frame owner, final DataManagerClient dataManagerClient) {
		this(owner, dataManagerClient, new ArrayList<String>());
	}

	public void valueChanged(final TreeSelectionEvent e) {
		final GSFileMetadataTreeNode node =
			(GSFileMetadataTreeNode)tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		if (acceptableExtensions.isEmpty()) {
			selectedFileMetadata = node.getFileMetadata();
			selectButton.setEnabled(selectedFileMetadata != null);
		} else {
			selectButton.setEnabled(false);
			selectedFileMetadata = null;
			final GSFileMetadata nodeFileMetadata = node.getFileMetadata();
			final String extension = getFileExtension(nodeFileMetadata.getName());
			for (final String acceptableExtension : acceptableExtensions) {
				if (extension.equalsIgnoreCase(acceptableExtension)) {
					selectButton.setEnabled(true);
					selectedFileMetadata = nodeFileMetadata;
					break;
				}
			}
		}
	}

	private static String getFileExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1) ? "" : fileName.substring(lastDotPos + 1);
	}

	public GSFileMetadata getSelectedFileMetadata() {
		return selectedFileMetadata;
	}
}