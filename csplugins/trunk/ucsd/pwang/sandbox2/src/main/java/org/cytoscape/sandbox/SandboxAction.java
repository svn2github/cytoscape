package org.cytoscape.sandbox;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Iterator;

import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.visual.VisualStyle;

/**
 * A simple action.  Change the names as appropriate and
 * then fill in your expected behavior in the actionPerformed()
 * method.
 */
public class SandboxAction extends CytoscapeAction {
		
		public SandboxAction() {
			// Give your action a name here
			super("Sandbox");

			// Set the menu you'd like here.  Plugins don't need
			// to live in the Plugins menu, so choose whatever
			// is appropriate!
	        setPreferredMenu("Plugins");
		}
		
		public void actionPerformed(ActionEvent e) {

			System.out.println( "\nMenuItem sandbox is clicked!\n ");
			
			CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();			
			Iterator<CyNode> it = Cytoscape.getCurrentNetwork().nodesIterator();
			
			
			while (it.hasNext()){
				CyNode node = it.next();

				String name = nodeAttrs.getAttribute(node.getIdentifier(), "canonicalName").toString();

				if (name.equalsIgnoreCase("ASDF")){
					
				}
				
				System.out.println( "\tname = "+ name);
			}

			
		}
}
