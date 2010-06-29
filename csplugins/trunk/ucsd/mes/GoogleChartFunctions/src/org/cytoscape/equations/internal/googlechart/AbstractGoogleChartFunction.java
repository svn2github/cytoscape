package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;
import org.cytoscape.equations.internal.googlechart.util.ColorUtil;

import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.GChart;

public abstract class AbstractGoogleChartFunction implements Function {
	
	protected static final int MINIMUM_NUM_ARGUMENTS = 3;

	protected GChart chart;
	
	protected List<Color> colors = null;
	
	// Common parameters for chart appearence
	protected String title = "";
	
	protected int width = 200;
	protected int height = 200;
	
	protected Double min = 0d;
	protected Double max = 100d;
	
	protected String extraArgs = "";
	
	protected List<List<Double>> dataSets;
	
	/**
	 * Always return URL string for Google Chart API.
	 */
	public Class<?> getReturnType() {
		return String.class;
	}
	
	
	// Setup common properties.
	protected void extractCommonSettings(final Object[] args) {
		//Get value range
		min = FunctionUtil.getArgAsDouble(args[0]);
		max = FunctionUtil.getArgAsDouble(args[1]);
		
		// Get actual data sets
		dataSets = new ArrayList<List<Double>>();
		int i = MINIMUM_NUM_ARGUMENTS-1;
		final int argLength = args.length;
		for(; i<argLength; i++) {
			if(args[i] instanceof List == false)
				break;
			dataSets.add((List<Double>) args[i]);
		}
		
		if (i<argLength)
			title = args[i].toString();
		i++;
		if (i<argLength)
			width = ((Double)FunctionUtil.getArgAsDouble(args[i])).intValue();
		i++;
		if (i<argLength)
			height = ((Double)FunctionUtil.getArgAsDouble(args[i])).intValue();
		i++;
		if (i<argLength)
			extraArgs = args[i].toString();
		
		if (colors == null)
			colors = ColorUtil.getColors(args.length - MINIMUM_NUM_ARGUMENTS + 1);
	}
	
	/**
	 * @return String.class or null if the args passed in have the wrong arity
	 *         or a type mismatch was found
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length < MINIMUM_NUM_ARGUMENTS)
			return null;
		
		// min and max
		if (argTypes[0] != Long.class && argTypes[0] != Double.class &&
				argTypes[1] != Long.class && argTypes[1] != Double.class)
			return null;
		
		// First list
		if (!FunctionUtil.isSomeKindOfList(argTypes[2]))
			return null;
		// Only one List data
		if (MINIMUM_NUM_ARGUMENTS == argTypes.length)
			return String.class;
		
		int nextArg = MINIMUM_NUM_ARGUMENTS;
		int argLen = argTypes.length;
		while(FunctionUtil.isSomeKindOfList(argTypes[nextArg])) {
			nextArg++;
			if (nextArg == argLen)
				return String.class;
		}
		
		// Title and extra arguments for query URL
		if (argTypes[nextArg] != String.class)
			return null;
		nextArg++;
		if (nextArg == argTypes.length)
			return String.class;
		
		// Check width option
		if (argTypes[nextArg] != Long.class && argTypes[nextArg] != Double.class)
			return null;
		nextArg++;
		if (nextArg == argTypes.length)
			return String.class;
		
		// Check height option
		if (argTypes[nextArg] != Long.class && argTypes[nextArg] != Double.class)
			return null;
		nextArg++;
		if (nextArg == argTypes.length)
			return String.class;
		
		// Check optional arguments
		if (argTypes[nextArg] != String.class)
			return null;
		
		return String.class;
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
		
		if (leadingArgs.length == 0 || leadingArgs.length == 1) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			return possibleNextArgs;
		}
		if (leadingArgs.length == 2) {
			possibleNextArgs.add(List.class);
			return possibleNextArgs;
		}
			
		if (leadingArgs[leadingArgs.length - 1] == List.class) {
			possibleNextArgs.add(List.class);
			possibleNextArgs.add(String.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}
		
		// Prev. arg was title or extra
		if (leadingArgs[leadingArgs.length - 1] == String.class) {
			if (leadingArgs[leadingArgs.length - 2] == List.class) { // Title
				possibleNextArgs.add(Long.class);
				possibleNextArgs.add(Double.class);
				possibleNextArgs.add(null);
				return possibleNextArgs;
			} else { // extra
				return null;
			}
		}

		// Prev. arg was height
		if (leadingArgs.length >= 6
		    && (leadingArgs[leadingArgs.length - 1] == Double.class || leadingArgs[leadingArgs.length - 1] == Long.class)
		    && (leadingArgs[leadingArgs.length - 2] == Double.class || leadingArgs[leadingArgs.length - 2] == Long.class)
		    && leadingArgs[leadingArgs.length - 3] == String.class)
		{
			possibleNextArgs.add(String.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}
		
		// Prev. arg was width
		if (leadingArgs[leadingArgs.length - 1] == Double.class || leadingArgs[leadingArgs.length - 1] == Long.class) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}

		return possibleNextArgs;
	}
	
	protected void applyAppearences() {
		if (chart == null)
			throw new IllegalStateException("GChart object should be initialized before calling this method.");
		
		chart.setSize(width, height);
		chart.setMargins(10, 10, 10, 10);
	}

}
