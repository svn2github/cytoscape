package org.cytoscape.equations.internal.googlechart;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.GChart;

public abstract class AbstractGoogleChartFunction implements Function {
	
	protected GChart chart;
	
	// Common parameters for chart appearence
	protected String title = "";
	
	protected int width = 200;
	protected int height = 200;
	
	protected Double min = 0d;
	protected Double max = 100d;
	
	protected String extraArgs = "";
	
	/**
	 * Always return URL string for Google Chart API.
	 */
	public Class<?> getReturnType() {
		return String.class;
	}
	
	
	// Setup common properties.
	protected void extractCommonSettings(final Object[] args) {
		
		// Extract data from list of Strings
		title = FunctionUtil.getArgAsString(args[0]);
		// Get chart dim.
		width = ((Double)FunctionUtil.getArgAsDouble(args[1])).intValue();
		height = ((Double)FunctionUtil.getArgAsDouble(args[2])).intValue();
		
		//Get value range
		min = FunctionUtil.getArgAsDouble(args[3]);
		max = FunctionUtil.getArgAsDouble(args[4]);
		
		// Extra parameter for the final URL
		extraArgs = FunctionUtil.getArgAsString(args[5]);
		
	}
	
	protected void applyAppearences() {
		if(chart == null)
			throw new IllegalStateException("GChart object should be initialized before calling this method.");
		
		chart.setSize(width, height);
		chart.setMargins(10, 10, 10, 10);
	}

}
