package ding.view;

import cytoscape.render.immed.GraphGraphics;
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
  Paint m_unselectedPaint;
  Paint m_selectedPaint;
  Paint m_sourceUnselectedPaint;
  Paint m_sourceSelectedPaint;
  Paint m_targetUnselectedPaint;
  Paint m_targetSelectedPaint;
  int m_sourceEdgeEnd; // One of the EdgeView edge end constants.
  int m_targetEdgeEnd; // Ditto.

  /*
   * @param inx the RootGraph index of edge (a negative number).
   */
  DEdgeView(DGraphView view, int inx)
  {
    m_view = view;
    m_inx = ~inx;
    m_selected = false;
    m_unselectedPaint = m_view.m_edgeDetails.segmentPaint(m_inx);
    m_selectedPaint = Color.red;
    m_sourceUnselectedPaint = m_view.m_edgeDetails.sourceArrowPaint(m_inx);
    m_sourceSelectedPaint = DEFAULT_ARROW_PAINT;
    m_targetUnselectedPaint = m_view.m_edgeDetails.targetArrowPaint(m_inx);
    m_targetSelectedPaint = DEFAULT_ARROW_PAINT;
    m_sourceEdgeEnd = EdgeView.NO_END;
    m_targetEdgeEnd = EdgeView.NO_END;
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
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_unselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_unselectedPaint);
        if (m_unselectedPaint instanceof Color) {
          m_view.m_edgeDetails.overrideColorLowDetail
            (m_inx, (Color) m_unselectedPaint); } } }
  }

  public Paint getUnselectedPaint()
  {
    return m_unselectedPaint;
  }

  public void setSelectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_selectedPaint = paint;
      if (isSelected()) {
        m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_selectedPaint);
        if (m_selectedPaint instanceof Color) {
          m_view.m_edgeDetails.overrideColorLowDetail
            (m_inx, (Color) m_selectedPaint); } } }
  }

  public Paint getSelectedPaint()
  {
    return m_selectedPaint;
  }

  public Paint getSourceEdgeEndPaint()
  {
    return m_sourceUnselectedPaint;
  }

  public Paint getSourceEdgeEndSelectedPaint()
  {
    return m_sourceSelectedPaint;
  }

  public Paint getTargetEdgeEndPaint()
  {
    return m_targetUnselectedPaint;
  }

  public Paint getTargetEdgeEndSelectedPaint()
  {
    return m_targetSelectedPaint;
  }

  public void setSourceEdgeEndSelectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_sourceSelectedPaint = paint;
      if (isSelected()) {
        m_view.m_edgeDetails.overrideSourceArrowPaint
          (m_inx, m_sourceSelectedPaint); } }
  }

  public void setTargetEdgeEndSelectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_targetSelectedPaint = paint;
      if (isSelected()) {
        m_view.m_edgeDetails.overrideTargetArrowPaint
          (m_inx, m_targetSelectedPaint); } }
  }

  /*
   * No-op.
   */
  public void setSourceEdgeEndStrokePaint(Paint paint)
  {
  }

  /*
   * No-op.
   */
  public void setTargetEdgeEndStrokePaint(Paint paint)
  {
  }

  public void setSourceEdgeEndPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_sourceUnselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_edgeDetails.overrideSourceArrowPaint
          (m_inx, m_sourceUnselectedPaint); } }
  }

  public void setTargetEdgeEndPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_targetUnselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_edgeDetails.overrideTargetArrowPaint
          (m_inx, m_targetUnselectedPaint); } }
  }

  public void select()
  {
    synchronized (m_view.m_lock) {
      if (m_selected) { return; }
      m_selected = true;
      m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_selectedPaint);
      m_view.m_edgeDetails.overrideSourceArrowPaint
        (m_inx, m_sourceSelectedPaint);
      m_view.m_edgeDetails.overrideTargetArrowPaint
        (m_inx, m_targetSelectedPaint);
      if (m_selectedPaint instanceof Color) {
        m_view.m_edgeDetails.overrideColorLowDetail
          (m_inx, (Color) m_selectedPaint); } }
  }

  public void unselect()
  {
    synchronized (m_view.m_lock) {
      if (!m_selected) { return; }
      m_selected = false;
      m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_unselectedPaint);
      m_view.m_edgeDetails.overrideSourceArrowPaint
        (m_inx, m_sourceUnselectedPaint);
      m_view.m_edgeDetails.overrideTargetArrowPaint
        (m_inx, m_targetUnselectedPaint);
      if (m_unselectedPaint instanceof Color) {
        m_view.m_edgeDetails.overrideColorLowDetail
          (m_inx, (Color) m_unselectedPaint); } }
  }

  public boolean setSelected(boolean state)
  {
    synchronized (m_view.m_lock) {
      if (state) {
        if (m_selected) { return false; }
        select(); return true; }
      else {
        if (!m_selected) { return false; }
        unselect(); return true; } }
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
    m_view.updateView();
  }

  public void updateTargetArrow()
  {
    m_view.updateView();
  }

  public void updateSourceArrow()
  {
    m_view.updateView();
  }

  public void setSourceEdgeEnd(final int type)
  {
    synchronized (m_view.m_lock) {
      if (type == m_sourceEdgeEnd) { return; }
      switch (type) {
      case NO_END:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_NONE);
        break;
      case WHITE_DELTA:
      case WHITE_ARROW:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setSourceEdgeEndPaint(Color.white);
        break;
      case BLACK_DELTA:
      case BLACK_ARROW:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setSourceEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_DELTA:
      case EDGE_COLOR_ARROW:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setSourceEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_DIAMOND:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setSourceEdgeEndPaint(Color.white);
        break;
      case BLACK_DIAMOND:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setSourceEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_DIAMOND:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setSourceEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_CIRCLE:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setSourceEdgeEndPaint(Color.white);
        break;
      case BLACK_CIRCLE:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setSourceEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_CIRCLE:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setSourceEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_T:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setSourceEdgeEndPaint(Color.white);
        break;
      case BLACK_T:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setSourceEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_T:
        m_view.m_edgeDetails.overrideSourceArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setSourceEdgeEndPaint(getUnselectedPaint());
        break;
      default:
        throw new IllegalArgumentException("unrecognized edge end type"); }
      m_sourceEdgeEnd = type; }
  }

  public void setTargetEdgeEnd(int type)
  {
  }

  public int getSourceEdgeEnd()
  {
    return m_sourceEdgeEnd;
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
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.labelPaint(m_inx, 0); }
  }

  public void setTextPaint(Paint textPaint)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideLabelPaint(m_inx, 0, textPaint); }
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
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.labelText(m_inx, 0); }
  }

  public void setText(String text)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideLabelText(m_inx, 0, text);
      if (DEFAULT_LABEL_TEXT.equals
          (m_view.m_edgeDetails.labelText(m_inx, 0))) {
        m_view.m_edgeDetails.overrideLabelCount(m_inx, 0); }
      else {
        m_view.m_edgeDetails.overrideLabelCount(m_inx, 1); } }
  }

  public Font getFont()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.labelFont(m_inx, 0); }
  }

  public void setFont(Font font)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideLabelFont(m_inx, 0, font); }
  }

}
