/*
  File: Equation.java

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
package org.cytoscape.equations;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Equation {
	private final String equation;
	private final Set<String> attribReferences;
	private final Object[] code;
	private final int[] sourceLocations;
	private final Class type;

	/**
	 *  @param equation          the string representing this equation
	 *  @param attribReferences  other attributes that are referenced by this equation
	 *  @param code              the instruction sequence representing the compiled equation
	 *  @param type              the type of the equation, String.class, Boolean.class or Double.class
	 */
	Equation(final String equation, final Set<String> attribReferences, final Object[] code,
	         final int[] sourceLocations, final Class type)
	{
		this.equation         = equation;
		this.attribReferences = attribReferences;
		this.code             = code;
		this.sourceLocations  = sourceLocations;
		this.type             = type;
	}

	@Override public String toString() { return equation; }

	@Override public boolean equals(final Object other) {
		if (other.getClass() != Equation.class)
			return false;

		final Equation otherEquation = (Equation)other;
		return equation.equals(otherEquation.equation);
	}

	public Set<String> getAttribReferences() { return attribReferences; }
	public Object[] getCode() { return code; }
	public int[] getSourceLocations() { return sourceLocations; }
	public Class getType() { return type; }

	/**
	 *  A factory method that returns an Equation that always fails at runtime.
	 *
	 *  @param equation      an arbitrary string that is usually a syntactically invalid equation
	 *  @param errorMessage  the runtime error message that the returned equation will produce
	 */
	public static Equation getErrorEquation(final String equation, final String errorMessage) {
		final EqnCompiler compiler = new EqnCompiler();
		final Map<String, Class> attribNameToTypeMap = new HashMap<String, Class>();
		if (!compiler.compile("=ERROR(\"" + errorMessage + "\")", attribNameToTypeMap))
			throw new IllegalStateException("internal error in Equation.getErrorEquation().  This should *never* happen!");

		final Equation errorEquation = compiler.getEquation();

		return new Equation(equation, errorEquation.attribReferences, errorEquation.code,
		                    errorEquation.sourceLocations, errorEquation.type);
	}
}
