/*
  File: JavaVersion.java

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
package cytoscape.util;


/**
 *  A class that determines various components of the JVM version.
 *  Please <a href="http://java.sun.com/j2se/versioning_naming.html">see</a> for details.
 */
public class JavaVersion {
	private int major;
	private int minor;
	private int maintenanceLevel;
	private int update;

	public JavaVersion() {
		final String javaVersion = System.getProperty("java.version");

		int nonDigitPos = 0;
		while (Character.isDigit(javaVersion.charAt(nonDigitPos)))
			++nonDigitPos;
		try {
			major = Integer.parseInt(javaVersion.substring(0, nonDigitPos));
		} catch (final NumberFormatException e) {
			System.err.println("Can't determine Java major version number from \"" + javaVersion + "\"!");
		}

		++nonDigitPos;
		int intStart = nonDigitPos;
		while (Character.isDigit(javaVersion.charAt(nonDigitPos)))
			++nonDigitPos;
		try {
			minor = Integer.parseInt(javaVersion.substring(intStart, nonDigitPos));
		} catch (final NumberFormatException e) {
			System.err.println("Can't determine Java major version number from \"" + javaVersion + "\"!");
		}

		++nonDigitPos;
		intStart = nonDigitPos;
		while (nonDigitPos < javaVersion.length() && Character.isDigit(javaVersion.charAt(nonDigitPos)))
			++nonDigitPos;
		try {
			maintenanceLevel = Integer.parseInt(javaVersion.substring(intStart, nonDigitPos));
		} catch (final NumberFormatException e) {
			System.err.println("Can't determine Java maintenance level from \"" + javaVersion + "\"!");
		}

		++nonDigitPos;
		intStart = nonDigitPos;
		while (nonDigitPos < javaVersion.length() && Character.isDigit(javaVersion.charAt(nonDigitPos)))
			++nonDigitPos;
		if (nonDigitPos > javaVersion.length())
			update = 0;
		else {
			try {
				update = Integer.parseInt(javaVersion.substring(intStart, nonDigitPos));
			} catch (final NumberFormatException e) {
				update = 0;
			}
		}
	}

	public int getMajor() { return major; }
	public int getMinor() { return minor; }
	public int getMaintenanceLevel() { return maintenanceLevel; }
	public int getUpdate() { return update; }
}
