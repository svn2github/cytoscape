package SessionForWebPlugin;

import java.io.File;
import java.io.IOException;
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
	public static BufferedImage convert(DGraphView view, SessionExporterSettings settings)
	{
		if (view == null)
			return null;
		ImageLOD imageLOD = new ImageLOD();
		Rectangle2D.Double graphBounds = graphBounds(view);

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
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
						
		double centerX = graphBounds.x + graphBounds.width / 2.0d;
		double centerY = graphBounds.y + graphBounds.height / 2.0d;
		Paint background = view.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS).getBackground();
		imageLOD.setParentLOD(view.getGraphLOD());
		view.drawSnapshot(image, imageLOD, background,
				centerX, centerY, zoom - zoom / 10.0);

		return image;
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
