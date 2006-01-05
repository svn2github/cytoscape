package ding.view;

import cytoscape.render.stateful.EdgeDetails;
import java.awt.Font;
import java.awt.Paint;

class IntermediateEdgeDetails extends EdgeDetails
{

  public Paint sourceArrowPaint(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_PAINT;
  }

  public Paint targetArrowPaint(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_PAINT;
  }

  public float segmentThickness(int edge)
  {
    return DEdgeView.DEFAULT_EDGE_THICKNESS;
  }

  public String labelText(int edge, int labelInx)
  {
    return DEdgeView.DEFAULT_LABEL_TEXT;
  }

  public Font labelFont(int edge, int labelInx)
  {
    return DEdgeView.DEFAULT_LABEL_FONT;
  }

  public Paint labelPaint(int edge, int labelInx)
  {
    return DEdgeView.DEFAULT_LABEL_PAINT;
  }

}
