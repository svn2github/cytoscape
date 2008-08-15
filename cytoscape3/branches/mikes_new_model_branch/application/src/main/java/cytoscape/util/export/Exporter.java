package cytoscape.util.export;

import org.cytoscape.view.GraphView;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Interface for exporting a network view to a graphics file.
 */
public interface Exporter
{
	/**
	 * Export a view as graphics to a stream.
	 * @param view The view to export
	 * @param stream The stream to write the graphics to;
	 *               the stream is not closed when exporting
	 *               is finished.
	 */
	public void export(GraphView view, FileOutputStream stream) throws IOException;
}
