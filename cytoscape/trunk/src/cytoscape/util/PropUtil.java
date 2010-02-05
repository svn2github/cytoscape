
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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
package cytoscape.util;

import cytoscape.logger.CyLogger;
import java.util.regex.Pattern;
import java.util.Properties;

/**
 * A stateless utility class that makes it easy to parse int and boolean values 
 * from a Properties object.
 */
public class PropUtil {

	private PropUtil() {}

	private static CyLogger logger = CyLogger.getLogger(PropUtil.class);

	private static Pattern truePattern = Pattern.compile("^\\s*true\\s*$", Pattern.CASE_INSENSITIVE);
	private static Pattern falsePattern = Pattern.compile("^\\s*false\\s*$", Pattern.CASE_INSENSITIVE);
	private static Pattern yesPattern = Pattern.compile("^\\s*yes\\s*$", Pattern.CASE_INSENSITIVE);
	private static Pattern noPattern = Pattern.compile("^\\s*no\\s*$", Pattern.CASE_INSENSITIVE);

	/**
	 * Will return an integer for the specified property key only if the
	 * value exists and is properly formatted as an integer.  Otherwise
	 * it will return the defaultValue.
	 */
	public static int getInt(Properties props, String key, int defaultValue) {
		String val = props.getProperty(key);

		if (val == null)
			return defaultValue;

		int ret = defaultValue;

		try {
			ret = Integer.parseInt(val);
		} catch (Exception e) {
			logger.warn("Property value for "+key+" must be an integer");
		}

		return ret;
	}

	/**
	 * Will return a boolean for the specified property key only if the
	 * value exists and if the string matches "true", "false", "yes", or "no"
	 * in a case insensitive manner.  Otherwise it will return the defaultValue.
	 */
	public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
		String val = props.getProperty(key);

		if (val == null)
			return defaultValue;

		boolean ret = defaultValue;

		try {
			if (truePattern.matcher(val).matches() || yesPattern.matcher(val).matches())
				ret = true;
			else if (falsePattern.matcher(val).matches() || noPattern.matcher(val).matches())
				ret = false;
			else
				ret = defaultValue;
		} catch (Exception e) {
			logger.warn("Property value for "+key+" must be a boolean");
		}

		return ret;
	}
}
