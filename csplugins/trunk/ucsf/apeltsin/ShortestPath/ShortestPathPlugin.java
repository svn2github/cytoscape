


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

/**
 * Plugin for Cytoscape to find the shortest path between 2 nodes in a network.
 * It is possible to find the shortest path in directed and undirected networks
 * 
 * @author mrsva
 *
 */
public class ShortestPathPlugin extends CytoscapePlugin {
	
	/**
	 * Constructor
	 * 
	 * Will set up the menu entries in the <pre>Plugins</pre> menu. 
	 *
	 */
	public ShortestPathPlugin() {
		JMenu menu = new JMenu("Shortest Path...");
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(menu);
		final SelectSetup selecter = new SelectSetup();
		
		menu.add(new JMenuItem(new AbstractAction("Update Availible Attribute List"){
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						
						
						 selecter.attributeUpdate();
	
					}
				}); 
			}
		}));
		
		/*menu.add(new JMenuItem(new AbstractAction("Shortest Path (directed)"){
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						AttributeSelect selecter = new AttributeSelect();
						selecter.attributeUpdate();
						ShortestPath sp = new ShortestPath();
						sp.calculate(true); 
					}
				}); 
			}
		})); */
		
		/* menu.add(new JMenuItem(new AbstractAction("Shortest Path (undirected)"){
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						ShortestPath sp = new ShortestPath();
						sp.calculate(false);
					}
				});
			}
		})); */
	}

}
