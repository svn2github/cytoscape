package csplugins.data.colt;


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
import csplugins.data.*;



/**
 * The DataPropertyMap holds all of the data for  
 * SharedIdentifiable objects. 
 *
 * The Attributes are stored as Columns, and the 
 * SharedIdentifiable as Rows.
 *
 */
public abstract class DataPropertyMap 
  implements DataMatrixLens, DataMatrix {


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
   * available attributes.  This is always equal to the number of columns
   * in the dataMatrix.
   * 
   * By having a colt matrix, it will also be easy to put a subset into
   * a returned data set.
   * 
   * int --> attribute
   */ 
  protected ObjectMatrix1D attributeIntNameMatrix;

  /**
   * The matrix of uids from the Parentdatapropertymap.  This provides the
   * lookup for current index to parent index.  As well as keeping track of 
   * all the available SharedIdentifiables.  Note that this is only necessary
   * for child maps.
   *
   * int --> int
   */
  protected ObjectMatrix1D identifierMatrix;


  
  //----------------------------------------//
  // Accessor Methods
  //----------------------------------------//

  protected ObjectMatrix2D getDataMatrix () {
    return dataMatrix;
  }

  protected ObjectMatrix1D getAttributeIntNameMatrix () {
    return attributeIntNameMatrix;
  }

  protected ObjectMatrix1D getIdentifierMatrix () {
    return identifierMatrix;
  }

  
  //----------------------------------------//
  // Data Information Methods
  //----------------------------------------//

  /**
   * @return all of the available attributes in this DataPropertyMap
   */
  public String[] getAttributes () {
    return ( String[] )attributeIntNameMatrix.toArray();
  }

  /**
   * Return the available attributes for the given 
   * uniqueIDs
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
    ObjectMatrix1D used_attributes = attributeIntNameMatrix.viewSelection( col.elements() );
    return ( String[] )used_attributes.toArray();
     
  }
  
  /**
   * Return the number of Attributes
   */
  public int getAttributeCount () {
    return dataMatrix.columns();
  }

  /**
   * Return all of the uniqueID ints, since this is a child we actually
   * return the parent indices.
   */
  public int[] getUniqueIDIndices () {

    // just loop through and un-box the Integers
    int[] uids = new int[ identifierMatrix.size() ];
    for ( int i = 0; i < identifierMatrix.size(); ++i ) {
      uids[i] = ( ( Integer )identifierMatrix.getQuick( i ) ).intValue();
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

   /**
   * This is a basic assign.  This will essentially inititate a new
   * uid for the given string.
   * @param id the ID ( often unique.. ) of the object to be assigned a uid
   * @return the uid that was assigned tothis object, or the the previsouly 
   * assigned uid if this id was known.
   */
  public int assignUID ( String id );

   /**
   * This will equate Aliases with each other.
   * If the aliases given are not currently assigned to a uid, then
   * one will be assigned.
   *
   * <B>NOTE: </B>I am not supporting uid equation just yet...
   * @param aliases an array of aliases to be assigned to the same
   *                uid. It can only have ONE alias that already has
   *                a uid.
   * @return "-1" is returned of two aliases both had a uid already, 
   *         otherwise the uid that they were all eqauted to is returned.
   */
  public int assignUID ( String[] aliases );

   /**
   * Since nodes are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special node access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>node: [root graph index]</i>, however, a map
   * will be maintained of all the node mappings
   */
  public int assignNodeUID ( int root_graph_index );

   /**
   * Since edges are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special edge access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>edge: [root graph index]</i>, however, a map
   * will be maintained of all the edge mappings
   */
  public int assignEdgeUID ( int root_graph_index );

  /**
   * Equate a node with other aliases.
   */
  public int assignNodeUID ( int node_root_graph_index, String[] aliases );

  /**
   * Equate a edge with other aliases.
   */
  public int assignEdgeUID ( int edge_root_graph_index, String[] aliases );


  
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
  public int[][] set ( int[] uniqueIDs, String[] attributes, Object[][] values );

  //----------------------------------------//
  // Data Access Methods
  //----------------------------------------//


  //--------------------//
  // Singelton Methods

  /**
   *  Return one value that corresponds to the given
   * uniqueID and attribute ID
   */
  public Object getValue ( int uniqueID, int attribute_id );

   /**
   * Return one value that corresponds to the given 
   * uniqueID and Attribute
   */
  public Object getValue ( int uniqueID, String attribute );

  /**
   * Return one value that corresponds to the given
   * SharedIdentifiable object and attribute
   */
  public Object getValue ( SharedIdentifiable ident, String attribute );

   /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute.
   */
  public Object getValue ( String ident, String attribute );

   /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute ID
   */
  public Object getValue ( String ident, int attribute_id );

   /**
   * Return one value that corresponds to the given
   *  SharedIdentifiable object and attribute ID
   */
  public Object getValue ( SharedIdentifiable ident, int attribute_id );
 
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
  public DataPropertyMap getData ( int[] shared_identifiables, int[] attributes );

  /**
   * This will return a DataPropertyMap that only contains the requested 
   * shared_identifiables and attributes
   *<BR><BR>
   *<B>Note:</B> if the same index is given twice, you will get back a DataPropertyMap that
   * contains two references to that index.
   */
  public DataPropertyMap getData ( String[] shared_identifiables, String[] attributes );

  
  
  
}
