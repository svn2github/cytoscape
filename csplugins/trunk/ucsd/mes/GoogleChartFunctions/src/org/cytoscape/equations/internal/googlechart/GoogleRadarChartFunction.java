package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.RadarChart;
import com.googlecode.charts4j.RadarPlot;

public class GoogleRadarChartFunction extends AbstractGoogleChartFunction {

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GRADARCHART";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a multiple-line radar chart URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \""
				+ "GRADARCHART(min(Number),max(Number),list of data 1(List of Doubles),..., list of data n, "
				+ "[title(String)[,width(Number)[,height(Number)[,extra arguments(String)]]])\"";
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		extractCommonSettings(args);

		final List<RadarPlot> plots = new ArrayList<RadarPlot>();
		int colorIndex = 0;
		for (final List<Double> data : dataSets) {
			final RadarPlot plot = Plots.newRadarPlot(com.googlecode.charts4j.DataUtil
					.scaleWithinRange(min, max, data), colors.get(colorIndex));
			plot.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			// line.addShapeMarkers(Shape.CIRCLE, Color.BLACK, 5);
			plots.add(plot);
			colorIndex++;
		}

		// Defining chart.
		chart = GCharts.newRadarChart(plots);
		this.applyAppearences();

//		((RadarChart)chart).addRadialAxisRangeMarker(0, 100, Color.DARKGRAY);
//		((RadarChart)chart).addConcentricAxisRangeMarker(0, 100, Color.CYAN);
		if (title.trim().length() != 0)
			((RadarChart) chart).setTitle(title);

		// Setup Axis label
		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.DARKGRAY, 12,
				AxisTextAlignment.CENTER);

		final Double center = (Math.abs(max) - Math.abs(min)) / 2;
		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(min.toString(),
				center.toString(), max.toString());
		yAxis.setAxisStyle(axisStyle);

		((RadarChart) chart).addConcentricAxisLabels(yAxis);
		final int dataLen = dataSets.get(0).size();
		final List<String> labels = new ArrayList<String>();
		for(int i=0; i<dataLen; i++) {
			labels.add(Integer.toString(i+1));
		}
		((RadarChart) chart).addRadialAxisLabels(AxisLabelsFactory.newRadialAxisLabels(labels));
		return chart.toURLString() + extraArgs;

		// return chart.toURLString().replace("cht=lc", "cht=ls") + extraArgs;
	}
}
