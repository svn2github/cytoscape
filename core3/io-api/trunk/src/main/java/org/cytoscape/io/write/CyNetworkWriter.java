
package org.cytoscape.io.write;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 */
public final class CyNetworkWriter extends AbstractCyWriter<CyNetworkWriterManager> {

	private final CyNetwork network;

    public CyNetworkWriter(CyNetworkWriterManager writerManager, CyNetwork network ) {
		super(writerManager);
		if ( network == null )
			throw new NullPointerException("Network is null");
		this.network = network;
	}

	protected CyWriter getWriter(CyFileFilter filter, File file) {
		return writerManager.getWriter(network,filter,file);
	}
}
