package org.cytoscape.io.internal.write.xgmml;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.internal.write.AbstractCyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyWriter;

public class XGMMLNetworkViewWriterFactory extends AbstractCyNetworkViewWriterFactory {
	
	public XGMMLNetworkViewWriterFactory(CyFileFilter filter) {
		super(filter);
	}
	
	@Override
	public CyWriter getWriterTask() {
		return new XGMMLWriter(outputStream, view, true);
	}
}
