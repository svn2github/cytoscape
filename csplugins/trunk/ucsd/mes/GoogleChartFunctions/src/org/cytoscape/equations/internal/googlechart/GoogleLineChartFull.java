package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.AbstractAxisChart;
import com.googlecode.charts4j.AbstractGraphChart;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Plots;


/**
 * Fully-featured Line Chart function.
 * 
 * <h2>Arguments</h2>
 * <h3>Common Parameters</h3>
 * <ol>
 * 	<li>title(String): title of chart</li>
 * 	<li>width(Integer): title of chart</li>
 * 	<li>height(Integer): title of chart</li>
 * 	<li>min(Double): title of chart</li>
 * 	<li>max(D): title of chart</li>
 * 	<li>extra(String): any extra argument for the URL</li>
 * </ol>
 * 
 * <h3>Line-Chart only parameters</h3>
 * <ol>
 * 	<li>axis(Boolean): show axis or not</li>
 * </ol>
 * @author kono
 *
 */
public class GoogleLineChartFull extends AbstractGoogleChartFunction {
		
	private List<Color> colors = null;
	private static final int MINIMUM_NUM_ARGUMENTS = 8;
	
	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GLINECHARTFULL";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a multiple-line chart URL based on the supplied arguments.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"GLINECHARTFULL(title, width, height, min, max, show axis, extra arguments, list of data 1, ... , list of data n)\"";
	}


	/**
	 * @return String.class or null if the args passed in have the wrong arity
	 *         or a type mismatch was found
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length < MINIMUM_NUM_ARGUMENTS)
			return null;
		
		// Title and extra arguments for query URL
		if (argTypes[0] != String.class || argTypes[5] != String.class)
			return null;
		
		// Width & Height, min & max
		for(int i=1; i<5; i++) {
			if ((argTypes[i] != Long.class) && argTypes[i] != Double.class)
				return null;
		}
		
		if (argTypes[6] != Boolean.class )
			return null;

		for(int i=MINIMUM_NUM_ARGUMENTS-1; i<argTypes.length; i++) {
			if (!FunctionUtil.isSomeKindOfList(argTypes[i]))
				return null;
		}

		return String.class;
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {
		
		extractCommonSettings(args);
		// Line-chart specific settings
		final Boolean axis = FunctionUtil.getArgAsBoolean(args[6]);

		
		// Get actual data sets
		final List<List<Double>> dataSets = new ArrayList<List<Double>>();
		for(int i=MINIMUM_NUM_ARGUMENTS-1; i<args.length; i++)
			dataSets.add((List<Double>) args[i]);
		
		if(colors == null)
			colors = ColorUtil.getColors(args.length - MINIMUM_NUM_ARGUMENTS + 1);
		
		final List<Line> lines = new ArrayList<Line>();
		
		int colorIndex = 0;
		for(final List<Double> data: dataSets) {
			final Line line = Plots.newLine(com.googlecode.charts4j.DataUtil.scaleWithinRange(min, max, data), colors.get(colorIndex));
			line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			//line.addShapeMarkers(Shape.CIRCLE, Color.BLACK, 5);
			lines.add(line);
			colorIndex++;
		}

		// Defining chart.
		chart = GCharts.newLineChart(lines);
		this.applyAppearences();
		
		
		if(title.trim().length() != 0)
			((AbstractGraphChart) chart).setTitle(title);

		if(axis) {
			// Setup Axis label
			AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.DARKGRAY, 12,
					AxisTextAlignment.CENTER);

			final Double center = (Math.abs(max)-Math.abs(min))/2;
			AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(min.toString(), center.toString(), max.toString());
			yAxis.setAxisStyle(axisStyle);

			((AbstractAxisChart) chart).addYAxisLabels(yAxis);
			return chart.toURLString() + extraArgs;
		} else
			return chart.toURLString().replace("cht=lc", "cht=ls") + extraArgs;
	}

	/**
	 * Used with the equation builder.
	 * 
	 * @param leadingArgs
	 *            the types of the arguments that have already been selected by
	 *            the user.
	 * @return the set of arguments (must be a collection of String.class,
	 *         Long.class, Double.class, Boolean.class and List.class) that are
	 *         candidates for the next argument. An empty set indicates that no
	 *         further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		final List<Class> possibleNextArgs = new ArrayList<Class>();
		
		if (leadingArgs.length == 0 || leadingArgs.length == 5) {
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		} else if(leadingArgs.length == 1 || leadingArgs.length == 2) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			return possibleNextArgs;
		} else if(leadingArgs.length == 3 || leadingArgs.length == 4) {
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(Long.class);
			return possibleNextArgs;
		} else if(leadingArgs.length == 6) {
			possibleNextArgs.add(Boolean.class);
			return possibleNextArgs;
		}
		
		possibleNextArgs.add(List.class);
		
		if(leadingArgs.length > MINIMUM_NUM_ARGUMENTS-1)
			possibleNextArgs.add(null);

		return possibleNextArgs;
	}
}
