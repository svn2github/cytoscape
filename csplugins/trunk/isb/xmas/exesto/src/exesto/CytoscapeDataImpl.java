package exesto;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import cytoscape.Cytoscape;
import cytoscape.data.readers.*;
import cytoscape.util.Misc;
import cytoscape.task.TaskMonitor;

import cytoscape.data.CyAttributes;
import cytoscape.data.attr.*;
import cytoscape.data.attr.util.MultiHashMapHelpers;

import giny.model.GraphObject;

public class CytoscapeDataImpl 
  implements CytoscapeData {

  private TaskMonitor taskMonitor;

  private Object objectModel;
  private MultiHashMap data;
  private MultiHashMapDefinition definition;
  private CyAttributes atts;

  public final byte TYPE_BOOLEAN = 1;
  public final byte TYPE_FLOATING_POINT = 2;
  public final byte TYPE_INTEGER = 3;
  public final byte TYPE_STRING = 4;

  private static final String LIST = "LIST";
  private static final Object[] LIST_KEY = {"LIST"};
  private static final Object[] ZERO = {Integer.toString(0)};

  private Map labelMap;
  Vector listeners = new Vector();

  public CytoscapeDataImpl ( CyAttributes atts ) {
    this.atts = atts;
    data = atts.getMultiHashMap();
    definition = atts.getMultiHashMapDefinition();

    labelMap = new HashMap();

  }

  ////////////////////////////////////////
  ////////////////////////////////////////
  // CytoscapeData Implementation
 
  public Object objectAsType ( Object value, byte type ) {
   
    if ( value == null )
      return null;

    if ( type == TYPE_BOOLEAN && value instanceof Boolean ) {
      return value;
    } else if ( type == TYPE_FLOATING_POINT && value instanceof Double ) {
      return value;
    } else if ( type == TYPE_INTEGER && value instanceof Integer ) {
      return value;
    } else if ( type == TYPE_STRING && value instanceof String ) {
      return value;
    } else {
      return value.toString();
    }
  }

//   /**
//    * If we are guessing, first try to cast as a Double. 
//    * Then Boolean, then default to String. We never guess Integer.
//    */
//   public byte wildGuessAndDefineObjectType ( Object value, String attributeName ) {
   
//     Object attribute;
//     // Test for Double
//     try { 
//       attribute = new Double( value.toString() );
//       defineAttribute( attributeName,
//                        TYPE_FLOATING_POINT,
//                        new byte[] {TYPE_STRING} );
//       return TYPE_FLOATING_POINT;
//     } catch ( Exception e ) {}
    
//     // Test for Boolean
//     try { 
//       if ( value.toString().equals("true") || 
//            value.toString().equals("false") ) {
//         defineAttribute( attributeName,
//                          TYPE_BOOLEAN,
//                          new byte[] {TYPE_STRING} );
//         return TYPE_BOOLEAN;
//       }
//     } catch ( Exception e ) {}
    
//     // Default is String
//     defineAttribute( attributeName,
//                      TYPE_STRING,
//                      new byte[] {TYPE_STRING} );
//     return TYPE_STRING;
    
//   }
  



  /**
   * If we are guessing, first try to cast as a Double. 
   * Then Boolean, then default to String. We never guess Integer.
   */
  public byte guessAndDefineObjectType ( Object value, String attributeName ) {
   


    if ( value instanceof Boolean ) {
      defineAttribute( attributeName,
                       TYPE_BOOLEAN,
                       new byte[] {TYPE_STRING} );
      return TYPE_BOOLEAN;
    } else if ( value instanceof String ) {
      defineAttribute( attributeName,
                       TYPE_STRING,
                       new byte[] {TYPE_STRING} );
      return TYPE_STRING;
    }else if ( value instanceof Double ) {
      defineAttribute( attributeName,
                       TYPE_FLOATING_POINT,
                       new byte[] {TYPE_STRING} );
      return TYPE_FLOATING_POINT;
    }else if ( value instanceof Integer ) {
      defineAttribute( attributeName,
                       TYPE_INTEGER,
                       new byte[] {TYPE_STRING} );
      return TYPE_INTEGER;
    } else {
      Object attribute;
      // Test for Double
      try { 
        attribute = new Double( value.toString() );
        defineAttribute( attributeName,
                         TYPE_FLOATING_POINT,
                         new byte[] {TYPE_STRING} );
        return TYPE_FLOATING_POINT;
      } catch ( Exception e ) {}
      
      // Test for Boolean
      try { 
        if ( value.toString().equals("true") || 
             value.toString().equals("false") ) {
          defineAttribute( attributeName,
                           TYPE_BOOLEAN,
                           new byte[] {TYPE_STRING} );
          return TYPE_BOOLEAN;
        }
      } catch ( Exception e ) {}
      
      // Default is String
      defineAttribute( attributeName,
                       TYPE_STRING,
                       new byte[] {TYPE_STRING} );
      return TYPE_STRING;
      
    }
  }


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
                                        byte type ) {

    defineAttribute( attribute,
                     type,
                     new byte[] {TYPE_STRING} );
                     
  }



  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param value the value for this identifier attribute pair
   * @return the previous value for this attribute, or null if empty 
   */
  public Object setAttributeValue ( String identifier,
                                    String attribute,
                                    Object value ) {
    byte set = definition.getAttributeValueType( attribute );
    if ( set == -1 ) {
      // not set, guess the type
      set = guessAndDefineObjectType( value, attribute );
    }
    try { 
      return setAttributeValue( identifier,
                                attribute,
                                objectAsType( value, set ),
                                ZERO );
      } catch ( Exception e ) {
        //System.out.println( "set is failing: "+attribute+" "+identifier+ "  "+value );
        e.printStackTrace();
        return null;
      }
  }

      
 
  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the value stored for this attribute and object
   */
  public Object getAttributeValue ( String identifier,
                                    String attribute ) {

    try {
      return getAttributeValue( identifier,
                                     attribute,
                                     ZERO );
    } catch ( Exception e ) {
      return null;
    }
  }

 /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the value removed for this attribute and object
   */
  public Object deleteAttributeValue ( String identifier,
                                       String attribute ) {
    try {
      return removeAttributeValue( identifier,
                                   attribute,
                                   ZERO );
    } catch ( Exception e ) {
      return null;
    }
  }


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
                                     Object value ) {

    byte set = definition.getAttributeValueType( attribute );
    if ( set == -1 ) {
      // first we need the type
      set = guessAndDefineObjectType( value, attribute );
    } 
    
    // first find the end of the list
    int span = getAttributeKeyspan( identifier, 
                                    attribute,
                                    null).numRemaining();
    try {
      // now insert the current_val into the end of the list
      setAttributeValue( identifier,
                         attribute,
                         objectAsType( value, set ),
                         new Object[] {Integer.toString(span)} );
    } catch ( Exception e ) {
      return -1;
    }
    return span++;
  }


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the number of values for this list of values
   *         for this attribute 
   */
  public int getAttributeValueListCount ( String identifier,
                                          String attribute ) {
    return getAttributeKeyspan( identifier, 
                                attribute,
                                null).numRemaining();
  }


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value stored for this attribute and object at this position
   */
  public Object getAttributeValueListElement ( String identifier,
                                               String attribute,
                                               int position ) {
 
    if ( position >= getAttributeValueListCount( identifier, attribute ) )
      return null;

    try {
      return getAttributeValue( identifier,
                                attribute,
                                new Object[] { Integer.toString(position) } );
    } catch ( Exception e ) {
      return null;
    }

  }

  /**
   * Returns the values as a new list
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return all of the values for this  identifier attribute pair
   *         as a list.
   */
  public List getAttributeValueList ( String identifier,
                                      String attribute ) {

    Set keys = getAttributeKeySet( identifier, attribute );
    List arraylist = new ArrayList(keys.size());
    for ( Iterator i = keys.iterator(); i.hasNext(); ) {
      arraylist.add( data.getAttributeValue( identifier,
                                             attribute,
                                             new Object[] { (String)i.next() } ) );
    }
    return arraylist;
  }


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value removed for this attribute and object at this position
   */
  public Object deleteAttributeListValue ( String identifier,
                                           String attribute,
                                           int position ) {

    if ( position >= getAttributeValueListCount( identifier, attribute ) )
      return null;

    try {
      return removeAttributeValue( identifier,
                                   attribute,
                                   new Object[] { Integer.toString(position) } );
      // @bug We're not plugging the hole.
    } catch ( Exception e ) {
      return null;
    }


  }

  ////////////////////////////////////////
  // SECTION 3: Hash Attributes


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param key the user-given key to access this value
   * @param value the value stored for this identifier, attribute, key 
   *              combination
   * @return the size of this hash
   */
  public int putAttributeKeyValue ( String identifier,
                                    String attribute,
                                    String key,
                                    Object value ) {

    byte set = definition.getAttributeValueType( attribute );
    if ( set == -1 ) {
      // first we need the type
      set = guessAndDefineObjectType( value, attribute );
    } 
    
    try {
      // now insert the current_val into the end of the list
      setAttributeValue( identifier,
                         attribute,
                         objectAsType( value, set ),
                         new Object[] {key} );
    } catch ( Exception e ) {
      return -1;
    }


    return getAttributeKeyspan( identifier, 
                                attribute,
                                null).numRemaining();
 
  }

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return a new Set of all the keys for this attribute
   */
  public Set getAttributeKeySet ( String identifier,
                                  String attribute ) {

    CountedIterator ce =  getAttributeKeyspan( identifier, 
                                                  attribute,
                                                  null);
    Set set = new HashSet();
    while ( ce.hasNext() ) {
      set.add( ce.next() );
    }
    return set;
  }

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return the value stored for this attribute and object for this key
   */
  public Object getAttributeKeyValue ( String identifier,
                                       String attribute,
                                       String key ) {
    try {
      return getAttributeValue( identifier,
                                attribute,
                                new Object[] { key } );
    } catch ( Exception e ) {
      return null;
    }
  }
  

  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @return the a new Map of the collection of keys and values 
   */
  public Map getAttributeValuesMap ( String identifier,
                                     String attribute ) {

    CountedIterator ce =  getAttributeKeyspan( identifier, 
                                                  attribute,
                                                  null);
    Map map = new HashMap();
    while ( ce.hasNext() ) {
      Object key = ce.next();
      map.put( key, getAttributeValue( identifier,
                                       attribute,
                                       new Object[] { key } ) );
    }
    return map;
  }


  /**
   * @param identifier the unique identifier of the GraphObject
   * @param attribute the name of the attribute
   * @param position the position in the list
   * @return remove the value stored for this attribute and object for this key
   */
  public Object deleteAttributeKeyValue ( String identifier,
                                          String attribute,
                                          String key ) {
    try {
      return removeAttributeValue( identifier,
                                   attribute,
                                   new Object[] { key } );
    } catch ( Exception e ) {
      return null;
    }
  }
  


  ////////////////////////////////////////
  // SECTION 4: helpepr methods and convience


  /**
   * @return the identifiers of all objects with a given attribute defined
   */
  public Set getDefinedForAttribute ( String attributeName ) {

    Set set = new HashSet();
    CountedIterator ce = getObjectKeys( attributeName );
    while ( ce.hasNext() ) {
      set.add( ce.next() );
    }
    return set;

  }


  /**
   * @return the unique values among the values of all objects with a given attribute.
   */
  public Set getUniquAttributeValues ( String attributeName ) {

    Set set = new HashSet();
    CountedIterator ce = getObjectKeys( attributeName );
    while ( ce.hasNext() ) {
      String id = ( String )ce.next();
      List list = MultiHashMapHelpers.getAllAttributeValues( id,  
                                                       attributeName, 
                                                       data, 
                                                       definition );
      set.addAll( list );
    }
    return set;
  }

  /**
   *  remove the specified attribute from the specified node or edge
   */
  public void deleteAttribute ( String attribute, String identifier ) {
    //data.removeAttributeValue( graphObjectName, attributeName, ZERO );
    CountedIterator ce =  getAttributeKeyspan( identifier, 
                                                  attribute,
                                                  null);
    while ( ce.hasNext() ) {
      Object key = ce.next();
      removeAttributeValue( identifier,
                            attribute,
                            new Object[] { key } );
    }  
  }

  /**
   *  remove the specified attribute value from the specified node or edge.
   *  there may be multiple values associated with the attribute, so search through
   *  the list, and if it is found, remove it
   */
  public void deleteAttributeValue ( String attributeName, String graphObjectName, Object value ) {
    try {
      data.removeAttributeValue( graphObjectName, attributeName, ZERO );
    } catch ( Exception e ) {}
  }

  
  //////////////////////////////
  //Implements MultiHashMap


  public Object setAttributeValue ( String objectKey,
                                    String attributeName,
                                    Object attributeValue,
                                    Object[] keyIntoValue ) {
    return data.setAttributeValue( objectKey, attributeName, attributeValue, keyIntoValue );
  }
   
  public Object getAttributeValue ( String objectKey, 
                                    String attributeName,
                                    Object[] keyIntoValue ) {
    return data.getAttributeValue( objectKey, attributeName, keyIntoValue );
  }

  public Object removeAttributeValue ( String objectKey, 
                                       String attributeName,
                                       Object[] keyIntoValue ) {
    return data.removeAttributeValue( objectKey, attributeName, keyIntoValue );
  }

  public boolean removeAllAttributeValues ( String objectKey,
                                            String attributeName ) {
    return data.removeAllAttributeValues( objectKey, attributeName );
  }

  public CountedIterator getAttributeKeyspan ( String objectKey,
                                               String attributeName,
                                               Object[] keyPrefix ) {
    return data.getAttributeKeyspan( objectKey, attributeName, keyPrefix );
  }

  public CountedIterator getObjectKeys ( String attributeName ) {
    return data.getObjectKeys( attributeName );
  }

  public void addDataListener(MultiHashMapListener listener) {
    data.addDataListener( listener );
  }

  public void removeDataListener(MultiHashMapListener listener) {
    data.removeDataListener( listener );
  }

  //////////////////////////////
  // Implements MultiHashMapDefinition

  public void defineAttribute( String attributeName,
                               byte valueType,
                               byte[] keyTypes ) {
    definition.defineAttribute( attributeName, valueType, keyTypes );
  }

  public CountedIterator getDefinedAttributes() {
    return definition.getDefinedAttributes();
  }

  public byte getAttributeValueType ( String attributeName ) {
    return definition.getAttributeValueType( attributeName );
  }

  public byte[] getAttributeKeyspaceDimensionTypes ( String attributeName ) {
    return definition.getAttributeKeyspaceDimensionTypes( attributeName );
  }

  public boolean undefineAttribute(String attributeName) {
    return undefineAttribute( attributeName );
  }

  public void addDataDefinitionListener( MultiHashMapDefinitionListener listener ) {
    definition.addDataDefinitionListener( listener );
  }

  public void removeDataDefinitionListener( MultiHashMapDefinitionListener listener ) {
    definition.removeDataDefinitionListener( listener );
  }



}