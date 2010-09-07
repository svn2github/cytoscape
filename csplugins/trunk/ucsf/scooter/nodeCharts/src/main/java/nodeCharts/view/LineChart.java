package nodeCharts.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nodeCharts.command.ValueUtils;
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.view.CyNetworkView;

/**
 * creates a list of custom graphics where each custom graphic represents a
 * segment of the line. The data for this is of the format: value1:value2:...,
 * with optional color where value is numeric and the color is optional, but if
 * specified, it must be one of the named Java colors, hex RGB values, or hex
 * RGBA values.
 */
public class LineChart implements NodeChartViewer {
	private static final String COLORS = "color";

	public String getName() {
		return "line";
	}

	public String getDescription() {
		return "Display the values passed as arguments as a line chart on the node";
	}

	public Map<String, String> getOptions() {
		Map<String, String> options = new HashMap<String, String>();
		options.put(COLORS, "");
		return options;
	}

	public List<CustomGraphic> getCustomGraphics(Map<String, Object> args,
			List<Double> values, List<String> labels, CyNode node,
			CyNetworkView view, Object position) throws CyCommandException {
		// Get our color
		// Get our colors
		List<Color> colors = ValueUtils.convertInputToColor(args.get(COLORS),
				labels.size());

		// Sanity check
		if (labels.size() != values.size() || labels.size() != colors.size())
			throw new CyCommandException("number of labels (" + labels.size()
					+ "), values (" + values.size() + "), and colors ("
					+ colors.size() + ") don't match");

		int nsegments = values.size();
		List<CustomGraphic> cgList = new ArrayList<CustomGraphic>();

		// We need to get our bounding box in order to scale our graphic
		// properly
		Rectangle2D bbox = ViewUtils.getNodeBoundingBox(node, view, position);
		double height = bbox.getHeight();
		double width = bbox.getWidth();
		double x = bbox.getX();
		double y = bbox.getY() - 1;
		
		System.out.println("Node bounding box = " + bbox);
		// divide width into equal size segments
		double slice = width / (values.size() - 1);
		System.out.println("slice = " + slice);
		double divisor = getDivisor(values, height);

		double min = Float.MAX_VALUE;
		for (double i : values) {
			min = Math.min(min, i);
		}

		System.out.println ("Divisor = " + divisor);
		if (divisor == 0.0) {
			// degenerate case -- a horizontal straight line
			divisor = 1.0d;
			y = -(height / 2.0);
		}

		for (int i = 0; i < values.size() - 1; i++) {
			double px1 = x;
			x += slice;
			double px2 = x;
			PaintFactory pf = new DefaultPaintFactory(colors.get(0));
			Line2D line = new Line2D.Double(
					px1,
					y
							+ (height - ((values.get(i).floatValue() - min) / divisor)),
					px2, y
							+ (height - (values.get(i + 1).floatValue() - min)
									/ divisor));						
			BasicStroke stroke = new BasicStroke(1.5f);
			CustomGraphic c = new CustomGraphic(stroke.createStrokedShape(line), pf);
			
			cgList.add(c);
			// Now, create the label.  We want to do this here so we can adjust the label for the slice
//			CustomGraphic c1  = ViewUtils.getLabelCustomGraphic(labels.get(i), null, 0, 0, bbox, view);
			cgList.add(c);
		}
		return cgList;
	}

	private Double getDivisor(List<Double> data, double height) {
		double max = Double.MIN_VALUE;
		double min = Float.MAX_VALUE;

		for (double i : data) {
			min = Math.min(min, i);
			max = Math.max(max, i);
		}

		if (max <= min) {
			return 1.0d;
		}

		return (max - min) / (height - 1.0);
	}



}
