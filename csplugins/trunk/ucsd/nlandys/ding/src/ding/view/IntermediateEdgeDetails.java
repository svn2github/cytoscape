package ding.view;

import cytoscape.render.stateful.EdgeDetails;
import java.awt.Paint;

class IntermediateEdgeDetails extends EdgeDetails
{

  public Paint sourceArrowPaint(int edge)
  {
    return DEdgeView.DEFAULT_ARROW_PAINT;
  }

}
