package csplugins.data.colt;

import csplugins.data.*;

import cern.colt.matrix.*;
import cern.colt.map.*;
import cern.colt.list.*;

import com.sosnoski.util.hashmap.*;

import java.util.*;

public class ParentDataPropertyMap 
  extends DataPropertyMap {

  protected int DEFAULT_MATRIX_SIZE = 10;



  protected String identifier = "ParentDataPropertyMap";

  private int currentUID = 0;

  //----------------------------------------//
  // Constructors
  //----------------------------------------//
  

  /**
   * Create a new ParentDataPropertyMap
   */
  public ParentDataPropertyMap () {
     
    this.dataMatrix = create2DMatrix();
    this.attributeVector = create1DMatrix();
    this.identifierVector = create1DMatrix();
    this.identifierIntMap = createObjectIntHashMap();
    this.attributeIntMap = createStringIntHashMap();
    this.nodeUIDMap = createIntIntHashMap();
    this.edgeUIDMap = createIntIntHashMap();
  }
  
  /**
   * Create a new ParentDataPropertyMap
   * @param uids number of unique identifiers
   * @param atts number of attributes
   */
  public ParentDataPropertyMap ( int uids, int atts ) {
    this.dataMatrix = create2DMatrix( uids, atts );
    this.attributeVector = create1DMatrix( atts );
    this.identifierVector = create1DMatrix( uids );
    this.identifierIntMap = createObjectIntHashMap( uids );
    this.attributeIntMap = createStringIntHashMap( atts );
    this.nodeUIDMap = createIntIntHashMap();
    this.edgeUIDMap = createIntIntHashMap();
    currentUID = uids;
  }

  

  /**
   * Create a new ParentDataPropertyMap with the given data.
   */
  public ParentDataPropertyMap ( Object[][] data,
                                 String[] attributes,
                                 String[] identifiers ) {

  }


  
  /**
   * @return a default name
   */
  public String getIdentifier () {
    return identifier;
  }


  //----------------------------------------//
  // Accessor Methods
  //----------------------------------------//

  protected ObjectMatrix2D getDataMatrix () {
    return dataMatrix;
  }

  protected ObjectMatrix1D getAttributeVector () {
    return attributeVector;
  }

  protected ObjectMatrix1D getIdentifierVector () {
    return identifierVector;
  }

  protected StringIntHashMap getAttributeIntMap () {
    return attributeIntMap;
  }

  protected ObjectIntHashMap getIdentifierIntMap () {
    return identifierIntMap;
  }
 
  protected OpenIntIntHashMap getNodeUIDMap () {
    return nodeUIDMap;
  }
  
  protected OpenIntIntHashMap getEdgeUIDMap () {
    return edgeUIDMap;
  }
 
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
  public int assignUID ( String id ) {
    if ( identifierIntMap.containsKey( id ) ) {
      return identifierIntMap.get( id );
    }

    // this is a new id that has not been previously added as an alias.
    int new_uid = getNewUID();
    identifierIntMap.add( id, new_uid );
    return new_uid;

  }

  /**
   * This is a basic assign.  This will essentially inititate a new
   * uid for the given string.
   * @param id the ID ( often unique.. ) of the object to be assigned a uid
   * @return the uid that was assigned tothis object, or the the previsouly 
   * assigned uid if this id was known.
   */
  public int assignUID ( Object id ) {
    if ( identifierIntMap.containsKey( id ) ) {
      return identifierIntMap.get( id );
    }

    // this is a new id that has not been previously added as an alias.
    int new_uid = getNewUID();
    identifierIntMap.add( id, new_uid );
    return new_uid;

  }
  

  /**
   * This will assign multiple Aliases to the same UID.  
   *
   * @param aliases an array of aliases to be assigned to the same
   *                uid. It can only have ONE alias that already has
   *                a uid.
   * @return "-1" is returned of two aliases both had a uid already, 
   *         otherwise the uid that they were all eqauted to is returned.
   */
  public int assignUID ( String[] aliases ) {
    int current_uid = -1;
    for ( int i = 0; i < aliases.length; ++i ) {
      if ( identifierIntMap.containsKey( aliases[i] ) ) {
        if ( current_uid != -1 ) {
          // fail since we don't support uid equation
          // TODO: add uid equation
          return -1;
        }
        current_uid = identifierIntMap.get( aliases[i] );
      }  
    }
   
    // if none of the given aliases had a uid, then assign one
    // by passing to the first alias on the array.
    if ( current_uid == -1 ) {
      current_uid = assignUID( aliases[0] );
    }

    for ( int i = 0; i < aliases.length; ++i ) {
      identifierIntMap.add( aliases[i], current_uid );
    }
    return current_uid;

  }
  

  /**
   * Since nodes are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special node access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>node: [root graph index]</i>, however, a map
   * will be maintained of all the node mappings
   */
  public int assignNodeUID ( int root_graph_index ) {
    if ( nodeUIDMap.get( root_graph_index ) != 0 ) {
      int uid = getNewUID();
      nodeUIDMap.put( root_graph_index, uid );
      identifierIntMap.add( "node: "+root_graph_index, uid );
    }
    return nodeUIDMap.get( root_graph_index );
  }

  /**
   * Since edges are often reffered to only by their 
   * index numbers, it is a convience method to have
   * special edge access methods.  This can be gotten
   * around by sending a String that looks like :
   * <i>edge: [root graph index]</i>, however, a map
   * will be maintained of all the edge mappings
   */
  public int assignEdgeUID ( int root_graph_index ) {
    if ( edgeUIDMap.get( root_graph_index ) != 0 ) {
      int uid = getNewUID();
      edgeUIDMap.put( root_graph_index, uid );
      identifierIntMap.add( "edge: "+root_graph_index, uid );
    }
    return edgeUIDMap.get( root_graph_index );
  }


  /**
   * Equate a node with other aliases.
   */
  public int assignNodeUID ( int node_root_graph_index, String[] aliases ) {
    int current_uid = nodeUIDMap.get( node_root_graph_index );
    for ( int i = 0; i < aliases.length; ++i ) {
      if ( identifierIntMap.containsKey( aliases[i] ) ) {
        // only the node can have a uid prior
        return 0;
      }
    }

    if ( current_uid == 0 ) {
      current_uid = assignNodeUID( node_root_graph_index );
    }
     
    for ( int i = 0; i < aliases.length; ++i ) {
       identifierIntMap.add( aliases[i], current_uid );
    }
    return current_uid;

  }
  
  /**
   * Equate a edge with other aliases.
   */
  public int assignEdgeUID ( int edge_root_graph_index, String[] aliases ) {
    int current_uid = edgeUIDMap.get( edge_root_graph_index );
    for ( int i = 0; i < aliases.length; ++i ) {
      if ( identifierIntMap.containsKey( aliases[i] ) ) {
        // only the edge can have a uid prior
        return 0;
      }
    }

    if ( current_uid == 0 ) {
      current_uid = assignEdgeUID( edge_root_graph_index );
    }
     
    for ( int i = 0; i < aliases.length; ++i ) {
       identifierIntMap.add( aliases[i], current_uid );
    }
    return current_uid;

  }

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
  
  private int getNewUID () {
    currentUID++;
    return currentUID;
  }

  //--------------------//
  // attributes

  public int getAttributeID ( String attribute ) {
    return attributeIntMap.get( attribute );
  }


  //--------------------//
  // Single triplet settings

  public void set ( int uniqueID, int attribute, Object value ) {
    dataMatrix.setQuick( uniqueID, attribute, value );
  }


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( int uniqueID, String attribute, Object value ) {
    dataMatrix.setQuick( uniqueID, attributeIntMap.get( attribute ), value );
  }

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( String alias, String attribute, Object value ) {
    dataMatrix.setQuick( getUID( alias ), attributeIntMap.get( attribute ), value );
  }


  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( SharedIdentifiable si, String attribute, Object value ) {
    dataMatrix.setQuick( getUID( si.getIdentifier() ), attributeIntMap.get( attribute ), value );
  }

  /**
   * New Attributes can be added to id nums, or strings.
   * if the String is not already an alias, then a new
   * uniqueID will be generated and returned.
   *
   * Otherwise the returned value will be -1, denoting 
   * a succesful input, or -2 denoting an unsuccessful 
   * input.
   */
  public void set ( Object si, String attribute, Object value ) {
    dataMatrix.setQuick( getUID( si ), attributeIntMap.get( attribute ), value );
  }


 
  
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
    return null;
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
    return dataMatrix.getQuick( uniqueID, getAttributeID( attribute ) );
  }

  /**
   * Return one value that corresponds to the given
   * SharedIdentifiable object and attribute
   */
  public Object getValue ( SharedIdentifiable ident, String attribute ) {
    return dataMatrix.getQuick( getUID( ident ),
                                getAttributeID( attribute ) );
  }

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute.
   */
  public Object getValue ( String ident, String attribute ) {
    return dataMatrix.getQuick( getUID( ident ),
                                getAttributeID( attribute ) );
  }

  /**
   *  Return one value that corresponds to the given
   * String ( which presumably corresponds to a SharedIdentifiable ) and attribute ID
   */
  public Object getValue ( String ident, int attribute_id ) {
    return dataMatrix.getQuick( getUID( ident ), attribute_id );
  }

  /**
   * Return one value that corresponds to the given
   *  SharedIdentifiable object and attribute ID
   */
  public Object getValue ( SharedIdentifiable ident, int attribute_id ) {
    return dataMatrix.getQuick( getUID( ident.getIdentifier() ),
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
  public DataPropertyMap getDataPropertyMap ( int[] shared_identifiables, int[] attributes ) {
    return new ChildDataPropertyMap( this, shared_identifiables, attributes );
  }
   
  /**
   * This will return a DataPropertyMap that only contains the requested 
   * shared_identifiables and attributes
   *<BR><BR>
   *<B>Note:</B> if the same index is given twice, you will get back a DataPropertyMap that
   * contains two references to that index.
   */
  public DataPropertyMap getDataPropertyMap ( String[] shared_identifiables, String[] attributes ) {
    IntArrayList idents = new IntArrayList( shared_identifiables.length );
    IntArrayList attrib = new IntArrayList( attributes.length );
    for ( int i = 0; i < shared_identifiables.length; ++i ) {
      idents.add( getUID( shared_identifiables[i] ) );
    }
    for ( int i = 0; i < attributes.length; ++i ) {
      attrib.add( getAttributeID( attributes[i] ) );
    }
    idents.trimToSize();
    attrib.trimToSize();

    return new ChildDataPropertyMap( this, idents.elements(), attrib.elements() );
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
      new_matrix.setQuick(
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
  protected OpenIntIntHashMap createIntIntHashMap () {
    return new OpenIntIntHashMap( PrimeFinder.nextPrime( DEFAULT_MATRIX_SIZE ) );
  }

  /**
   * Create a new StringIntHashMap of the given size. the actual
   * size will be the next closest prime to accomadate the 
   * least number of collisions.
   */
  protected OpenIntIntHashMap createIntIntHashMap ( int size ) {
    return new OpenIntIntHashMap( PrimeFinder.nextPrime( size ) );
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
 

   /**
   * Create a new ObjectIntHashMap of a default size, the actual
   * size will be the next closest prime to accomadate the 
   * least number of collisions.
   */
  protected ObjectIntHashMap createObjectIntHashMap () {
    return new ObjectIntHashMap( PrimeFinder.nextPrime( DEFAULT_MATRIX_SIZE ) );
  }

  /**
   * Create a new ObjectIntHashMap of the given size. the actual
   * size will be the next closest prime to accomadate the 
   * least number of collisions.
   */
  protected ObjectIntHashMap createObjectIntHashMap ( int size ) {
    return new ObjectIntHashMap( PrimeFinder.nextPrime( size ) );
  }

  /**
   * Create and populate a ObjectIntHashMap based on the 
   * given 1DMatrix.  The keys will be the contents of the
   * matrix, the values the location of the object in the matrix.
   */
  protected ObjectIntHashMap createObjectIntHashMap ( ObjectMatrix1D matrix ) {
    IntArrayList index_list = new IntArrayList();
    ObjectArrayList value_list = new ObjectArrayList();
    matrix.getNonZeros( index_list, value_list );
    ObjectIntHashMap object_int_hash_map = new ObjectIntHashMap( PrimeFinder.nextPrime( value_list.size() ) );
    for ( int i = 0; i < value_list.size(); ++i ) {
      object_int_hash_map.add( ( Object )value_list.get( i ),
                               index_list.get( i ) );
    }
    return object_int_hash_map;
  }
 

 
}
