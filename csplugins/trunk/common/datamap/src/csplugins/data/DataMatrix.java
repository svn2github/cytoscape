package csplugins.data;

import java.util.*;
import java.io.*;

public interface DataMatrix {

  public String getName ();
 
  public void setSize ( int rows, int columns );

  public void set ( int row, int column, double value );

  public void setColumnTitles ( String [] newValues );

  public void setRowTitles ( String [] newValues );

  public int getRowCount ();

  /**
   * returns a count of all enabled columns
   */
  public int getColumnCount ();

  public double get ( int row, int column ); 
 
  /**
   * returns the original data, ignoring swapped and/or disabled columns.
   */
  public double[] getUntransformed ( int row );

  public double[] get ( int row );

  public double[] get ( String rowName );

  public String[] getRowTitles ();

  public String[] getUnmaskedColumnTitles ();

  public String[] getColumnTitles ();

  public String toString ();

}
