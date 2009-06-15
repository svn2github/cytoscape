package cytoscape.groups.results;

// System imports
import java.util.List;
import java.util.HashMap;
import java.awt.Dimension;

// Swing imports
import javax.swing.JOptionPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;

// Cytoscape group system imports
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;

/**
 * The GroupPanel is the implementation for the Cytopanel that presents
 * the group list mechanism to the user.
 */
public class GroupPanel extends JPanel implements ListSelectionListener {
	JList navList;
	DefaultListModel listModel;

	public GroupPanel() {
		super();

		this.setPreferredSize(new Dimension(240, 600));
	
		// Create the list
		listModel = new DefaultListModel();

		navList = new JList(listModel);
		navList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		navList.setLayoutOrientation(JList.VERTICAL);
		navList.setVisibleRowCount(20);
		navList.addListSelectionListener(this);
		navList.setBackground(Cytoscape.getDesktop().getBackground());
		navList.setPreferredSize(new Dimension(240, 580));

		JScrollPane listView = new JScrollPane(navList);
		listView.setBorder(BorderFactory.createEtchedBorder());
		listView.setBackground(Cytoscape.getDesktop().getBackground());

		this.add(listView);
	}

	/**
	 * This is called when the user changes the selection
 	 *
	 * @param e the event that caused us to be called
 	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			CyNetwork network = Cytoscape.getCurrentNetwork();
			for (int i = 0; i < listModel.getSize(); i++) {
				if (navList.getSelectionModel().isSelectedIndex(i)) {
					CyGroup group = (CyGroup)listModel.getElementAt(i);
					network.setSelectedNodeState(group.getNodes(), true);
				} else {
					CyGroup group = (CyGroup)listModel.getElementAt(i);
					network.setSelectedNodeState(group.getNodes(), false);
				}
			}
			Cytoscape.getCurrentNetworkView().updateView();
		}
	}

	public void groupCreated(CyGroup group) {
		listModel.addElement(group);
	}

	public void groupRemoved(CyGroup group) {
		listModel.removeElement(group);
	}

	public void groupChanged(CyGroup group) {
		System.out.println("groupChanged event for group " + group.toString());
	}
}

