package csplugins.data;

import cern.colt.matrix.*;
import cern.colt.map.*;
import cern.colt.list.*;

import com.sosnoski.util.hashmap.*;

import java.util.*;

/**
 * The DataPropertyMap holds all of the data for  
 * SharedIdentifiable objects. 
 *
 * The Attributes are stored as Columns, and the 
 * SharedIdentifiable as Rows.
 *
 */
public abstract class DataPropertyMap {

  /**
   * Since every SharedIdentifiable object set is assigned 
   * a uniqueID that is an int, we can store the data in
   * cern.colt.matrix.impl.SparseMatrix2D.  This will allow
   * for the easy return of 2-dimensional data sets, the
   * returned data set can then be combined however the user
   * feels is appropriate.
   */
  protected ObjectMatrix2D dataMatrix;

  /**
   * This matrix will simply be a list of Integers.
   */
  protected ObjectMatrix1D identifierVector;

  /**
   * This matrix is the list of Attributes
   */
  protected ObjectMatrix1D attributeVector;


  /**
   * This is _the_ one palce to reverse lookup Attribute strings 
   * to their ints.
   */
  protected StringIntHashMap attributeIntMap;


  /**
   * This is _the_ primary reverse lookup for aliases to uids. 
   */
  protected ObjectIntHashMap identifierIntMap;

  /**
   * The map of RootGraphIndices to the uid.
   */
  protected OpenIntIntHashMap nodeUIDMap;
  protected OpenIntIntHashMap edgeUIDMap;



  //----------------------------------------//
  // Data Information Methods
  //----------------------------------------//

  /**
   * Returns all the attribute names in this DataPropertyMap
   * by returning the Array of values in the attributeVector.
   *
   * @return all of the available attributes in this DataPropertyMap
   */
  public String[] getAttributes () {
    return ( String[] )attributeVector.toArray();
  }

  /**
   * Return the available attributes for the given 
   * uniqueIDs
   *
   * @param uniqueIDs an array of UIDS that presumably belong to 
   *                  this DataPropertyMap
   * @return the array of all attributes that are present in one 
   *         or more of 
   */
  public String[] getAttributes ( int[] uniqueIDs ) {


    // create a temporary Matrix2D that has the given
    // uids, and all columns
    int col_size = dataMatrix.columns();
    int[] all_columns = new int[ col_size ];
    int[] used_columns = new int[ col_size ];
    Arrays.fill( used_columns, 0 );
    for ( int i = 0; i < col_size; ++i ) {
      all_columns[i] = i;
    }
    ObjectMatrix2D column_calc = dataMatrix.viewSelection( uniqueIDs, all_columns );
    

    // go through the non-zero values of the temporary matrix, and
    // for every value, make sure that that column is included

    IntArrayList row = new IntArrayList();
    IntArrayList col = new IntArrayList();
    ObjectArrayList values = new ObjectArrayList();
    column_calc.getNonZeros( row, col, values );
   
    for ( int i = 0; i < col.size(); ++i ) {
      used_columns[ col.get( i ) ] = 1;
    }

    col.clear();
    
    for ( int i = 0; i < used_columns.length; ++i ) {
      if ( used_columns[i] == 1 ) {
        col.add( i );
      }
    }
    col.trimToSize();
    
    // take a view of just the used columns from the name matrix
    ObjectMatrix1D used_attributes = attributeVector.viewSelection( col.elements() );
    return ( String[] )used_attributes.toArray();
     
  }
  
  /**
   * Return the number of Attributes
   */
  public int getAttributeCount () {
    // this should be the same size as
    // attributeVector as well.
    return dataMatrix.columns();
  }

  /**
   * Return all of the uniqueID ints
   */
  public int[] getUniqueIDIndices () {

    // just loop through and un-box the Integers
    int[] uids = new int[ identifierVector.size() ];
    for ( int i = 0; i < identifierVector.size(); ++i ) {
      uids[i] = ( ( Integer )identifierVector.getQuick( i ) ).intValue();
    }
    return uids;
  }
    

  /**
   * Return all aliases of SharedIdentifiables, this array will
   * return the most likely alias.
   */
  public String[] getSharedIndentifiableArray () {
    return null;
  }


  //----------------------------------------//
  // Data Loading and Maintentance Methods
  //----------------------------------------//

  //--------------------//
  // uids

  /**
   * This is a basic assign.  This will essentially inititate a new
   * uid for the given string.
   * @param id the ID ( often unique.. ) of the object to be assigned a uid
   * @return the uid that was assigned tothis object, or the the previsouly 
   * assigned uid if this id was known.
   */
  public abstract int assignUID ( String id );

  /**
   * This is a basic assign.  This will essentially inititate a new
   * uid for the given string.
   * @param id the ID ( often unique.. ) of the object to be assigned a uid
   * @return the uid that was assigned tothis object, or the the previsouly 
   * assigned uid if this id was known.
   */
  public abstract int assignUID ( Object id );
  

  /**
   * This will assign multiple Aliases to the same UID.  
   *
   * @param aliases an array of aliases to be assigned to the same
   *                uid. It can only have ONE alias that already has
   *                a uid.
   * @return "-1" is returned of two aliases both had a uid already, 
   *         otherwise the uid that they were all eqauted to is returned.
   */
  public abstract int assignUID ( String[] aliases ) ;
  

  /**
   * Since nodes are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special node access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>node: [root graph index]</i>, however, a map
   * will be maintained of all the node mappings
   */
  public abstract int assignNodeUID ( int root_graph_index ) ;

  /**
   * Since edges are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special edge access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>edge: [root graph index]</i>, however, a map
   * will be maintained of all the edge mappings
   */
  public abstract int assignEdgeUID ( int root_graph_index ) ;


  /**
   * Equate a node with other aliases.
   */
  public abstract int assignNodeUID ( int node_root_graph_index, String[] aliases ) ;
  
  /**
   * Equate a edge with other aliases.
   */
  public abstract int assignEdgeUID ( int edge_root_graph_index, String[] aliases ) ;


  /**
   * @return the uid for a given alias
   */
  public int getUID ( String alias ) {
    return identifierIntMap.get( alias );
  }

  /**
   * @return the uid for given SharedIdentifiable
   */
  public int getUID ( SharedIdentifiable si ) {
    return identifierIntMap.get( si.getIdentifier() );
  }

  /**
   * @return the uid for the given Object
   */
  public int getUID ( Object si ) {
    return 0;
  }
  
 
  //--------------------//
  // attributes

  public int getAttributeID ( String attribute ) {
    return attributeIntMap.get( attribute );
  }


  //--------------------//
  // Single triplet settings

  public void set ( int uniqueID, int attribute, Object value ) ;


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( int uniqueID, String attribute, Object value ) ;

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( String alias, String attribute, Object value ) ;


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( SharedIdentifiable si, String attribute, Object value ) ;

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( Object si, String attribute, Object value ) ;


 
  
  //--------------------//
  // Multi-Triplet Settings
  
  /**
   * A collection of uniqueIDs can be passed, along with a collection
   * of attributes and a collection of values.  
   *
   * An int[][] array is returned that records any uniqueIDs that failed 
   * to put in the proper value for the attribute.  So an array 
   * of { { 2, 3, 4}, { 4, 3, 5 } } means that uniqueID 2 failed for 
   * attributes 3 and 4, and uniqueID 4 failed for attributes 3 and 5 in 
   * the array that was passed.
   *
   * This method will return immediatly if the arrays are of the wrong length.
   *
   * @param uniqueIDs this is an int[] array of uniqueIDs
   * @param attributes this is a String[] array fo attributes to be set for each uniqueID
   * @param values this is an Object[][] array.  The first dimension is the same length as
   *               the attributes array, the second dimension is the same length as the 
   *               uniqueID array.  Each value in the array will be set to the corresponding
   *               attribute, for the appropriate uniqueID.  If the second dimension is of 
   *               length 1, then that single value will be sete for each uniqueID for that
   *               attribute.
   */
  public int[][] set ( int[] uniqueIDs, String[] attributes, Object[][] values ) ;

  //----------------------------------------//
  // Data Access Methods
  //----------------------------------------//


  //--------------------//
  // Singelton Methods

  /**
   *  Return one value that corresponds to the given
   * uniqueID and attribute ID
   */
  public Object getValue ( int uniqueID, int attribute_id ) ;

  /**
   * Return one value that corresponds to the given 
   * uniqueID and Attribute
   */
  public Object getValue ( int uniqueID, String attribute ) ;

  /**
   * Return one value that corresponds to the given
   * SharedIdentifiable object and attribute
   */
  public Object getValue ( SharedIdentifiable ident, String attribute ) ;

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute.
   */
  public Object getValue ( String ident, String attribute ) ;

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute ID
   */
  public Object getValue ( String ident, int attribute_id ) ;

  /**
   * Return one value that corresponds to the given
   *  SharedIdentifiable object and attribute ID
   */
  public Object getValue ( SharedIdentifiable ident, int attribute_id ) ;

  
  //--------------------//
  // Multi-dimensional Methods

  // All multi-dimensional methods will return a DataPropertyMap which inherits
  // from the default DataPropertyMap.

  /**
   * This will return a DataPropertyMap that only contains the requested 
   * shared_identifiables and attributes
   *<BR><BR>
   *<B>Note:</B> if the same index is given twice, you will get back a DataPropertyMap that
   * contains two references to that index.
   */
  public DataPropertyMap getDataPropertyMap ( int[] shared_identifiables, int[] attributes ) 
   
    /**
     * This will return a DataPropertyMap that only contains the requested 
     * shared_identifiables and attributes
     *<BR><BR>
     *<B>Note:</B> if the same index is given twice, you will get back a DataPropertyMap that
     * contains two references to that index.
     */
    public DataPropertyMap getDataPropertyMap ( String[] shared_identifiables, String[] attributes ) ;


  //----------------------------------------//
  // GraphObjAttributesMethods
  //----------------------------------------//

  // Every method that was available in GraphObjAttributes
  // should have an equivalent method in DataPropertyMap.
  // Therefore this abstract class can provide the direct
  // mapping to the public interface.

  /**
   *  create an identical and unique copy, so that any subsequent changes to
   *  the clone will not affect the original, and vice versa.
   */
  public Object clone () {
  } // clone
  
  
  /**
   * Overwrite current data with the given data.
   */
  public void inputAll ( GraphObjAttributes newAttributes ) {
  }


  /**
   * @see #assignUID
   */
  public void addNameMapping ( String canonicalName, Object graphObject ) {
  
    //equation

  }

  /**
   * alias release...
   */
  public void removeNameMapping ( String canonicalName ) {
  }


  /**
   * removes a mapping between a canonical name and its graph object
   */
  public void removeObjectMapping (Object graphObj ) {
  }

  /**
   *  remove all entries from the nameMap
   */
  public void clearNameMap () {
    // ???
    //nameFinder = new HashMap ();
    //clearObjectMap();
  }

  /**
   * remove all entries in the canonicalToGraphObject map
   */
  public void clearObjectMap() {
    // ???    canonicalToGraphObject = new HashMap();
  }

  public HashMap getNameMap () {
    // this is now exposed in the API
    //return nameFinder;
  }

  public HashMap getClassMap () {
    //    return classMap;
  }

  public void addClassMap ( HashMap newClassMap ) {
    //classMap.putAll (newClassMap);
  }

  public HashMap getObjectMap () {
    //return canonicalToGraphObject;
  }

  /**
   *  a wholesale addition of all entries in a <graphObject> -> <canonicalName> 
   *  HashMap.
   */
  public void addNameMap ( HashMap nameMapping ) {
 
   //  nameFinder.putAll (nameMapping);
  

//     Set keySet = nameMapping.keySet();
//     Iterator it = keySet.iterator();
//     HashMap objectMap = new HashMap();
//     while(it.hasNext()){
//       Object graphObj = it.next();
//       String canonical = (String)nameFinder.get(graphObj);
//       objectMap.put(canonical,graphObj);
//     }
//     addObjectMap(objectMap);
  }

  /**
   * add all entries in the given HashMap (entry: <canonicalName> -> <graphObject>)
   * to the canonicalToGraphObject HashMap.
   */
  public void addObjectMap( HashMap objectMapping ) {
    //canonicalToGraphObject.putAll(objectMapping);
  }


  /**
   *  return the canonical name associated with this graphObject (a node or edge,
   *  previously stored with a call to addNameMapping).  if no mapping exists, null
   *  is returned.
   */
  public String getCanonicalName ( Object graphObject ) { 
   //  if(nameFinder == null){
//       System.out.println("oh oh, nameFinder is NULL !!!!!!!!!!!!!!");
//     }
//     return (String) nameFinder.get (graphObject);
  }

  /**
   * return the graph object (node or edge) associated with this canonical name,
   * previously stored with a call to addNameMapping. if no mapping exists, null
   * is returned.
   */
  public Object getGraphObject ( String canonicalName ) {
    return canonicalToGraphObject.get(canonicalName);
  }

  //--------------------------------------------------------------------------------
  /**
   *  copy all attributes in the supplied GraphObjAttributes object into this
   *  GraphObjAttributes.  any pre-existing attributes survive intact as long
   *  as they do not have the same attribute name as the attributes passed
   *  in
   */
  public void set (GraphObjAttributes attributes)
  {
    String [] newAttributeNames = attributes.getAttributeNames ();

    for (int i=0; i < newAttributeNames.length; i++) {
      String name =  newAttributeNames [i];
      HashMap hash = attributes.getAttribute (newAttributeNames [i]);
      map.put (name, hash);
    }

  } // add
  //--------------------------------------------------------------------------------
  /**
   *  when a new attribute is assigned, some preliminary set up may need to be done:
   *  <ul>
   *    <li> if the attribute is previously unknown, then a new HashMap must be
   *         created for it
   *    <li> if the class of the attribute has not yet been set, then it can
   *         be deduced from the specified value ('obj').  if 'obj' is an array,
   *         then the deduced class is that of first element in the array
   *    <li> if the class of the attribute has already been set, then the
   *         class of the new 'obj' (or its first element, if it is an array)
   *         must be the same as that of the class; we test that here, and throw
   *         an exception is thrown if they do not agree
   *  </ul>
   */
  protected void initializeAttributeAsRequired (String attributeName,  
                                                String graphObjectName, 
                                                Object obj)
  {

    if (!map.containsKey (attributeName)) {
      // System.out.println (" --- map does not contain key " + attributeName);
      map.put (attributeName, new HashMap ());
      if (getClass (attributeName) == null) {
        Class deducedClass = obj.getClass ();
        //System.out.println ("\nObject: " + obj + " attribute: " +attributeName +
        //                    " deduced class: " + deducedClass);
        //System.out.flush();
        if (obj.getClass().isArray())
          deducedClass = ((Object []) obj)[0].getClass ();
        setClass (attributeName, deducedClass);
      } // if no class assigned yet
    } // if new attribute
    else {
      ; // System.out.println (" +++ map does contain key " + attributeName);
    }

    Class expectedClass = getClass (attributeName);
    Class actualClass = obj.getClass ();

    if (obj.getClass().isArray()) {
      Object [] objAsArray = (Object []) obj;
      Object first = objAsArray [0];
      actualClass = first.getClass ();
    }
    
    if (actualClass != expectedClass)
      throw new IllegalArgumentException ("class mismatch during set for attribute " +
                                          attributeName + ",\n object: " + graphObjectName +
                                          "\n expected " + expectedClass + 
                                          "\n got " + actualClass);

  } // initializeAttributeAsRequired
  //--------------------------------------------------------------------------------
  /**
   *  set an attribute of the specified name to the graph object with the specified name.
   *  the attribute may be any java type, or an array of any java type; it can be neither
   *  a primitive value nor an array of primitves.  if this attribute has not previously
   *  been assigned a class, either explicitly or implicitly, then the class is deduced from
   *  the object (or the first element in the array of objects).  once a class has been
   *  assigned for this attribute, every subsequent addition must be 
   *  an object of that same class.
   *
   * @param attributeName    eg, "expression", "GO molecular function level 4", "tissue count"
   * @param graphObjectName  the canonical name of a node or edge
   * @param obj              the value of this attribute, either a java object or an array of java
   *                         objects
   *
   * @throws IllegalArgumentException   if the class of obj (or of obj [0], if obj is an array)
   *                                    does not match the already assigned or deduced java class
   *                                    for this attribute
   */
  public boolean set (String attributeName, String graphObjectName, Object obj)
  {
    // if this is a new attribute, it needs special treatment:
    //
    //   - create a new HashMap (which is the value of the top-level hashmap, and
    //     which will hold <String graphObjectName> -> <Object obj> mappings
    //
    //   - sometimes people will explicitly assign the java class of this attribute, but
    //     other times we must deduce the class from the <obj>, allowing for the
    //     possiblity that it may be an array of objects, in which case we check the
    //     class of the first object in the array

    if (obj == null) return false;
    if(!implementsSerializable(obj.getClass())){
      throw new IllegalArgumentException("The class " + obj.getClass().getName()+ " of the object that represents the value for the attribute \"" 
                                         + attributeName + "\" must implement java.io.Serializable.");
    }
    initializeAttributeAsRequired (attributeName, graphObjectName, obj);
    HashMap attributeMap = (HashMap) map.get (attributeName);
    attributeMap.put (graphObjectName, obj);
    
    return true;

  } // set
  //--------------------------------------------------------------------------------
  public boolean append (String attributeName, String graphObjectName, Object obj)
  {
    // if this is a new attribute, it needs special treatment:
    //
    //   - create a new HashMap (which is the value of the top-level hashmap, and
    //     which will hold <String graphObjectName> -> <Object obj> mappings
    //
    //   - sometimes people will explicitly assign the java class of this attribute, but
    //     other times we must deduce the class from the <obj>, allowing for the
    //     possiblity that it may be an array of objects, in which case we check the
    //     class of the first object in the array

    if (obj == null) return false;

    if(!implementsSerializable(obj.getClass())){
      throw new IllegalArgumentException("The class " + obj.getClass().getName()+ " of the object that represents the value for the attribute \"" 
                                         + attributeName + "\" must implement java.io.Serializable.");
    }
    initializeAttributeAsRequired (attributeName, graphObjectName, obj);
    HashMap attributeMap = (HashMap) map.get (attributeName);

    Vector list = null;
    if (attributeMap.containsKey (graphObjectName)) {
      list = (Vector) attributeMap.get (graphObjectName);
    }
    else {
      list = new Vector ();
    }

    if (obj.getClass().isArray ()) {
      Object [] objAsArray = (Object []) obj;
      for (int i=0; i < objAsArray.length; i++)
        // if (!list.contains (objAsArray [i]))
        list.add (objAsArray [i]);
    }
    else { //if (!list.contains (obj)) {
      list.add (obj);
    }

    attributeMap.put (graphObjectName, list);

    return true;

  } // append
  //--------------------------------------------------------------------------------
  /**
   *  a convenience method; value will be promoted to Double 
   */
  public boolean set (String attributeName, String graphObjectName, double value)
  {
    return set (attributeName, graphObjectName, new Double (value));

  } // set
  //--------------------------------------------------------------------------------
  /**
   *  a convenience method allowing the addition of multiple different attributes for
   *  one graphObject at the same time.
   */
  public boolean set (String graphObjectName, HashMap bundle)
  {
    String [] keys = (String []) bundle.keySet().toArray (new String [0]);
    boolean success = true;
    for (int i=0; i < keys.length; i++) {
      String attributeName = keys [i];
      Object value = bundle.get (attributeName);
      if(!set (attributeName, graphObjectName, value)){
        success = false;
      }
    }

    return success;

  } // set
  //--------------------------------------------------------------------------------
  /**
   * @deprecated  use set instead
   * @see #set (GraphObjAttributes)
   */
  public void add (GraphObjAttributes attributes) 
  { 
    set (attributes); 
  }
  //--------------------------------------------------------------------------------
  /**
   * @deprecated  use set instead
   * @see #set (String, GraphObjAttributes, Object)
   */
  //--------------------------------------------------------------------------------
  public boolean add (String attributeName, String graphObjectName, Object obj)
  { 
    return set (attributeName, graphObjectName, obj);
    	
  }
  //--------------------------------------------------------------------------------
  /**
   * @deprecated  use set instead
   * @see #set (String, String, double)
   */
  public boolean add (String attributeName, String graphObjectName, double value)
  { 
    return set (attributeName, graphObjectName, value); 
  }
  //--------------------------------------------------------------------------------
  /**
   * @deprecated  use set instead
   * @see #set (String, HashMap)
   */
  public boolean add (String graphObjectName, HashMap bundle)
  { 
    return set (graphObjectName, bundle); 
  }
  //--------------------------------------------------------------------------------
  /**
   * the number of different attributes currently registered
   *
   * @deprecated  use numberOfAttributes instead
   * @see #numberOfAttributes
   */
  public int size ()
  {
    return numberOfAttributes ();
  }
  //--------------------------------------------------------------------------------
  /**
   * the number of different attributes currently registered
   *
   */
  public int numberOfAttributes ()
  {
    return map.size ();
  }
  //--------------------------------------------------------------------------------
  /**
   * get the names of all of the attributes
   *
   */
  public String [] getAttributeNames ()
  {
    return (String []) map.keySet().toArray (new String [0]);
  }
  //--------------------------------------------------------------------------------
  /**
   * return the canonical names of all objects with a given attribute.
   */
  public String [] getObjectNames (String attributeName)
  {
    HashMap attributeMap = getAttribute (attributeName);
    if (attributeMap == null)
      return new String [0];
  
    return (String []) attributeMap.keySet().toArray (new String [0]);

  } // getObjectNames
  //--------------------------------------------------------------------------------
  /**
   * return the unique values among the values of all objects with a given attribute.
   */
  public Object [] getUniqueValues (String attributeName)
  {
    HashMap hash = getAttribute (attributeName);
    if (hash == null)
      return null;
    Object [] allValues = (Object []) hash.values().toArray (new Object [0]);
    Vector nonredundantList = new Vector ();
    for (int i=0; i < allValues.length; i++)
      if (allValues [i].getClass() == (nonredundantList.getClass ())) {
        Object [] allValuesI = (Object []) ((Vector) allValues [i]).toArray (new Object [0]);
        for (int j=0; j < allValuesI.length; j++)
          if (!nonredundantList.contains (allValuesI [j]))
            nonredundantList.add (allValuesI [j]);
      }
      else if (allValues [i].getClass().isArray ()) {
        Object [] allValuesI = (Object []) allValues [i];
        for (int j=0; j < allValuesI.length; j++)
          if (!nonredundantList.contains (allValuesI [j]))
            nonredundantList.add (allValuesI [j]);
      }
      else { // allValues [i] is -not- a Vector
        if (!nonredundantList.contains (allValues [i]))
          nonredundantList.add (allValues [i]);
      }

    return (Object []) nonredundantList.toArray (new Object [0]);

  } // getUniqueValues
  //--------------------------------------------------------------------------------
  /**
   * return the unique Strings among the values of all objects with a given attribute.
   */
  public String [] getUniqueStringValues (String attributeName)
  {
    Object [] objs = getUniqueValues (attributeName);
    if (objs == null)
      return new String [0];

    String [] result = new String [objs.length];
    for (int i=0; i < objs.length; i++) 
      result [i] = (String) objs [i];

    return result;

  } // getUniqueValues
  //--------------------------------------------------------------------------------
  /**
   * return the number of graph objects with the specified attribute.
   */
  public int getObjectCount (String attributeName)
  {
    HashMap attributeMap = getAttribute (attributeName);
    if (attributeMap == null)
      return 0;
  
    return attributeMap.size ();

  } // getObjectCount
  //--------------------------------------------------------------------------------
  /**
   *  assign an arbitrary category name to the specified attribute  
   */
  public void setCategory (String attributeName, String newValue)
  {
    categoryMap.put (attributeName, newValue);
  }
  //--------------------------------------------------------------------------------
  /**
   *
   */
  public String getCategory (String attributeName)
  {
    return (String) categoryMap.get (attributeName);
  }
  //--------------------------------------------------------------------------------
  /**
   *  
   */
  public boolean hasAttribute (String attributeName)
  {
    return map.containsKey (attributeName);
  }
  //--------------------------------------------------------------------------------
  /**
   *  
   */
  public boolean hasAttribute (String attributeName, String graphObjName)
  {
    HashMap attributeMap = (HashMap) map.get (attributeName);
    if (attributeMap == null)
      return false;

    return attributeMap.containsKey (graphObjName);

  }
  //--------------------------------------------------------------------------------
  /**
   *  return a hash whose keys are graphObjectName Strings, and whose values are 
   *  a Vector of java objects the class of these objects, and
   *  the category of the attribute (annotation, data, URL) may be learned
   *  by calling getClass and getCategory
   *
   *  @return   a HashMap whose keys are graph object names (typically canonical names
   *            for nodes and edges) and whose values are Vectors of java objects.
   *
   *  @see #getClass
   *  @see #getCategory
   *  
   */
  public HashMap getAttribute (String attributeName)
  {
    return (HashMap) map.get (attributeName);
  }
  //--------------------------------------------------------------------------------
  /**
   *  remove the entire second level Hashmap whose key is the specified attributeName
   */
  public void deleteAttribute (String attributeName)
  {
    if (hasAttribute (attributeName))
      map.remove (attributeName);

  }
  //--------------------------------------------------------------------------------
  /**
   *  remove the specified attribute from the specified node or edge
   */
  public void deleteAttribute (String attributeName, String graphObjectName)
  {
    if (!hasAttribute (attributeName)) {
      return;
    }
    HashMap oneAttributeMap = getAttribute (attributeName);
    oneAttributeMap.remove (graphObjectName);

  }
  //--------------------------------------------------------------------------------
  /**
   *  remove the specified attribute value from the specified node or edge.
   *  there may be multiple values associated with the attribute, so search through
   *  the list, and if it is found, remove it
   */
  public void deleteAttributeValue (String attributeName, String graphObjectName, Object value)
  {
    if (!hasAttribute (attributeName)) return;
    Vector list = (Vector) getList (attributeName, graphObjectName);
 
    if (list.contains (value))
      list.remove (value);

  }
  //--------------------------------------------------------------------------------
  /**
   *  specify the class of this attribute.  all subsequently added attribute
   *  values must be of the exactly this class
   */
  public boolean setClass (String attributeName, Class attributeClass)
  {
    if(implementsSerializable(attributeClass) || attributeClass == null){
      classMap.put(attributeName,attributeClass);
      return true;
    }else{
      throw new IllegalArgumentException("Attribute class " + attributeClass.toString() + " must implement java.io.Serializable");
    }
	
  }
  //--------------------------------------------------------------------------------
  /**
   *  all attributes are lists (java.lang.Vector) sharing the same base type; discover
   *  and return that here
   */
  public Class getClass (String attributeName)
  {
    return (Class) classMap.get (attributeName);

  } // getClass
  //--------------------------------------------------------------------------------
  /**
   *  get all values associated with this graphObjectName and this attributeName
   *
   *  @return a java.util.Vector of size zero or more, containing java objects
   *          whose types may be learned via a call to getType
   *
   *  @see #getCategory
   */
  public Vector getList (String attributeName, String graphObjectName)
  {
    HashMap attributeMap = (HashMap) map.get (attributeName);
    if (attributeMap == null)
      return new Vector ();

    if (!attributeMap.containsKey (graphObjectName))
      return new Vector ();
 
    Object obj = attributeMap.get (graphObjectName);
    Vector tmp = new Vector ();
    if (obj.getClass() != tmp.getClass ()) {
      tmp.add (obj);
      return tmp;
    }
    else 
      return (Vector) attributeMap.get (graphObjectName);
    
  } // getList
  //--------------------------------------------------------------------------------
  /**
   *  for backwards compatibility:  the value of an attribute used to be
   *  strictly a single scalar; now -- even though attributes are all lists
   *  of scalars -- we support the old idiom by retrieving the first scalar
   *  from the list.
   */
  public Object getValue (String attributeName, String graphObjectName)
  {
    return get (attributeName, graphObjectName);
  }
  public Object get (String attributeName, String graphObjectName)
  {
   
    HashMap attributeMap = (HashMap) map.get (attributeName);
    if (attributeMap == null)
      return null;

    if (!attributeMap.containsKey (graphObjectName))
      return null;
 
    return attributeMap.get (graphObjectName);

  } // getValue
  //--------------------------------------------------------------------------------
  /**
   *  a convenience method:  convert the Vector of objects into an array
   */
  public Object [] getArrayValues (String attributeName, String graphObjectName)
  {
    Vector list = (Vector) getList (attributeName, graphObjectName);
    if (list == null) 
      return new Object [0];

    Object [] result = (Object []) list.toArray (new Object [0]);
    return result;
  }
  //--------------------------------------------------------------------------------
  /**
   *  a convenience method, useful if you are certain that the attribute stores
   *  Strings; convert the Vector of Objects into an array of Strings
   */
  public String [] getStringArrayValues (String attributeName, String graphObjectName)
  {
    Vector list = (Vector) getList (attributeName, graphObjectName);
    if (list == null) 
      return new String [0];

    String [] result = (String []) list.toArray (new String [0]);
    return result;

  }
  //--------------------------------------------------------------------------------
  /**
   *  construe the possibly multiple values of the attribute as a scalar Double,
   *  if possible
   */
  public Double getDoubleValue (String attributeName, String graphObjectName)
  {
    Object object = getValue (attributeName, graphObjectName); 
    if (object == null) 
      return null;

    try {
      if (object.getClass() == Class.forName ("java.util.Vector")) {
        Vector tmp = (Vector) object;
        if (tmp.size () < 1)
          object = null;
        else
          object = tmp.get (0);
      }
    }
    catch (ClassNotFoundException shouldNeverOccur) {;}

    return (Double) object;
  
  } // getDoubleValue
  //--------------------------------------------------------------------------------
  /**
   *  construe the possibly multiple values of the attribute as a scalar Integer,
   *  if possible
   */
  public Integer getIntegerValue (String attributeName, String graphObjectName)
  {
    Object object = getValue (attributeName, graphObjectName); 
    if (object == null) 
      return null;

    try {
      if (object.getClass() == Class.forName ("java.util.Vector")) {
        Vector tmp = (Vector) object;
        if (tmp.size () < 1)
          object = null;
        else
          object = tmp.get (0);
      }
    }
    catch (ClassNotFoundException shouldNeverOccur) {;}

    return (Integer) object;
  
  } // getIntegerValue
  //--------------------------------------------------------------------------------
  /**
   *  construe the possibly multiple values of the attribute as a scalar String
   */
  public String getStringValue (String attributeName, String graphObjectName)
  {
    Object object = getValue (attributeName, graphObjectName);
    // added by iliana - it is useful to know that this attribute has not been assigned a value
    // plus if object is null this crashes
    if(object == null){return null;}
    try {
      if (object.getClass() == Class.forName ("java.util.Vector")) {
        Vector tmp = (Vector) object;
        if (tmp.size () < 1)
          object = null;
        else
          object = tmp.get (0);
      }
    }
    catch (ClassNotFoundException shouldNeverOccur) {;}

    return object.toString ();
  }
  //--------------------------------------------------------------------------------
  /**
   *  deduce attribute name, category, and java class from the first
   *  line of the attributes file.   the form of the first line is
   *  <pre>
   *  attribute name  (category=xxxx) (class=yyyy)
   *  </pre>
   *  category and class are optional; if absent, class will be inferred
   *  (see deduceClass), and category set to DEFAULT_CATEGORY
   */
  public String processFileHeader (String text)
    // every attribute file must have, at minimum, the name attribute in the first line,
    // possibly with embedded spaces
    // in addition, the first line may have category and class information, as in
    //     homologene  (category=staticWebPage) (class=java.net.URL)
    // the present method extracts the mandatory attribute name, and the optional
    // category and class information.
    // 
    // note: category and class information, if present, are not only parsed here:  the
    //       information is also stored as appropriate in the current class data members
  {
    String attributeName = "";
    String attributeCategory = DEFAULT_CATEGORY;
    Class  attributeClass = null; 
  
    if (text.indexOf ("(") < 0)
      attributeName = text.trim ();
    else {
      StringTokenizer strtok = new StringTokenizer (text, "(");
      attributeName = strtok.nextToken ().trim();
      while (strtok.hasMoreElements ()) {
        String rawValuePair = strtok.nextToken().trim();
        if (!rawValuePair.endsWith (")")) continue;
        String valuePair = rawValuePair.substring (0,rawValuePair.length()-1);
        int locationOfEqualSign = valuePair.indexOf ("=");
        if (locationOfEqualSign < 0) continue;
        if (valuePair.endsWith ("=")) continue;
        StringTokenizer strtok2 = new StringTokenizer (valuePair, "=");
        String name = strtok2.nextToken ();
        String value = strtok2.nextToken ();
        if (name.equals ("category"))
          attributeCategory = value;
        if (name.equals ("class")) {
          try {
            attributeClass = Class.forName (value);
          }
          catch (ClassNotFoundException ignore) {;}
        } // if name == 'class'
      } // while strtok
    } // else: at least one (x=y) found

    setCategory (attributeName, attributeCategory);
    setClass (attributeName, attributeClass); // ******* Could fail *********

    return attributeName;

  } // processFileHeader
  //--------------------------------------------------------------------------------
  public void readAttributesFromFile (String filename)
    throws FileNotFoundException, IllegalArgumentException, NumberFormatException
  {
    readAttributesFromFile (null, "unknown",  filename, true);
  }
  //--------------------------------------------------------------------------------
  public void readAttributesFromFile (File file)
    throws FileNotFoundException, IllegalArgumentException, NumberFormatException
  {
    readAttributesFromFile (null, "unknown",  file.getPath (), true);
  }
  //--------------------------------------------------------------------------------
  //public void readAttributesFromFile(BioDataServer dataServer, String species, String filename)
  //   throws FileNotFoundException, IllegalArgumentException, NumberFormatException
  //{
  //  readAttributesFromFile (dataServer, species, new File (filename));
  //}
  //--------------------------------------------------------------------------------
  /**
   *  determine (heuristically) the most-specialized class instance which can be
   *  constructed from the supplied string.
   */
  static public Class deduceClass (String string)
  {
    String [] classNames = {"java.net.URL",
                            "java.lang.Integer",    // using this breaks the vizmapper, see below
                            "java.lang.Double",
                            "java.lang.String"};

    /** vizmapper error:
     * Exception in thread "main" java.lang.ClassCastException: java.lang.Double
     *  at java.lang.Integer.compareTo(Integer.java:913)
     * at cytoscape.vizmap.ContinuousMapper.getRangeValue(ContinuousMapper.java:78)
     */

    for (int i=0; i < classNames.length; i++) {
      try {
        Object obj = createInstanceFromString (Class.forName (classNames [i]), string);
        return obj.getClass ();
      }
      catch (Exception e) {
        ; // try the next class
      }
    } // for i

    return null;   

  } // deduceClass
  //--------------------------------------------------------------------------------
  /**
   *  given a string and a class, dynamically create an instance of that class from
   *  the string
   */
  static public Object createInstanceFromString (Class requestedClass, String ctorArg) 
    throws Exception
          
  {
    Class [] ctorArgsClasses = new Class [1];
    ctorArgsClasses [0] =  Class.forName ("java.lang.String");
    Object [] ctorArgs = new Object [1];
    ctorArgs [0] = new String (ctorArg);
    Constructor ctor = requestedClass.getConstructor (ctorArgsClasses);
    return ctor.newInstance (ctorArgs);

  } // createInstanceFromString
  //--------------------------------------------------------------------------------
  /**
   *  read attributes from a file.  there is one basic format for attribute files,
   *  but a few aspects of the format are flexible.
   *
   *  the simplest form looks like this:
   *  <pre>
   *  expresssion ratio
   *  geneA = 0.1
   *  geneB = 8.9
   *  ...
   *  geneZ = 23.2
   *  </pre>
   *  In this form, the reader
   *  <p>
   *
   */
  public void readAttributesFromFile (BioDataServer dataServer, String species, String filename, boolean canonicalize)
    throws FileNotFoundException, IllegalArgumentException, NumberFormatException
  {

    String rawText;
    try {
      if (filename.trim().startsWith ("jar://")) {
        TextJarReader reader = new TextJarReader (filename);
        reader.read ();
        rawText = reader.getText ();
      }
      else {
        TextFileReader reader = new TextFileReader (filename);
        reader.read ();
        rawText = reader.getText ();
      }
    }
    catch (Exception e0) {
      System.err.println ("-- Exception while reading attributes file " + filename);
      System.err.println (e0.getMessage ());
      e0.printStackTrace();
      return;
    }

    StringTokenizer lineTokenizer = new StringTokenizer (rawText, "\n");

    int lineNumber = 0;
    if (lineTokenizer.countTokens () < 2) 
      throw new IllegalArgumentException (filename + " must have at least 2 lines");

    String attributeName = processFileHeader (lineTokenizer.nextToken().trim ());
    boolean extractingFirstValue = true; 
    boolean attributeHasStringValue = true;   // he default

    while (lineTokenizer.hasMoreElements ()) {
      String newLine = (String) lineTokenizer.nextElement ();
      if (newLine.trim().startsWith ("#")) continue;
      lineNumber++;
      StringTokenizer strtok2 = new StringTokenizer (newLine, "=");
      if (strtok2.countTokens () < 2)
        throw new IllegalArgumentException ("cannot parse line number " + lineNumber +
                                            ":\n\t" + newLine);
      String graphObjectName = strtok2.nextToken().trim();
      if (canonicalize && dataServer != null){
        graphObjectName = dataServer.getCanonicalName (species, graphObjectName);
      }
      // System.out.println ("--- reading attribute for graphObjectName: " + graphObjectName);
      String rawString = newLine.substring (newLine.indexOf ("=") + 1).trim();
      String [] rawList;
      boolean isList = false;
      if (Misc.isList (rawString, "(", ")", "::")) {
        rawList = Misc.parseList (rawString, "(", ")", "::");
        isList = true;
      }
      else {
        rawList = new String [1];
        rawList [0] = rawString;
      }
      if (extractingFirstValue && getClass (attributeName) == null) {
        extractingFirstValue = false;  // henceforth
        Class deducedClass = deduceClass (rawList [0]);
        setClass (attributeName, deducedClass); // ***** Could fail ******* //
      }
      Object [] objs = new Object [rawList.length];
      for (int i=0; i < rawList.length; i++) {
        try {
          objs [i] = createInstanceFromString (getClass (attributeName), rawList [i]);
          if (isList)
            append (attributeName, graphObjectName, objs [i]);
          else 
            set (attributeName, graphObjectName, objs [i]);
        }
        catch (Exception e) {
          throw new IllegalArgumentException ("\tcould not create an instance of\n" +
                                              getClass (attributeName) + " from\n" +
                                              rawList [i]);
        } // catch
      } // for i
    } // while strtok finds new lines

  } // readAttributesFromFile
  //--------------------------------------------------------------------------------
  /**
   *  return attributeName/attributeClass pairs, for every known attribute
   */
  public HashMap getSummary ()
  {
    HashMap result = new HashMap ();
    String [] attributeNames = getAttributeNames ();

    for (int i=0; i < attributeNames.length; i++) {
      String attributeName = attributeNames [i];
      String firstObjectName = getObjectNames (attributeName)[0];
      Object firstValue = getValue (attributeName, firstObjectName);
      result.put (attributeName, firstValue.getClass ());
    } // for i

    return result;

  } // getSummary
  //--------------------------------------------------------------------------------
  /**
   *  for the graphObject named by canonicalName, extract and return all attributes
   *
   *  @see #getValue
   */
  public HashMap getAttributes  (String canonicalName)
  {
    HashMap bundle = new HashMap ();
    String [] allAttributes = getAttributeNames ();
    for (int i=0; i < allAttributes.length; i++) {
      String attributeName = allAttributes [i];
      Object value = getValue (attributeName, canonicalName);
      if (value != null)
        bundle.put (attributeName, value);
    } // for i

    return bundle;

  } // getAttributes
  //--------------------------------------------------------------------------------
  /**
   *  multiple GraphObj's (edges in particular) may have the same name; this method
   *  counts names which begin with the same string.  for instance
   *  there may be two edges between the same pair of nodes:
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G
   *    VNG0382G (geneFusion) VNG1232G
   *  </pre>
   * the first pair encountered may be give the name
   *  
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G
   *  </pre>
   * we may wish to give the second pair the name
   *
   *  <pre>
   *    VNG0382G (geneFusion) VNG1230G_1
   *  </pre>
   * this method provides a count of matches based on 
   * String.startsWith ("VNG0382G (geneFusion) VNG1230G") which solves the problem
   * of all subsequent duplicates simply append a number to the base name.
   * <p>
   * whoever calls this method must construct the new object's name -first-
   * and then is expected to append "_N" where N is the value returned here
   * (and of course, if the result is 0, there is no need to append '_0'
   */
  public int countIdentical (String graphObjectName)  
  {
    if(countIdMap == null){
      countIdMap = new HashMap();
    }
    Integer count = (Integer) countIdMap.get (graphObjectName);
    if (count == null) 
      count = new Integer(0);
    
    // update the counter as well
    countIdMap.put (graphObjectName, new Integer (count.intValue() + 1));
    return count.intValue();
  } 
  //--------------------------------------------------------------------------------
  /**
   *  create a human readable version.
   */
  public String toString ()
  {
    StringBuffer sb = new StringBuffer ();

    sb.append ("\n-- canonicalNames: " + nameFinder.size ());
    String nameFinderString = nameFinder.toString ();
    StringTokenizer strtok = new StringTokenizer (nameFinderString, ",");
    while (strtok.hasMoreElements ()) {
      sb.append ("\n");
      sb.append (strtok.nextToken ());
    }
  
    String [] names = getAttributeNames ();
    sb.append ("\n-- attributes: " + names.length + "\n");
    for (int i=0; i < names.length; i++) {
      sb.append ("attribute " + i + ": " + names [i] + "  ");
      Class attributeClass = getClass (names [i]);
      String category = getCategory (names [i]);
      sb.append ("(class:" + attributeClass + ") ");
      sb.append ("(category: " + category + ")");
      sb.append ("\n");
      String [] keys = getObjectNames (names [i]);
      for (int j=0; j < keys.length; j++) {
        //Object value = getValue (names [i], keys [j]);
        Object [] arrayValue = getArrayValues (names [i], keys [j]);
        sb.append ("   " + keys [j] + " -> (");
        for (int k=0; k < arrayValue.length; k++) {
          sb.append (arrayValue [k]);
          if (k < arrayValue.length - 1)
            sb.append (", ");
        } // for k
        sb.append (")\n");
      } // for j
    } // for i

    return sb.toString ();

  } // toString


  /**
   * Whether or not the given class implements java.io.Serializable
   */
  protected boolean implementsSerializable(Class objClass){
    
    if(objClass == null){
      return false;
    }
    
    Class [] interfaces = objClass.getInterfaces();
    Class serializable = null;
    try{
      serializable = Class.forName("java.io.Serializable");
    }catch(ClassNotFoundException e){;}
	
    for(int i = 0; i < interfaces.length; i++){
      if(serializable.isAssignableFrom(interfaces[i])){
        return true;
      }
    }// for

    // if we got here, that means that this class does not implement Serializable, but maybe its parent does
    return implementsSerializable(objClass.getSuperclass());
	
  }//implementsSerializable
  //--------------------------------------------------------------------------------
  public static String [] unpackPossiblyCompoundStringAttributeValue (Object value)
  {
    String [] result = new String [0];
    try {
      if (value.getClass () == Class.forName ("java.lang.String")) {
        result = new String [1];
        result [0] = (String) value;
      }    
      else if (value.getClass () == Class.forName ("[Ljava.lang.String;")) {
        result = (String []) value; 
      }
      else if (value.getClass () == Class.forName ("java.util.Vector")) {
        Vector tmp = (Vector) value;
        result = (String []) tmp.toArray (new String [0]);
      }
      else {
        String msg = "AnnotationGui.unpackPossiblyCompoundAttributeValue, unrecognized class: " + 
          value.getClass ();
        System.err.println (msg);
      }
    } // try
    catch (ClassNotFoundException ignore) {
      ignore.printStackTrace ();
    }

    return result;

  } // unpackPossiblyCompoundAttributeValue
  //----------------------------------------------------------------------------------------
  private void writeObject(ObjectOutputStream out) throws Exception{
    // super.writeObject gets called automatically
    System.out.println("Writing GraphObjAttributes...");
    System.out.flush();
    out.defaultWriteObject();
    System.out.println("Wrote GraphObjAttributes");
    System.out.flush();
  }//writeObject
  //--------------------------------------------------------------------------------
} // class GraphObjAttributes





}
