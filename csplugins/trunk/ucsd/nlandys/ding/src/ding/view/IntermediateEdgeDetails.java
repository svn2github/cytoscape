package ding.view;

import cytoscape.render.stateful.EdgeDetails;
import java.awt.Font;
import java.awt.Color;
import java.awt.Paint;

class IntermediateEdgeDetails extends EdgeDetails
{

  // Note: It is extremely important that the methds sourceArrow(int) and
  // targetArrow(int) both return GraphGraphics.ARROW_NONE.  Methods in
  // DEdgeView rely on this.  Right now EdgeDetails does return these values
  // by default.  I could even override those methods here and redundantly
  // return those same values, but I prefer not to.

  public Color colorLowDetail(int edge)
  {
    return DEdgeView.DEFAULT_EDGE_PAINT;
  }

  public float sourceArrowSize(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_SIZE;
  }

  public Paint sourceArrowPaint(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_PAINT;
  }

  public float targetArrowSize(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_SIZE;
  }

  public Paint targetArrowPaint(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_PAINT;
  }

  public float segmentThickness(int edge)
  {
    return DEdgeView.DEFAULT_EDGE_THICKNESS;
  }

  public Paint segmentPaint(int edge)
  {
    return DEdgeView.DEFAULT_EDGE_PAINT;
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
