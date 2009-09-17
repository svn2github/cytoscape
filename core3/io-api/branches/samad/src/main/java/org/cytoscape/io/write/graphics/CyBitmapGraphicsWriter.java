package org.cytoscape.io.write.graphics;

import java.io.OutputStream;
import java.io.IOException;
import org.cytoscape.view.presentation.RenderingEngine;

/**
 * Writes bitmap graphics given a <code>renderingEngine</code> to an output stream.
 * @author Pasteur
 */
public interface CyBitmapGraphicsWriter
{
	/**
	 * Initiates writing
	 * @param renderingEngine The rendering engine to use to write the graphics
	 * @param output The output stream to use for storing the graphics
	 * @param scale The zoom scale to use to write the graphics
	 * @throws IOException if the graphics file could not be written to <code>output</code>
	 *                     or if <code>cancel</code> was called
	 */
	public void write(RenderingEngine renderingEngine, OutputStream output, double scale) throws IOException;

	/**
	 * Causes <code>write</code> to prematurely terminate
	 * @throws IllegalStateException if this method is called before or after <code>write</code> has been executed
	 */
	public void cancel();
}
