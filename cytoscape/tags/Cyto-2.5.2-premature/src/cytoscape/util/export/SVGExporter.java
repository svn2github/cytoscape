package cytoscape.util.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

/*
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.ImageHandlerPNGEncoder;
import org.apache.batik.svggen.ImageHandlerBase64Encoder;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
*/

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

	/*
	public void export(CyNetworkView view, FileOutputStream stream) throws IOException
	{
		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
		Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
		SVGGeneratorContext context = SVGGeneratorContext.createDefault(document);
		//context.setImageHandler(new ImageHandlerPNGEncoder("/cellar/users/slotia/", null));
		context.setImageHandler(new ImageHandlerBase64Encoder());
		SVGGraphics2D svgGenerator = new SVGGraphics2D(context, false);
		ifc.print(svgGenerator);
		svgGenerator.stream(new OutputStreamWriter(stream), true);
	}
	*/
	public void export(CyNetworkView view, FileOutputStream stream) throws IOException
	{
		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		VectorGraphics g = new SVGGraphics2D(stream, ifc);
		g.startExport();
		ifc.print(g);
		g.endExport();
	}
}
