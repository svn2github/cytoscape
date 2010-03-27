package org.cytoscape.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;

public class NetworkLibraryPlugin extends CytoscapePlugin {

	private static final GraphDatabaseService graphDb = new EmbeddedGraphDatabase(
			"/Users/kono/Documents/wsC2/cytoscape_customGraphics/build/cytoscape-v2.7.0/db");
	
	private CyNetworkWriter wtiter;

	public NetworkLibraryPlugin() {
		wtiter = new CyNetworkWriter();
		
		final JMenu menu = new JMenu("Network Library");
		final JMenuItem write = new JMenuItem("Wite to DB");
		write.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				write();
			}
			
		});
		menu.add(write);
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add(menu);
		
		StatusChecker checker = new StatusChecker();
		
	}
	
	private void write() {
		wtiter.write(Cytoscape.getCurrentNetwork());
	}

	public static GraphDatabaseService getDB() {
		return graphDb;
	}
	
	public void onCytoscapeExit() {
		graphDb.shutdown();
	}

}
