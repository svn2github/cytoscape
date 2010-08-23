

package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;

import org.cytoscape.model.internal.CyDataTableFactoryImpl;

public class DataTableTestSupport {

	protected CyDataTableFactory tableFactory;
	protected DummyCyEventHelper eventHelper;

	public DataTableTestSupport() {
		eventHelper = new DummyCyEventHelper();
		tableFactory = new CyDataTableFactoryImpl( eventHelper );
	}

	public CyDataTableFactory getDataTableFactory() {
		return tableFactory;	
	}

	public DummyCyEventHelper getDummyCyEventHelper() {
		return eventHelper;
	}
}


