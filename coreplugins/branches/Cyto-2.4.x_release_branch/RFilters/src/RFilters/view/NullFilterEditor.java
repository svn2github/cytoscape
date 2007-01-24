package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;

public class NullFilterEditor 
  extends FilterEditor { 
   
  public NullFilterEditor () {
    add( new JLabel( "There is no Editor for this Filter" ));
  }

  /**
   * Return the Description of this Editor that will 
   * go into its tab
   */
  public String getFilterID () {
    return "";
  }

  
  /**
   * Return a user friendly description of the class of filters edited by this editor
   */
  public String getDescription() {
    return "";
  }
  
  /**
   * This Editor should be able to read in a Filter that it produced 
   * and redisplay that Filter in the Editor, so that the Filter can be 
   * edited.
   */
  public void editFilter ( Filter filter ) {
  }

  /**
   * Create a filter initialized to the proper default values
   */
  public Filter createDefaultFilter() {
    return null;
  }

  /**
   * Return the class of filter subclass that I actually want to edit
   */
  public Class getFilterClass() {
    return null;
  }

  

}
