package cytoscape.org.BipartiteVisualiserPlugin;


import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import giny.view.EdgeView;
import ding.view.EdgeContextMenuListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;


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


		public MyPluginAction(BipartiteVisualiserPlugin myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("BipartiteVisualiser");
			setPreferredMenu("Plugins");
		}


		public void actionPerformed(ActionEvent e) {
			if (Cytoscape.getCurrentNetworkView().getTitle() == null || Cytoscape.getCurrentNetworkView().getTitle().equalsIgnoreCase("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "No network view!");
				return;
			}

			MyEdgeContextMenuListener l = new MyEdgeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(l);	
		}

		
		class MyEdgeContextMenuListener implements EdgeContextMenuListener {
			public void addEdgeContextMenuItems(EdgeView nodeView, JPopupMenu menu) 
			{ 
				JMenuItem myMenuItem = new JMenuItem("MyEdgeMenuItem");
				
				myMenuItem.addActionListener(new MyEdgeAction(nodeView));					

				if (menu == null) {
					menu = new JPopupMenu();
				}
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
