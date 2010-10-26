

package org.cytoscape.test.support;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

import org.cytoscape.model.internal.CyTableFactoryImpl;
import org.cytoscape.model.internal.CyTableManagerImpl;

import static org.mockito.Mockito.*;

public class DataTableTestSupport {

	protected CyTableFactory tableFactory;
	protected DummyCyEventHelper eventHelper;

	public DataTableTestSupport() {
		eventHelper = new DummyCyEventHelper();
		tableFactory = new CyTableFactoryImpl( eventHelper, mock(CyTableManagerImpl.class) );
	}

	public CyTableFactory getDataTableFactory() {
		return tableFactory;	
	}

	public DummyCyEventHelper getDummyCyEventHelper() {
		return eventHelper;
	}
}


