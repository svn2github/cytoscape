package filter.view;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import filter.model.*;
import javax.swing.border.*;
import java.beans.*;

public abstract class  FilterEditor extends JPanel {

  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this );
  public FilterEditor () {
    super();
  }

  /**
   * Return the Description of this Editor that will 
   * go into its tab
   */
  public abstract String getFilterID ();

  
  /**
   * Return a user friendly description of the class of filters edited by this editor
   */
  public abstract String getDescription();
  
  /**
   * This Editor should be able to read in a Filter that it produced 
   * and redisplay that Filter in the Editor, so that the Filter can be 
   * edited.
   */
  public abstract void editFilter ( Filter filter );

  /**
   * Create a filter initialized to the proper default values
   */
  public abstract Filter createDefaultFilter();

  /**
   * Return the class of filter subclass that I actually want to edit
   */
  public abstract Class getFilterClass();

  public  SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }
}
