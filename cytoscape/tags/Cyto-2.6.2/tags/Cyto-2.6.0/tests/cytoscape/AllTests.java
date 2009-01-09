/*
  File: AllTests.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape;

import junit.framework.*;

import java.lang.*;
import java.lang.reflect.*;

import java.net.*;

import java.util.*;
import java.util.jar.*;
import java.util.regex.*;


/**
 * A TestSuite that examines the jar that it is contained in, discovers
 * all of the unit tests also contained in the jar, and executes them.
 * This is useful for running unit tests independent of ant.
 */
public class AllTests extends TestCase {
	/**
	 *
	 */
	public static final String TEST_ALL = "JUNIT_TEST_ALL";

	/**
	 * Parses the command line and executes the test suite in either
	 * gui mode or text based.  If the first argument on the command line
	 * is "-ui" the gui will execute all tests found.  If the -ui
	 * argument is followed by a fully qualified test class name
	 * (e.g. com.example.MyTest), then only that test will be executed
	 * in gui mode. Only one test case can be specified in this manner,
	 * any subsequent test cases (or arguments at all) will cause all
	 * tests to be run.  If no arguments are found, or if the first
	 * argument is not "-ui", then all tests will be run in text mode.
	 */
	public static void main(String[] args) {
		if ((args.length > 0) && (args[0] != null) && args[0].equals("-ui")) {
			if (args.length == 2) {
				String[] newargs = { "cytoscape.AllTests", "-noloading", args[1] };
				junit.swingui.TestRunner.main(newargs);
			} else {
				String[] newargs = { "cytoscape.AllTests", "-noloading" };
				junit.swingui.TestRunner.main(newargs);
			}
		} else
			junit.textui.TestRunner.run(suite());
	}

	/**
	 * Reflects to find the test classes in this jar and then
	 * adds them to this test suite.
	 */
	public static Test suite() {
		System.out.println("suite started");

		TestSuite suite = new TestSuite("All JUnit Tests");

		try {
			// figure out the name of the jar file that is executing 
			// this code
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			URL urlJar = cl.getSystemResource("cytoscape/AllTests.class");
			String urlStr = urlJar.toString();
			System.out.println("urlString " + urlStr);
			urlStr = urlStr.substring(0, urlStr.indexOf("!/") + 2);

			// once we have the jar file, open a connection to it and
			// read the names of all of the entries
			URL url = new URL(urlStr);
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			JarFile thisJar = jarConnection.getJarFile();

			// create a list of URLs of the test classes
			Enumeration entries = thisJar.entries();
			List testNames = new ArrayList();

			while (entries.hasMoreElements()) {
				String name = ((JarEntry) entries.nextElement()).getName();

				if (name.matches(".*Test.class"))
					testNames.add(name.substring(0, name.length() - 6));
			}

			// create the a new ClassLoader
			URL[] testUrlArray = new URL[testNames.size()];

			for (int i = 0; i < testUrlArray.length; i++)
				testUrlArray[i] = new URL(urlStr + (String) testNames.get(i) + ".class");

			URLClassLoader urlLoader = new URLClassLoader(testUrlArray, cl);

			// finally, iterate over each test class name, reflect an
			// instantiation of the class using the new class loader, 
			// and add it to this test suite.
			String sep = System.getProperty("file.separator");

			for (int i = 0; i < testNames.size(); i++) {
				String testname = (String) testNames.get(i);
				testname = testname.replace(sep.charAt(0), '.');

				Class c = urlLoader.loadClass(testname);
				suite.addTest(new TestSuite(c));
				System.out.println("loading class " + testname);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return suite;
	}

	/**
	 * Conditionally output a message to System.out.
	 * If we are running All Tests, messages will not be shown.
	 * Otherwise, messages will be shown.
	 * @param msg Message to output.
	 */
	public static void standardOut(String msg) {
		String runAllTests = System.getProperty(AllTests.TEST_ALL);

		if (runAllTests == null) {
			System.out.println(msg);
		}
	}

	/**
	 * Is the JUNIT_TEST_ALL Property Set?
	 * @return true or false.
	 */
	public static boolean runAllTests() {
		String runAllTestProperty = System.getProperty(AllTests.TEST_ALL);

		if (runAllTestProperty == null) {
			return false;
		} else {
			return true;
		}
	}
}
