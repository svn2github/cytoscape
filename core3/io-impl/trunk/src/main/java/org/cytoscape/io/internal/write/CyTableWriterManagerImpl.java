package org.cytoscape.io.internal.read;


import org.cytoscape.io.write.CyTableWriter;
import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.io.write.CyTableWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.model.CyTable;
import java.io.File;


public class CyTableWriterManagerImpl extends AbstractWriterManager<CyTableWriterFactory> 
	implements CyTableWriterManager {

	public CyTableWriterManagerImpl() {
		super(DataCategory.TABLE);
	}

	public CyWriter getWriter(CyTable table, CyFileFilter filter, File outFile) {
		CyTableWriterFactory tf = getMatchingFactory(filter,outFile);
		if ( tf == null )
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		tf.setTable(table);
		return tf.getWriter();
	}
}
