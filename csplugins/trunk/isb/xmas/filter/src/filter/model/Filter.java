package filter.model;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;

public interface Filter extends PropertyChangeListener {

  public static String FILTER_MODIFIED = "FILTER_MODIFIED";
 
  /**
   * Note that to truly pass this Filter an object must <b>both</b> be an
   * instance of at least one of the passingTypes (unless {@link
   * #getPassingTypes()} returns null) <i>and</i> passesFilter( object ).
   * @see FilterUtilities#passesFilter( Filter, Object )
   * @return true iff the given Object passes the filter.
   */
  public boolean passesFilter ( Object object );

  /**
   * @return an array of Classes; all passers of this filter must be an
   * instance of at least one of these classes; null if all object types could
   * potentially pass this filter.
   */
  public Class[] getPassingTypes ();

  /**
   * @return the name of this Filter.
   */
  public String toString ();

  public String getEditorName();


  /**
   * All filters should override the Object equals(..) method to return true
   * when compared to an equivalent filter.  This method should <b>not</b> call
   * passesFilter(..) on this or on the given object.
   * @see #hashCode()
   */
  public boolean equals ( Object other_object );

  /**
   * All filters should implement the clone() method, as Filters will be
   * cloned.
   */
  public Object clone ();

  public SwingPropertyChangeSupport getSwingPropertyChangeSupport();

} // interface Filter
