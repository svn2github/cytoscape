package SessionExporterPlugin;

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
	protected double zoom;
	protected ImageLOD imageLOD;

	public GraphViewToImage(double zoom, boolean renderNodeLabels, boolean renderEdgeLabels)
	{
		this.zoom = zoom;
		imageLOD = new ImageLOD(renderNodeLabels, renderEdgeLabels);
	}

	public BufferedImage convert(DGraphView view)
	{
		if (view == null)
			return null;

		Rectangle2D.Double graphBounds = graphBounds(view);
		BufferedImage image = new BufferedImage((int) (graphBounds.width * zoom),
							(int) (graphBounds.height * zoom),
							BufferedImage.TYPE_INT_RGB);
						
		double centerX = graphBounds.x + graphBounds.width / 2.0d;
		double centerY = graphBounds.y + graphBounds.height / 2.0d;
		Paint background = view.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS).getBackground();
		imageLOD.setParentLOD(view.getGraphLOD());
		view.drawSnapshot(image, imageLOD, background, centerX, centerY, zoom - zoom / 10.0);

		return image;
	}

	private Rectangle2D.Double graphBounds(DGraphView graphView)
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
	
	private class ImageLOD extends GraphLOD
	{
		private GraphLOD parentLOD = null;
		private boolean renderNodeLabels;
		private boolean renderEdgeLabels;

		public ImageLOD(boolean renderNodeLabels, boolean renderEdgeLabels)
		{
			this.renderNodeLabels = renderNodeLabels;
			this.renderEdgeLabels = renderEdgeLabels;
		}

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
			return renderNodeLabels;
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
			return renderEdgeLabels;
		}

		public boolean textAsShape(final int renderNodeCount, final int renderEdgeCount)
		{
			return parentLOD.textAsShape(renderNodeCount, renderEdgeCount);
		}
	}
}
