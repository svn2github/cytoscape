package cytoscape.genomespace;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import cytoscape.genomespace.filechoosersupport.GenomeSpaceTree;
import cytoscape.genomespace.filechoosersupport.GSFileMetadataTreeNode;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;


final class AttrFileSelectionDialog extends JDialog implements TreeSelectionListener {
	private final GenomeSpaceTree tree;
	private final JButton selectButton;
	private final JButton cancelButton;
	private GSFileMetadata selectedFileMetadata;
	private boolean useNodeAttrs;

	AttrFileSelectionDialog(final Frame owner, final DataManagerClient dataManagerClient) {
		super(owner, /* modal = */ true);
		this.selectedFileMetadata = null;
		this.useNodeAttrs = true;

		final List<String> acceptableExtensions = new ArrayList<String>();
		acceptableExtensions.add("attrs");
		tree = new GenomeSpaceTree(dataManagerClient, acceptableExtensions);
		tree.addTreeSelectionListener(this);
		final JScrollPane treeScrollPane = new JScrollPane(tree);
		treeScrollPane.setPreferredSize(new Dimension(450, 300));
		final JPanel treePane = new JPanel();
		treePane.add(treeScrollPane);
		getContentPane().add(treePane);

		final JPanel radioButtonPane = new JPanel();

		final JRadioButton nodeAttrButton = new JRadioButton("Load node attributes");
		nodeAttrButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent actionEvent) {
					AttrFileSelectionDialog.this.useNodeAttrs = true;
				}
			});
		nodeAttrButton.setSelected(true);
		radioButtonPane.add(nodeAttrButton);

		final JRadioButton edgeAttrButton = new JRadioButton("Load edge attributes");
		nodeAttrButton.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent actionEvent) {
					AttrFileSelectionDialog.this.useNodeAttrs = false;
				}
			});
		edgeAttrButton.setSelected(false);
		radioButtonPane.add(edgeAttrButton);

		final ButtonGroup group = new ButtonGroup();
		group.add(nodeAttrButton);
		group.add(edgeAttrButton);

		getContentPane().add(radioButtonPane, BorderLayout.SOUTH);

		final JPanel buttonPane = new JPanel();

		selectButton = new JButton("Select");
		selectButton.setEnabled(false);
		selectButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					AttrFileSelectionDialog.this.dispose();
				}
			});
		buttonPane.add(selectButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					AttrFileSelectionDialog.this.selectedFileMetadata = null;
					AttrFileSelectionDialog.this.dispose();
				}
			});
		buttonPane.add(cancelButton);

		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void valueChanged(final TreeSelectionEvent e) {
		final GSFileMetadataTreeNode node =
			(GSFileMetadataTreeNode)tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		selectButton.setEnabled(false);
		selectedFileMetadata = null;
		final GSFileMetadata nodeFileMetadata = node.getFileMetadata();
		final String extension = getFileExtension(nodeFileMetadata.getName());
		if (extension.equalsIgnoreCase("attrs")) {
			selectButton.setEnabled(true);
			selectedFileMetadata = nodeFileMetadata;
		}
	}

	private static String getFileExtension(final String fileName) {
		final int lastDotPos = fileName.lastIndexOf('.');
		return (lastDotPos == -1) ? "" : fileName.substring(lastDotPos + 1);
	}

	public GSFileMetadata getSelectedFileMetadata() {
		return selectedFileMetadata;
	}

	public boolean useNodeAttrs() {
		return useNodeAttrs;
	}
}