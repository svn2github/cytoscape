package org.cytoscape.linkout.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkoutTaskContext {

	private static final Logger logger = LoggerFactory.getLogger(DynamicSupport.class);

	private static final String EXTERNAL_LINK_ATTR = "Linkout.ExternalLinks";

	private ListSingleSelection<String> menuTitleSelection;

	@Tunable(description="List of configurable URLs")
	public ListSingleSelection<String> getSubmenuOptions() {
		return menuTitleSelection;
	}

    /**
     * This method is a no-op.  Don't use it.
     */
    public void setSubmenuOptions(ListSingleSelection<String> opts) {
        // no-op
    }

	private Map<String,String> menuTitleURLMap = new HashMap<String,String>();
	private CyTableEntry[] tableEntries;
	private CyTableEntry tableEntry2;
	private CyNetwork network;

	protected synchronized void setURLs(CyNetwork network, CyTableEntry... entries) {
		this.network = network;
		menuTitleURLMap.clear();
		if ( entries == null || network == null) {
			menuTitleSelection = null;
			menuTitleURLMap.clear();
			return;
		}

		tableEntries = entries; 

		for ( CyTableEntry entry : tableEntries )
			generateExternalLinks(network.getRow(entry), menuTitleURLMap);

		List<String> menuTitles = new ArrayList<String>( menuTitleURLMap.keySet() );
		Collections.sort(menuTitles);
		menuTitleSelection = new ListSingleSelection<String>(menuTitles);
	}

	private void generateExternalLinks(CyRow row, Map<String,String> urlMap) {
		//System.out.println("looking for external links for CyRow: " + row.get("name", String.class));
		CyColumn column = row.getTable().getColumn(EXTERNAL_LINK_ATTR); 
		if (column != null) {
		
			Class<?> attrType = column.getType();

			// Single title=url pair
			if (attrType == String.class) { 
				//System.out.println(" it's a String");
				String linkAttr = row.get(EXTERNAL_LINK_ATTR,String.class);
				addExternalLink(linkAttr, urlMap);
			// List of title=url pairs 
			} else if (attrType == List.class) { 
				//System.out.println(" it's a List");
				List<String> attrList = row.getList(EXTERNAL_LINK_ATTR, String.class);
				for (String linkAttr : (List<String>) attrList) {
					addExternalLink(linkAttr, urlMap);
				}
			}
		}
	}

	private void addExternalLink(String linkAttr, Map<String,String> urlMap) {
		if (linkAttr == null) {
			//System.out.println("link attr is null");
			return;
		}

		String[] pair = linkAttr.split("=", 2);

		if (pair.length != 2) {
			//System.out.println("Didn't tokenize on equals" + linkAttr);
			return;
		}

		if (!pair[1].startsWith("http")) {
			//System.out.println("not a url: " + pair[1]);
			return;
		}

		//System.out.println("EXTERNAL LINK putting menu: " + pair[0] + "    and link: " + pair[1]);

		urlMap.put(removeMarker(pair[0]),pair[1]);
	}

	private String removeMarker(String s) {
		if ( s.startsWith(LinkOut.NODEMARKER) )
			return s.substring(LinkOut.NODEMARKER.length());
		else if ( s.startsWith(LinkOut.EDGEMARKER) )
			return s.substring(LinkOut.EDGEMARKER.length());
		else 
			return s;
	}

	public String getUrl() {
		return menuTitleURLMap.get( menuTitleSelection.getSelectedValue() );
	}

	public CyNetwork getNetwork() {
		return network;
	}

	public CyTableEntry[] getTableEntries() {
		return tableEntries;
	}
}
