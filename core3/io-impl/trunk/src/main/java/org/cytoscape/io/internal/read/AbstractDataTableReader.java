package org.cytoscape.io.internal.read;


import java.io.IOException;
import java.io.InputStream;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.io.read.CyDataTableReader;
import org.cytoscape.work.AbstractTask;


public abstract class AbstractDataTableReader extends AbstractTask 
	implements CyDataTableReader {

	protected CyDataTable[] cyDataTables;
	protected InputStream inputStream;

	protected final CyDataTableFactory tableFactory;
	                      
	public AbstractDataTableReader(InputStream inputStream, CyDataTableFactory tableFactory) {
		if ( inputStream == null )
			throw new NullPointerException("InputStream is null");
		this.inputStream = inputStream;
		if ( tableFactory == null )
			throw new NullPointerException("tableFactory is null");
		this.tableFactory = tableFactory;
	}
	
	public CyDataTable[] getCyDataTables(){
		return cyDataTables;
	}
}
