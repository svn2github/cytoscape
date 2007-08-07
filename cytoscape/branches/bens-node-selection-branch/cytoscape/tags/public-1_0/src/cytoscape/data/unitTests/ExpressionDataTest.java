// ExpressionDataTest.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute of Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------------------
package cytoscape.data.unitTests;
//--------------------------------------------------------------------------------------
import junit.framework.*;
import java.io.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

import cytoscape.data.mRNAMeasurement;
import cytoscape.data.ExpressionData;
//------------------------------------------------------------------------------
public class ExpressionDataTest extends TestCase {

  private static String testDataDir = "../../testData";
  private static String testDataFile = testDataDir + "/gal1.22x5.mRNA";

//------------------------------------------------------------------------------
public ExpressionDataTest (String name) 
{
  super (name);
}
//------------------------------------------------------------------------------
public void setUp () throws Exception
{
  }
//------------------------------------------------------------------------------
public void tearDown () throws Exception
{
}
//------------------------------------------------------------------------------
public void testCtor () throws Exception
{ 
  System.out.println ("testCtor");
  ExpressionData data = new ExpressionData (testDataFile);
  Vector measurements = data.getAllMeasurements ();
  assertTrue (data.getNumberOfGenes () == measurements.size ());
  if ( data.getNumberOfGenes() > 0 ) {
      //String gene = "YHR051W";
      String gene = data.getGeneNames () [0];
      Vector geneInfo = (Vector) measurements.get(0);
      if ( data.getNumberOfConditions() > 0 ) {
	  //String condition = "gal1RG.sig";
	  String condition = data.getConditionNames () [0];
	  mRNAMeasurement measurement = (mRNAMeasurement) geneInfo.get (0);
	  //System.out.println ("----------" + gene + ": " + measurement);
	  assertTrue (measurement.getRatio () >= -200.0);
	  assertTrue (measurement.getSignificance () >= 0.0);
      }
  }

} // testCtor
//-------------------------------------------------------------------------
public void testGetConditionNames () throws Exception
{
  System.out.println ("testGetConditionNames");
  ExpressionData data = new ExpressionData (testDataFile);
  String [] conditionNames = data.getConditionNames ();
  assertTrue (conditionNames.length == data.getNumberOfConditions ());

} // testGetConditionNames
//-------------------------------------------------------------------------
public void testGetGeneNames () throws Exception
{
  System.out.println ("testGetGeneNames");
  ExpressionData data = new ExpressionData (testDataFile);
  String [] geneNames = data.getGeneNames ();
  //for (int i=0; i < geneNames.length; i++)
  //  System.out.println (geneNames [i]);
  //System.out.println ("geneNames.length: " + geneNames.length);
  //System.out.println ("numberOfGenes: " + data.getNumberOfGenes ());
  assertTrue (geneNames.length == data.getNumberOfGenes ());

} // testGetGeneNames
//-------------------------------------------------------------------------
public void testGetGeneDescriptors () throws Exception
{
  System.out.println ("testGetGeneDescriptors");
  ExpressionData data = new ExpressionData (testDataFile);
  String [] geneDescriptors = data.getGeneDescriptors ();
  //for (int i=0; i < geneDescriptors.length; i++)
  //  System.out.println (geneDescriptors [i]);
  //System.out.println ("geneDescriptors.length: " + geneDescriptors.length);
  //System.out.println ("numberOfGenes: " + data.getNumberOfGenes ());
  assertTrue (geneDescriptors.length == data.getNumberOfGenes ());

} // testGetGeneNames
//-------------------------------------------------------------------------
public void testGetMeasurement () throws Exception
{
  System.out.println ("testGetMeasurement");
  ExpressionData data = new ExpressionData (testDataFile);

  if ( data.getNumberOfGenes() > 0 && data.getNumberOfConditions() > 0) {
      String gene = data.getGeneNames () [0];
      String condition = data.getConditionNames () [0];

      mRNAMeasurement measurement = data.getMeasurement (gene, condition);
      // System.out.println ("---------- measurement: " + measurement);
      double ratio = measurement.getRatio ();
      double sig = measurement.getSignificance ();

      assertTrue (ratio > -100.0);
      assertTrue (ratio < 1000.0);

      assertTrue (sig >= 0.0);
      assertTrue (sig < 10000.0);
  }
  
} // testGetMeasurement
//-------------------------------------------------------------------------
public static void main (String [] args) 
{
  if (args.length == 1)
    testDataFile = args [0];

  junit.textui.TestRunner.run (new TestSuite (ExpressionDataTest.class));
}
//------------------------------------------------------------------------------
} // ExpressionDataTest


