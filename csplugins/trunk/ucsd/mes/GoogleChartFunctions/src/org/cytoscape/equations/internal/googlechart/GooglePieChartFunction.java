package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.FunctionError;
import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Slice;

/**
 * Pie chart geenrator. Currently supports only one list of data.
 * 
 * @author kono
 * 
 */
public class GooglePieChartFunction extends AbstractGoogleChartFunction {

	private static final String TRANSPARENT_BG = "&chf=bg,s,0000FF00";
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
				+ "GPIECHART(list of labels (List of String), list of data (percentage, List of Doubles), "
				+ "[title(String)[,width(Number)[,height(Number)[,extra arguments(String)]]])\"";
	}

	public Object evaluateFunction(final Object[] args)
			throws FunctionError {

		final List<String> labels = (List<String>) args[0];
		final List<Double> data = (List<Double>) args[1];
		
		// Adjust default value
		width = height*2;
		
		final int argLength = args.length;
		int i = 2;
		if (i < argLength)
			title = args[i].toString();
		i++;
		if (i < argLength)
			width = ((Double) FunctionUtil.getArgAsDouble(args[i])).intValue();
		i++;
		if (i < argLength)
			height = ((Double) FunctionUtil.getArgAsDouble(args[i])).intValue();
		i++;
		if (i < argLength)
			extraArgs = args[i].toString();

		// Defining chart.
		chart = validate(labels, data);
		this.applyAppearences();

		if (title.trim().length() != 0)
			((PieChart) chart).setTitle(title);

		return chart.toURLString() + TRANSPARENT_BG + extraArgs;
	}

	private PieChart validate(final List<String> labels, final List<Double> data) throws FunctionError {
		if(labels.size() == 0) {
			throw new FunctionError("Label list is empty.", 1);
		}
		
		if(data.size() == 0) {
			throw new FunctionError("Data list is empty.", 2);
		}
		
		if(data.size() != labels.size()) {
			throw new FunctionError("Number of data is not equal to number of labels. " +
					"(labels, data) = (" + labels.size() + ", " + data.size() + ")", 3);
		}
		
		final List<Slice> slices = new ArrayList<Slice>();
		int idx = 0;
		for (Double val : data) {
			slices.add(Slice.newSlice(val.intValue(), labels.get(idx)));
			idx++;
		}
		return GCharts.newPieChart(slices);
	}

	/**
	 * @return String.class or null if the args passed in have the wrong arity
	 *         or a type mismatch was found
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Class validateArgTypes(final Class[] argTypes) {
		// Two lists is the minimum required arguments
		if (argTypes.length < 2)
			return null;

		// Labels and percentages
		if (!FunctionUtil.isSomeKindOfList(argTypes[0])
				|| !FunctionUtil.isSomeKindOfList(argTypes[1]))
			return null;

		// Contains required parameters only.
		if (argTypes.length == 2)
			return String.class;

		int nextArg = MINIMUM_NUM_ARGUMENTS;
		int argLen = argTypes.length;
		while (FunctionUtil.isSomeKindOfList(argTypes[nextArg])) {
			nextArg++;
			if (nextArg == argLen)
				return String.class;
		}

		// Title and extra arguments for query URL
		if (argTypes[2] != String.class)
			return null;

		if (argTypes.length == 3)
			return String.class;

		// Check width option
		if (argTypes[3] != Long.class && argTypes[3] != Double.class)
			return null;

		if (argTypes.length == 4)
			return String.class;

		// Check height option
		if (argTypes[4] != Long.class && argTypes[4] != Double.class)
			return null;

		if (argTypes.length == 5)
			return String.class;

		// Check optional arguments
		if (argTypes[5] != String.class)
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
	@SuppressWarnings("unchecked")
	@Override
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		final List<Class> possibleNextArgs = new ArrayList<Class>();

		// First two arguments are lists.
		if (leadingArgs.length == 0 || leadingArgs.length == 1) {
			possibleNextArgs.add(List.class);
			return possibleNextArgs;
		}

		// Option: title
		if (leadingArgs.length == 2) {
			possibleNextArgs.add(String.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}

		// Option: width and height
		if (leadingArgs.length == 3 || leadingArgs.length == 4) {
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}

		// Option: extra args.
		if (leadingArgs.length == 5) {
			possibleNextArgs.add(String.class);
			possibleNextArgs.add(null);
			return possibleNextArgs;
		}

		return possibleNextArgs;
	}
}
