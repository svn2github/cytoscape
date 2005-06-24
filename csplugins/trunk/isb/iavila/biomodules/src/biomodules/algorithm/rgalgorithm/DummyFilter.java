package biomodules.algorithm.rgalgorithm;

import filter.model.*;
import java.lang.*;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * A Filter that does nothing (no nodes or edges pass) used for combo boxes of
 * filters so that users can select a filter called "No Filter Selected".
 *
 * @author iliana
 */

public class DummyFilter implements Filter{
  
  protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
  
  /**
   * Constructor
   */
  public DummyFilter (){}
  
  /**
   * @return false always
   */
  public boolean passesFilter (Object object){
    return false;
  }
  
  /**
   * @return null always
   */
  public Class[] getPassingTypes (){
    return null;
  }

  /**
   * @return the name of this Filter.
   */
  public String toString (){
    return "No filter selected";
  }

  public String getFilterID(){
    return toString();
  }

  public String getDescription(){
    return "A filter that no nodes or edges pass.";
  }
  
  /**
   * Every filter needs to know how to output itself in such a way that 
   * it knows how to re-create itself from a flat file.<BR>
   * The required format is: <BR>
   * Filter <I>class name</I> <B><small>whatever a filter wants</small></B>
   *
   * @return always null
   */
  public String output (){
    return null;
  }
  
  /**
   * By passing a String that was ouput by this Filter it will create and return
   * a new Filter that is equivalent to the instance that was output. Note that
   * the output is modifiable, so that one could change how a Filter behaves if
   * they wanted to.
   * Does nothing.
   */
  public void input (String desc){}

  /**
   * All filters should override the Object equals(..) method to return true
   * when compared to an equivalent filter.  This method should <b>not</b> call
   * passesFilter(..) on this or on the given object.
   * @see #hashCode()
   * @return true iff other_object is a DummyFilter, false otherwise
   */
  public boolean equals (Object other_object){
    if(other_object instanceof DummyFilter){
      return true;
    }
    return false;
  }

  /**
   * All filters should implement the clone() method, as Filters will be
   * cloned.
   *
   * @return a new DummyFilter
   */
  public Object clone (){
    return new DummyFilter();
  }

  /**
   * @return null always
   */
  public SwingPropertyChangeSupport getSwingPropertyChangeSupport(){
    return this.pcs; // ???
  }

}//DummyFilter
