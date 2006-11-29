
//============================================================================
// 
//  file: AllTests.java
// 
//  Copyright (c) 2006, University of California San Diego 
// 
//  This program is free software; you can redistribute it and/or modify it 
//  under the terms of the GNU General Public License as published by the 
//  Free Software Foundation; either version 2 of the License, or (at your 
//  option) any later version.
//  
//  This program is distributed in the hope that it will be useful, but 
//  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
//  or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
//  for more details.
//  
//  You should have received a copy of the GNU General Public License along 
//  with this program; if not, write to the Free Software Foundation, Inc., 
//  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
//============================================================================






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
public class AllTests extends TestCase
{
	/**
	 * Parses the command line and executes the test suite in either
	 * gui mode or text based.  If the first argument on the command line
	 * is "-gui" the gui will execute all tests found.  If the -gui 
	 * argument is followed by a fully qualified test class name 
	 * (e.g. com.example.MyTest), then only that test will be executed
	 * in gui mode. Only one test case can be specified in this manner, 
	 * any subsequent test cases (or arguments at all) will cause all
	 * tests to be run.  If no arguments are found, or if the first 
	 * argument is not "-gui", then all tests will be run in text mode.
	 */
        public static void main (String[] args) {

		if ( args.length > 0 && 
		     args[0] != null && 
		     args[0].equals("-gui") ) {
			if ( args.length == 2 ) {
				String newargs[] = {"AllTests", "-noloading", args[1]};
               			junit.swingui.TestRunner.main( newargs );
			} else {
				String newargs[] = {"AllTests", "-noloading"};
               			junit.swingui.TestRunner.main( newargs );
			}
		} else
                	junit.textui.TestRunner.run( suite() );
        }

	/**
	 * Reflects to find the test classes in this jar and then
	 * adds them to this test suite.
	 */
        public static Test suite() {
                TestSuite suite= new TestSuite("All JUnit Tests");

		try {

		// figure out the name of the jar file that is executing 
		// this code
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL urlJar = cl.getSystemResource("AllTests.class");
		String urlStr = urlJar.toString();
		urlStr = urlStr.substring(0,  urlStr.indexOf("!/") + 2 );

		// once we have the jar file, open a connection to it and
		// read the names of all of the entries
		URL url = new URL(urlStr);
		JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
		JarFile thisJar = jarConnection.getJarFile();

		// create a list of URLs of the test classes
		Enumeration<JarEntry> entries = thisJar.entries();
		List<String> testNames = new ArrayList<String>();
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if ( name.matches(".*Test.class") ) 
				testNames.add(name.substring(0,name.length()-6));
		}

		// create the a new ClassLoader
		URL[] testUrlArray = new URL[testNames.size()];
		for ( int i = 0; i < testUrlArray.length; i++ ) 
			testUrlArray[i] = new URL( urlStr + testNames.get(i) + ".class" ); 
		URLClassLoader urlLoader = new URLClassLoader( testUrlArray, cl ); 
	
		// finally, iterate over each test class name, reflect an
		// instantiation of the class using the new class loader, 
		// and add it to this test suite.
		String sep = System.getProperty("file.separator");
		for ( String testname : testNames ) {
			testname = testname.replace(sep.charAt(0),'.');
			Class testclass = urlLoader.loadClass( testname );
                	suite.addTest( new TestSuite(testclass) ); 
			//System.out.println("loading class " + testname );
		}
		
		} catch (Exception e) { e.printStackTrace(); }
		return suite;
        }
}

