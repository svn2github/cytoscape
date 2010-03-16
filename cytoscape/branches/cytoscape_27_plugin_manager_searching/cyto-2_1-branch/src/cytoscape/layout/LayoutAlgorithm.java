package cytoscape.layout;

import cytoscape.util.*;
import giny.view.NodeView;

public interface LayoutAlgorithm extends MonitoredTask {

  public abstract void doLayout ();

  public void lockNodes ( NodeView[] nodes );

  public void lockNode ( NodeView v );

  public void unlockNode( NodeView v );

}
