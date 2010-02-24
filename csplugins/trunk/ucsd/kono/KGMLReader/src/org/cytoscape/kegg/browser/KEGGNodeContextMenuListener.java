package org.cytoscape.kegg.browser;

import java.net.MalformedURLException;
import java.net.URL;

import giny.view.NodeView;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.data.reader.kgml.KEGGEntryType;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import ding.view.NodeContextMenuListener;

public class KEGGNodeContextMenuListener implements NodeContextMenuListener {
	
	private static final String COMPOUND_URL = "http://www.kegg.jp/Fig/compound/";
	private static final String MAP_URL = "http://www.genome.jp/tmp/pathway_thumbnail/";
	
	private CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

	@Override
	public void addNodeContextMenuItems(NodeView nv, JPopupMenu menu) {
		if (menu == null)
			return;

		final JMenu keggMenu = new JMenu("KEGG Options");
		
		final String attrValue = nodeAttr.getStringAttribute(nv.getNode().getIdentifier(), "KEGG.entry");
		if (attrValue == null) return;
		
		final KEGGEntryType entryType = KEGGEntryType.getType(attrValue);
		
		if (entryType == null) return;
		
		JMenuItem item = new JMenuItem(); 
		
		if(entryType.equals(KEGGEntryType.COMPOUND)) {
			try {
				final String compoundID = nodeAttr.getStringAttribute(nv.getNode().getIdentifier(), "KEGG.label");
				URL image = new URL(COMPOUND_URL + compoundID + ".gif");
				item.setIcon(new ImageIcon(image));
				item.setText("Compound: " + compoundID);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if (entryType.equals(KEGGEntryType.MAP)) {
			final String mapID = nodeAttr.getStringAttribute(nv.getNode().getIdentifier(), "KEGG.name").split(":")[1];
			URL image = null;
			try {
				image = new URL(MAP_URL + mapID + ".png");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			item.setIcon(new ImageIcon(image));
			item.setText("Pathway: " + mapID);
		}
	
		
		menu.addSeparator();
		menu.add(keggMenu);
		menu.add(item);
	}
	
	

}
