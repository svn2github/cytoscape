// DataMatrixLens

// DataMatrixLens.java
//-----------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-----------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix;
//-----------------------------------------------------------------------------------
import java.util.*;
import java.io.*;
//-----------------------------------------------------------------------------------
/**
 * Implements a filtered view of a data matrix, reflecting user changes to
 * column order, column enabling, and row selection.
 * 
 * @author Paul Shannon
 * @author Dan Tenenbaum
 */
//-----------------------------------------------------------------------------------
public class DataMatrixLens {

  DataMatrix matrix;
  int [] columnOrder;       // tracks the user's preferred column order
  boolean [] columnState;   // tracks which columns in each table are enabled
  

// the two parallel arrays can be (to me, anyway) very confusing.  here
// is how to think of them:
// 
// 1) columnOrder: this array always tells you, in order, which of the
//        original data matrix columns is in which row.  you can read off
//        the current display order of the matrix columns by just reading
//        through the list:
//               0:  2
//               1:  0
//               2:  1
//        this says the original 
//                 column two is now in column 0
//                                zero is in column 1
//                                one is in column 2
// 
//      2) columnState:  whether or not to display whatever is currently 
//               located in the specified column, for example
//               0:  F
//               1:  T
//               2:  T
//         says that column zero (which,see above, contains the original column 2)
//         is now disabled.  
//

        private int[] selectedRows;

/**************************************************
 * How Row selection works:
 * 
 * When the lens is first constructed, the array above, selectedRows, is 
 * equal in length to the number of rows in the matrix and 
 * selectedRows[x] = x, for any x. The user can later change the row 
 * selection by calling setSelectedRows() with an int[] that is a list
 * of indexes of the rows to be considered selected.
 * 
 * Once that is done, there are a number of paired methods, some of which operate
 * on all rows and some of which operate on the selected rows only. For example:
 * 
 * getAllRowTitles
 * getSelectedRowTitles
 * --these methods get row titles from the entire matrix, and the user
 *   selection, respectively.
 * 
 * Also:
 *  getFromAll 
 *  getFromSelected
 *   --returns rows or double values from the entire matrix, and the user
 *     selection, respectively. 
 *
 * An exception to this pattern is the toString(boolean) method, which takes
 * a flag determining whether to display all rows (true) or just user-
 * selected rows (false).
 *
 * It is useful to note that throughout this class, the term "filtered" is
 * used to refer to operations involving columns (ordering and
 * enabling/disabling) whereas the term "selected" refers to operations
 * involving a user-selected subset of rows. 
 * 
 * I notice that the term "all" sometimes means "whatever is in the underlying
 * matrix, without any filtering," and sometimes it means "whatever is in the
 * underlying matrix, plus filtering."  This should probably be fixed so that
 * "all" means the latter exclusively, as we already have the term "raw" that
 * is equivalent to the former.
 * TODO - make it so.
 * 
 * NB. The filtered "get by string" methods, getFromAll(String) and
 * getFromSelected(String), are functionally identical (one just calls the
 * other. Should they be conflated into a get(String) method? 
 * Or would that break the pseudo naming scheme we've got going here?
 * I kind of think it would.
 *
 * NB. There is an implicit assumption in DataMatrix (which holds true here
 * as well) that rows will all have unique names. If it's good enough
 * for DataMatrix it's good enough for DataMatrixLens. I just mention it
 * here so it will be explicit, somewhere. 
 * 
 */

        
        private  boolean DEBUG = false;

//-----------------------------------------------------------------------------------
/**
 * Creates a lens from a DataMatrix object.
 * @param matrix The matrix to create a lens from. 
 */
public DataMatrixLens (DataMatrix matrix) throws Exception
{
  this.matrix = matrix;
  clear ();
  setSelectedRows (new int [0]);

} //ctor
//-----------------------------------------------------------------------------------
/**
 * Initializes the DataMatrixLens.
 */
public void clear ()
{
  int columnCount = matrix.getColumnCount ();
  columnState = new boolean [columnCount];
  columnOrder = new int [columnCount];
  
  for (int c=0; c < columnCount; c++) {
    columnState [c] = true;
    columnOrder [c] = c;
    }
   
} // clear
//-----------------------------------------------------------------------------------
/**
 * Returns the name of the underlying matrix.
 */
public String getMatrixName ()
{
  return matrix.getName ();
} // getMatrixName
//-----------------------------------------------------------------------------------
/**
 * Sets  the state (enabled or disabled) of a given column.
 * 
 * @param column The column to change the state of.
 * @param newState Whether the column should be enabled.
 */
public void setColumnState (int column, boolean newState)
{
  columnState [column] = newState;

} // setColumnState
//-----------------------------------------------------------------------------------
/**
 * Returns an array of booleans, one for each column, indicating whether
 * the given column is enabled.
 * @return
 */
public boolean [] getColumnState ()
{
  return columnState;

} // getColumnState()
//-----------------------------------------------------------------------------------
/**
 * Returns a boolean indicating whether the specified column is enabled.
 * @param column The column to check.
 * @return 
 */
public boolean getColumnState (int column)
{
  return columnState [column];

} // getColumnState (int)
//-----------------------------------------------------------------------------------
/**
 * Moves the column at position <code>from</code> to 
 * position <code>to</code>.
 * @param from
 * @param to
 */
public void swapColumnOrder (int from, int to)
{
  int oldFrom = columnOrder [from];
  int oldTo = columnOrder [to];
  columnOrder [from] = oldTo;
  columnOrder [to] = oldFrom;

  boolean oldFromState = columnState [from];
  boolean oldToState = columnState [to];
  columnState [from] = oldToState;
  columnState [to] = oldFromState;
  printTransformation ();

} // swapColumnOrder
//-----------------------------------------------------------------------------------
/**
 * Returns the order (position) of the specified column. 
 * 
 * @param column The original position of the column in the underlying matrix.
 * @return The current position of that column.
 */
public int getColumnOrder (int column)
{
  return columnOrder [column];
} // getColumnOrder
//-----------------------------------------------------------------------------------
/**
 * Returns the number of columns, regardless of how many are enabled.
 * @return
 */
public int getRawColumnCount ()
{
  return columnState.length;

} // getRawColumnCount
//-----------------------------------------------------------------------------------
/**
 * Returns the number of enabled columns.
 * @return
 */
public int getEnabledColumnCount ()
{
  int count = 0;
  for (int i=0; i < columnState.length; i++)
   if (columnState [i])
     count++;

  return count;

} // getColumnCount
//-----------------------------------------------------------------------------------
public DataMatrix getSelectedSubMatrix () throws Exception
{
  DataMatrix result = new DataMatrix ();
  int dataRowCount = getSelectedRowCount ();
  int dataColumnCount = getEnabledColumnCount ();
  //System.out.println ("DML.getSelectedSubMatrix, dataColumnCount: " + 
  //                    dataColumnCount);
  result.setSize (dataRowCount, dataColumnCount);
  result.setColumnTitles (getFilteredColumnTitles ());
  result.setRowTitles (getSelectedRowTitles ());
  result.setRowTitlesTitle (getRowTitlesTitle ());

  for (int r=0; r < dataRowCount; r++) {
    double [] selectedRow = getFromSelected (r);
    for (int c=0; c < selectedRow.length; c++) {
      result.set (r, c, selectedRow [c]);
      }
    } // for r

  return result;

} // getSelectedSubMatrix
//-----------------------------------------------------------------------------------
/**
 * Returns the number of selected rows. 
 * @return
 */
public int getSelectedRowCount ()
{
  return selectedRows.length;

}
//-----------------------------------------------------------------------------------
/**
 * Returns a list of indexes into the underlying matrix representing
 * the current user selection. Functionally equivalent to the method of the
 * same name in the JTable class.
 * @return
 */
public int [] getSelectedRowIndexes() 
{
  return selectedRows;

} // getSelectedRows
//-----------------------------------------------------------------------------------
/**
 * Returns the total number of rows in the matrix.
 * @return 
 */
public int getRawRowCount()
{
        return matrix.getRowCount();
} //getRawRowCount
//-----------------------------------------------------------------------------------
/**
 * Sets the user's selection of rows.
 * 
 * @param selectedRows An array of the row indices in the underlying matrix
 * which are to be selected.  the indices are zero-based.
 */
public void setSelectedRows (int[] selectedRows) 
{
  this.selectedRows = selectedRows;

} // setSelectedRows
//-----------------------------------------------------------------------------------
/**
 */
public void selectAllRows ()
{
  int rowsInTable = getRawRowCount ();
  int [] selectedRowIndices = new int [rowsInTable];
  for (int i=0; i < rowsInTable; i++)
       selectedRowIndices [i] = i;
  
  this.selectedRows = selectedRowIndices;

} // selectAllRows
//-----------------------------------------------------------------------------------
/**
 * Returns the original data, ignoring swapped and/or disabled columns.
 * 
 * @deprecated Deprecated - use getRaw() instead.
 * @param row Index of row desired out of all rows.
 * @return
 */

public double [] getUntransformed (int row)
{
  return getRaw(row);

} //getUntransformed
//-----------------------------------------------------------------------------------
/**
 * Prints debugging information about lens transformations.
 *
 */
public void printTransformation ()
{
  if (!DEBUG) return;
  
  for (int i=0; i < getRawColumnCount (); i++)
        System.out.print (i + " ");
  System.out.println ();

  for (int i=0; i < getRawColumnCount (); i++) {
        System.out.print (columnOrder [i] + " ");
        }
  System.out.println ();

  for (int i=0; i < getRawColumnCount (); i++) {
        String stateString = "F";
        if (columnState [i]) stateString = "T";
        System.out.print (stateString + " ");
        }
  System.out.println ();
  System.out.println ();

} // printTransformation
//-----------------------------------------------------------------------------------
/**
 * Returns a row from the underlying matrix, unaffected by user changes to 
 * column order, state, or row selection.
 * @param row the index of the row to return.
 * @return
 */
public double[] getRaw(int row) {
        return matrix.get(row);
} //getRaw(int)
//-----------------------------------------------------------------------------------
/**
 * Returns a row from the underlying matrix, unaffected by user changes to 
 * column order, state, or row selection. 
 * @param rowName The name of the row to return
 * @return
 */
public double[] getRaw(String rowName) {
        return matrix.get(rowName);
} // getRaw(String)
//-----------------------------------------------------------------------------------
/**
 * Returns a double value from the underlying matrix, unaffected
 * by user changes to column order, state, or row selection.
 * @param row The row to retrieve a value from
 * @param column The column to retrieve a value from
 * @return
 */
public double getRaw(int row, int column) {
        return matrix.get(row, column);
} //getRaw(int, int)
//-----------------------------------------------------------------------------------
/**
 * Returns a row which reflects user changes to column order and state,
 * but not selected rows.
 * @param row the index of the row to return
 * @return
 */
public double[] getFromAll(int row) {
        double[] origRow = matrix.get(row);
        return adjustForColumnOrderAndState(origRow);
} //getFromAll(int)
//-----------------------------------------------------------------------------------
/**
 * Returns a row which reflects user changes to column order and state,
 * but not selected rows.
 * @param rowName The name of the row to return 
 * @return
 */
public double[] getFromAll(String rowName) {
        double[] origRow  = matrix.get(rowName);
        return adjustForColumnOrderAndState(origRow);
} //getFromAll(String)
//-----------------------------------------------------------------------------------
/**
 * Returns a double value which reflects user changes to column order and state,
 * but not selected rows. 
 * @param row The index of the row from which to retrieve a value
 * @param column The index of the column from which to retrieve a value
 * @return
 */
public double getFromAll(int row, int column) {
        double[] origRow = matrix.get(row);
        double[] transformedRow = adjustForColumnOrderAndState(origRow);
        return transformedRow[column];
} //return getFromAll(int, int)
//-----------------------------------------------------------------------------------
/**
 * Returns a row which reflects user changes to column order and state,
 * AND row selection. 
 * @param row The index, within the subset of selected rows, of the desired row.
 * @return
 */
public double[] getFromSelected(int row) {
        int selectedRowIndex = getRowIndexFromSelection(row);
        return getFromAll(selectedRowIndex);
} // getFromSelected(int)
//-----------------------------------------------------------------------------------
/**
 * Returns a row which reflects user changes to column order and state,
 * AND row selection
 * @param rowName The name of the row to return
 * @return
 */
public double[] getFromSelected(String rowName) {
         return getFromAll(rowName);
} //getFromSelected(String)
//-----------------------------------------------------------------------------------
public double getFromSelected(int row, int column) {
        int selectedRowIndex = getRowIndexFromSelection(row);
        return getFromAll(selectedRowIndex, column);
}
//-----------------------------------------------------------------------------------
/**
 * This method takes as its argument an int representing an index of 
 * one of the selected rows. It returns the index of that row in the
 * underlying matrix. Do we really need a method for this?
 * 
 * TODO - find a better name for this method.
 * @param index
 * @return
 */
private int getRowIndexFromSelection(int index) {
        return selectedRows[index];
}
//-----------------------------------------------------------------------------------
/**
 *  use column ordering, and column status, to create and return
 *  a possibly transformed view of the data row
 */
public double [] adjustForColumnOrderAndState (double [] row)
{
  int dataColumnCount = getRawColumnCount ();

  ArrayList list = new ArrayList ();
  for (int i=0; i < columnOrder.length; i++) {
   if (columnState [i]) {
          double nextValue = row [columnOrder [i]];
          list.add (new Double (nextValue));
          } // if
        } // for i

  double [] result = new double [list.size ()];
  Double [] tmp = (Double []) list.toArray (new Double [0]);
  for (int i=0; i < tmp.length; i++)
        result [i] = tmp [i].doubleValue ();
  
  return result;

} // adjustForColumnOrderAndState (double [])
//-----------------------------------------------------------------------------------
/**
 *  use column titles to create and return
 *  a possibly transformed view of the column titles
 */
public String [] adjustForColumnOrderAndState (String [] columnTitles)
// <columnTitles> includes column 0, which never changes its position.
// so start, below, by creating an array which is 1 element shorter, and
// therefore equal in length to the number of data columns in the matrix.
// add the column zero label to the list, then traverse the data column
// titles, placing them as the columnOrder & columnStatus info decree.
{
  if (DEBUG) {
   System.out.println ("---- incoming Lens.adjust column titles");
   for (int i=0; i < columnTitles.length; i++)
     System.out.println (columnTitles [i]);
    }

  int dataColumnCount = getRawColumnCount ();
  //System.out.println ("DML.adjust, dataColumnCount: " + dataColumnCount);
  String [] movableTitles = new String [dataColumnCount];
  for (int i=0; i < dataColumnCount; i++) 
        movableTitles [i] = columnTitles [i];

  printTransformation ();
  ArrayList list = new ArrayList ();
  // list.add (matrix.getColumnTitles()[0]);
  for (int i=0; i < columnOrder.length; i++) {
    if (DEBUG) System.out.println ("   column " + i + ": " + columnState [i]);
    if (columnState [i]) {
      String nextValue = movableTitles [columnOrder [i]];
      if (DEBUG)
        System.out.println ("  got " + nextValue + " from " + columnOrder [i]);
        list.add (nextValue);
        } // if
    } // for i

  String [] result = (String []) list.toArray (new String [0]);
  if (DEBUG) {
    System.out.println ("---- outgoing Lens.adjust column titles");
    for (int i=0; i < result.length; i++)
      System.out.println (result [i]);
    }

  DEBUG = false;
  
  return result;

} // adjustForColumnOrderAndState (String [])
//-----------------------------------------------------------------------------------
/**
 * Returns all row titles regardless of user selection.
 * @return
 */
public String[] getAllRowTitles() 
{
  return matrix.getRowTitles();

} //getAllRowTitles
//-----------------------------------------------------------------------------------
/**
 * Returns the title of the rowTitles (zeroth) column
 * @return
 */
public String getRowTitlesTitle () 
{
  return matrix.getRowTitlesTitle ();

} // getRowTitlesTitle
//-----------------------------------------------------------------------------------
/**
 * Return the row titles in the user selection.
 * @return
 */
public String [] getSelectedRowTitles ()
{
  String[] selectedRowTitles = new String[selectedRows.length];
  String[] allRowTitles = matrix.getRowTitles();
  for (int i = 0; i < selectedRowTitles.length; i++) {
        selectedRowTitles[i] = allRowTitles[selectedRows[i]];
  }
  return selectedRowTitles;
} //getRowTitles
//-----------------------------------------------------------------------------------
/**
 * Returns the unfiltered column titles in the unfiltered column order.
 * @deprecated Use getAllColumnTitles() instead.
 */
public String [] getUnmaskedColumnTitles ()
{
  return getAllColumnTitles();
} // getUnmaskedColumnTitles
//-----------------------------------------------------------------------------------
/**
 * Returns the unfiltered column titles in the unfiltered column order.
 */
public String[] getAllColumnTitles() {
        return matrix.getColumnTitles();
} // getAllColumnTitles
//-----------------------------------------------------------------------------------
/**
 * Returns the filtered column titles, along with the header column
 * which is always at position 0. 
 * The following always holds true:
 * // (no longer) getFilteredColumnTitles().length = getEnabledColumnCount() + 1;
 * @return
 */
public String [] getFilteredColumnTitles ()
{
  String [] columnTitles = matrix.getColumnTitles ();
  //System.out.println ("===== DataMatrixLens.getFilteredColumnTitles");
  //for (int i=0; i < columnTitles.length; i++)
  //  System.out.println ("  title " + i + ": " + columnTitles [i]);

  return adjustForColumnOrderAndState (matrix.getColumnTitles ());

} // getColumnTitles
//-----------------------------------------------------------------------------------
/**
 * Returns a string representation of this DataMatrixLens
 * 
 * @param allRows Whether to show all rows (true) or just the selected rows.
 * @return
 */
public String toString (boolean allRows)
{
  int[] rowsToDisplay;
  if (allRows) {
        rowsToDisplay = new int[matrix.getRowCount()];
    for (int i = 0; i < rowsToDisplay.length; i++) {
                rowsToDisplay[i] = i;
        }       
  } else {
        rowsToDisplay = selectedRows;
  }
  
  
  StringBuffer sb = new StringBuffer ();
  String [] adjustedColumnTitles = getFilteredColumnTitles ();
  int colMax = adjustedColumnTitles.length;
  
  if (DEBUG)
        System.out.println ("adjustedColumnTitles.length: " + adjustedColumnTitles.length);

  sb.append (getRowTitlesTitle ());
  for (int c=0; c < colMax; c++) {
        sb.append ("\t");
        sb.append (adjustedColumnTitles [c]);
  }

  sb.append ("\n");

  String [] rowTitles;
  
  if (allRows) {
        rowTitles = getAllRowTitles();
  } else {
        rowTitles = getSelectedRowTitles();
  }
  
  int rowMax = rowTitles.length;
  
  for (int i=0; i < rowsToDisplay.length; i++) {

        double [] adjustedRow;

        if (allRows) {
                adjustedRow = getFromAll(i);
        } else {
                adjustedRow = getFromAll(getRowIndexFromSelection(i));
        }
        
        sb.append (rowTitles [i]);
        for (int c=0; c < colMax; c++) {
          sb.append ("\t");
          sb.append (adjustedRow [c]);
          } // for c
        sb.append ("\n");
        } // for i


  return sb.toString ();

} // toString(int[])
//-----------------------------------------------------------------------------------
/**
 * Returns a string representation of this DataMatrixLens showing
 * all rows, regardless of the current row selection. 
 */
public String toString ()
{
  return toString (true);

} // toString()
//--------------------------------------------------------------------
} // class DataMatrixLens
