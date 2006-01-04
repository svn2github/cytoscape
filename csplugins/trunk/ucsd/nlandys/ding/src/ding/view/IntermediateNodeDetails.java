package ding.view;

import cytoscape.render.stateful.NodeDetails;
import java.awt.Paint;

class IntermediateNodeDetails extends NodeDetails
{

  public Paint borderPaint(int node)
  {
    return DNodeView.DEFAULT_BORDER_PAINT;
  }

}
