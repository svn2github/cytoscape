package org.cytoscape.BipartiteVisualiserPlugin;


import giny.view.EdgeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import ding.view.EdgeContextMenuListener;
import giny.model.Edge;
import giny.model.Node;


/**
 * 
 */
public class BipartiteVisualiserPlugin extends CytoscapePlugin {
	/**
	 * create a menu item
	 */
	public BipartiteVisualiserPlugin() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	

	public class MyPluginAction extends CytoscapeAction {
		private static final long serialVersionUID = 8529971296045L;
		private CyNetwork network1 = null;
		private CyNetwork network2 = null;


		public MyPluginAction(final BipartiteVisualiserPlugin myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("BipartiteVisualiser");
			setPreferredMenu("Plugins");
		}


		public void actionPerformed(final ActionEvent e) {
			if (Cytoscape.getCurrentNetworkView().getTitle() == null || Cytoscape.getCurrentNetworkView().getTitle().equalsIgnoreCase("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No network view!");
				return;
			}

			MyEdgeContextMenuListener l = new MyEdgeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(l);	
		}

		
		class MyEdgeContextMenuListener implements EdgeContextMenuListener {
			public void addEdgeContextMenuItems(final EdgeView edgeView, final JPopupMenu menu) {
				if (menu == null)
					return;

				final Edge edge = edgeView.getEdge();

				final Node source = edge.getSource();
				network1 = (CyNetwork)source.getNestedNetwork();
				if (network1 == null)
					return;

				final Node target = edge.getTarget();
				network2 = (CyNetwork)source.getNestedNetwork();
				if (network2 == null)
					return;

				JMenuItem myMenuItem = new JMenuItem("MyEdgeMenuItem");
				myMenuItem.addActionListener(new MyEdgeAction(edgeView));					
				menu.add(myMenuItem); 
			} 
		}

		class MyEdgeAction implements ActionListener {
			EdgeView edgeView;
			public MyEdgeAction(EdgeView pEdgeView) {
				edgeView = pEdgeView;
			}
			
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MyEdgeMenuItem on edge "+ edgeView.getEdge().getIdentifier() + " is clicked");
			}
		}
	}

}
