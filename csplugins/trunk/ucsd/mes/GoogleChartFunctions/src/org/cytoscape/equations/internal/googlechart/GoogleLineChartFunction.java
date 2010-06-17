package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

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
 * <h2>Arguments</h2> <h3>Common Parameters</h3>
 * <ol>
 * <li>title(String): title of chart</li>
 * <li>width(Integer): title of chart</li>
 * <li>height(Integer): title of chart</li>
 * <li>min(Double): title of chart</li>
 * <li>max(D): title of chart</li>
 * <li>extra(String): any extra argument for the URL</li>
 * </ol>
 * 
 * <h3>Line-Chart only parameters</h3>
 * <ol>
 * <li>axis(Boolean): show axis or not</li>
 * </ol>
 * 
 * @author kono
 * 
 */
public class GoogleLineChartFunction extends AbstractGoogleChartFunction {

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GLINECHART";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a multiple-line chart URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"" + 
			"GLINECHART(min(Number),max(Number),list of data 1(List of Doubles),..., list of data n, " + 
			"[title(String)[,width(Number)[,height(Number)[,extra arguments(String)]]])\"";
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		extractCommonSettings(args);

		final List<Line> lines = new ArrayList<Line>();
		int colorIndex = 0;
		for (final List<Double> data : dataSets) {
			final Line line = Plots.newLine(com.googlecode.charts4j.DataUtil
					.scaleWithinRange(min, max, data), colors.get(colorIndex));
			line.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			// line.addShapeMarkers(Shape.CIRCLE, Color.BLACK, 5);
			lines.add(line);
			colorIndex++;
		}

		// Defining chart.
		chart = GCharts.newLineChart(lines);
		this.applyAppearences();

		if (title.trim().length() != 0)
			((AbstractGraphChart) chart).setTitle(title);

		// Setup Axis label
		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.DARKGRAY, 12,
				AxisTextAlignment.CENTER);

		final Double center = (Math.abs(max) - Math.abs(min)) / 2;
		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(min.toString(),
				center.toString(), max.toString());
		yAxis.setAxisStyle(axisStyle);

		((AbstractAxisChart) chart).addYAxisLabels(yAxis);
		return chart.toURLString() + extraArgs;

		// return chart.toURLString().replace("cht=lc", "cht=ls") + extraArgs;
	}
}
