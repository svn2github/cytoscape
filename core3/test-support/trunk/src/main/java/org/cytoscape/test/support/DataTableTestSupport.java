

package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

import org.cytoscape.model.internal.CyTableFactoryImpl;

public class DataTableTestSupport {

	protected CyTableFactory tableFactory;
	protected DummyCyEventHelper eventHelper;

	public DataTableTestSupport() {
		eventHelper = new DummyCyEventHelper();
		tableFactory = new CyTableFactoryImpl( eventHelper );
	}

	public CyTableFactory getDataTableFactory() {
		return tableFactory;	
	}

	public DummyCyEventHelper getDummyCyEventHelper() {
		return eventHelper;
	}
}


