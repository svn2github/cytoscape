package nodeCharts.view;

import giny.view.Position;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nodeCharts.command.ValueUtils;
import nodeCharts.view.ViewUtils.TextAlignment;
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.view.CyNetworkView;

/**
 * creates a list of custom graphics where each custom graphic represents a
 * bar of a abr chart. The data for this is of the format: value1:value2:...,
 * with optional color where value is numeric and the color is optional, but if
 * specified, it must be one of the named Java colors, hex RGB values, or hex
 * RGBA values.
 * 
 * Bar starts at centerline of node and goes upwards (for positive value) or 
 * downwards (for negative values)
 * 
 */
public class BarChart implements NodeChartViewer {
	private static final String COLORS = "colorlist";
	private static final String SEPARATION = "separation";

	public String getName() {
		return "bar";
	}

	public String getDescription() {
		return "Display the values passed as arguments as a line chart on the node";
	}

	public Map<String, String> getOptions() {
		Map<String, String> options = new HashMap<String, String>();
		options.put(COLORS, "");
		options.put(SEPARATION, "");
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object> args,
			List<Double> values, List<String> labels, CyNode node,
			CyNetworkView view, Object position) throws CyCommandException {
	
		// Get our colors
		List<Color> colors = ValueUtils.convertInputToColor(args.get(COLORS),
				labels.size());
		int separation = 0;
		Object separationObj = args.get(SEPARATION);
		if (separationObj instanceof String)
		{
			if (! separationObj.equals(""))
			{
				separation = Integer.parseInt(separationObj.toString());
			}
		}

		// Sanity check
		if (labels.size() != values.size() || labels.size() != colors.size())
			throw new CyCommandException("number of labels (" + labels.size()
					+ "), values (" + values.size() + "), and colors ("
					+ colors.size() + ") don't match");

		int nbrSlices = values.size();
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();

		// We need to get our bounding box in order to scale our graphic
		// properly
		Rectangle2D bbox = ViewUtils.getNodeBoundingBox(node, view, position);
		double height = bbox.getHeight();
		double width = bbox.getWidth();
		
		double x = bbox.getX();
		double y = bbox.getY();
		
		double yMid = y + (0.5 * height);	
		
		System.out.println("Node bounding box = " + bbox);
		// divide width into equal size segments
		double slice = (width / values.size()) - (values.size() * separation) + separation;  // only have n-1 separators
//		System.out.println("slice = " + slice);

		double min = 0.000001;   // endure no division by zero
		double max = -0.000001;
		for (double i : values) {
			min = Math.min(min, i);
			max = Math.max(max, i);
		}

		// make the graph symmetrical around max and min, set to the larger  (maybe will be too influenced by outliers?)
		if (Math.abs(max) > Math.abs(min))
		{
			min = -1.0 * max;
		}
		else
		{
			max = -1.0 * min;
		}

		for (int i = 0; i < values.size(); i++) {
			double px1 = x + (i * slice);
			double w = slice;
			PaintFactory pf = new DefaultPaintFactory(colors.get(i));
		    double val = values.get(i);
		    double py1 = y + (0.5 * height);
		    if (val > 0.0) // positive, work down to midpoint
		    {
		    	py1 = py1 - ((0.5 * height) * (val / max));
		    }
		    else // negative, work down from midpoint
		    {
		    	val = -val;
		    }
		    	
		    double h = (0.5 * height) * (val / max);
		    
			Rectangle2D drect = new Rectangle2D.Double(px1, py1, w, h);
			System.out.println ("Got rectangle from: " + px1 + "," + py1 + " of width " + w + " and height " + h);
			
			CustomGraphic c = new CustomGraphic(drect, pf);
//			System.out.println("added custome graphic for line from " + drect.getP1() + " to " + .getP2());
			cgList.add(c);
			// Now, create the label.  We want to do this here so we can adjust the label for the slice
			
			
			// add labels
			TextAlignment tAlign = TextAlignment.ALIGN_CENTER_BOTTOM;
			
			// Now, create the label.  Put the label on the outer edge of the circle.
			Point2D labelPosition = new Point2D.Double(bbox.getMaxX(), bbox.getMaxY());
			// vals[1] = ViewUtils.getLabelCustomGraphic(label, null, 0, 0, labelPosition, tAlign, view);
			Shape textShape = ViewUtils.getLabelShape(labels.get(i), null, 0, 0, labelPosition, tAlign, view);

			// Combine the shapes
			Area textArea = new Area(textShape);
//			textArea.add(new Area(labelLine));


//			vals[1] = new CustomGraphic(textArea, new DefaultPaintFactory(Color.BLACK));
			CustomGraphic c1  = new CustomGraphic(textArea, new DefaultPaintFactory(Color.BLACK));
			cgList.add(c);

		}
		return cgList;
	}


}
