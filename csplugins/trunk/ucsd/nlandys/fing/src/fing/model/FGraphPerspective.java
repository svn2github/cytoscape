package fing.model;

import giny.model.GraphPerspective;
import giny.model.GraphPerspectiveChangeEvent;
import giny.model.GraphPerspectiveChangeListener;

// Package visible class.
class FGraphPerspective //implements GraphPerspective
{

  public void addGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  {
    lis = GraphPerspectiveChangeListenerChain.add(lis, listener);
  }

  public void removeGraphPerspectiveChangeListener
    (GraphPerspectiveChangeListener listener)
  {
    lis = GraphPerspectiveChangeListenerChain.remove(lis, listener);
  }

  private GraphPerspectiveChangeListener lis;

  // Package visible constructor.
  FGraphPerspective()
  {
    lis = new GraphPerspectiveChangeListener() {
        public void graphPerspectiveChanged(GraphPerspectiveChangeEvent evt)
        { } }; // Dummy listener so that member variable lis never null.
  }

}
