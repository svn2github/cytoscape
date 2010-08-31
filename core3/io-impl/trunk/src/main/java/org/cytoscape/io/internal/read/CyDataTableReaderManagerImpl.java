package org.cytoscape.io.internal.read;


import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.io.read.CyDataTableReaderManager;
import org.cytoscape.io.read.InputStreamTaskFactory;
import org.cytoscape.io.DataCategory;


public class CyDataTableReaderManagerImpl extends GenericReaderManager<InputStreamTaskFactory, CyDataTableReader> 
	implements CyDataTableReaderManager {

	public CyDataTableReaderManagerImpl() {
		super(DataCategory.TABLE);
	}
}
