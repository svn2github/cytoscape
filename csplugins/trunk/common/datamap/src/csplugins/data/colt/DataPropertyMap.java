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

//Data Cube Import ////////////////////
import csplugins.trial.pshannon.dataCube.DataMatrix;
import csplugins.trial.pshannon.dataCube.DataMatrixLens;


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
public class DataPropertyMap 
  implements DataMatrixLens, DataMatrix {

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
  
  int[] childColumns;
  int[] childRows;


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
   * Copy the contents of this matrix to a new matrix
   * of a different size. This method will not allow \
   * for the matrix to shrink 
   * 
   */
  protected void growMatrix ( int rows, int cols ) {

    if ( dataMatrix.rows() > rows || dataMatrix.columns() > cols ) {
      // must specify a bigger matrix
      return;
    }

    // create new matrix
    ObjectMatrix2D new_matrix = create2DMatrix( rows, cols );
    // copy old values
    IntArrayList row_list = new IntArrayList();
    IntArrayList column_list = new IntArrayList();
    ObjectArrayList value_list = new ObjectArrayList();
    dataMatrix.getNonZeros( row_list, column_list, value_list );

    int num_values = value_list.size();
    for( int value_i = 0; value_i < num_values; value_i++ ) {
      new_node_data.setQuick(
        row_list.getQuick( value_i ),
        column_list.getQuick( value_i ),
        value_list.getQuick( value_i )
      );
    } // End for each non-zero-cell, copy it into the new array.

    dataMatrix = new_matrix;

  }


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


  //----------------------------------------//
  // Implements DataMatrix 
  //----------------------------------------//

  /**
   * @return a default name
   */
  public String getName () {
    if ( parentDataPropertyMap == null ) {
      return "TopLevel DataPropertyMap";
    } 
    return "Child DataPropertyMap";
  }
 
  /**
   * Set size makes a call to growMatrix which will
   * make a new matrix and repopulate it with the 
   * new data.
   */
  public void setSize ( int rows, int columns ) {
    growMatrix( rows, columns );
  }

  /**
   * This method is a wrapper around the more general
   * "setValue"
   */
  public void set ( int row, int column, double value ) {
    dataMatrix.setQuick( row, column, new Double( value ) );
  }

  /**
   * This method populates the 1D attributeNameMatrix with new values,
   * and update the attributeNameMap with the appropriate values.
   */
  public void setColumnTitles ( String [] new_titles ) {
    this.attributeNameMatrix = create1DMatrix( new_titles );
    this.attributeNameMap = createStringIntHashMap( this.attributeNameMatrix );
  }

  public void setRowTitles ( String [] new_names ) {
    this.identifierMatrix = create1DMatrix( new_names  );
    this.sharedIndentifierUniqueIntMap = createStringIntHashMap( this.identifierMatrix );
  }

  public int getRowCount () {
    return dataMatrix.rows();
  }

  /**
   * returns a count of all enabled columns
   */
  public int getColumnCount () {
    return dataMatrix.columns();
  }

  public double get ( int row, int column ) {
    return ( ( Double )dataMatrix.getQuick( row, column ) ).doubleValue();
  }
 
  /**
   * returns the original data, ignoring swapped and/or disabled columns.
   */
  public double[] getUntransformed ( int row ) {
    ObjectMatrix1D row_data = dataMatrix.viewRow( row );
    double[] double_data = new double[ row_data.size() ];
    for ( int i = 0; i < double_data.length; ++i ) {
      double_data[i] = row_data.getQuick( i );
    }
    return double_data;
  }

  public double[] get ( int row ) {
    ObjectMatrix1D row_data = dataMatrix.viewRow( row );
    double[] double_data = new double[ row_data.size() ];
    for ( int i = 0; i < double_data.length; ++i ) {
      double_data[i] = row_data.getQuick( i );
    }
    return double_data;
  }

  public double[] get ( String rowName ) {
    int uid = sharedIndentifierUniqueIntMap.get( rowName );
    uid = childUniqueIDMap.get( uid );
    ObjectMatrix1D row_data = dataMatrix.viewRow( uid );
    double[] double_data = new double[ row_data.size() ];
    for ( int i = 0; i < double_data.length; ++i ) {
      double_data[i] = row_data.getQuick( i );
    }
    return double_data;
  }

  public String[] getRowTitles () {
    // not exactly sure what to do, since
    // rowTitles are not set in stone.
    return null;
  }

  public String[] getUnmaskedColumnTitles () {
    String[] column_names = new String[ attributeNameMatrix.size() ];
    for ( int i = 0; i < column_names.length; ++i ) {
      column_names[i] = attributeNameMatrix.getQuick( i );
    }
    return column_names;
  }

  public String[] getColumnTitles () {
    String[] column_names = new String[ attributeNameMatrix.size() ];
    for ( int i = 0; i < column_names.length; ++i ) {
      column_names[i] = attributeNameMatrix.getQuick( i );
    }
    return column_names;
  }

  public String toString () {
    return dataMatrix.toString();
  }
  

  //----------------------------------------//
  // DataMatrixLens Implementation
  //----------------------------------------//

  /**
   * I feel that this should restore the Lens to the
   * state that it had before any switching and stuff.
   */
  public void clear ();
  

  /**
   * Returns the name of the underlying matrix.
   */
  public String getMatrixName () {
    return getName();
  }
 

  /**
   * Sets  the state (enabled or disabled) of a given column.
   * 
   * This is tricky as it does not necessarily make sense for
   * having a view...
   * 
   * @param column The column to change the state of.
   * @param newState Whether the column should be enabled.
   */
  public void setColumnState ( int column, boolean newState ) {

    
    
  }



  /**
   * Returns an array of booleans, one for each column, indicating whether
   * the given column is enabled.
   * @return
   */
  public boolean[] getColumnState ();

  /**
   * Returns a boolean indicating whether the specified column is enabled.
   * @param column The column to check.
   * @return 
   */
  public boolean getColumnState ( int column );

  /**
   * Moves the column at position <code>from</code> to 
   * position <code>to</code>.
   * @param from
   * @param to
   */
  public void swapColumnOrder ( int from, int to );


  /**
   * Returns the order (position) of the specified column. 
   * 
   * @param column The original position of the column in the underlying matrix.
   * @return The current position of that column.
   */
  public int getColumnOrder ( int column );

  /**
   * Returns the number of columns, regardless of how many are enabled.
   * @return
   */
  public int getRawColumnCount ();


  /**
   * Returns the number of enabled columns.
   * @return
   */
  public int getEnabledColumnCount ();
  

  /**
   * Returns the number of selected rows. 
   * @return
   */
  public int getSelectedRowCount ();

  /**
   * Returns a list of indexes into the underlying matrix representing
   * the current user selection. Functionally equivalent to the method of the
   * same name in the JTable class.
   * @return
   */
  public int[] getSelectedRowIndexes ();

  /**
   * Returns the total number of rows in the matrix.
   * @return 
   */
  public int getRawRowCount ();

  /**
   * Sets the user's selection of rows.
   * 
   * @param selectedRows An array of the row indices in the underlying matrix
   * which are to be selected.
   */
  public void setSelectedRows ( int[] selectedRows );


  /**
   * Returns a row from the underlying matrix, unaffected by user changes to 
   * column order, state, or row selection.
   * @param row the index of the row to return.
   * @return
   */
  public double[] getRaw ( int row );

  /**
   * Returns a row from the underlying matrix, unaffected by user changes to 
   * column order, state, or row selection. 
   * @param rowName The name of the row to return
   * @return
   */
  public double[] getRaw ( String rowName );

  /**
   * Returns a double value from the underlying matrix, unaffected
   * by user changes to column order, state, or row selection.
   * @param row The row to retrieve a value from
   * @param column The column to retrieve a value from
   * @return
   */
  public double getRaw ( int row, int column );

  /**
   * Returns a row which reflects user changes to column order and state,
   * but not selected rows.
   * @param row the index of the row to return
   * @return
   */
  public double[] getFromAll ( int row );

  /**
   * Returns a row which reflects user changes to column order and state,
   * but not selected rows.
   * @param rowName The name of the row to return 
   * @return
   */
  public double[] getFromAll ( String rowName );

  /**
   * Returns a double value which reflects user changes to column order and state,
   * but not selected rows. 
   * @param row The index of the row from which to retrieve a value
   * @param column The index of the column from which to retrieve a value
   * @return
   */
  public double getFromAll ( int row, int column );

  /**
   * Returns a row which reflects user changes to column order and state,
   * AND row selection. 
   * @param row The index, within the subset of selected rows, of the desired row.
   * @return
   */
  public double[] getFromSelected ( int row );

  /**
   * Returns a row which reflects user changes to column order and state,
   * AND row selection
   * @param rowName The name of the row to return
   * @return
   */
  public double[] getFromSelected ( String rowName );

  public double getFromSelected ( int row, int column );

  /**
   * This method takes as its argument an int representing an index of 
   * one of the selected rows. It returns the index of that row in the
   * underlying matrix. Do we really need a method for this?
   * 
   * TODO - find a better name for this method.
   * @param index
   * @return
   */
  private int getRowIndexFromSelection ( int index );



  /**
   *  use column ordering, and column status, to create and return
   *  a possibly transformed view of the data row
   */
  public double [] adjustForColumnOrderAndState ( double [] row );
  

  /**
   *  use column titles to create and return
   *  a possibly transformed view of the column titles
   */
  public String [] adjustForColumnOrderAndState (String [] columnTitles);
    // <columnTitles> includes column 0, which never changes its position.
    // so start, below, by creating an array which is 1 element shorter, and
    // therefore equal in length to the number of data columns in the matrix.
    // add the column zero label to the list, then traverse the data column
    // titles, placing them as the columnOrder & columnStatus info decree.
    // adjustForColumnOrderAndState (String [])


  /**
   * Returns all row titles regardless of user selection.
   * @return
   */
  public String[] getAllRowTitles ();

  /**
   * Return the row titles in the user selection.
   * @return
   */
  public String[] getSelectedRowTitles ();

  /**
   * Returns the unfiltered column titles in the unfiltered column order.
   * @deprecated Use getAllColumnTitles() instead.
   */
  public String[] getUnmaskedColumnTitles ();
 
  /**
   * Returns the unfiltered column titles in the unfiltered column order.
   */
  public String[] getAllColumnTitles ();

  /**
   * Returns the filtered column titles, along with the header column
   * which is always at position 0. 
   * The following always holds true:
   * getFilteredColumnTitles().length = getEnabledColumnCount() + 1;
   * @return
   */
  public String [] getFilteredColumnTitles ();

  /**
   * Returns a string representation of this DataMatrixLens
   * 
   * @param allRows Whether to show all rows (true) or just the selected rows.
   * @return
   */
  public String toString ( boolean allRows );


}
