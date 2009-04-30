package org.cytoscape.view.vizmap.gui.theme;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;

public class IconManager {
	
	private Map<String, Icon> iconMap;
	
	public IconManager() {
		System.out.println("--------------> IconManager created!!!!!!!!!!!!!!!!!!!!!!!!!!");
		// for setter injection
		iconMap = new HashMap<String, Icon>();
		loadIcon();
	}
	
	public Icon getIcon(String name) {
		final Icon icon = iconMap.get(name);
		
		if(icon == null) {
			// This should return default icon.
			return null;
		} else
			return icon;
	}

	private void loadIcon() {
		// TODO Auto-generated method stub
		iconMap.put("optionIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_form-properties.png")));
		iconMap.put("delIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_delete-16.png")));
		iconMap.put("addIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_data-new-table-16.png")));
		iconMap.put("rndIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_filters-16.png")));
		iconMap.put("renameIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_redo-16.png")));
		iconMap.put("copyIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_slide-duplicate.png")));
		iconMap.put("legendIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_graphic-styles-16.png")));
		iconMap.put("editIcon", new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_edit-16.png")));
	}

	
	
	
}
