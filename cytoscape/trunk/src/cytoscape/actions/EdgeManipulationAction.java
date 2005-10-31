//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.NetworkView;
import cytoscape.dialogs.GinyEdgeControlDialog;

//-------------------------------------------------------------------------
public class EdgeManipulationAction extends AbstractAction {

	NetworkView networkView;

	public EdgeManipulationAction(NetworkView networkView) {
		super("Edge select or hide by attributes...");
		this.networkView = networkView;
	}

	public void actionPerformed(ActionEvent e) {
		
		CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
		
		String [] edgeAttributeNames = edgeAtts.getAttributeNames();
		HashMap attributesTree = new HashMap();
		
		for (int i = 0; i < edgeAttributeNames.length; i++) {
			String att = edgeAttributeNames[i];
			if (edgeAtts.getType(att) == CyAttributes.TYPE_STRING) {
				// This iterator contains the edge uids that contain a value
				// for this attribute
				Iterator it = edgeAtts.getMultiHashMap().getObjectKeys(att);
				HashSet nameSet = new HashSet();
				while(it.hasNext()){
					String edgeId = (String)it.next();
					nameSet.add(edgeAtts.getStringAttribute(edgeId,att));
				}
				attributesTree.put(att, (String [])nameSet.toArray(new String[nameSet.size()]));
			} // if a string attribute
		} // for i
		
		if (attributesTree.size() > 0) {
			JDialog dialog = new GinyEdgeControlDialog(networkView,
					attributesTree, "Edge Selection Control");
			dialog.pack();
			dialog.setLocationRelativeTo(networkView.getMainFrame());
			dialog.setVisible(true);
		} else {
			JOptionPane
					.showMessageDialog(null,
							"There are no String edge attributes suitable for controlling edge display");
		}
	} // actionPerformed
}
