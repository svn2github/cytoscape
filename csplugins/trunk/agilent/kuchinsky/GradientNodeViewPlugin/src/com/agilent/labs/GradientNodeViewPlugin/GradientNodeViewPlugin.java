package com.agilent.labs.GradientNodeViewPlugin;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Iterator;

import javax.swing.AbstractAction;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.DNodeView;
import ding.view.InnerCanvas;
import ding.view.DGraphView.Canvas;

/**
 * Attempts to give an impression of depth to Cytoscape Nodes by:
 *   1. drawing beveled edges for the node borders and
 *   2. using a GradientPaint for the node fill color
 *   
 * First version uses CustomGraphics and hardcoded colors, but subsequent versions should work of Vizmapper and, 
 * assuming that performance would not be impacted, the nodeviews should be generated by the Cytoscape rendering 
 * engine.
 * @author ajk
 *
 */
public class GradientNodeViewPlugin extends CytoscapePlugin {

	static final int CUSTOM_GRAPHICS_LAYER = 8088;

	protected static final String CUSTOM_GRAPHICS_ATTRIBUTE = "custom";
	
	protected static final String ROUND_RECT = "RoundRect";
	protected static final String ELLIPSE = "Ellipse";
	protected String _shape_mode = ROUND_RECT;
	private boolean drawingGradient = false;

	public GradientNodeViewPlugin() {
		Cytoscape.getDesktop().getCyMenus().getViewMenu().add(
				new MainPluginAction());
	}

	// ~ Inner Classes
	// //////////////////////////////////////////////////////////

	/**
	 * This class gets attached to the menu item.
	 */
	public class MainPluginAction extends AbstractAction {
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MainPluginAction() {
			super("Toggle Gradient NodeViews");
		}

		/**
		 * Gives a description of this plugin.
		 */
		public String describe() {
			StringBuffer sb = new StringBuffer();
			sb.append("Show NodeViews with Gradient fill");
			return sb.toString();
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
			
			drawingGradient = !drawingGradient;

			CyNetwork net = Cytoscape.getCurrentNetwork();
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			
			Iterator it = net.nodesIterator();
			while (it.hasNext()) {
				Node n = (Node) it.next();
				NodeView nView = view.getNodeView(n);
				DNodeView dnv = (DNodeView) nView;
				
				if (!drawingGradient)
				{
					// careful, this will remove all custom graphics, so could clobber custom graphic added by
					// another plugin
	   		 		for (int i=0;i<dnv.getCustomGraphicCount();i++){
	   		 			dnv.removeCustomGraphic(i);
	   		 		}
					continue;
				}
			
				dnv.setWidth(dnv.getHeight() * 1.5);
				
				_shape_mode = (Math.random() > 0.5) ? ROUND_RECT : ELLIPSE;

//				 First add custom graphic//				
				// special processing for star alert
				Rectangle2D rect;
				rect = new java.awt.geom.Rectangle2D.Double(- (0.5 * dnv.getWidth()) - 3, 
						- (0.5 * dnv.getHeight()) -3, dnv.getWidth()  + 6, dnv.getHeight() + 6);

				TexturePaint paint;
				
				BufferedImage image = null;     
				try {
		            image = new BufferedImage(
		                    (int) rect.getWidth(),
		                    (int) rect.getHeight(),
		                    BufferedImage.TYPE_INT_RGB);
		        } catch (OutOfMemoryError e) { e.printStackTrace();
		  
		        }
		        
		        if (image != null)
		        {
		        	// first do beveling
		        	Graphics g = image.getGraphics();
		        	g.setColor(((DGraphView) view).getCanvas(Canvas.BACKGROUND_CANVAS).getBackground());
		            g.fillRect(0, 0, (int) rect.getWidth(), (int) rect.getHeight());
		        	Graphics2D g2 = (Graphics2D) g;

		        	g.setColor(Color.gray);
		        	
		        	if (_shape_mode == ROUND_RECT)
		        	{
			            g.fillRoundRect(1, 1, (int) dnv.getWidth() - 1 + 3, (int) dnv.getHeight() - 1,  10, 10);
			            g.setColor(Color.white);
			            g.fillRoundRect(0, 0, (int) dnv.getWidth() - 2 + 3, (int) dnv.getHeight() - 2, 10, 10);
		        	}
		        	else
		        	{
			            g.fillOval (1, 1, (int) dnv.getWidth() - 1 + 3, (int) dnv.getHeight() - 1);
			            g.setColor(Color.white);
			            g.fillOval(0, 0, (int) dnv.getWidth() - 2 + 3, (int) dnv.getHeight() - 2);
		        	}

		        	// now do the gradient fill	        	
		            Color secondColor;
		            Double random = Math.random();
		            // map from green to red through black, THIS IS A TEMPORARY HACK
		            secondColor = (random < 0.5) ? new Color (0, ((int) (255 * (1.0 - random ))), 0) :
		            	new Color ((int) (255 *  random), 0, 0);
		            
		            
		        	RoundGradientPaint rgp = new RoundGradientPaint((int) (dnv.getWidth() * 0.5), 
		        			(int) (dnv.getHeight() * 0.5), Color.white,
		        	        new Point2D.Double((int) (dnv.getWidth () * 0.6), 
		        	        		(int) (dnv.getHeight() * 0.6)), secondColor);
		        	    g2.setPaint(rgp);
		        	    if (_shape_mode == ROUND_RECT)
		        	    {
		        	    	g2.fillRoundRect(2, 2, (int) dnv.getWidth(), (int) dnv.getHeight() -3, 10, 10);
		        	    }
		        	    else
		        	    {
		        	    	g2.fillOval (2, 2, (int) dnv.getWidth(), (int) dnv.getHeight() - 3 );
		        	    }
		        	    
		        }
		        
				try {
					paint = new TexturePaint(image, rect);
				} catch (Exception exc) {
					paint = null;
				}

				if (paint != null) {
					dnv.addCustomGraphic(rect, paint, CUSTOM_GRAPHICS_LAYER);
				}

			}
			view.redrawGraph(true, true);

		}
	}
  
		 class RoundGradientPaint implements Paint {
			    protected Point2D point;

			    protected Point2D mRadius;

			    protected Color mPointColor, mBackgroundColor;
		
		   public RoundGradientPaint(double x, double y, Color pointColor,
			        Point2D radius, Color backgroundColor) {
			      if (radius.distance(0, 0) <= 0)
			        throw new IllegalArgumentException("Radius must be greater than 0.");
			      point = new Point2D.Double(x, y);
			      mPointColor = pointColor;
			      mRadius = radius;
			      mBackgroundColor = backgroundColor;
			    }

			    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			        Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
			      Point2D transformedPoint = xform.transform(point, null);
			      Point2D transformedRadius = xform.deltaTransform(mRadius, null);
			      return new RoundGradientContext(transformedPoint, mPointColor,
			          transformedRadius, mBackgroundColor);
			    }

			    public int getTransparency() {
			      int a1 = mPointColor.getAlpha();
			      int a2 = mBackgroundColor.getAlpha();
			      return (((a1 & a2) == 0xff) ? OPAQUE : TRANSLUCENT);
			    }
			  }
			  public class RoundGradientContext implements PaintContext {
			    protected Point2D mPoint;

			    protected Point2D mRadius;

			    protected Color color1, color2;

			    public RoundGradientContext(Point2D p, Color c1, Point2D r, Color c2) {
			      mPoint = p;
			      color1 = c1;
			      mRadius = r;
			      color2 = c2;
			    }

			    public void dispose() {
			    }

			    public ColorModel getColorModel() {
			      return ColorModel.getRGBdefault();
			    }

			    public Raster getRaster(int x, int y, int w, int h) {
			      WritableRaster raster = getColorModel().createCompatibleWritableRaster(
			          w, h);

			      int[] data = new int[w * h * 4];
			      for (int j = 0; j < h; j++) {
			        for (int i = 0; i < w; i++) {
			          double distance = mPoint.distance(x + i, y + j);
			          double radius = mRadius.distance(0, 0);
			          double ratio = distance / radius;
			          if (ratio > 1.0)
			            ratio = 1.0;

			          int base = (j * w + i) * 4;
			          data[base + 0] = (int) (color1.getRed() + ratio
			              * (color2.getRed() - color1.getRed()));
			          data[base + 1] = (int) (color1.getGreen() + ratio
			              * (color2.getGreen() - color1.getGreen()));
			          data[base + 2] = (int) (color1.getBlue() + ratio
			              * (color2.getBlue() - color1.getBlue()));
			          data[base + 3] = (int) (color1.getAlpha() + ratio
			              * (color2.getAlpha() - color1.getAlpha()));
			        }
			      }
			      raster.setPixels(0, 0, w, h, data);

			      return raster;
			    }
			  }		


}
