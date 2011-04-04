package org.cytoscape.internal.shutdown;


import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.AbstractCyWriter;
import org.cytoscape.io.write.CyPropertyWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.property.CyProperty;

import java.io.File;


/**
 * A utility Task implementation that will write the specified View to the
 * the specified image file using the specified RenderingEngine.
 */
public final class PropertyWriter extends AbstractCyWriter<CyPropertyWriterManager> {
	private final CyProperty property;

	/**
	 * @param writerManager The {@link org.cytoscape.io.write.PresentationWriterManager} used to determine which type of
	 * file should be written.
	 * @param view The View object to be written to the specified file.
	 * @param re The RenderingEngine used to generate the image to be written to the file.  
	 */
	public PropertyWriter(final CyPropertyWriterManager writerManager, final CyProperty property) {
		super(writerManager);
		this.property = property;
	}

	protected String getExportFileFormat() {
		return "props";
	}

	/**
	 * {@inheritDoc}
	 */
	protected CyWriter getWriter(final CyFileFilter filter, final File file) throws Exception {
		return writerManager.getWriter(property, filter, file);
	}
}
