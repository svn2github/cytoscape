package ding.view;

import giny.model.Node;
import giny.view.GraphView;
import giny.view.Label;
import giny.view.NodeView;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.List;

class DNodeView implements NodeView
{

  DGraphView m_view;
  final int m_inx = -1;

  public GraphView getGraphView()
  {
    return m_view;
  }

  public Node getNode()
  {
    return null;
  }

  public int getGraphPerspectiveIndex()
  {
    return 0;
  }

  public int getRootGraphIndex()
  {
    return 0;
  }

  public List getEdgeViewsList(NodeView otherNode)
  {
    return null;
  }

  public int getShape()
  {
    return 0;
  }

  public void setSelectedPaint(Paint paint)
  {
  }

  public Paint getSelectedPaint()
  {
    return null;
  }

  public void setUnselectedPaint(Paint paint)
  {
  }

  public Paint getUnselectedPaint()
  {
    return null;
  }

  public void setBorderPaint(Paint paint)
  {
  }

  public Paint getBorderPaint()
  {
    return null;
  }

  public void setBorderWidth(float width)
  {
  }

  public float getBorderWidth()
  {
    return 0.0f;
  }

  public void setBorder(Stroke stroke)
  {
  }

  public Stroke getBorder()
  {
    return null;
  }

  public void setTransparency(float trans)
  {
  }

  public float getTransparency()
  {
    return 0.0f;
  }

  public boolean setWidth(double width)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return false; }
      final double xCenter =
        (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
      final double wDiv2 = width / 2.0d;
      final float xMin = (float) (xCenter - wDiv2);
      final float xMax = (float) (xCenter + wDiv2);
      if (!(xMax > xMin))
        throw new IllegalArgumentException("width is too small");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, xMin, m_view.m_extentsBuff[1],
                              xMax, m_view.m_extentsBuff[3]);
      return true; }
  }

  public double getWidth()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return -1.0d; }
      return m_view.m_extentsBuff[2] - m_view.m_extentsBuff[0]; }
  }

  public boolean setHeight(double height)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return false; }
      final double yCenter =
        (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
      final double hDiv2 = height / 2.0d;
      final float yMin = (float) (yCenter - hDiv2);
      final float yMax = (float) (yCenter + hDiv2);
      if (!(yMax > yMin))
        throw new IllegalArgumentException("height is too small");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, m_view.m_extentsBuff[0], yMin,
                              m_view.m_extentsBuff[2], yMax);
      return true; }
  }

  public double getHeight()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return -1.0d; }
      return m_view.m_extentsBuff[3] - m_view.m_extentsBuff[1]; }
  }

  public Label getLabel()
  {
    return null;
  }

  public int getDegree()
  {
    return 0;
  }

  public void setOffset(double x, double y)
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return; }
      final double wDiv2 =
        (((double) m_view.m_extentsBuff[2]) - m_view.m_extentsBuff[0]) / 2.0d;
      final double hDiv2 =
        (((double) m_view.m_extentsBuff[3]) - m_view.m_extentsBuff[1]) / 2.0d;
      final float xMin = (float) (x - wDiv2);
      final float xMax = (float) (x + wDiv2);
      final float yMin = (float) (y - hDiv2);
      final float yMax = (float) (y + hDiv2);
      if (!(xMax > xMin)) throw new IllegalStateException
                            ("width of node has degenerated to zero after " +
                             "rounding");
      if (!(yMax > yMin)) throw new IllegalStateException
                            ("height of node has degenerated to zero after " +
                             "rounding");
      m_view.m_spacial.delete(m_inx);
      m_view.m_spacial.insert(m_inx, xMin, yMin, xMax, yMax); }
  }

  public Point2D getOffset()
  {
    synchronized (m_view.m_lock) {
      if (!m_view.m_spacial.exists(m_inx, m_view.m_extentsBuff, 0)) {
        return null; }
      final double xCenter =
        (((double) m_view.m_extentsBuff[0]) + m_view.m_extentsBuff[2]) / 2.0d;
      final double yCenter =
        (((double) m_view.m_extentsBuff[1]) + m_view.m_extentsBuff[3]) / 2.0d;
      return new Point2D.Double(xCenter, yCenter); }
  }

  public void setXPosition(double xPos)
  {
  }

  public void setXPosition(double xPos, boolean update)
  {
  }

  public double getXPosition()
  {
    return 0.0d;
  }

  public void setYPosition(double yPos)
  {
  }

  public void setYPosition(double yPos, boolean update)
  {
  }

  public double getYPosition()
  {
    return 0.0d;
  }

  public void setNodePosition(boolean animate)
  {
  }

  public void select()
  {
  }

  public void unselect()
  {
  }

  public boolean isSelected()
  {
    return false;
  }

  public boolean setSelected(boolean selected)
  {
    return false;
  }

  public void setShape(int shape)
  {
  }

  public void setToolTip(String tip)
  {
  }

}
