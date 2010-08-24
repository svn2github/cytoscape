package org.cytoscape.io.internal.read;

import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.io.read.CyDataTableReaderFactory;
import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.io.DataCategory;

public class CyDataTableReaderManagerImpl 
	extends GenericReaderManager<CyDataTableReaderFactory,CyDataTableReader> 
	implements CyDataTableReaderManager {

	public CyDataTableReaderManagerImpl() {
		super(DataCategory.TABLE);
	}
}
