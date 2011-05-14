package org.cytoscape.task.internal.io;


import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyNetworkViewWriterFactory;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CyWriter;

import java.io.File;


/**
 * A utility Task implementation specifically for writing a {@link org.cytoscape.view.model.CyNetworkView}.
 */
public final class CyNetworkViewWriter extends TunableAbstractCyWriter<CyNetworkViewWriterManager> {
	// the view to be written
	private final CyNetworkView view;

	/**
	 * @param writerManager The {@link org.cytoscape.io.write.CyNetworkViewWriterManager} used to determine which 
	 * {@link org.cytoscape.io.write.CyNetworkViewWriterFactory} to use to write the file.
	 * @param view The {@link org.cytoscape.view.model.CyNetworkView} to be written out. 
	 */
	public CyNetworkViewWriter(CyNetworkViewWriterManager writerManager, CyNetworkView view ) {
		super(writerManager);
		if (view == null)
			throw new NullPointerException("View is null!");
		this.view = view;
	}

	/**
	 * {@inheritDoc}  
	 */
	protected CyWriter getWriter(CyFileFilter filter, File file)  throws Exception{
		return writerManager.getWriter(view,filter,file);
	}
}
