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


    CytoscapeData node_data = Cytoscape.getNodeNetworkData();

    // create node and test attribute creation, for a single value
    CyNode node1 = Cytoscape.getCyNode( "node1", true );
    node_data.set( "single", node1.getIdentifier(), "foo1" );
    assertTrue( node_data.get( "single", node1.getIdentifier() ) == "foo1" );



    CyNode node2 = Cytoscape.getCyNode( "node2", true );
    CyNode node3 = Cytoscape.getCyNode( "node3", true );
    CyNode node4 = Cytoscape.getCyNode( "node4", true );

    


    

    System.out.println( node_data );



    // String[] atts = node_data.getAttributeNames();
//     for ( int i = 0; i < atts.length; ++i ) {
//       System.out.println( " "+i+"Att: "+atts[i] );
    
//       for ( Iterator j = Cytoscape.getRootGraph().nodesIterator(); j.hasNext(); ) {
//         CyNode node = ( CyNode )j.next();
//         System.out.println( atts[i]+ " "+node.getIdentifier()+" "+node_data.get( atts[i],
//                                                                               node.getIdentifier() ) );
//       }
//     }


  }


  public static void main (String[] args) 
  {
    junit.textui.TestRunner.run (new TestSuite (CytoscapeDataTest.class));
}

}
