/*
 File: AllTests.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	/**
	 * The main test suite for SemanticSummaryPlugin
	 */
	
	public static Test suite() 
	{
		//Add all classes here to test
		Class[] testClasses = {
				SemanticSummaryParametersUnitTest.class,
				WordFilterUnitTest.class,
				CloudParametersUnitTest.class,
				SemanticSummaryClusterBuilderUnitTest.class};
		
		TestSuite suite = new TestSuite(testClasses);
		suite.setName("SemanticSummaryPlugin Test");
		
		return suite;
	}

}
