package datamap;

import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.ObjectMatrix1D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectFactory1D;
import cern.colt.map.PrimeFinder;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import com.sosnoski.util.hashmap.StringIntHashMap;

import java.util.Arrays;
import java.util.ArrayList;


/**
 * The DataPropertyMap holds all of the data for  
 * SharedIdentifiable objects. 
 *
 * The Attributes are stored as Columns, and the 
 * SharedIdentifiable as Rows.
 *
 * It is best to access the DataPropertyMap by using 
 * the default map: DataPropertyMap.defaultMap()
 */
public  class DataPropertyMap {


  protected static int DEFAULT_MATRIX_SIZE = 29;

  /**
   * The Default DataPropertyMap that will most likely
   * be the parent of many PropertyMaps, although indpendent
   * creation is possible.
   */
  protected static DataPropertyMap defaultDataPropertyMap = null;

  /**
   * Many DataPropertyMaps will have a parent that is a DataPropertyMap.  
   * Often, this will be the Default DataPropertyMap, in case it isn't
   * you can keep track of that parent.
   */
  protected DataPropertyMap parentDataPropertyMap;


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
   * The names of the Attributes are stored in a 1D Matrix.  
   * This will allow for the fast lookup of attribute names
   * given the indices.  The normal use will be to find out which 
   * attributes A SharedIdentifiable, or group of SharedIdentifiable
   * objects has, find the non-null columns, and return the list of 
   * available attributes.  This is alwasy equal to the number of columns
   * in the dataMatrix.
   * 
   * By having a colt matrix, it will also be easy to put a subset into
   * a returned data set.
   */ 
  protected ObjectMatrix1D attributeNameMatrix;


  /**
   * The uniqueIDs that are the ids of the SharedIdentifiable objects 
   * are stored as a 1D Matrix.  This is to facilitate the creation of
   * subsets. This is always equal to the number of rows in the dataMatrix.
   */
  protected ObjectMatrix1D identifierMatrix;

  /**
   * The relationship between the SharedIdentifiable objects and
   * their uniqueID number is stored as a StringIntHashMap.
   */
  protected StringIntHashMap sharedIndentifierUniqueIntMap;
  protected StringIntHashMap attributeNameMap;
  
  
  //----------------------------------------//
  // Constructors
  //----------------------------------------//
  

  /**
   * This constructor should only be used when wishing to create a 
   * DataPropertyMap that has a parent other than the Default.
   *
   * @param parent_data_property_map The non-default parent DataPropertyMap
   * @param shared_identifiables the indices of the shared_identifiables that will be in this DataPropertyMap
   * @param attributes the indices of the attributes that will be in this DataPropertyMap
   */
  public DataPropertyMap ( DataPropertyMap parent_data_property_map, 
                           int[] shared_identifiables,
                           int[] attributes 
                           ) {
    // assign the parent
    this.parentDataPropertyMap = parent_data_property_map;
    
    // assign the matrices to the views of the parent matrices.
    this.dataMatrix = parentDataPropertyMap.
      getDataMatrix().
      viewSelection( shared_identifiables, attributes );
    this.attributeNameMatrix = parentDataPropertyMap.
      getAttributeNameMatrix().
      viewSelection( attributes );
    this.identifierMatrix = parentDataPropertyMap.
      getIdentifierMatrix().
      viewSelection( shared_identifiables );

    // link the other variables to those of the parent.
    this.sharedIndentifierUniqueIntMap = parentDataPropertyMap.
      getSharedIndentifierUniqueIntMap();
    this.attributeNameMap = parentDataPropertyMap.
      getAttributeNameMap();
    
  }


  /**
   * This internal constructor will be used when @see{getData} is called.  It 
   * is almost identical to the more generic constructor, but is less flexable.
   *  
   * @param shared_identifiables the indices of the shared_identifiables that will be in this DataPropertyMap
   * @param attributes the indices of the attributes that will be in this DataPropertyMap
   */
  protected DataPropertyMap (  int[] shared_identifiables,
                               int[] attributes 
                               ) {

    // assign the matrices to the views of the parent matrices.
    this.dataMatrix = defaultMap().
      getDataMatrix().
      viewSelection( shared_identifiables, attributes );
    this.attributeNameMatrix = defaultMap().
      getAttributeNameMatrix().
      viewSelection( attributes );
    this.identifierMatrix = defaultMap().
      getIdentifierMatrix().
      viewSelection( shared_identifiables );

    // link the other variables to those of the parent.
    this.sharedIndentifierUniqueIntMap = defaultMap().
      getSharedIndentifierUniqueIntMap();
    this.attributeNameMap = defaultMap().
      getAttributeNameMap();
  }


  /**
   * This contstructor is used for the initial creation of the 
   * defaultDataPropertyMap, but can also be used to create 
   * additional DataPropertyMaps, if desired.
   */
  public DataPropertyMap () {
    this.dataMatrix = create2DMatrix();
    this.attributeNameMatrix = create1DMatrix();
    this.identifierMatrix = create1DMatrix();
    this.sharedIndentifierUniqueIntMap = createStringIntHashMap();
  }


  /**
   * This constructor will allow for the creation of a DataPropertyMap
   * that is ready to accomodate an already known size.
   */
  public DataPropertyMap ( int shared_identifiables , int attributes ) {
    this.dataMatrix = create2DMatrix( shared_identifiables, attributes );
    this.attributeNameMatrix = create1DMatrix( attributes );
    this.identifierMatrix = create1DMatrix( shared_identifiables );
    this.sharedIndentifierUniqueIntMap = createStringIntHashMap( shared_identifiables );
  }

  /**
   * This Constructor will create a DataPropertyMap that is initialized 
   * with the given set of data.
   */
  public DataPropertyMap ( String[] shared_identifiable_names, 
                           String[] attribute_names,
                           Object[][] data 
                           ) {

    this.dataMatrix = create2DMatrix( data );
    this.identifierMatrix = create1DMatrix( shared_identifiable_names  );
    this.attributeNameMatrix = create1DMatrix( attribute_names );
    this.sharedIndentifierUniqueIntMap = createStringIntHashMap( this.identifierMatrix );
    this.attributeNameMap = createStringIntHashMap( this.attributeNameMatrix );

  }

  //----------------------------------------//
  // Accessor Methods
  //----------------------------------------//

  public static DataPropertyMap defaultMap () {
    if ( defaultDataPropertyMap == null ) {
      defaultDataPropertyMap = new DataPropertyMap();
    }
    return defaultDataPropertyMap;
  }

  protected ObjectMatrix2D getDataMatrix () {
    return dataMatrix;
  }

  protected ObjectMatrix1D getAttributeNameMatrix () {
    return attributeNameMatrix;
  }

  protected StringIntHashMap getAttributeNameMap () {
    return attributeNameMap;
  }

  protected StringIntHashMap getSharedIndentifierUniqueIntMap () {
    return sharedIndentifierUniqueIntMap;
  }

  protected ObjectMatrix1D getIdentifierMatrix () {
    return identifierMatrix;
  }

  //----------------------------------------//
  // Data Information Methods
  //----------------------------------------//

  /**
   * Return All of the available attributes
   */
  public String[] getAttributes () {
    return ( String[] )attributeNameMatrix.toArray();
  }

  /**
   * Return the available attributes for the given 
   * uniqueIDs
   */
  public String[] getAttributes ( int[] uniqueIDs ) {

    int col_size = dataMatrix.columns();
    int[] all_columns = new int[ col_size ];
    int[] used_columns = new int[ col_size ];
    Arrays.fill( used_columns, 0 );
    for ( int i = 0; i < col_size; ++i ) {
      all_columns[i] = i;
    }
    
    ObjectMatrix2D column_calc = dataMatrix.viewSelection( uniqueIDs, all_columns );
    
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
    ObjectMatrix1D used_attributes = attributeNameMatrix.viewSelection( col.elements() );
    return ( String[] )used_attributes.toArray();
     
  }
  
  /**
   * Return the number of Attributes
   */
  public int getAttributeCount () {
    return dataMatrix.columns();
  }

  /**
   * Return all of the uniqueID ints
   */
  public int[] getUniqueIDIndices ();

  /**
   * Return all aliases of SharedIdentifiables
   */
  public String[] getSharedIndentifiableArray ();
    

  


  //----------------------------------------//
  // Data Loading and Maintentance Methods
  //----------------------------------------//


  /**
   * A given string will return an int for 
   * the given node.
   */
  public int addUniqueID ( String id );
  
  /**
   * Since nodes are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special node access methods.  This can be gotten
   * around by sending a String that looks like :
   * "Node: <i>root graph index</i>", however, a map
   * will be maintained of all the node mappings
   */
  public int addNode ( int root_graph_index );

  /**
   * Since edges are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special edge access methods.  This can be gotten
   * around by sending a String that looks like :
   * "Edge: <i>root graph index</i>", however, a map
   * will be maintained of all the edge mappings
   */
  public int addEdge ( int root_graph_index );


  //--------------------//
  // Single triplet settings

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public int set ( int uniqueID, String attribute, Object value );

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public int set ( String alias, String attribute, Object value );


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public int set ( SharedIdentifiable si, String attribute, Object value );

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public int set ( Object si, String attribute, Object value );

   /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   *
   * by setting the "multi_value" boolean to true, an 
   * array of values will be maintained for this attribute,
   * for this uniqueID.
   */
  public int set ( int uniqueID, String attribute, Object value, boolean multi_value );

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   *
   * by setting the "multi_value" boolean to true, an 
   * array of values will be maintained for this attribute,
   * for this uniqueID.
   */
  public int set ( String alias, String attribute, Object value, boolean multi_value );


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   *
   * by setting the "multi_value" boolean to true, an 
   * array of values will be maintained for this attribute,
   * for this uniqueID.
   */
  public int set ( SharedIdentifiable si, String attribute, Object value, boolean multi_value );

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   *
   * by setting the "multi_value" boolean to true, an 
   * array of values will be maintained for this attribute,
   * for this uniqueID.
   */
  public int set ( Object si, String attribute, Object value, boolean multi_value );

  
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
  public int[][] set ( int[] uniqueIDs, String[] attributes, Object[][] values ) {

    if ( uniqueIDs.length != values.length ) {
      return null;
    }

    // record all the errors that we generate
    IntArrayList error_uids = new IntArrayList();
    // this list will hold IntArrayLists of problem attributes
    ArrayList error_attributes = new ArrayList();

    // this will be the uniqueID that we are working with
    int uid;
    // attributeID being used
    int attribute;

    for ( int attribute_i = 0; attribute_i < attributes.length; ++attribute_i ) {
      attribute = getAttributeID( attributes[ attribute_i ] );
      for ( int uid_i = 0; uid_i < uniqueIDs.length; ++uid_i ) {
        uid = uniqueIDs[ uid_i ];
        if ( values[attribute_i].length == attributes.length ) {
          // set the passed value for this uid
          set( uid, attribute, values[attribute_i][uid_i] );
        } else if ( values[attribute_i].length == 1 ) {
          // set the default value
          set( uid, attribute, values[attribute_i][0] );
        } else {
          // the array size was wrong
          // TODO: error reporting
        } 
      } // end uid iteration
    } // end attribute iteration
  }

  //----------------------------------------//
  // Data Access Methods
  //----------------------------------------//


  //--------------------//
  // Singelton Methods

  /**
   *  Return one value that corresponds to the given
   * uniqueID and attribute ID
   */
  public Object getValue ( int uniqueID, int attribute_id ) {
    return dataMatrix.getQuick( uniqueID, attribute_id );
  }

  /**
   * Return one value that corresponds to the given 
   * uniqueID and Attribute
   */
  public Object getValue ( int uniqueID, String attribute ) {
    return dataMatrix.getQuick( uniqueID, attributeNameMap.get( attribute ) );
  }

  /**
   * Return one value that corresponds to the given
   * SharedIdentifiable object and attribute
   */
  public Object getValue ( SharedIdentifiable ident, String attribute ) {
    return dataMatrix.getQuick( sharedIndentifierUniqueIntMap.get( ident.getIdentifier() ),
                                attributeNameMap.get( attribute ) );
  }

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute.
   */
  public Object getValue ( String ident, String attribute ) {
    return dataMatrix.getQuick( sharedIndentifierUniqueIntMap.get( ident ),
                                attributeNameMap.get( attribute ) );
  }

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute ID
   */
  public Object getValue ( String ident, int attribute_id ) {
    return dataMatrix.getQuick( sharedIndentifierUniqueIntMap.get( ident ), attribute_id );
  }

  /**
   * Return one value that corresponds to the given
   *  SharedIdentifiable object and attribute ID
   */
  public Object getValue ( SharedIdentifiable ident, int attribute_id ) {
    return dataMatrix.getQuick( sharedIndentifierUniqueIntMap.get( ident.getIdentifier() ),
                                attribute_id );
  }

  
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
  public DataPropertyMap getData ( int[] shared_identifiables, int[] attributes ) {
    return new DataPropertyMap( shared_identifiables, attributes );
  }
   
  /**
   * This will return a DataPropertyMap that only contains the requested 
   * shared_identifiables and attributes
   *<BR><BR>
   *<B>Note:</B> if the same index is given twice, you will get back a DataPropertyMap that
   * contains two references to that index.
   */
  public DataPropertyMap getData ( String[] shared_identifiables, String[] attributes ) {
    IntArrayList idents = new IntArrayList( shared_identifiables.length );
    IntArrayList attrib = new IntArrayList( attributes.length );
    for ( int i = 0; i < shared_identifiables.length; ++i ) {
      idents.add( sharedIndentifierUniqueIntMap.get( shared_identifiables[i] ) );
    }
    for ( int i = 0; i < attributes.length; ++i ) {
      attrib.add( attributeNameMap.get( attributes[i] ) );
    }
    idents.trimToSize();
    attrib.trimToSize();

    return new DataPropertyMap( idents.elements(), attrib.elements() );
  }

  
  //----------------------------------------//
  // Factory Creation Methods
  //----------------------------------------//

  /**
   * Will Create an empty data matrix of a default size.
   */
  protected ObjectMatrix2D create2DMatrix () {
    return ObjectFactory2D.sparse.make( DEFAULT_MATRIX_SIZE, DEFAULT_MATRIX_SIZE );
  }

  /**
   * Will create a DataMatrix of the appropriate size given
   * sizes for the rows and columns.
   */
  protected ObjectMatrix2D create2DMatrix ( int rows, int cols ) {
    return ObjectFactory2D.sparse.make( rows, cols );
  }

  /**
   * Will create a DataMatrix that is filled with the given data
   */
  protected ObjectMatrix2D create2DMatrix ( Object[][] data ) {
    return ObjectFactory2D.sparse.make( data );
  }


  /**
   * Create a 1D matrix of a default size
   */
  protected ObjectMatrix1D create1DMatrix () {
    return ObjectFactory1D.dense.make( DEFAULT_MATRIX_SIZE );
  }

  /**
   * Create a 1D matrix of the given size 
   */
  protected ObjectMatrix1D create1DMatrix ( int size ) {
    return ObjectFactory1D.dense.make( size );
  }

  /**
   * Create a 1D matrix filled with the given data
   */
  protected ObjectMatrix1D create1DMatrix ( Object[] data ) {
    return ObjectFactory1D.dense.make( data );
  }
  
  /**
   * Create a new StringIntHashMap of a default size, the actual
   * size will be the next closest prime to accomadate the 
   * least number of collisions.
   */
  protected StringIntHashMap createStringIntHashMap () {
    return new StringIntHashMap( PrimeFinder.nextPrime( DEFAULT_MATRIX_SIZE ) );
  }

  /**
   * Create a new StringIntHashMap of the given size. the actual
   * size will be the next closest prime to accomadate the 
   * least number of collisions.
   */
  protected StringIntHashMap createStringIntHashMap ( int size ) {
    return new StringIntHashMap( PrimeFinder.nextPrime( size ) );
  }

  /**
   * Create and populate a StringIntHashMap based on the 
   * given 1DMatrix.  The keys will be the contents of the
   * matrix, the values the location of the string in the matrix.
   */
  protected StringIntHashMap createStringIntHashMap ( ObjectMatrix1D matrix ) {
    IntArrayList index_list = new IntArrayList();
    ObjectArrayList value_list = new ObjectArrayList();
    matrix.getNonZeros( index_list, value_list );
    StringIntHashMap string_int_hash_map = new StringIntHashMap( PrimeFinder.nextPrime( value_list.size() ) );
    for ( int i = 0; i < value_list.size(); ++i ) {
      string_int_hash_map.add( ( String )value_list.get( i ),
                               index_list.get( i ) );
    }
    return string_int_hash_map;
  }

}
