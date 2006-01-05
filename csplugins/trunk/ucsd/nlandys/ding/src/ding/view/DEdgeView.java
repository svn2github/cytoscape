package ding.view;

import giny.model.Edge;
import giny.view.Bend;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.Label;
import java.awt.Paint;
import java.awt.Stroke;

class DEdgeView implements EdgeView
{

  public int getGraphPerspectiveIndex()
  {
    return 0;
  }

  public int getRootGraphIndex()
  {
    return 0;
  }

  public Edge getEdge()
  {
    return null;
  }

  public GraphView getGraphView()
  {
    return null;
  }

  public void setStrokeWidth(float width)
  {
  }

  public float getStrokeWidth()
  {
    return 0.0f;
  }

  public void setStroke(Stroke stroke)
  {
  }

  public Stroke getStroke()
  {
    return null;
  }

  public void setLineType(int lineType)
  {
  }

  public int getLineType()
  {
    return 0;
  }

  public void setUnselectedPaint(Paint paint)
  {
  }

  public Paint getUnselectedPaint()
  {
    return null;
  }

  public void setSelectedPaint(Paint paint)
  {
  }

  public Paint getSelectedPaint()
  {
    return null;
  }

  public Paint getSourceEdgeEndPaint()
  {
    return null;
  }

  public Paint getSourceEdgeEndSelectedPaint()
  {
    return null;
  }

  public Paint getTargetEdgeEndPaint()
  {
    return null;
  }

  public Paint getTargetEdgeEndSelectedPaint()
  {
    return null;
  }

  public void setSourceEdgeEndSelectedPaint(Paint paint)
  {
  }

  public void setTargetEdgeEndSelectedPaint(Paint paint)
  {
  }

  public void setSourceEdgeEndStrokePaint(Paint paint)
  {
  }

  public void setTargetEdgeEndStrokePaint(Paint paint)
  {
  }

  public void setSourceEdgeEndPaint(Paint paint)
  {
  }

  public void setTargetEdgeEndPaint(Paint paint)
  {
  }

  public void select()
  {
  }

  public void unselect()
  {
  }

  public boolean setSelected(boolean state)
  {
    return false;
  }

  public boolean isSelected()
  {
    return false;
  }

  public boolean getSelected()
  {
    return false;
  }

  public void updateEdgeView()
  {
  }

  public void updateTargetArrow()
  {
  }

  public void updateSourceArrow()
  {
  }

  public void setSourceEdgeEnd(int type)
  {
  }

  public void setTargetEdgeEnd(int type)
  {
  }

  public int getSourceEdgeEnd()
  {
    return 0;
  }

  public int getTargetEdgeEnd()
  {
    return 0;
  }

  public void updateLine()
  {
  }

  public void drawSelected()
  {
  }

  public void drawUnselected()
  {
  }

  public Bend getBend()
  {
    return null;
  }

  public void clearBends()
  {
  }

  public Label getLabel()
  {
    return null;
  }

  public void setToolTip(String tip)
  {
  }

}
