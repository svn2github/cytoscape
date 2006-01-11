package nct;

import junit.framework.*;

import java.lang.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.regex.*;


/**
 * TestSuite that runs all the sample tests.  
 * This is really superfluous since ant will run everything from the junit
 * task.  This is used to run tests independently of ant.
 */
public class AllTests 
{
        public static void main (String[] args) 
        {
		if ( args.length > 0 && 
		     args[0] != null && 
		     args[0].equals("-gui") ) {
		    	String newargs[] = {"nct.AllTests", "-noloading"};
                	junit.swingui.TestRunner.main( newargs );
		} else
                	junit.textui.TestRunner.run( suite() );
        }

        public static Test suite ( ) {
                TestSuite suite= new TestSuite("All JUnit Tests");

		try {

		// figure out the name of the jar file that is executing this code
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL urlJar = cl.getSystemResource("nct/AllTests.class");
		String urlStr = urlJar.toString();
		int to = urlStr.indexOf("!/") + 2;
		urlStr = urlStr.substring(0, to);

		// once we have the jar file, open a connection to it and
		// read the names of all of the entries
		URL url = new URL(urlStr);
		JarURLConnection jarConnection = (JarURLConnection)url.openConnection();
		JarFile thisJar = jarConnection.getJarFile();
		Enumeration<JarEntry> entries = thisJar.entries();

		// create a list of URLs of the test classes
		List<String> testNames = new ArrayList<String>();
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if ( name.matches(".*Test.class") ) 
				testNames.add(name.substring(0,name.length()-6));
		}

		URL[] testUrlArray = new URL[testNames.size()];
		for ( int i = 0; i < testUrlArray.length; i++ ) 
			testUrlArray[i] = new URL( urlStr + testNames.get(i) + ".class" ); 
			
		URLClassLoader urlLoader = new URLClassLoader( testUrlArray, cl ); 
		
		String sep = System.getProperty("file.separator");
		for ( String testname : testNames ) {
			testname = testname.replace(sep.charAt(0),'.');
			Class c = urlLoader.loadClass( testname );
	            	Object o = c.newInstance(); 
			Class[] parameterTypes = new Class[] {};
	          	Method suiteMethod = c.getMethod("suite", parameterTypes);
                	suite.addTest( (TestSuite)(suiteMethod.invoke(o)) ); 
			System.out.println("loading class " + testname );
		}
		
		} catch (Exception e) { e.printStackTrace(); }
            return suite;
        }
}

