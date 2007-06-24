package edu.ucsd.bioeng.kono.keggbrowser;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import edu.ucsd.bioeng.kono.keggbrowser.ui.KEGGBrowserMainGUI;
import edu.ucsd.bioeng.kono.keggbrowser.ui.SpeciesGUI;

public class KEGGBrowserPlugin extends CytoscapePlugin {
	
	final Map<String, String> speciesList = new HashMap<String, String>();
	
	private static final String KEGG_ORGANISM = "http://www.genome.jp/kegg-bin/show_organism?org="; 

	/**
	 * Constructor
	 * 
	 * Will set up the menu entries in the
	 * 
	 * <pre>
	 * Plugins
	 * </pre>
	 * 
	 * menu.
	 * 
	 */
	public KEGGBrowserPlugin() {

		
		
		
		final JMenu menu = new JMenu("KEGG Browser...");
		
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins")
				.add(menu);

		menu.add(new JMenuItem(new AbstractAction("Open KEGG Browser") {
			public void actionPerformed(ActionEvent e) {
				
				// Select Species
				String test = SpeciesGUI.showDialog();
				
				System.out.println();
				String species = null;
				if(test.startsWith(KEGG_ORGANISM)) {
					
					String[] parts = test.split("=");
					System.out.println("PS ========== " + parts[1]);
					species = parts[1];
				}
				
				KEGGBrowserMainGUI main = new KEGGBrowserMainGUI(species);				
				main.setVisible(true);
				main.setLocationRelativeTo(Cytoscape.getDesktop());
			}
		}));

	}

}