
package org.cytoscape.io.write;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 * A utility Task implementation that will write the specified View to the
 * the specified image file using the specified RenderingEngine.
 */
public final class ViewWriter extends AbstractCyWriter<ViewWriterManager> {

	private final View<?> view;
	private final RenderingEngine re;

	/**
	 * @param writerManager The ViewWriterManager used to determine which type of
	 * file should be written.
	 * @param view The View object to be written to the specified file.
	 * @param re The RenderingEngine used to generate the image to be written to the file.  
	 */
    public ViewWriter(ViewWriterManager writerManager, View<?> view, RenderingEngine re ) {
		super(writerManager);

		if ( view == null )
			throw new NullPointerException("view is null");
		this.view = view;

		if ( re == null )
			throw new NullPointerException("rendering engine is null");
		this.re = re;
	}

	/**
	 * {@inheritDoc}
	 */
	protected CyWriter getWriter(CyFileFilter filter, File file) throws Exception {
		return writerManager.getWriter(view,re,filter,file);
	}
}
