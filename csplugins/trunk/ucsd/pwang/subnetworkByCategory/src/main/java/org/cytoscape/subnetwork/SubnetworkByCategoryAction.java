package org.cytoscape.subnetwork;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SubnetworkByCategoryAction extends CytoscapeAction {

		private static final long serialVersionUID = 123456789023451239L;

		public SubnetworkByCategoryAction() {
			// Give your action a name here
			super("Subnetwork by Category");

			// Set the menu you'd like here.  Plugins don't need
			// to live in the Plugins menu, so choose whatever
			// is appropriate!
	        setPreferredMenu("Plugins");
		}
		
		public void actionPerformed(ActionEvent e) {

			String attributeName = "Category";// this is the default attribute name			
			// get attributeName (Type String or int) from user
			
			
        	String[] attrNames = Cytoscape.getNodeAttributes().getAttributeNames();        	

			ChooseCategoryAttributeDialog dlg = new ChooseCategoryAttributeDialog(Cytoscape.getDesktop(), true, attrNames);

			dlg.setVisible(true);
			
			if (dlg.getSelectedAttribute() == null){
				return;
			}

			CyNetwork net = Cytoscape.getCurrentNetwork();
			
			SubnetworkByCategory w = new SubnetworkByCategory();
			
			w.execute(net, attributeName);
			
		}
}
