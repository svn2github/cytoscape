/*
  File: NormDist.java

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
package org.cytoscape.equations.builtins;


import java.util.ArrayList;
import java.util.List;
import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionUtil;


public class NormDist implements Function {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "NORMDIST"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns the Normal Probability Density Function or the Cumulative Normal Distribution Function."; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"NORMDIST(x,mean,stddev,cumulative?)\""; }

	public Class getReturnType() { return Double.class; }

	/**
	 *  @return Double.class or null if there are 2 args or the args are not of type Double, Long, Boolean or String
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 4)
			return null;

		for (final Class argType : argTypes) {
			if (argType != Double.class && argType != Long.class && argType != String.class && argType != Boolean.class)
				return null;
		}

		return Double.class;
	}

	/**
	 *  @param args the function arguments which must be either one or two objects of type Double
	 *  @return the result of the function evaluation which is the logarithm of the first argument
	 *  @throws ArithmeticException 
	 *  @throws IllegalArgumentException thrown if any of the arguments is not of type Double
	 */
	public Object evaluateFunction(final Object[] args) throws IllegalArgumentException, ArithmeticException {
		final double x = FunctionUtil.getArgAsDouble(args[0]);
		final double mu = FunctionUtil.getArgAsDouble(args[1]);
		final double sigma = FunctionUtil.getArgAsDouble(args[2]);
		if (sigma <= 0)
			throw new IllegalArgumentException("mean parameter in call to NORMDIST must be nonnegative!");
		final boolean cumulative = FunctionUtil.getArgAsBoolean(args[3]);

		if (cumulative) {
			final double z = (x - mu) / sigma;
			return (Double)cdf(z);
		}
		else
			return (Double)pdf(x, mu, sigma);
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
		if (leadingArgs.length < 4) {
			final List<Class> possibleNextArgs = new ArrayList<Class>();
			possibleNextArgs.add(Double.class);
			possibleNextArgs.add(Long.class);
			possibleNextArgs.add(Boolean.class);
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}

		return null;
	}

	/**
	 *  @return the pdf of a normal distribution with mean "mu" and std. dev. "sigma"
	 */
	private double pdf(final double x, final double mu, final double sigma) {
		final double z = (x - mu) / sigma;
		return Math.exp(- 0.5 * z * z) / (Math.sqrt(2.0 * Math.PI) * sigma);
	}

	/**
	 *  Calculates an approximation to the CDF of the standard normal distribution
	 *  based on "BETTER APPROXIMATIONS TO CUMULATIVE NORMAL FUNCTIONS" by Graeme West.
	 *
	 *  @return the CDF of the standard normal distribution
	 */
	private double cdf(final double z) {
		double cumulativeNorm;
		final double zAbs = Math.abs(z);
		if (zAbs > 37.0)
			cumulativeNorm = 0.0;
		else {
			final double Exponential = Math.exp(-zAbs * zAbs / 2.0);
			if (zAbs < 7.07106781186547) {
				double build = 3.52624965998911E-02 * zAbs + 0.700383064443688;
				build = build * zAbs + 6.37396220353165;
				build = build * zAbs + 33.912866078383;
				build = build * zAbs + 112.079291497871;
				build = build * zAbs + 221.213596169931;
				build = build * zAbs + 220.206867912376;
				cumulativeNorm = Exponential * build;
				build = 8.83883476483184E-02 * zAbs + 1.75566716318264;
				build = build * zAbs + 16.064177579207;
				build = build * zAbs + 86.7807322029461;
				build = build * zAbs + 296.564248779674;
				build = build * zAbs + 637.333633378831;
				build = build * zAbs + 793.826512519948;
				build = build * zAbs + 440.413735824752;
				cumulativeNorm = cumulativeNorm / build;
			} else {
				double build = zAbs + 0.65;
				build = zAbs + 4 / build;
				build = zAbs + 3 / build;
				build = zAbs + 2 / build;
				build = zAbs + 1 / build;
				cumulativeNorm = Exponential / build / 2.506628274631;
			}
		}

		if (z > 0.0)
			cumulativeNorm = 1.0 - cumulativeNorm;

		return cumulativeNorm;
	}
}
