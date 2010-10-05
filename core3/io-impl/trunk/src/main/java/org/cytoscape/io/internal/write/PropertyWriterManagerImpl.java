package org.cytoscape.io.internal.write;


import org.cytoscape.io.write.PropertyWriterManager;
import org.cytoscape.io.write.PropertyWriterFactory;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.DataCategory;
import org.cytoscape.io.CyFileFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class PropertyWriterManagerImpl extends AbstractWriterManager<PropertyWriterFactory> 
	implements PropertyWriterManager {

	public PropertyWriterManagerImpl() {
		super(DataCategory.PROPERTIES);
	}

	public CyWriter getWriter(Object property, CyFileFilter filter, File outFile) throws Exception {
		return getWriter(property,filter,new FileOutputStream(outFile));
	}

	public CyWriter getWriter(Object property, CyFileFilter filter, OutputStream os) throws Exception {
		PropertyWriterFactory tf = getMatchingFactory(filter,os);
		if ( tf == null )
			throw new NullPointerException("Couldn't find matching factory for filter: " + filter);
		tf.setProperty(property);
		return tf.getWriter();
	}
}

