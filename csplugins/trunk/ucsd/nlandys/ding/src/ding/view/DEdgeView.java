package ding.view;

import giny.model.Edge;
import giny.view.Bend;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.Label;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;

class DEdgeView implements EdgeView, Label
{

  static final Paint DEFAULT_ARROW_PAINT = Color.black;
  static final float DEFAULT_EDGE_THICKNESS = 0.2f;
  static final String DEFAULT_LABEL_TEXT = "";
  static final Font DEFAULT_LABEL_FONT = new Font(null, Font.PLAIN, 1);
  static final Paint DEFAULT_LABEL_PAINT = Color.black;

  DGraphView m_view;
  final int m_inx;
  boolean m_selected;

  /*
   * @param inx the RootGraph index of edge (a negative number).
   */
  DEdgeView(DGraphView view, int inx)
  {
    m_view = view;
    m_inx = ~inx;
    m_selected = false;
  }

  public int getGraphPerspectiveIndex()
  {
    return ~m_inx;
  }

  public int getRootGraphIndex()
  {
    return ~m_inx;
  }

  public Edge getEdge()
  {
    return null;
  }

  public GraphView getGraphView()
  {
    return m_view;
  }

  public void setStrokeWidth(float width)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideSegmentThickness(m_inx, width); }
  }

  public float getStrokeWidth()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.segmentThickness(m_inx); }
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
    return m_selected;
  }

  public boolean getSelected()
  {
    return m_selected;
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
    return this;
  }

  public void setToolTip(String tip)
  {
  }


  // Interface giny.view.Label:

  public void setPositionHint(int position)
  {
  }

  public Paint getTextPaint()
  {
    return null;
  }

  public void setTextPaint(Paint textPaint)
  {
  }

  public double getGreekThreshold()
  {
    return 0.0d;
  }

  public void setGreekThreshold(double threshold)
  {
  }

  public String getText()
  {
    return null;
  }

  public void setText(String text)
  {
  }

  public Font getFont()
  {
    return null;
  }

  public void setFont(Font font)
  {
  }

}
