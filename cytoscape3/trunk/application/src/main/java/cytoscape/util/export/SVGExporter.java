package cytoscape.util.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cytoscape.Cytoscape;
import org.cytoscape.view.GraphView;

import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.graphics2d.VectorGraphics;

/**
 * SVG exporter by the batik library.
 * @author Samad Lotia
 */
public class SVGExporter implements Exporter
{
	public SVGExporter()
	{
	}

	public void export(GraphView view, FileOutputStream stream) throws IOException
	{
		VectorGraphics g = new SVGGraphics2D(stream, view.getComponent());
		g.startExport();
		view.print(g);
		g.endExport();
	}
}
