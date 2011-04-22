package cytoscape.genomespace;


import cytoscape.Cytoscape;
import cytoscape.genomespace.filechoosersupport.GenomeSpaceTree;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.logger.CyLogger;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import java.io.File;
import java.awt.FileDialog;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.genomespace.client.GsSession;
import org.genomespace.client.DataManagerClient;
import org.genomespace.client.User;


/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class ListFilesInGenomeSpace extends CytoscapeAction {
	private static final long serialVersionUID = 1234487711999989L;
	private static final CyLogger logger = CyLogger.getLogger(ListFilesInGenomeSpace.class);

	public ListFilesInGenomeSpace() {
		// Give your action a name here
		super("List Available Files");

		// Set the menu you'd like here.  Plugins don't need
		// to live in the Plugins menu, so choose whatever
		// is appropriate!
		setPreferredMenu("Plugins.GenomeSpace");
	}

	public void actionPerformed(ActionEvent e) {
		try {
			GsSession client = GSUtils.getSession();
			DataManagerClient dmc = client.getDataManagerClient();

			// list the files present for this user
			displayTree(dmc);
		} catch (Exception ex) {
			logger.error("GenomeSpace failed",ex);
		}
	}

	private void displayTree(final DataManagerClient dataManagerClient) {
		final GenomeSpaceTree tree = new GenomeSpaceTree(dataManagerClient);
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(350, 600));
		JPanel jp = new JPanel();
		jp.add(scrollPane);
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), jp);
	}
}


class MyCellRenderer extends JLabel implements ListCellRenderer {
	final static ImageIcon regularFileIcon = new ImageIcon("images/regularFile.png");
	final static ImageIcon directoryIcon = new ImageIcon("images/directory.png");

	// This is the only method defined by ListCellRenderer.
	// We just reconfigure the JLabel each time we're called.

	public Component getListCellRendererComponent(JList list,              // the list
						      Object value,            // value to display
						      int index,               // cell index
						      boolean isSelected,      // is the cell selected
						      boolean cellHasFocus)    // does the cell have focus
	{
		final GSFileMetadata fileMetadata = (GSFileMetadata)value;
		setText(fileMetadata.getName() + " (" + fileMetadata.getOwner() + ")" + fileMetadata.getSize());
		setIcon(fileMetadata.isDirectory() ? directoryIcon : regularFileIcon);

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setOpaque(true);

		return this;
	}

}