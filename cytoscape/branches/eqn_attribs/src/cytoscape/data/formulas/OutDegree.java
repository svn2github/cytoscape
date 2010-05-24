/*
  File: OutDegree.java

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
package cytoscape.data.formulas;


import java.util.ArrayList;
import java.util.List;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.eqn_attribs.AttribFunction;


public class OutDegree implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @return the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "OUTDEGREE"; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns indegree of a node."; }

	/**
	 *  Used to provide help for users.
	 *  @return a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"OUTDEGREE(node_ID)\""; }

	public Class getReturnType() { return Long.class; }

	/**
	 *  @return String.class or null if there is not exactly 1 arg or the arg is not of type String
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 1 || (argTypes[0] != String.class))
			return null;

		return Long.class;
	}

	/**
	 *  @param args the function arguments which must be either one object of type Double or Long
	 *  @return the result of the function evaluation which is the natural logarithm of the first argument
	 */
	public Object evaluateFunction(final Object[] args) {
		final String nodeID = (String)args[0];

		final CyNetwork currentNetwork = Cytoscape.getCurrentNetwork();
		if (currentNetwork == null)
			return (Long)(-1L);

		final CyNode node = Cytoscape.getCyNode(nodeID);
		if (node == null)
			return (Long)(-1L);
		
		return (Long)(long)currentNetwork.getOutDegree(node, /* count_undirected_edges = */ false);
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
		if (leadingArgs.length == 0) {
			final List<Class> possibleNextArgs = new ArrayList<Class>();
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}

		return null;
	}
}
