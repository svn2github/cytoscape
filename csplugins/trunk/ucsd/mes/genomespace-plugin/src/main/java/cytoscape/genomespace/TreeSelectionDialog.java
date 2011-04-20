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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import cytoscape.genomespace.filechoosersupport.GenomeSpaceTree;
import cytoscape.genomespace.filechoosersupport.GSFileMetadataTreeNode;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


final class TreeSelectionDialog extends JDialog implements TreeSelectionListener {
	private final DataManagerClient dataManagerClient;
	private final List<String> acceptableExtensions;
	private final GenomeSpaceTree tree;
	private final JButton newFolderButton;
	private final JButton selectButton;
	private final JButton cancelButton;
	private GSFileMetadata selectedFileMetadata;
	private GSFileMetadataTreeNode currentNode;

	TreeSelectionDialog(final Frame owner, final DataManagerClient dataManagerClient,
			    final List<String> acceptableExtensions)
	{
		super(owner, /* modal = */ true);

		this.dataManagerClient    = dataManagerClient;
		this.acceptableExtensions = acceptableExtensions;
		this.selectedFileMetadata = null;
		this.currentNode          = null;

		tree = new GenomeSpaceTree(dataManagerClient, acceptableExtensions);
		tree.setEditable(true);
		tree.addTreeSelectionListener(this);
		final JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(450, 300));
		final JPanel treePane = new JPanel();
		treePane.add(treeScrollPane);
		getContentPane().add(treePane);

		final JPanel buttonPane = new JPanel();

		newFolderButton = new JButton("New Folder");
		newFolderButton.setToolTipText("You need to select a folder in which to create the new subfolder!");
		newFolderButton.setEnabled(false);
		newFolderButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					TreeSelectionDialog.this.createNewFolder();
				}
			});
		buttonPane.add(newFolderButton);

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
		currentNode = (GSFileMetadataTreeNode)tree.getLastSelectedPathComponent();

		if (currentNode != null)
			newFolderButton.setEnabled(currentNode.getFileMetadata().isDirectory());

		if (currentNode == null || currentNode.getFileMetadata().isDirectory()) {
			selectButton.setEnabled(false);
			selectedFileMetadata = null;
			return;
		}

		if (acceptableExtensions.isEmpty()) {
			selectedFileMetadata = currentNode.getFileMetadata();
			selectButton.setEnabled(selectedFileMetadata != null);
		} else {
			selectButton.setEnabled(false);
			selectedFileMetadata = null;
			final GSFileMetadata nodeFileMetadata = currentNode.getFileMetadata();
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

	void createNewFolder() {
		final String newFolderName = JOptionPane.showInputDialog("New folder name:");
		if (newFolderName == null || newFolderName.isEmpty())
			return;

		final GSFileMetadata parentMetadata = currentNode.getFileMetadata();
		GSFileMetadata newDirMetadata = null;
		try {
			newDirMetadata =
				dataManagerClient.createDirectory(parentMetadata, newFolderName);
		} catch (Exception e) {
		}
		if (newDirMetadata == null) {
			JOptionPane.showMessageDialog(this, "failed to create the new folder!",
						      "GenomeSpace Error",
						      JOptionPane.ERROR_MESSAGE);
			return;
		}

		final TreePath path = tree.getSelectionPath();
		if (tree.isCollapsed(path))
			tree.expandPath(path);
		else {
			final GSFileMetadataTreeNode newDirNode =
				new GSFileMetadataTreeNode(newDirMetadata, dataManagerClient,
							   acceptableExtensions);
			final DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
			// TODO insert new dir node in model, sorted appropriately (alphabetically, dirs first)
		}
	}
}