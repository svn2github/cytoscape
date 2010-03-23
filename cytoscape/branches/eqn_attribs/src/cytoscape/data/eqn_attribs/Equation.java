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
package cytoscape.data.eqn_attribs;


import java.util.Set;


public class Equation {
	final String equation;
	final Set<String> attribReferences;
	final Object[] code;
	final Class type;

	/**
	 *  @param equation          the string representing this equation
	 *  @param attribReferences  other attributes that are referenced by this equation
	 *  @param code              the instruction sequence representing the compiled equation
	 *  @param type              the type of the equation, String.class, Boolean.class or Double.class
	 */
	Equation(final String equation, final Set<String> attribReferences, final Object[] code, final Class type) {
		this.equation         = equation;
		this.attribReferences = attribReferences;
		this.code             = code;
		this.type             = type;
	}

	@Override public String toString() { return equation; }
	public Set<String> getAttribReferences() { return attribReferences; }
	public Object[] getCode() { return code; }
	public Class getType() { return type; }
}
