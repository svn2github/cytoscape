
package org.cytoscape.io.write;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 */
public final class CyNetworkViewWriter extends AbstractCyWriter<CyNetworkViewWriterManager> {

	private final CyNetworkView view;

    public CyNetworkViewWriter(CyNetworkViewWriterManager writerManager, CyNetworkView view ) {
		super(writerManager);
		if ( view == null )
			throw new NullPointerException("View is null");
		this.view = view;
	}

	protected CyWriter getWriter(CyFileFilter filter, File file)  throws Exception{
		return writerManager.getWriter(view,filter,file);
	}
}
