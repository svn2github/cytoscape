package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;

public class  FilterTreeEditor extends FilterEditor {


  

  public FilterTreeEditor () {
    super();
    setBorder( new TitledBorder( "Filter Tree Editor" ) );
    add( new JLabel( "Filter Trees are Edited using the Filter Tree Viewer." ) );
  }


  /**
   * Return the Description of this Editor that will 
   * go into its tab
   */
  public  String toString () {
    return "FilterTree";
  }
  
  public String getFilterID () {
    return FilterTree.FILTER_ID ;
  }


  /**
   * This Editor should be able to read in a Filter that it produced 
   * and redisplay that Filter in the Editor, so that the Filter can be 
   * edited.
   */
  public  void editFilter ( Filter filter ) {}

  /**
   * This Editor should be able to return the Editor that it is working on,
   * this can be either a Filter that is being edited, or a new Filter.
   */
  public  Filter getFilter () {
    return null;
  }

  /**
   * Resets  all of tnhe Editor Fields, if a new Editor, or reverts
   * back to the values that were read in when a Filter was read in.
   */
  public  void reset () {}

  /**
   * Clear the FilterEditor
   */
  public  void clear () {}

 
}
