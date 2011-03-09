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

import nodeCharts.command.NodeChartCommandHandler;
import nodeCharts.command.ValueUtils;
import nodeCharts.view.ViewUtils.TextAlignment;
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.view.CyNetworkView;

/**
 * creates a list of custom graphics where each custom graphic represents a
 * bar of a heat strip. The data for this is of the format: value1:value2:....
 * The colors are specified as a colorlist triple: up:zero:down, or a quad
 * up:zero:down:missing, or a keyword: redgreen, yellowcyan.  If nothing
 * is specified, yellowcyan is assumed
 * 
 * Bar starts at centerline of node and goes upwards (for positive value) or 
 * downwards (for negative values)
 * 
 */
public class HeatStrip implements NodeChartViewer {
	private static final String COLORS = "colorlist";
	private static final String SEPARATION = "separation";
	private static final String REDGREEN = "redgreen";
	private static final String YELLOWCYAN = "yellowcyan";

	float[] dist = {0.0f, 0.5f, 1.0f};
	Color[] redGreen = {Color.GREEN, Color.WHITE, Color.RED};
	Color[] yellowCyan = {Color.CYAN, Color.WHITE, Color.YELLOW};
	

	public String getName() {
		return "heatstrip";
	}

	public String getDescription() {
		return "Display the values passed as arguments as a heat strip on the node";
	}

	public Map<String, String> getOptions() {
		Map<String, String> options = new HashMap<String, String>();
		options.put(COLORS, "yellowcyan");
		options.put(SEPARATION, "");
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object> args,
			List<Double> values, List<String> labels, Rectangle2D bbox,
			CyNetworkView view) throws CyCommandException {

		Color[] colorScale = null;

		// Get our colors
		String colorSpec = args.get(COLORS).toString();
		if (colorSpec.equalsIgnoreCase(YELLOWCYAN)) {
			colorScale = yellowCyan;
		} else if (colorSpec.equalsIgnoreCase(REDGREEN)) {
			colorScale = redGreen;
		} else {
			colorScale = new Color[3];
			String [] colorArray = colorSpec.split(",");
			List<Color> colors = ValueUtils.parseUpDownColor(colorArray);
			colorScale[0] = colors.get(0);
			colorScale[2] = colors.get(1);
			colorScale[1] = colors.get(2);
		}
		int separation = 0;
		Object separationObj = args.get(SEPARATION);
		if (separationObj instanceof String)
		{
			if (! separationObj.equals(""))
			{
				separation = Integer.parseInt(separationObj.toString());
			}
		}

		int nbrSlices = values.size();
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();

		// We need to get our bounding box in order to scale our graphic
		// properly
		double height = bbox.getHeight();
		double width = bbox.getWidth();
		
		double x = bbox.getX();
		double y = bbox.getY();
		
		double yMid = y + (0.5 * height);	
		
		// System.out.println("Node bounding box = " + bbox);
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

		Rectangle2D [] barArray = new Rectangle2D[values.size()];
		double maxY = 0.0;
		PaintFactory pf = new LinearGradientPaintFactory(x, y+height, x, y, colorScale, dist);

		for (int i = 0; i < values.size(); i++) {
			double px1 = x + (i * slice);
			double w = slice;
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

			// Outline the bars for clarity
			Rectangle2D outline = new Rectangle2D.Double(px1, py1, w, h);
			cgList.add(new CustomGraphic(outline, new DefaultPaintFactory(Color.BLACK)));

			barArray[i] = new Rectangle2D.Double(px1+0.2, py1+0.2, w-0.2, h-0.2);
			// System.out.println ("Got rectangle from: " + px1 + "," + py1 + " of width " + w + " and height " + h);
			maxY = Math.max(maxY, barArray[i].getMaxY());
			
			CustomGraphic c = new CustomGraphic(barArray[i], pf);
//			System.out.println("added custome graphic for line from " + drect.getP1() + " to " + .getP2());
			cgList.add(c);
		}

		if (labels != null && labels.size() > 0) {
			// Now, create the labels.  We want to do this here so we can adjust the label for the slice
			for (int i = 0; i < values.size(); i++) {
				
				// add labels
				TextAlignment tAlign = TextAlignment.ALIGN_LEFT;
				
				// Now, create the label.  Put the label on the outer edge of the circle.
				Point2D labelPosition = new Point2D.Double(barArray[i].getCenterX(), maxY);
				// vals[1] = ViewUtils.getLabelCustomGraphic(label, null, 0, 0, labelPosition, tAlign, view);
				Shape textShape = ViewUtils.getLabelShape(labels.get(i), null, 0, 0, view);
	
				double maxHeight = barArray[i].getWidth();
				textShape = ViewUtils.positionLabel(textShape, labelPosition, tAlign, maxHeight, 0.0, 70.0);
				if (textShape == null) continue;
	
	//			vals[1] = new CustomGraphic(textArea, new DefaultPaintFactory(Color.BLACK));
				CustomGraphic c1  = new CustomGraphic(textShape, new DefaultPaintFactory(Color.BLACK));
				cgList.add(c1);
			}
		}
		CustomGraphic cLine  = new CustomGraphic(new Rectangle2D.Double(x, yMid, width, .5), new DefaultPaintFactory(Color.BLACK));
		cgList.add(cLine);
		return cgList;
	}


}
