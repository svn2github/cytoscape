package org.cytoscape.graph.layout.jgraph;

import org.cytoscape.graph.layout.LayoutAlgorithm;
import org.cytoscape.graph.layout.LayoutGraph;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphModel;
import org.jgraph.layout.CircleGraphLayout;
import org.jgraph.layout.JGraphLayoutAlgorithm;

public final class JGraphLayout extends LayoutAlgorithm
{

  public static final int TYPE_ANNEALING = 0;
  public static final int TYPE_MOEN = 1;
  public static final int TYPE_CIRCLE = 2;
  public static final int TYPE_RADIAL = 3;
  public static final int TYPE_GEM = 4;
  public static final int TYPE_SPRING = 5;
  public static final int TYPE_SUGIYAMA = 6;
  public static final int TYPE_TREE = 7;

  public JGraphLayoutAlgorithm(LayoutGraph graph, int layoutType)
  {
    super(graph);
  }

  public void run()
  {
    final JGraphLayoutAlgorithm layout = new CircleGraphLayout();
    final GraphModel model = new DefaultGraphModel();
    final JGraph graph = new JGraph(model);
  }

  public void destroy()
  {
  }

}
