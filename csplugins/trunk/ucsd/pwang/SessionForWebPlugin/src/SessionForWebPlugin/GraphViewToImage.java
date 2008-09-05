package SessionForWebPlugin;

import java.io.File;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import cytoscape.render.stateful.GraphLOD;

public class GraphViewToImage
{
	public static Dimension getImageDimensions(DGraphView view, SessionExporterSettings settings)
	{
		if (view == null)
			return null;
		Rectangle2D.Double graphBounds = graphBounds(view);
		if (graphBounds == null)
			return null;

		double zoom = settings.imageZoom;
		int width = (int) (graphBounds.width * zoom);
		int height = (int) (graphBounds.height * zoom);
		if (settings.doSetMaxImageSize)
		{
			if (width > settings.maxImageWidth)
			{
				width = settings.maxImageWidth;
				zoom = width / ((double) graphBounds.width);
				height = (int) (graphBounds.height * zoom);
			}
			else if (height > settings.maxImageHeight)
			{
				height = settings.maxImageHeight;
				zoom = height / ((double) graphBounds.height);
				width = (int) (graphBounds.width * zoom);
			}
		}
		Dimension result = new Dimension();
		result.setSize(width, height);
		return result;
	}
	
	public static BufferedImage convert(DGraphView view, SessionExporterSettings settings)
	{
		if (view == null)
			return null;
		Rectangle2D.Double graphBounds = graphBounds(view);
		Dimension imageDimensions = getImageDimensions(view, settings);

		System.gc();
		BufferedImage image = null;
		try
		{
			image = new BufferedImage((int) imageDimensions.getWidth(), (int) imageDimensions.getHeight(), BufferedImage.TYPE_INT_RGB);
		}
		catch(OutOfMemoryError e)
		{
			outOfMemoryError();
			return null;
		}
						
		double centerX = graphBounds.x + graphBounds.width / 2.0d;
		double centerY = graphBounds.y + graphBounds.height / 2.0d;
		double zoom = ((double) imageDimensions.getWidth()) / graphBounds.width;
		Paint background = view.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS).getBackground();
		ImageLOD imageLOD = new ImageLOD();
		imageLOD.setParentLOD(view.getGraphLOD());
		view.drawSnapshot(image, imageLOD, background,
				centerX, centerY, zoom - zoom / 10.0);

		return image;
	}

	private static void outOfMemoryError()
	{
		System.gc();
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
			"Could not export network because there was not enough memory.",
			"Session for Web",
			JOptionPane.ERROR_MESSAGE);
	}

	private static Rectangle2D.Double graphBounds(DGraphView graphView)
	{
		Rectangle2D.Double bounds = new Rectangle2D.Double();
		double[] extents = new double[4];
		if (!graphView.getExtents(extents))
			return null;
		bounds.x = extents[0];
		bounds.y = extents[1];
		bounds.width = extents[2] - extents[0];
		bounds.height = extents[3] - extents[1];
		return bounds;
	}
}
	
class ImageLOD extends GraphLOD
{
	private GraphLOD parentLOD = null;

	public void setParentLOD(GraphLOD parentLOD)
	{
		this.parentLOD = parentLOD;
	}

	public byte renderEdges(final int visibleNodeCount, final int totalNodeCount,
			final int totalEdgeCount)
	{
		return parentLOD.renderEdges(visibleNodeCount, totalNodeCount, totalEdgeCount);
	}

	public boolean detail(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.detail(renderNodeCount, renderEdgeCount);
	}

	public boolean nodeBorders(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.nodeBorders(renderNodeCount, renderEdgeCount);
	}

	public boolean nodeLabels(final int renderNodeCount, final int renderEdgeCount)
	{
		return true;
	}

	public boolean customGraphics(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.customGraphics(renderNodeCount, renderEdgeCount);
	}

	public boolean edgeArrows(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.edgeArrows(renderNodeCount, renderEdgeCount);
	}
	
	public boolean dashedEdges(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.dashedEdges(renderNodeCount, renderEdgeCount);
	}

	public boolean edgeAnchors(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.edgeAnchors(renderNodeCount, renderEdgeCount);
	}

	public boolean edgeLabels(final int renderNodeCount, final int renderEdgeCount)
	{
		return true;
	}

	public boolean textAsShape(final int renderNodeCount, final int renderEdgeCount)
	{
		return parentLOD.textAsShape(renderNodeCount, renderEdgeCount);
	}
}
