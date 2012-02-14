package org.cytoscape.io.internal.write;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.write.CyTableWriterContext;
import org.cytoscape.io.write.CyTableWriterFactory;
import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyTable;


public class CyTableWriterManagerImpl extends AbstractWriterManager<CyTableWriterFactory<CyTableWriterContext>> 
	implements CyTableWriterManager {

	public CyTableWriterManagerImpl() {
		super(DataCategory.TABLE);
	}

	@Override
	public CyWriter getWriter(CyTable table, CyFileFilter filter, File outFile) throws Exception{
		return getWriter(table,filter,new FileOutputStream(outFile));
	}

	@Override
	public CyWriter getWriter(CyTable table, CyFileFilter filter, OutputStream os) throws Exception{
		CyTableWriterFactory<CyTableWriterContext> tf = getMatchingFactory(filter);
		CyTableWriterContext context = tf.createTaskContext();
		
		if ( tf == null )
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		context.setOutputStream(os);
		context.setTable(table);
		return tf.createWriterTask(context);
	}
}
