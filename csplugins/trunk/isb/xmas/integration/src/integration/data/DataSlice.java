package integration.data;

import cern.colt.matrix.*;
import cern.colt.map.*;
import integration.util.*;

/**
 * A DataSlice is a View onto a DataCube.
 */
public class DataSlice {

  /**
   * The Wrapped Matrix2D object
   */
  protected ObjectMatrix2D matrix2d;


  /**
   *  Row name-index map
   */
  protected ObjectIntHashMap rowNameIndexMap;

  /**
   * Column name-index map
   */
  protected ObjectIntHashMap columnNameIndexMap;

  /**
   * A Null Constructor
   */
  public DataSlice () {
  }
  
  public DataSlice ( ObjectMatrix2D matrix2d, DataCube cube ) {

  }

  public DataSlice ( ObjectMatrix2D matrix2d, 
                     ObjectIntHashMap row_names,
                     ObjectIntHashMap column_names,
                     String slice_name ) {


  }
  
  



}
