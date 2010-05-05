package cytoscape.tutorial23;


import java.util.ArrayList;
import java.util.List;
import cytoscape.data.eqn_attribs.AttribFunction;


public class IXor implements AttribFunction {
	/**
	 *  Used to parse the function string.  This name is treated in a case-insensitive manner!
	 *  @returns the name by which you must call the function when used in an attribute equation.
	 */
	public String getName() { return "IXOR"; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of what this function does
	 */
	public String getFunctionSummary() { return "Returns an integer value that is the exclusive-or of 2 other integer values."; }

	/**
	 *  Used to provide help for users.
	 *  @returns a description of how to use this function
	 */
	public String getUsageDescription() { return "Call this with \"IXOR(integer1,integer2)\""; }

	public Class getReturnType() { return Long.class; }

	/**
	 *  @returns Long.class or null if there are not exactly 2 args or the args are not both of type Long
	 */
	public Class validateArgTypes(final Class[] argTypes) {
		if (argTypes.length != 2 || argTypes[0] != Long.class || argTypes[1] != Long.class)
			return null;

		return Long.class;
	}

	/**
	 *  @param args the function arguments which must be two objects of type Long
	 *  @returns the result of the function evaluation which is the exclusive-or of the bits of the 2 arguments
	 */
	public Object evaluateFunction(final Object[] args) {
		final long result = (Long)args[0] ^ (Long)args[1];
		return (Long)result;
	}

	/**
	 *  Used with the equation builder.
	 *
	 *  @params leadingArgs the types of the arguments that have already been selected by the user.
	 *  @returns the set of arguments (must be a collection of String.class, Long.class, Double.class,
	 *           Boolean.class and List.class) that are candidates for the next argument.  An empty set
	 *           indicates that no further arguments are valid.
	 */
	public List<Class> getPossibleArgTypes(final Class[] leadingArgs) {
		if (leadingArgs.length < 2) {
			final List<Class> possibleNextArgs = new ArrayList<Class>();
			possibleNextArgs.add(Long.class);
			return possibleNextArgs;
		}

		return null;
	}
}
