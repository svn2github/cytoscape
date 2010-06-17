package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionError;
import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.GeographicalArea;
import com.googlecode.charts4j.MapChart;

public class GoogleMapFunction implements Function {

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GMAP";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a Map URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"" + "GMAP(region(String), country(String), Data(List of String), [extra args(String)])\"";
	}

	public Object evaluateFunction(Object[] args) throws FunctionError {
		final String region = args[0].toString();
		final String countryCode = args[1].toString();
		final List<Double> data = (List<Double>) args[2];
		int dataLength = data.size();
		
		
		final MapChart chart = GCharts.newMapChart(GeographicalArea.valueOf(region));
		return chart.toURLString();
	}

	public List<Class> getPossibleArgTypes(Class[] args) {
		if (args.length == 4)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();

		if (args.length == 0 || args.length == 1) {
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}
		
		if(args.length == 2) {
			possibleNextArgs.add(List.class);
			return possibleNextArgs;
		}
		
		if(args.length == 3) {
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}

		return possibleNextArgs;

	}

	/**
	 * Always return URL string for Google Chart API.
	 */
	public Class<?> getReturnType() {
		return String.class;
	}

	public Class validateArgTypes(Class[] args) {
		if (args.length < 3)
			return null;
		
		if (args[0] != String.class)
			return null;
		
		if (args[0] != String.class)
			return null;
		
		if (!FunctionUtil.isSomeKindOfList(args[2]))
			return null;
		
		if(args.length == 3)
			return String.class;
		
		if (args[3] != String.class)
			return null;
		
		return String.class;
	}

}
