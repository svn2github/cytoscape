package cytoscape.data;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import cytoscape.Cytoscape;
import cytoscape.data.readers.*;
import cytoscape.util.Misc;
import cytoscape.task.TaskMonitor;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.CyData;
import cytoscape.data.attr.CyDataDefinition;
import cytoscape.data.attr.CyDataDefinitionListener;
import cytoscape.data.attr.CyDataListener;
import cytoscape.data.attr.util.CyDataFactory;

import giny.model.GraphObject;
/**
  CytoscapeData provides an interface for storing multi-dimensional data for any given object that has a unique String identifier.  For Cytoscape this means that all Nodes and Edges have a "getIdentifer" method that returns a unique identifer that can be used to access the data stored by CytoscapeData.<br>
<br>
 The Data that is stored is restricted to being either: Boolean, Double, Integer, or String.  So this class will not be able to hold general Objects that are of a special type, and are not one of these primitives.  However, CytoscapeData does provide for limited data structures in that all data can be stored as a collection of multi-dimensional hashes, or lists.<br>
<br>
 The underlying implementation is that all values are always stored as a hash. To access this low level API use the documentation of CyData.  For less advanced use there are three levels of this API that offer increasingly powerful ways to store your data.<br>
<br>
 Initializing Attributes:<br>
<br>
 <br>
<br>
 initializeAttributeType<br>
<br>
<br>
 Level 1 (Single Value)<br>
<br>
 At this most basic level, there is just one value for each attribute identifer pair. The methods for using this simple case are:<br>
  <br>
 setAttributeValue<br>
 getAttributeValue<br>
 deleteAttributeValue <br>
<br>
 This level is very straightforward, and is suitable for most situations. <br>
<br>
 Level 2 (Lists)<br>
<br>
 The level 2 API is completely compatible with the level 1 API.  In the level 2 API any value that was set using methods of the level 1 is now usable as the first element in a list of values. In addition, any modification made to the first element of the list will be returned when the level 1 methods are used. The level 2 methods are very similar to the methods of java.util.List :<br>
<br>
 addAttributeListValue<br>
 getAttributeValueListCount <br>
 getAttributeValueListElement<br>
 getAttributeValueList<br>
 deleteAttributeListValue<br>
<br>
 Level 3 (Hashes)<br>
<br>
 Useing Hashes is an easy way to store mutliple key value pairs for a single attribute. For instance for each attribute "Experiment" one can have keys for values that are things like "Condition" or "Pvalue".  Thus data that belongs under one title cna be grouped there easily.  The level 3 API is completely compatible with the level 1 and 2 API, in the level 3 API previously set values are now accesable via the key "0", or any integer used in level 2. The methods for level 3 are: <br>
<br>
 addAttributeKeyValue<br>
 getAttributeKeySet<br>
 getAttributeKeyValue<br>
 getAttributeValuesMap<br>
 deleteAttributeKeyValue<br>
<br>
<br>
 Level 4 (to-the-metal)<br>
<br>
 If you need more flexibility, such as a hash of hashes of lists of hashes, then please refer to the CyData API.  This will most likely create data structures that are not usavle with levels 1-3, but will still be fully supported in terms of UI and IO.<br>
<br>
*/
public interface CytoscapeData 
  extends GraphObjAttributes,
          CyData,
          CyDataDefinition {
  
  public final byte TYPE_BOOLEAN = 1;
  public final byte TYPE_FLOATING_POINT = 2;
  public final byte TYPE_INTEGER = 3;
  public final byte TYPE_STRING = 4;

  public static final byte NODES = 1;
  public static final byte EDGES = 2;
  
  
  public void applyLabel ( String attributeName, String labelName );

  public void removeLabel ( String attributeName, String labelName );

  public Set getAttributesByLabel ( String labelName );

  public Set getLabelNames ();

  public void addCytoscapeDataListener ( CytoscapeDataListener listener );

  public void removeCytoscapeDataListener ( CytoscapeDataListener listener );

  /**
   * SLOW
   */
  public Set getLabelsOfAttribute ( String attributeName );

  ////////////////////////////////////////
  //SECTION 1: 1 value per attribute

  /**
   * Preset the type of value for a given attribute.  Any values that 
   * do not conform to this type will throw an error. Once a type is set, 
   * any attempt to change the value will result in an  error.
   * @param attribute the name of the attribute
   * @param type one of TYPE_BOOLEAN, TYPE_FLOATING_POINT, TYPE_INTEGER, TYPE_STRING
   */
  public void initializeAttributeType ( String attribute,
                                        byte type );


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param value the value for this identifier attribute pair
   * @return the previous value for this attribute, or null if empty 
   */
  public Object setAttributeValue ( String identifier,
                                    String attribute,
                                    Object value );
  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the value stored for this attribute and object
   */
  public Object getAttributeValue ( String identifier,
                                    String attribute );

 /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the value removed for this attribute and object
   */
  public Object deleteAttributeValue ( String identifier,
                                       String attribute );


  ////////////////////////////////////////
  // SECTION 2: List attributes

  /**
   * Using "Add" means that you adding this value to the 
   * end of the list of values, similar to a java.util.List
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param value the value for this identifier attribute pair, that
   *              will be added to the end of the list
   * @return the new length of the list of values for this identifier
   *          attribute pair
   */
  public int addAttributeListValue ( String identifier,
                                     String attribute,
                                     Object value );


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the number of values for this list of values
   *         for this attribute 
   */
  public int getAttributeValueListCount ( String identifier,
                                          String attribute );


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value stored for this attribute and object at this position
   */
  public Object getAttributeValueListElement ( String identifier,
                                               String attribute,
                                               int position );

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return all of the values for this  identifier attribute pair
   *         as a list.
   */
  public List getAttributeValueList ( String identifier,
                                      String attribute );
  

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value removed for this attribute and object at this position
   */
  public Object deleteAttributeListValue ( String identifier,
                                           String attribute,
                                           int position );

  ////////////////////////////////////////
  // SECTION 3: Hash Attributes


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param key the user-given key to access this value
   * @param value the value stored for this identifier, attribute, key 
   *              combination
   */
  public int putAttributeKeyValue ( String identifier,
                                    String attribute,
                                    String key,
                                    Object value );
  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return a set of all the keys for this attribute
   */
  public Set getAttributeKeySet ( String identifier,
                                  String attribute );

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value stored for this attribute and object for this key
   */
  public Object getAttributeKeyValue ( String identifier,
                                       String attribute,
                                       String key );
  

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the collection of keys and values returned as a map
   */
  public Map getAttributeValuesMap ( String identifier,
                                       String attribute );


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return remove the value stored for this attribute and object for this key
   */
  public Object deleteAttributeKeyValue ( String identifier,
                                          String attribute,
                                          String key );
  


  ////////////////////////////////////////
  // SECTION 4: helpepr methods and convience


  // /**
//    * @return the identifiers of all objects with a given attribute defined
//    */
//   public Set getDefinedForAttribute ( String attributeName );


//   /**
//    * @return the unique values among the values of all objects with a given attribute.
//    */
//   public Set getUniqueAttributeValues ( String attributeName );



}
