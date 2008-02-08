package cytoscape;

import java.util.EventListener;

public interface GraphPerspectiveChangeListener
  extends EventListener {

  /**
   * Invoked when a GraphPerspective to which this
   * GraphPerspectiveChangeListener listens changes.
   */
  public void graphPerspectiveChanged ( GraphPerspectiveChangeEvent event );

} // interface GraphPerspectiveChangeListener
