package org.cytoscape.analysis.shortestpath;



import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.cytoscape.analysis.shortestpath.internal.ShortestPathDialog;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CytoscapeDesktop;

/**
 * Plugin for Cytoscape to find the shortest path between 2 nodes in a network.
 * It is possible to find the shortest path in directed and undirected networks
 * 
 * @author mrsva
 *
 */
public class ShortestPathPlugin extends CytoscapePlugin 
	implements PropertyChangeListener {

	JMenu pluginMenu = null;
	
	/**
	 * Constructor
	 * 
	 * Will set up the menu entries in the <pre>Plugins</pre> menu. 
	 *
	 */
	public ShortestPathPlugin() {
		// Set ourselves up to listen for new networks
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			.addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
		pluginMenu = new JMenu("Shortest Path...");
		createMenu(pluginMenu);
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(pluginMenu);
	}

	private void createMenu(JMenu menu) {
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		String[] attributeNames = edgeAttributes.getAttributeNames();

		// Clear the menu
		menu.removeAll();

		// Now add our default
		addMenuItem(menu, "Hop Distance");
		
		JMenuItem item = new JMenuItem("Search All");
		SearchAllMenuActionListener va = new SearchAllMenuActionListener();
		item.addActionListener(va);
		menu.add(item);

		//Finds all attributes that are integers or doubles, and adds them to list
		for(int i = 0; i < attributeNames.length; i++)
		{
			String name = attributeNames[i];
			byte type = edgeAttributes.getType(name);

			if((type == edgeAttributes.TYPE_INTEGER) || (type == edgeAttributes.TYPE_FLOATING)) {
				addMenuItem(menu, name);
			}
		}
	}

	private JMenuItem addMenuItem(JMenu menu, String label) {
		JMenuItem item = new JMenuItem(label);
		{
			MenuActionListener va = new MenuActionListener(label);
			item.addActionListener(va);
		}
		menu.add(item);
		return item;
	}

	private class MenuActionListener extends AbstractAction {
		String selectedAttribute = null;

		public MenuActionListener (String label) {
			this.selectedAttribute = label;
		}

		public void actionPerformed(ActionEvent ev) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ShortestPathDialog d = new ShortestPathDialog(Cytoscape.getDesktop(),selectedAttribute);
					d.pack();
					d.setLocationRelativeTo(Cytoscape.getDesktop());
					d.setVisible(true);
				}
			});
		}
	}
	
	private class SearchAllMenuActionListener extends AbstractAction {

		public SearchAllMenuActionListener () {
		}

		public void actionPerformed(ActionEvent ev) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ShortestPath alg = new ShortestPath();
					alg.searchAll();
				}
			});
		}
	}

  public void propertyChange(PropertyChangeEvent evt) {
    if ( evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED ){
      // Recreate the menu
			createMenu(pluginMenu);
    }
  }
}
