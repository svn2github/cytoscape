package cytoscape;

import java.util.EventListener;

public interface RootGraphChangeListener
  extends EventListener {

  /**
   * Invoked when a RootGraph to which this RootGraphChangeListener listens
   * changes.
   */
  public void rootGraphChanged ( RootGraphChangeEvent event );

} // interface RootGraphChangeListener
