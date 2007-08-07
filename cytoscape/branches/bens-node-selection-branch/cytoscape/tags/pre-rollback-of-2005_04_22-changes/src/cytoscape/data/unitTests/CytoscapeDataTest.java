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
  
  public void testSingle () {


    CytoscapeData node_data = Cytoscape.getNodeNetworkData();


    // Single Values:
    // create node and test attribute creation, for a single value
    CyNode node1 = Cytoscape.getCyNode( "node1", true );


    // add a string value, and make sure it is a string
    node_data.setAttributeValue( node1.getIdentifier(), 
                                 "single_string", 
                                 "foo1" );
    
    assertTrue( node_data.getAttributeValueType( "single_string" ) == CytoscapeData.TYPE_STRING );
    assertTrue( node_data.get( "single_string", 
                               node1.getIdentifier() ) == "foo1" );

    // add a double value, and make sure it is a double
    node_data.set( "single_double", node1.getIdentifier(), "2.2" );
    assertTrue( node_data.getAttributeValueType( "single_double" ) == CytoscapeData.TYPE_FLOATING_POINT );
    assertTrue( ( (Double )node_data.get( "single_double", node1.getIdentifier() )).doubleValue() == 2.2 );



    // add an integer value, and make sure it is an integer
    // we must explicity state that it is an Integer
    node_data.initializeAttributeType( "single_integer", CytoscapeData.TYPE_INTEGER );
    node_data.set( "single_integer", node1.getIdentifier(), "3" );
    assertTrue( node_data.getAttributeValueType( "single_integer" ) == CytoscapeData.TYPE_INTEGER );
    assertTrue( ( (Integer)node_data.get( "single_integer", node1.getIdentifier() ) ).intValue() == 3 );
   
    // add a Boolean value, and make sure it is an boolean
    node_data.set( "single_boolean", node1.getIdentifier(), "true" );
    assertTrue( node_data.getAttributeValueType( "single_boolean" ) == CytoscapeData.TYPE_BOOLEAN );
    assertTrue( ( (Boolean)node_data.get( "single_boolean", node1.getIdentifier() ) ).booleanValue() );

  }


  public void testList () {


    CytoscapeData node_data = Cytoscape.getNodeNetworkData();


    // List Values:
    // create node and test attribute creation, for a list value
    CyNode node2 = Cytoscape.getCyNode( "node2", true );


    // add a string value, and make sure it is a string
    node_data.setAttributeValue( node2.getIdentifier(), 
                                 "list_string", 
                                 "foo1" );
    assertTrue( node_data.getAttributeValueType( "list_string" ) == CytoscapeData.TYPE_STRING );
    assertTrue( node_data.get( "list_string", 
                               node2.getIdentifier() ) == "foo1" );

    node_data.addAttributeListValue( node2.getIdentifier(), 
                                     "list_string", 
                                     "foo2" );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_string") == 2 );
    
    assertTrue( node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                        "list_string",
                                                        0 ) == "foo1" );
    assertTrue( node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                        "list_string",
                                                        1 ) == "foo2" );



    // add a double value, and make sure it is a double
    node_data.set( "list_double", node2.getIdentifier(), "2.2" );
    assertTrue( node_data.getAttributeValueType( "list_double" ) == CytoscapeData.TYPE_FLOATING_POINT );
    assertTrue( ( (Double )node_data.get( "list_double", node2.getIdentifier() )).doubleValue() == 2.2 );
    node_data.addAttributeListValue( node2.getIdentifier(), 
                                     "list_double", 
                                     "2.3" );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_double") == 2 );
    
    assertTrue( ( (Double )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_double",
                                                                   0 ) ).doubleValue() == 2.2 );
    assertTrue( ( (Double )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_double",
                                                                   1 ) ).doubleValue() == 2.3 );
    node_data.deleteAttributeListValue( node2.getIdentifier(),
                                        "list_double",
                                        1 );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_double") == 1 );
    

    // add an integer value, and make sure it is an integer
    // we must explicity state that it is an Integer
    node_data.initializeAttributeType( "list_integer", CytoscapeData.TYPE_INTEGER );
    node_data.set( "list_integer", node2.getIdentifier(), "3" );
    assertTrue( node_data.getAttributeValueType( "list_integer" ) == CytoscapeData.TYPE_INTEGER );
    assertTrue( ( (Integer)node_data.get( "list_integer", node2.getIdentifier() ) ).intValue() == 3 );
    node_data.addAttributeListValue( node2.getIdentifier(), 
                                     "list_integer", 
                                     "4" );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_integer") == 2 );
    
    assertTrue( ( (Integer )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_integer",
                                                                   0 ) ).intValue() == 3 );
    assertTrue( ( (Integer )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_integer",
                                                                   1 ) ).intValue() == 4 );
    node_data.deleteAttributeListValue( node2.getIdentifier(),
                                        "list_integer",
                                        1 );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_integer") == 1 );
    


   
    // add a Boolean value, and make sure it is an boolean
    node_data.set( "list_boolean", node2.getIdentifier(), "true" );
    assertTrue( node_data.getAttributeValueType( "list_boolean" ) == CytoscapeData.TYPE_BOOLEAN );
    assertTrue( ( (Boolean)node_data.get( "list_boolean", node2.getIdentifier() ) ).booleanValue() );
    node_data.addAttributeListValue( node2.getIdentifier(), 
                                     "list_boolean", 
                                     "false" );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_boolean") == 2 );
    
    assertTrue( ( (Boolean )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_boolean",
                                                                   0 ) ).booleanValue() );
    assertTrue( !( (Boolean )node_data.getAttributeValueListElement( node2.getIdentifier(),
                                                                   "list_boolean",
                                                                   1 ) ).booleanValue() );
    node_data.deleteAttributeListValue( node2.getIdentifier(),
                                        "list_boolean",
                                        1 );
    assertTrue( node_data.getAttributeValueListCount(node2.getIdentifier(),
                                                     "list_boolean") == 1 );
  }

  public void testKey () {


    CytoscapeData node_data = Cytoscape.getNodeNetworkData();


    // Key Values:
    // create node and test attribute creation, for a key value
    CyNode node3 = Cytoscape.getCyNode( "node3", true );


    // add a string value, and make sure it is a string
    node_data.setAttributeValue( node3.getIdentifier(), 
                                 "key_string", 
                                 "foo1" );
    assertTrue( node_data.getAttributeValueType( "key_string" ) == CytoscapeData.TYPE_STRING );
    assertTrue( node_data.get( "key_string", 
                               node3.getIdentifier() ) == "foo1" );

    node_data.putAttributeKeyValue( node3.getIdentifier(), 
                                    "key_string",
                                    "key",
                                    "foo2" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_string").size() == 2 );
    
    assertTrue( node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                       "key_string",
                                                       "0" ) == "foo1" );
    assertTrue( node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                       "key_string",
                                                       "key" ) == "foo2" ); 


    // add a double value, and make sure it is a double
    node_data.set( "key_double", node3.getIdentifier(), "2.2" );
    assertTrue( node_data.getAttributeValueType( "key_double" ) == CytoscapeData.TYPE_FLOATING_POINT );
    assertTrue( ( (Double )node_data.get( "key_double", node3.getIdentifier() )).doubleValue() == 2.2 );
    node_data.putAttributeKeyValue( node3.getIdentifier(), 
                                    "key_double", 
                                    "key",
                                    "2.3" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_double").size() == 2 );
    
    assertTrue( ( (Double )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                  "key_double",
                                                                  "0" ) ).doubleValue() == 2.2 );
    assertTrue( ( (Double )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                  "key_double",
                                                                  "key" ) ).doubleValue() == 2.3 );
    node_data.deleteAttributeKeyValue( node3.getIdentifier(),
                                       "key_double",
                                       "key" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_double").size() == 1 );
    

    // add an integer value, and make sure it is an integer
    // we must explicity state that it is an Integer
    node_data.initializeAttributeType( "key_integer", CytoscapeData.TYPE_INTEGER );
    node_data.set( "key_integer", node3.getIdentifier(), "3" );
    assertTrue( node_data.getAttributeValueType( "key_integer" ) == CytoscapeData.TYPE_INTEGER );
    assertTrue( ( (Integer)node_data.get( "key_integer", node3.getIdentifier() ) ).intValue() == 3 );
    node_data.putAttributeKeyValue( node3.getIdentifier(), 
                                    "key_integer", 
                                    "key",
                                    "4" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_integer").size() == 2 );
    
    assertTrue( ( (Integer )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                   "key_integer",
                                                                   "0" ) ).intValue() == 3 );
    assertTrue( ( (Integer )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                   "key_integer",
                                                                   "key" ) ).intValue() == 4 );
    node_data.deleteAttributeKeyValue( node3.getIdentifier(),
                                       "key_integer",
                                       "key" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_integer").size() == 1 );
    


   
    // add a Boolean value, and make sure it is an boolean
    node_data.set( "key_boolean", node3.getIdentifier(), "true" );
    assertTrue( node_data.getAttributeValueType( "key_boolean" ) == CytoscapeData.TYPE_BOOLEAN );
    assertTrue( ( (Boolean)node_data.get( "key_boolean", node3.getIdentifier() ) ).booleanValue() );
    node_data.putAttributeKeyValue( node3.getIdentifier(), 
                                    "key_boolean",
                                    "key",
                                    "false" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_boolean").size() == 2 );
    
    assertTrue( ( (Boolean )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                   "key_boolean",
                                                                   "0" ) ).booleanValue() );
    assertTrue( !( (Boolean )node_data.getAttributeKeyValue( node3.getIdentifier(),
                                                                   "key_boolean",
                                                                   "key" ) ).booleanValue() );
    node_data.deleteAttributeKeyValue( node3.getIdentifier(),
                                       "key_boolean",
                                       "key" );
    assertTrue( node_data.getAttributeKeySet(node3.getIdentifier(),
                                                  "key_boolean").size() == 1 );
  }

  


  public static void main (String[] args) 
  {
    junit.textui.TestRunner.run (new TestSuite (CytoscapeDataTest.class));
}

}
