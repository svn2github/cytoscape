package org.cytoscape.search.internal;

import javax.swing.JPanel;

public abstract class SearchPanel extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	abstract public void performSearch(boolean reindex);
		
}
