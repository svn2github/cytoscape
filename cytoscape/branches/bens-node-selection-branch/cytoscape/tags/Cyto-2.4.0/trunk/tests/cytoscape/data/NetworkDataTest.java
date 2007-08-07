
/*
  File: NetworkDataTest.java 
  
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

package cytoscape.data;

import junit.framework.*;
import cytoscape.AllTests;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.NetworkData;
import giny.model.*;

public class NetworkDataTest extends TestCase {

 

  public void  setUp () throws Exception {
    // no set up required
  }

  public void tearDown () throws Exception {
    // TODO: clear all data structires in case other test reference.
  }

  public void testAddNodeAttribute () throws Exception {
    String testObjectAttribute0 = "testObjectAttribute0";
    int id = NetworkData.addNodeAttribute( testObjectAttribute0 );
    assertTrue( id > 0 );
    int type = NetworkData.getNodeAttributeType( testObjectAttribute0 );
    assertTrue( type == NetworkData.OBJECT_TYPE );
    assertTrue( type > 0 );
  }

  public void testSetNodeAttributeValue () throws Exception {
    Object otest1 = new String( "object1" );
    Object otest2 = new String( "object2" );
    String stest1 = new String( "string1" );
    String stest2 = new String( "string2" );
    double dtest1 = 1;
    double dtest2 = 2;
    Double dtest3 = new Double( 3 );
    String dtest4 = new String( "4" );
    String dtest5 = new String( "five" );

    
    String testObjectAttribute1 = "testObjectAttribute1";
    String testObjectAttribute2 = "testObjectAttribute2";
    String testDoubleAttribute1 = "testDoubleAttribute1";
    String testDoubleAttribute2 = "testDoubleAttribute2";
    String testDoubleAttribute3 = "testDoubleAttribute3";
    String testDoubleAttribute4 = "testDoubleAttribute4";
    String testDoubleAttribute5 = "testDoubleAttribute5";
    String testStringAttribute1 = "testStringAttribute1";
    String testStringAttribute2 = "testStringAttribute2";



    Node node = Cytoscape.getCyNode( "node1", true );
    
    // object
    int r1 = NetworkData.setNodeAttributeValue( node, testObjectAttribute1, otest1, NetworkData.OBJECT_TYPE );
    System.out.println( r1 );
    assertTrue( r1 > 0 );
    int r2 = NetworkData.setNodeAttributeObjectValue( node, testObjectAttribute2, otest2 );
    System.out.println( r2 );
    assertTrue( r2 > 0 );
    // string
    int r3 = NetworkData.setNodeAttributeValue( node, testStringAttribute1, stest1, NetworkData.STRING_TYPE );
    System.out.println( r3 );
    assertTrue( r3 > 0 );
    int r4 = NetworkData.setNodeAttributeStringValue( node, testStringAttribute2, stest2 );
    System.out.println( r4 );
    assertTrue( r4 > 0 );
    // double
    int r5 = NetworkData.setNodeAttributeValue( node, testDoubleAttribute1, new Double(dtest1), NetworkData.DOUBLE_TYPE );
    System.out.println( r5 );
    assertTrue( r5 > 0 );
    int r6 = NetworkData.setNodeAttributeDoubleValue( node, testDoubleAttribute2, dtest2 );
    System.out.println( r6 );
    assertTrue( r6 > 0 );
    int r7 = NetworkData.setNodeAttributeValue( node, testDoubleAttribute3, dtest3, NetworkData.DOUBLE_TYPE );
    System.out.println( r7 );
    assertTrue( r7 > 0 );
    int r8 = NetworkData.setNodeAttributeValue( node, testDoubleAttribute4, dtest4, NetworkData.DOUBLE_TYPE );
    System.out.println( r8 );
    assertTrue( r8 > 0 );
    int r9 = NetworkData.setNodeAttributeValue( node, testDoubleAttribute5, dtest5, NetworkData.DOUBLE_TYPE );
    System.out.println( r9 );
    assertTrue( r9 < 0 );
    
    // test the att types.
     // object
    assertTrue( NetworkData.getNodeAttributeType( testObjectAttribute1 ) == NetworkData.OBJECT_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testObjectAttribute2 ) == NetworkData.OBJECT_TYPE );
    // string
    assertTrue( NetworkData.getNodeAttributeType( testStringAttribute2 ) == NetworkData.STRING_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testStringAttribute1 ) == NetworkData.STRING_TYPE );
    // double
    assertTrue( NetworkData.getNodeAttributeType( testDoubleAttribute5 ) == NetworkData.DOUBLE_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testDoubleAttribute4 ) == NetworkData.DOUBLE_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testDoubleAttribute3 ) == NetworkData.DOUBLE_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testDoubleAttribute2 ) == NetworkData.DOUBLE_TYPE );
    assertTrue( NetworkData.getNodeAttributeType( testDoubleAttribute1 ) == NetworkData.DOUBLE_TYPE );

    // now test the get methods
    // object
    Object o1 = NetworkData.getNodeAttributeValue( node, testObjectAttribute1 );
    System.out.println( otest1+" :o1: "+o1 );
    // assertTrue( o1.equals( otest1 ) );
    Object o2 = NetworkData.getNodeAttributeObjectValue( node, testObjectAttribute2 );
    System.out.println( otest2+" :o2: "+o2 );
    //assertTrue( o2.equals( otest2 ) );
    // string
    Object s1 = NetworkData.getNodeAttributeValue( node, testStringAttribute1 );
    System.out.println( stest1+" :s1: "+s1 );
    //assertTrue( s1 instanceof String );
    //assertTrue( s1.equals( stest1 ) );
    String s2 = NetworkData.getNodeAttributeStringValue( node, testStringAttribute2 );
    System.out.println( stest2+" :s2: "+s2 );
    //assertTrue( s2.equals( stest2 ) );
    // double
    Object d1 = NetworkData.getNodeAttributeValue( node, testDoubleAttribute1 );
    System.out.println( dtest1+" :d1: "+d1 );
    //assertTrue( d1 instanceof Double );
    //assertTrue( ( ( Double )d1).doubleValue() == dtest1 );
    double d2 = NetworkData.getNodeAttributeDoubleValue( node, testDoubleAttribute2 );
    System.out.println( dtest2+" :d2: "+d2 );
    //assertTrue( d2 == dtest2 );
    double d3 = NetworkData.getNodeAttributeDoubleValue( node, testDoubleAttribute3 );
    System.out.println( dtest3+" :d3: "+d3 );
    //assertTrue( d3 == dtest3.doubleValue() );
    double d4 = NetworkData.getNodeAttributeDoubleValue( node, testDoubleAttribute4 );
    System.out.println( dtest4+" :d4: "+d4 );
    //assertTrue( d4 == 4 );
    Object d5 = NetworkData.getNodeAttributeValue( node, testDoubleAttribute5 );
    System.err.println( "Double test5: "+d5 );
    double d6 = NetworkData.getNodeAttributeDoubleValue( node, "blah" );
    System.err.println( "Double test6: "+d6 );



  }

  public static void main ( String[] args )  {
    junit.textui.TestRunner.run (new TestSuite (NetworkDataTest.class));
  }
  
}
