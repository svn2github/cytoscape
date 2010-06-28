package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.charts4j.AbstractGraphChart;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Plots;

public class GoogleBarChartFunction extends AbstractGoogleChartFunction {

	private static final int BAR_SPACE = 1;
	private static final int BAR_GROUP_SPACE = 5;

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GBARCHART";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a multiple-bar chart URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \""
				+ "GBARCHART(min(Number),max(Number),list of data 1(List of Doubles),..., list of data n, "
				+ "[title(String)[,width(Number)[,height(Number)[,extra arguments(String)]]])\"";
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		extractCommonSettings(args);

		final List<BarChartPlot> bars = new ArrayList<BarChartPlot>();

		int colorIndex = 0;
		for (final List<Double> data : dataSets) {
			final BarChartPlot bar = Plots.newBarChartPlot(
					com.googlecode.charts4j.DataUtil.scaleWithinRange(min, max,
							data), colors.get(colorIndex));
			if(min<0 && max>=0)
				bar.setZeroLine(50);
			else
				bar.setZeroLine(0);
			bars.add(bar);
			colorIndex++;
		}

		// Defining chart.
		chart = GCharts.newBarChart(bars);
		this.applyAppearences();

		((BarChart) chart).setSpaceWithinGroupsOfBars(BAR_SPACE);
		((BarChart) chart).setSpaceBetweenGroupsOfBars(BAR_GROUP_SPACE);
		((BarChart) chart).setBarWidth(BarChart.AUTO_RESIZE);

		if (title.trim().length() != 0)
			((AbstractGraphChart) chart).setTitle(title);
		
		return chart.toURLString() + extraArgs;
	}
}
