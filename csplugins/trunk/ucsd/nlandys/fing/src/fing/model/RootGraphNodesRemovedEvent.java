package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

final class RootGraphNodesRemovedEvent extends RootGraphChangeEventAdapter
{

  private final Node[] m_removedNodes;

  // Note that no copy of the array removedNodes is made - the exact
  // array reference is kept.  Methods on this class return this same
  // array reference.  Note that the Node objects in the input array
  // must contain valid RootGraph indices at the time this constructor is
  // called; further behavior of the Node objects is not too important
  // becuase the getRemovedNodes() method has been deprecated in both
  // GraphPerspective and RootGraph listener systems.
  RootGraphNodesRemovedEvent(RootGraph rootGraph, Node[] removedNodes)
  {
    super(rootGraph);
    m_removedNodes = removedNodes;
  }

  public final int getType()
  {
    return NODES_REMOVED_TYPE;
  }

  // If this system of listeners and events is to be used publicly (outside
  // of this package, that is), then we need to make a copy of this array
  // and return that copy.
  public final Node[] getRemovedNodes()
  {
    return m_removedNodes;
  }

  // This method throws an exception, which is fine, because this system of
  // listeners and events is only used internally by this package.  Nothing
  // in this package calls this method.
  public final int[] getRemovedNodeIndices()
  {
    throw new UnsupportedOperationException("don't call this method!");
  }

}
