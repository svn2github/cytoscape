package csplugins.trial.pshannon.dataCube;

import java.util.*;
import java.io.*;


/**
 * Implements a filtered view of a data matrix, reflecting user changes to
 * column order, column enabling, and row selection.
 * 
 * @author Paul Shannon
 * @author Dan Tenenbaum
 * @author Rowan Christmas ( interfacized )
 */

public interface DataMatrixLens {

  /**
   * Initializes the DataMatrixLens.
   */
  public void clear ();
  

  /**
   * Returns the name of the underlying matrix.
   */
  public String getMatrixName ();
 

  /**
   * Sets  the state (enabled or disabled) of a given column.
   * 
   * @param column The column to change the state of.
   * @param newState Whether the column should be enabled.
   */
  public void setColumnState ( int column, boolean newState );


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
