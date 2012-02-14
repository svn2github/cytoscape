package org.cytoscape.io.webservice;

import java.awt.Container;

public class WebServiceClientContextImpl implements WebServiceClientContext {
	private Object query;
	private Container gui;

	@Override
	public void setQuery(Object query) {
		this.query = query;
	}
	
	@Override
	public Object getQuery() {
		return query;
	}
	
	@Override
	public Container getQueryBuilderGUI() {
		return gui;
	}
	
	@Override
	public void setQueryBuilderGUI(Container gui) {
		this.gui = gui;
	}
}
