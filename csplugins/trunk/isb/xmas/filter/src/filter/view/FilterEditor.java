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
   * This Editor should be able to read in a Filter that it produced 
   * and redisplay that Filter in the Editor, so that the Filter can be 
   * edited.
   */
  public abstract void editFilter ( Filter filter );

  /**
   * This Editor should be able to return the Editor that it is working on,
   * this can be either a Filter that is being edited, or a new Filter.
   */
  public abstract Filter getFilter ();

  /**
   * Resets  all of tnhe Editor Fields, if a new Editor, or reverts
   * back to the values that were read in when a Filter was read in.
   */
  public abstract void reset ();

  /**
   * Clear the FilterEditor
   */
  public abstract void clear ();

  public  SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
    return pcs;
  }
}
