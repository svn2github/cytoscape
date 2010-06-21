package org.cytoscape.equations.internal.googlechart;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.equations.Function;
import org.cytoscape.equations.FunctionError;

/**
 * Generate QR Code for a given string value.
 * 
 * @author kono
 *
 */
public class GoogleQRCodeFunction implements Function {

	private static final String BASE_URL = "http://chart.apis.google.com/chart?cht=qr&chs=200x200&choe=UTF-8&chl=";

	/**
	 * Used to parse the function string. This name is treated in a
	 * case-insensitive manner!
	 * 
	 * @return the name by which you must call the function when used in an
	 *         attribute equation.
	 */
	public String getName() {
		return "GQRCODE";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of what this function does
	 */
	public String getFunctionSummary() {
		return "Creates a QR Code URL for Google Chart API.";
	}

	/**
	 * Used to provide help for users.
	 * 
	 * @return a description of how to use this function
	 */
	public String getUsageDescription() {
		return "Call this with \"" + "GQRCODE(text(String))\"";
	}

	public Object evaluateFunction(Object[] args) throws FunctionError {
		final String text = args[0].toString();
		return BASE_URL + text.replace(" ", "+");
	}

	public List<Class> getPossibleArgTypes(Class[] args) {
		if (args.length == 1)
			return null;

		final List<Class> possibleNextArgs = new ArrayList<Class>();

		if (args.length == 0) {
			possibleNextArgs.add(String.class);
			return possibleNextArgs;
		}

		return possibleNextArgs;

	}

	/**
	 * Always return URL string for Google Chart API.
	 */
	public Class<?> getReturnType() {
		return String.class;
	}

	public Class validateArgTypes(Class[] args) {
		if (args.length != 1)
			return null;
		if (args[0] != String.class)
			return null;

		return String.class;
	}

}
