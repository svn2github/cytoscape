package ding.view;

import cytoscape.render.stateful.NodeDetails;
import java.awt.Font;
import java.awt.Paint;

class IntermediateNodeDetails extends NodeDetails
{

  public byte shape(int node)
  {
    return DNodeView.DEFAULT_SHAPE;
  }

  public Paint borderPaint(int node)
  {
    return DNodeView.DEFAULT_BORDER_PAINT;
  }

  public String labelText(int node, int labelInx)
  {
    return DNodeView.DEFAULT_LABEL_TEXT;
  }

  public Font labelFont(int node, int labelInx)
  {
    return DNodeView.DEFAULT_LABEL_FONT;
  }

  public Paint labelPaint(int node, int labelInx)
  {
    return DNodeView.DEFAULT_LABEL_PAINT;
  }

}
