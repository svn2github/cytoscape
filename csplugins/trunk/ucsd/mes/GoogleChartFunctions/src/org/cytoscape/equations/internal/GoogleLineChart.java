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
package org.cytoscape.equations.internal;


import java.util.ArrayList;
import java.util.List;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;
import cytoscape.util.ProbabilityScaler;
import cytoscape.util.ScalingMethod;

import static com.googlecode.charts4j.Color.*;
import com.googlecode.charts4j.*;

public class GoogleLineChart implements Function {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "GLINECHART"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Creates a line chart URL based on the supplied arguments."; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"GLINECHART(title,list)\""; }

	public Class getReturnType() { return String.class; }

	/**
	 *  @return String.class or null if the args passed in have the wrong arity or a type mismatch was found
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 2)
			return null;

		if ( argTypes[0] != String.class )
			return null;

		if ( !FunctionUtil.isSomeKindOfList(argTypes[1]) )
			return null;

		return String.class;
	}

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @return the result of the function evaluation which is the natural logarithm of the first argument
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Boolean
	 */
	private double[] extractDoubles(Object o) {
		List<Double> ld = (List<Double>)o;

		float[] data = new float[ld.size()];
		int i = 0;
		for ( Double d : ld )
			data[i++] = d.floatValue();

		float[] data2 = ProbabilityScaler.scale((float[])data,ScalingMethod.LINEAR_LOWER,new StringBuilder());
		double[] ret = new double[data2.length];
		i = 0;
		for (float f : data2)
			ret[i++] = 100.0 * f;  // scaled by 100 for Google

		return ret; 
	}

	public Object evaluateFunction(final Object[] args) 
		throws IllegalArgumentException, ArithmeticException {
		final String title = FunctionUtil.getArgAsString(args[0]);
		final double[] data1 = extractDoubles( args[1] );

        Line line1 = Plots.newLine(Data.newData(data1), Color.RED);
        line1.setLineStyle(LineStyle.newLineStyle(5, 1, 0));
        line1.addShapeMarkers(Shape.DIAMOND, Color.BLACK, 10);

        // Defining chart.
        LineChart chart = GCharts.newLineChart(line1);
        chart.setSize(200, 200);
        chart.setTitle(title, BLACK, 14);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 12, AxisTextAlignment.CENTER);

        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("a", "b", "c", "d", "e", "f", "g");
        xAxis.setAxisStyle(axisStyle);
        chart.addXAxisLabels(xAxis);

        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("0", "25", "50", "75", "100");
        yAxis.setAxisStyle(axisStyle);
        chart.addYAxisLabels(yAxis);

		return chart.toURLString(); 
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @param leadingArgs the types of the arguments that have already been selected by the user.
	 *  @return the set of arguments (must be a collection of String.class, Long.class, Double.class,
	 *           Boolean.class and List.class) that are candidates for the next argument.  An empty
	 *           set indicates that no further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length == 2)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();

		if (leadingArgs.length == 0) {
			possibleNextArgs.add(String.class);
		} else if (leadingArgs.length == 1) {
			possibleNextArgs.add(List.class);
		}

		return possibleNextArgs;
	}
}
