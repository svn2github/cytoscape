package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CytoscapeAction;

public class NewSessionAction extends CytoscapeAction {

	public NewSessionAction () {
	    super("Session");
	    setPreferredMenu( "File.New" );
	    //setAcceleratorCombo(java.awt.event. KeyEvent.VK_N, ActionEvent.CTRL_MASK );
	  }
	
	// Create dialog
	public void actionPerformed(ActionEvent e) {

		int currentNetworkCount = Cytoscape.getNetworkSet().size();

		if (currentNetworkCount != 0) {
			// Show warning
			String warning = "Current session will be lost.\nDo you want to continue?";

			int result = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
					warning, "Caution!", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null);

			if (result == JOptionPane.YES_OPTION) {
				cleanWorkspace();
			} else {
				return;
			}
		} 
		
		
	}
	
	private void cleanWorkspace() {

		Set netSet = Cytoscape.getNetworkSet();
		Iterator it = netSet.iterator();

		while (it.hasNext()) {
			CyNetwork net = (CyNetwork) it.next();
			Cytoscape.destroyNetwork(net);
		}
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String[] nodeAttrNames = nodeAttributes.getAttributeNames();
		for(int i = 0; i<nodeAttrNames.length; i++) {
			nodeAttributes.deleteAttribute(nodeAttrNames[i]);
		}
		
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String[] edgeAttrNames = edgeAttributes.getAttributeNames();
		for(int i = 0; i<edgeAttrNames.length; i++) {
			edgeAttributes.deleteAttribute(edgeAttrNames[i]);
		}
		

	}

}
