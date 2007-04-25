package browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.data.ontology.DBCrossReferences;
import cytoscape.util.OpenBrowser;

public class HyperLinkOut extends JMenu {

	private static final String TITLE = "Search";
	private String value;
	private DBCrossReferences xref;
	
	private Map<String, List> structure;

	public HyperLinkOut(String value) {
		this(value, null);
	}
	
	public HyperLinkOut(String value, Map<String, List> menuStructure) {
		this.value = value;
		this.structure = menuStructure;
		
		if(structure == null) {
			structure = getDefaultMenu();
		}
		
		if(Cytoscape.getOntologyServer() != null) {
			xref = Cytoscape.getOntologyServer().getCrossReferences();
			setText("Search " + value + " on the web...");
			buildLinks();
		} 
		
	}
	
	private Map<String, List> getDefaultMenu() {
		Map<String, List> def = new HashMap<String, List>();
		
		List<String> se = new ArrayList<String>();
		se.add("Google");
		se.add("Ask");
		def.put("Search Engines", se );
		List<String> bio = new ArrayList<String>();
		bio.add("SGD");
		bio.add("GO");
		bio.add("MGD");
		def.put("Biological Databases", bio);
		return def;
	}

	public void search() {

	}

	private void buildLinks() {
		Set<String> dbNames = xref.getDBNames();
		JMenuItem dbLink;
		String fullName;

		JMenu cat;
		for(String category: structure.keySet()) {
			cat = new JMenu(category);
			
			List<String> children = structure.get(category);
			for (String name : children) {
				fullName = xref.getDBReference(name).getFullName();
				dbLink = new JMenuItem(name);
				dbLink.setToolTipText(fullName);
				dbLink.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openBrowser(e.getActionCommand());
					}
				});
				cat.add(dbLink);
			}
			this.add(cat);
		}
	}

	private void openBrowser(String dbName) {
		try {
			OpenBrowser.openURL(xref.getDBReference(dbName).getQueryURL(value)
					.toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
