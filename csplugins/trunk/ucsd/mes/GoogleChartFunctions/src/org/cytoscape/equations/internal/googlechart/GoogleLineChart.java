/*
  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.equations.internal.googlechart;

import static com.googlecode.charts4j.Color.BLACK;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

class GoogleLineChart implements Function {

	private static final Color DEF_LINE_COLOR = Color.RED;

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
		return "Creates a line chart URL based on the supplied arguments.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"GLINECHART(title,list of numbers)\"";
	}

	public Class<?> getReturnType() {
		return String.class;
	}

	/**
	 * @return String.class or null if the args passed in have the wrong arity
	 *         or a type mismatch was found
	 */
	public Class<?> validateArgTypes(final Class[] argTypes) {
		if (argTypes.length < 2)
			return null;

		if (argTypes[0] != String.class)
			return null;

		for (int i = 1; i < argTypes.length; i++) {
			if (!FunctionUtil.isSomeKindOfList(argTypes[i]))
				return null;
		}

		return String.class;
	}

	public Object evaluateFunction(final Object[] args)
			throws IllegalArgumentException, ArithmeticException {

		final String title = FunctionUtil.getArgAsString(args[0]);
		final double[] data1 = DataUtil.extractDoubles(args[1]);

		final Line line1 = Plots.newLine(Data.newData(data1), DEF_LINE_COLOR);
		line1.setLineStyle(LineStyle.newLineStyle(5, 1, 0));
		line1.addShapeMarkers(Shape.DIAMOND, Color.BLACK, 10);

		// Defining chart.
		LineChart chart = GCharts.newLineChart(line1);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 12,
				AxisTextAlignment.CENTER);

		AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("a", "b", "c", "d",
				"e", "f", "g");
		xAxis.setAxisStyle(axisStyle);
		chart.addXAxisLabels(xAxis);

		AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("0", "25", "50",
				"75", "100");
		yAxis.setAxisStyle(axisStyle);
		chart.addYAxisLabels(yAxis);

		return chart.toURLString();
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
		if (leadingArgs.length == 2)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();

		if (leadingArgs.length == 0) {
			possibleNextArgs.add(String.class);
		}

		possibleNextArgs.add(List.class);

		if (leadingArgs.length > 1)
			possibleNextArgs.add(null);
		
		return possibleNextArgs;
	}
}
