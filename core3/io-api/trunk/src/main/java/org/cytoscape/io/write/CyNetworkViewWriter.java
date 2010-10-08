
package org.cytoscape.io.write;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 * A utility Task implementation specifically for writing a CyNetworkView.
 */
public final class CyNetworkViewWriter extends AbstractCyWriter<CyNetworkViewWriterManager> {

	// the view to be written
	private final CyNetworkView view;

	/**
	 * @param writerManager The CyNetworkViewWriterManager used to determine which 
	 * CyNetworkViewWriterFactory to use to write the file.
	 * @param view The CyNetworkView to be written out. 
	 */
    public CyNetworkViewWriter(CyNetworkViewWriterManager writerManager, CyNetworkView view ) {
		super(writerManager);
		if ( view == null )
			throw new NullPointerException("View is null");
		this.view = view;
	}

	/**
	 * {@inheritDoc}  
	 */
	protected CyWriter getWriter(CyFileFilter filter, File file)  throws Exception{
		return writerManager.getWriter(view,filter,file);
	}
}
