package cytoscape.data.unitTests;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import junit.framework.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.util.Misc;
import cytoscape.task.TaskMonitor;

import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.CyDataDefinitionListener;
import cytoscape.data.attr.CyDataListener;
import cytoscape.data.attr.util.CyDataFactory;

import giny.model.GraphObject;

public class CytoscapeDataTest extends TestCase {

  public void setUp () throws Exception
  {
  }

  public void tearDown () throws Exception
  {
  }
  
  public void testData () {


    // create some nodes
    CyNode node1 = Cytoscape.getCyNode( "node1", true );
    CyNode node2 = Cytoscape.getCyNode( "node2", true );
    CyNode node3 = Cytoscape.getCyNode( "node3", true );
    CyNode node4 = Cytoscape.getCyNode( "node4", true );

    System.out.println( node1 );


    CytoscapeData node_data = Cytoscape.getNodeNetworkData();

    System.out.println( node_data );


    node_data.set( "test", node1.getIdentifier(), "foo" );
    //Assert.assertTrue( node_data.get( "test", node1.getIdentifier() ) == "foo" );
    System.out.println( node_data.get( "test", node1.getIdentifier() ) );


    String[] atts = node_data.getAttributeNames();
    for ( int i = 0; i < atts.length; ++i ) {
      System.out.println( " "+i+"Att: "+atts[i] );
    
      for ( Iterator j = Cytoscape.getRootGraph().nodesIterator(); j.hasNext(); ) {
        CyNode node = ( CyNode )j.next();
        System.out.println( atts[i]+ " "+node.getIdentifier()+" "+node_data.get( atts[i],
                                                                              node.getIdentifier() ) );
      }
    }


  }


  public static void main (String[] args) 
  {
    junit.textui.TestRunner.run (new TestSuite (CytoscapeDataTest.class));
}

}
