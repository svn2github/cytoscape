package integration.data;

import cern.colt.matrix.*;
import cern.colt.map.*;

import integration.util.*;

import java.util.*;

/**
 * A DataCube is essentially a wrapper around a cern.colt.matrix.Matrix3D, 
 * it contains the necessary Meta-info to support data access.<BR>
 * <ol><li>Slices: Measurment type <br> 
 * <ul><li>Array Expression</li>
 * <li>ICAT Ratio</li>
 * <li>Lamba Score</li>
 * <li>Phenotype</li>
 * <li>...</li></ul>
 * <li>Rows: Genes</li>
 * <ul><li>YMR123W</li>
 * <li>Human Gene</li>
 * <li>...</li></ul>
 * <li>Columns: Experiments</li>
 * <ul><li>Peroxisome time point <i>n</i></li>
 * <li>Galactose Utilization</li>
 * <li>Filamentaion</li>
 * <li>...</li></ul>
 * </ol>
 * @author Rowan Christmas
 */
public class DataCube {

  /**
   * The Wrapped Matrix3D Object
   */
  protected ObjectMatrix3D matrix3d;

  /**
   * Slice name-index map
   */
  protected ObjectIntHashMap sliceNameIndexMap;
  
  /**
   * Row name-index map
   */
  protected ObjectIntHashMap rowNameIndexMap;

  /**
   * Column name-index map
   */
  protected ObjectIntHashMap columnNameIndexMap;

  /** 
   * Create a new data Cube
   */
  public DataCube () {

  }

  public DataCube ( ObjectMatrix3D matrix, DataCube cube ) {
  }

  /**
   * Create a new Data Cube with the given size for each dimension
   * @param num_slices the number of slices ( measurments )
   * @param num_rows the number of rows ( experiments )
   * @param num_columns the number of columns ( genes )
   */
  public DataCube ( int num_slices,
                    int num_rows,
                    int num_columns ) {

    matrix3d = ObjectFactory3D.sparse.make( num_slices, num_rows, num_columns );    

  }

  /**
   * Create a DataCube object that contains the given values
   * @param values the values given by slice-row-column 
   */
  public DataCube ( Object[][][] values, 
                    String[] slice_names, 
                    String[] row_names, 
                    String[] column_names ) {
    matrix3d = ObjectFactory3D.dense.make( values );
    initialize();
    addSliceNames( slice_names );
    addRowNames( row_names );
    addColumnNames( column_names );
    
  }

  protected void initialize () {
    
    sliceNameIndexMap = new ObjectIntHashMap();
    rowNameIndexMap = new ObjectIntHashMap();
    columnNameIndexMap = new ObjectIntHashMap();

  }

  public ObjectMatrix3D getMatrix () {
    return matrix3d;
  }


  /** 
   * Appends the given slice names to the data cube
   */
  public void addSliceNames ( String[] slice_names ) {
    int slice_num = sliceNameIndexMap.keySet().size();
    for ( int i = 0; i < slice_names.length; ++i ) {
      sliceNameIndexMap.putInt( slice_names[i], i + slice_num );
    }
  }
  
  /** 
   * Appends the given row names to the data cube
   */
  public void addRowNames ( String[] row_names ) {
    int row_num = rowNameIndexMap.keySet().size();
    for ( int i = 0; i < row_names.length; ++i ) {
      rowNameIndexMap.putInt( row_names[i], i + row_num );
    }
  }

  /** 
   * Appends the given column names to the data cube
   */
  public void addColumnNames ( String[] column_names ) {
    int column_num = columnNameIndexMap.keySet().size();
    for ( int i = 0; i < column_names.length; ++i ) {
      columnNameIndexMap.putInt( column_names[i], i + column_num );
    }
  }

  /**
   * @return the names of all the Slices ( Measurments )
   */
  public Set getSliceNames () {
    return sliceNameIndexMap.keySet();
  }

  /**
   * @return the names of all the Rows ( Experiments )
   */
  public Set getRowNames () {
    return rowNameIndexMap.keySet();
  }

  /**
   * @return the names of all the Columns ( Genes )
   */
  public Set getColumnNames () {
    return columnNameIndexMap.keySet();
  }

  public int getSliceIndex ( String name ) {
    return sliceNameIndexMap.getInt( name );
  }
  public int getRowIndex ( String name ) {
    return rowNameIndexMap.getInt( name );
  }
  public int getColumnIndex ( String name ) {
    return columnNameIndexMap.getInt( name );
  }
                               


  // Objects get returned -- for now
  // TODO: make easy accessor methods for int/double/float/boolean
  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( int slice, int row, int column ) {
    return matrix3d.get( slice, row, column );
  }
 
  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( String slice, String row, String column ) {
    return matrix3d.get( sliceNameIndexMap.getInt( slice ),
                         rowNameIndexMap.getInt( row ),
                         columnNameIndexMap.getInt( column ) );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( String slice, String row, int column ) {
    return matrix3d.get( sliceNameIndexMap.getInt( slice ),
                         rowNameIndexMap.getInt( row ),
                         column );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( String slice, int row, String column ) {
    return matrix3d.get( sliceNameIndexMap.getInt( slice ),
                         row,
                         columnNameIndexMap.getInt( column ) );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( int slice, String row, String column ) {
    return matrix3d.get( slice,
                         rowNameIndexMap.getInt( row ),
                         columnNameIndexMap.getInt( column ) );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( String slice, int row, int column ) {
    return matrix3d.get( sliceNameIndexMap.getInt( slice ),
                         row,
                         column );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( int slice, int row, String column ) {
    return matrix3d.get( slice,
                         row,
                         columnNameIndexMap.getInt( column ) );
  }

  /**
   * @return the Object that is stored for the given spot
   */
  public Object getObject ( int slice, String row, int column ) {
    return matrix3d.get( slice,
                         rowNameIndexMap.getInt( row ),
                         column );
  }
  
  

  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all genes and all 
   * experiments given a measurment
   */
  public DataSlice viewSlice ( int slice ) {
    return new DataSlice( matrix3d.viewSlice( slice ), this );
  }
  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all genes and all 
   * experiments given a measurment
   */
  public DataSlice viewSlice ( String sliceName ) {
    return viewSlice( sliceNameIndexMap.getInt( sliceName ) );
  }
  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all genes and all measurments 
   * given an experiment
   */
  public DataSlice viewRow ( int row ) {
    return new DataSlice( matrix3d.viewRow( row ), this );
  }
  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all genes and all measurments 
   * given an experiment
   */
  public DataSlice viewRow ( String rowName ) {
    return viewRow( rowNameIndexMap.getInt( rowName ) );
  }
  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all measurments 
   * and all experiments given a gene
   */
  public DataSlice viewColumn ( int column ) {
    return new DataSlice( matrix3d.viewColumn( column ), this );
  }
  /**
   * get a DataSlice that has the appropriate meta-data given the 
   * dimension that we are viewing.
   * @return the DataSlice that represents all the info for all measurments 
   * and all experiments given a gene
   */ 
  public DataSlice viewColumn ( String columnName ) {
    return viewColumn( columnNameIndexMap.getInt( columnName ) );
  }

  //----------------------------------------//
  //  DataSlice methods that only return parts

  /**
   * @return a DataCube that has all of the relevant Meta-Data of this cube
   * but only contains a subset of the actual data.
   */
  public DataCube viewDataCube ( int[] slices, int[] rows, int[] columns ) {
    return new DataCube( matrix3d.viewSelection( slices, rows, columns ), this );
  }

  /**
   * @return a DataSlice that only contains the subset of rows and columns
   */
  public DataSlice viewSlice ( int slice, int[] rows, int[] columns ) {
    return new DataSlice( matrix3d.viewSlice( slice ).viewSelection( rows, columns), this );
  }

  /**
   * @return a DataSlice that only contains the subset of slices and columns
   */
  public DataSlice viewRow ( int[] slices, int row, int[] columns ) {
    return new DataSlice( matrix3d.viewRow( row ).viewSelection( slices, columns), this );
  }
  
  /**
   * @return a DataSlice that only contains the subset of slices and rows
   */
  public DataSlice viewColumn ( int[] slices, int[] rows, int column ) {
    return new DataSlice( matrix3d.viewRow( column ).viewSelection( slices, rows), this );
  }

  public Object getMetaDataStuffOrHoweverThisShouldBeDone () {
    return null;
  }
  

} // class DataCube
