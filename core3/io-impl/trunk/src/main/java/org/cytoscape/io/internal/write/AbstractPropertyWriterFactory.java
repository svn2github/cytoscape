package org.cytoscape.io.internal.write;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.io.write.PropertyWriterFactory;
import java.io.OutputStream;

public abstract class AbstractPropertyWriterFactory implements PropertyWriterFactory {
	
	private final CyFileFilter thisFilter;

	protected OutputStream outputStream;
	protected Object props;

	public AbstractPropertyWriterFactory(CyFileFilter thisFilter) {
		this.thisFilter = thisFilter;
	}
	
	@Override
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	@Override
	public CyFileFilter getCyFileFilter() {
		return thisFilter;
	}

	@Override
	public void setProperty(Object props) {
		this.props = props;
	}
}
