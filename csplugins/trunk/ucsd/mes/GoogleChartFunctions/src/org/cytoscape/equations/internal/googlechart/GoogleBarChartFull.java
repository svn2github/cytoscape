package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.AbstractGraphChart;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Plots;

public class GoogleBarChartFull extends AbstractGoogleChartFunction {

	private static final int BAR_SPACE = 1;
	private static final int BAR_GROUP_SPACE = 5;

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
		return "GBARCHARTFULL";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a bar chart URL based on the supplied arguments.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"GBARCHARTFULL(title, width, height, min, max, extra arguments, space betweeb bars, list of data 1, ... , list of data n)\"";
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
		for (int i = 1; i < 5; i++) {
			if ((argTypes[i] != Long.class) && argTypes[i] != Double.class)
				return null;
		}

		if ((argTypes[6] != Long.class) && argTypes[6] != Double.class)
			return null;

		for (int i = MINIMUM_NUM_ARGUMENTS - 1; i < argTypes.length; i++) {
			if (!FunctionUtil.isSomeKindOfList(argTypes[i]))
				return null;
		}

		return String.class;
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		extractCommonSettings(args);
		// Line-chart specific settings
		final int spaceBetweenBars = ((Double) FunctionUtil
				.getArgAsDouble(args[6])).intValue();

		// Get actual data sets
		final List<List<Double>> dataSets = new ArrayList<List<Double>>();
		for (int i = MINIMUM_NUM_ARGUMENTS - 1; i < args.length; i++)
			dataSets.add((List<Double>) args[i]);

		if (colors == null)
			colors = ColorUtil.getColors(args.length - MINIMUM_NUM_ARGUMENTS
					+ 1);

		final List<BarChartPlot> bars = new ArrayList<BarChartPlot>();

		int colorIndex = 0;
		int groupCount = dataSets.size();
		int dataCount = dataSets.get(0).size();
		for (final List<Double> data : dataSets) {
			final BarChartPlot bar = Plots.newBarChartPlot(
					com.googlecode.charts4j.DataUtil.scaleWithinRange(min, max,
							data), colors.get(colorIndex));
			bar.setZeroLine(50);
			bars.add(bar);
			colorIndex++;
		}

		// Defining chart.
		chart = GCharts.newBarChart(bars);
		this.applyAppearences();

		((BarChart) chart).setSpaceWithinGroupsOfBars(spaceBetweenBars);
		((BarChart) chart).setSpaceBetweenGroupsOfBars(BAR_GROUP_SPACE);
		((BarChart) chart).setBarWidth(BarChart.AUTO_RESIZE);

		if (title.trim().length() != 0)
			((AbstractGraphChart) chart).setTitle(title);
		//
		// if(axis) {
		// // Setup Axis label
		// AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.DARKGRAY, 12,
		// AxisTextAlignment.CENTER);
		//
		// final Double center = (Math.abs(max)-Math.abs(min))/2;
		// AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(min.toString(),
		// center.toString(), max.toString());
		// yAxis.setAxisStyle(axisStyle);
		//
		// ((AbstractAxisChart) chart).addYAxisLabels(yAxis);
		// return chart.toURLString() + extraArgs;
		// } else
		return chart.toURLString() + extraArgs;
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
		} else if (leadingArgs.length == 1 || leadingArgs.length == 2) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			return possibleNextArgs;
		} else if (leadingArgs.length == 3 || leadingArgs.length == 4) {
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(Long.class);
			return possibleNextArgs;
		} else if (leadingArgs.length == 6) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			return possibleNextArgs;
		}

		possibleNextArgs.add(List.class);

		if (leadingArgs.length > MINIMUM_NUM_ARGUMENTS - 1)
			possibleNextArgs.add(null);

		return possibleNextArgs;
	}
}
