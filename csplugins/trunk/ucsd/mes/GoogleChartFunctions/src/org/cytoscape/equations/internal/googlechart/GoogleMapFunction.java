package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionError;
import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Country;
import com.googlecode.charts4j.DataEncoding;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.GeographicalArea;
import com.googlecode.charts4j.MapChart;
import com.googlecode.charts4j.PoliticalBoundary;
import com.googlecode.charts4j.USAState;
import com.googlecode.charts4j.Country.Code;

public class GoogleMapFunction implements Function {
	
	private static final int MINIMUM_NUM_ARGUMENTS = 3;
	private static final Color[] GRADIENT = {Color.WHITE, Color.RED};
	
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
		return "Call this with \"" + "GMAP(region(String), country(List of String), Data(List of String), [extra args(String)])\"";
	}

	public Object evaluateFunction(Object[] args) throws FunctionError {
		final String region = args[0].toString();
		final List<String> countryCodes = (List<String>) args[1];
		final List<Double> data = (List<Double>) args[2];
		String extra = null;
		if(args.length == 4)
			extra = args[3].toString();
		
		final MapChart chart = validate(region, countryCodes, data);
		if(extra != null)
			return chart.toURLString() + extra;
		else
			return chart.toURLString();
	}
	
	private MapChart validate(final String region, final List<String> countryCodes, final List<Double> data) throws FunctionError {
		GeographicalArea area = null;
		try {
			 area = GeographicalArea.valueOf(region.toUpperCase());
		} catch(IllegalArgumentException e) {
			throw new FunctionError("Invalid region code: " + region, 1);
		}
		if(countryCodes.size() == 0) {
			throw new FunctionError("Country code list is empty.", 2);
		}
		
		if(data.size() == 0) {
			throw new FunctionError("Data list is empty.", 3);
		}
		
		if(data.size() != countryCodes.size()) {
			throw new FunctionError("Number of data is not equal to number of countries/states. " +
					"(country, data) = (" + countryCodes.size() + ", " + data.size() + ")", 3);
		}
		
		// Convert country code into enum.
		final List<PoliticalBoundary> pb = new ArrayList<PoliticalBoundary>();
		int idx = 0;
		int value = 0;
		for(final String country: countryCodes) {
			value = data.get(idx).intValue();
			if(value < 0 || value > 100)
				throw new FunctionError("Data contains invalid number.  Should be 0-100: value = " + value, 3);
			
			try {
				Code code = Country.Code.valueOf(country.toUpperCase());
				pb.add(new Country(code, value));
			} catch (IllegalArgumentException e1) {
				try {
					com.googlecode.charts4j.USAState.Code code = USAState.Code.valueOf(country.toUpperCase());
					pb.add(new USAState(code, value));
				} catch (IllegalArgumentException e2) {
					throw new FunctionError("List contains invalid country/state code: " + country, 2);
				}
			}
			idx++;
		}
		
		MapChart chart = GCharts.newMapChart(area);
		chart.addPoliticalBoundaries(pb);
		// Set appearence parameters
		chart.setMargins(10,10,10,10);
		chart.setDataEncoding(DataEncoding.TEXT);
		chart.setColorGradient(Color.WHITE, GRADIENT);
		
		return chart;
	}

	public List<Class> getPossibleArgTypes(Class[] args) {
		final List<Class> possibleNextArgs = new ArrayList<Class>();

		if (args.length == 0) {
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}
		
		if(args.length == 1 || args.length == 2) {
			possibleNextArgs.add(List.class);
			return possibleNextArgs;
		}
		
		if(args.length == 3) {
			possibleNextArgs.add(String.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}
		
		if(args.length == 4)
			return null;

		return possibleNextArgs;

	}

	/**
	 * Always return URL string for Google Chart API.
	 */
	public Class<?> getReturnType() {
		return String.class;
	}

	/**
	 * Validate arguments
	 */
	public Class<?> validateArgTypes(Class[] args) {
		if (args.length != MINIMUM_NUM_ARGUMENTS && args.length != MINIMUM_NUM_ARGUMENTS+1)
			return null;
		
		// Geographical area
		if (args[0] != String.class)
			return null;
		
		// List of countries (or states), and values
		if (!FunctionUtil.isSomeKindOfList(args[1]) || !FunctionUtil.isSomeKindOfList(args[2]))
			return null;
		
		if(args.length == MINIMUM_NUM_ARGUMENTS)
			return String.class;
		
		// Extra arguments
		if (args[3] != String.class)
			return null;
		
		return String.class;
	}
}
