package org.cytoscape.io.internal.write.cysession;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PropertyWriterFactory;
import java.io.OutputStream;
import org.cytoscape.io.internal.write.AbstractPropertyWriterFactory;

public class CysessionWriterFactoryImpl extends AbstractPropertyWriterFactory {

	public CysessionWriterFactoryImpl(CyFileFilter filter) {
		super(filter);
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new CysessionWriterImpl(outputStream, props);
	}
}
