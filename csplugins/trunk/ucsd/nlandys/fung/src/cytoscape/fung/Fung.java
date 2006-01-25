package cytoscape.fung;

import cytoscape.graph.dynamic.DynamicGraph;
import java.awt.Canvas;
import java.awt.Component;

public class Fung
{

  private final Canvas m_canvas;
  private final DynamicGraph m_graphModel;

  public Component getComponent()
  {
    return m_canvas;
  }

  public DynamicGraph getGraphModel()
  {
    return m_graphModel;
  }

}
