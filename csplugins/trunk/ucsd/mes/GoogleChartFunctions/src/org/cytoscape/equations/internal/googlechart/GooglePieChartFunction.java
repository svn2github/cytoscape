package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;

public class GooglePieChartFunction extends AbstractGoogleChartFunction {

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GPIECHART";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a pie chart URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \""
				+ "GPIECHART(min(Number),max(Number),list of data 1(List of Doubles),..., list of data n, "
				+ "[title(String)[,width(Number)[,height(Number)[,extra arguments(String)]]])\"";
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		extractCommonSettings(args);

		final List<Slice> slices = new ArrayList<Slice>();
		// for (final List<Double> data : dataSets) {
		for (Double val : dataSets.get(0)) {
			final Slice slice = Slice.newSlice(val.intValue(),
					"test");
			
			slices.add(slice);
		}
		// }

		// Defining chart.
		chart = GCharts.newPieChart(slices);
		this.applyAppearences();
		chart.setSize(350, 200);

		// ((RadarChart)chart).addRadialAxisRangeMarker(0, 100, Color.DARKGRAY);
		// ((RadarChart)chart).addConcentricAxisRangeMarker(0, 100, Color.CYAN);
		if (title.trim().length() != 0)
			((PieChart) chart).setTitle(title);

		// Setup Axis label
//		AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.DARKGRAY, 12,
//				AxisTextAlignment.CENTER);
//
//		final Double center = (Math.abs(max) - Math.abs(min)) / 2;
//		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(min.toString(),
//				center.toString(), max.toString());
//		yAxis.setAxisStyle(axisStyle);
//
//		((RadarChart) chart).addConcentricAxisLabels(yAxis);
//		final int dataLen = dataSets.get(0).size();
//		final List<String> labels = new ArrayList<String>();
//		for (int i = 0; i < dataLen; i++) {
//			labels.add(Integer.toString(i + 1));
//		}
//		((RadarChart) chart).addRadialAxisLabels(AxisLabelsFactory
//				.newRadialAxisLabels(labels));
		return chart.toURLString() + extraArgs;

		// return chart.toURLString().replace("cht=lc", "cht=ls") + extraArgs;
	}
}
