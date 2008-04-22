package cytoscape.tutorial20;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import giny.view.NodeView;
import java.util.Iterator;
import ding.view.NodeContextMenuListener;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
/**
 * 
 */
public class Tutorial20 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial20() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial20 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial20");
			setPreferredMenu("Plugins");
		}


		public void actionPerformed(ActionEvent e) {
			
			if (Cytoscape.getCurrentNetworkView().getTitle() == null || Cytoscape.getCurrentNetworkView().getTitle().equalsIgnoreCase("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No network view!");	
				return;
			}

			MyNodeContextMenuListener l = new MyNodeContextMenuListener();
			
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(l);			
		}

		
		class MyNodeContextMenuListener implements NodeContextMenuListener {
			public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) 
			{ 
				JMenuItem myMenuItem = new JMenuItem("MyNodeMenuItem");
				
				myMenuItem.addActionListener(new MyNodeAction(nodeView));					

				if (menu == null) {
					menu = new JPopupMenu();
				}
				//menu.add(new JSeparator());
				menu.add(myMenuItem); 
			} 
		}

		class MyNodeAction implements ActionListener {
			NodeView nodeView;
			public MyNodeAction(NodeView pNodeView) {
				nodeView = pNodeView;
			}
			
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MyNodeMenuItem on node "+ nodeView.getNode().getIdentifier() + " is clicked");	
			}
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInToolBar() {
			return false;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public boolean isInMenuBar() {
			return true;
		}
	}

}
