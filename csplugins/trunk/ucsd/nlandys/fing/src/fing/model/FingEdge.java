package fing.model;

import giny.model.Edge;
import giny.model.RootGraph;

/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.
 * @deprecated Use FingRootGraphFactory and ignore this class.
 * @see FingRootGraphFactory
 **/
public interface FingEdge extends Edge
{

  /**
   * This method is used by the internal Fing implementation to initialize
   * state of this edge once an edge is created in a RootGraph.
   **/
  public void _setRootGraph(RootGraph root);

  /**
   * This method is used by the internal Fing implementation to initialize
   * state of this edge once an edge is created in a RootGraph.
   **/
  public void _setRootGraphIndex(int index);

  /**
   * This method is used by the internal Fing implementation to initialize
   * state of this edge once an edge is created in a RootGraph.
   **/
  public void _setIdentifier(String id);

}
