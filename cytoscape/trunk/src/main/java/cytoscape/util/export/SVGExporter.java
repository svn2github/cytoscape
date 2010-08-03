package cytoscape.util.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.graphics2d.VectorGraphics;

/**
 * SVG exporter by the batik library.
 * @author Samad Lotia
 */
public class SVGExporter implements Exporter
{
	private boolean exportTextAsFont = true;

	public SVGExporter()
	{
	}

	public void export(CyNetworkView view, FileOutputStream stream) throws IOException
	{
		DingNetworkView theView = (DingNetworkView) view;
		theView.setPrintingTextAsShape(!exportTextAsFont);

		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		SVGGraphics2D g = new SVGGraphics2D(stream, ifc);

		// this sets text as shape
		java.util.Properties p = new java.util.Properties();
		p.setProperty("org.freehep.graphicsio.AbstractVectorGraphicsIO.TEXT_AS_SHAPES", 
		              Boolean.toString(!exportTextAsFont));
		g.setProperties(p);

		g.startExport();
		ifc.print(g);
		g.endExport();
	}
	
	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}

}
