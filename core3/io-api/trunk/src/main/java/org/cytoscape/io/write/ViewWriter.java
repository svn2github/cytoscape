
package org.cytoscape.io.write;

import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.io.CyFileFilter;
import java.io.File;

/**
 */
public final class ViewWriter extends AbstractCyWriter<ViewWriterManager> {

	private final View<?> view;
	private final RenderingEngine re;

    public ViewWriter(ViewWriterManager writerManager, View<?> view, RenderingEngine re ) {
		super(writerManager);

		if ( view == null )
			throw new NullPointerException("view is null");
		this.view = view;

		if ( re == null )
			throw new NullPointerException("rendering engine is null");
		this.re = re;
	}

	protected CyWriter getWriter(CyFileFilter filter, File file) throws Exception {
		return writerManager.getWriter(view,re,filter,file);
	}
}
