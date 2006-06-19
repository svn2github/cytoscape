package ding.view;

import cytoscape.graph.fixed.FixedGraph;
import cytoscape.render.immed.EdgeAnchors;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntBTree;
import giny.model.Edge;
import giny.view.Bend;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeListener;
import giny.view.Label;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

class DEdgeView implements EdgeView, Label, Bend, EdgeAnchors
{

  static final float DEFAULT_ARROW_SIZE = 5.0f;
  static final Paint DEFAULT_ARROW_PAINT = Color.black;
  static final float DEFAULT_EDGE_THICKNESS = 1.0f;
  static final Color DEFAULT_EDGE_PAINT = Color.black;
  static final String DEFAULT_LABEL_TEXT = "";
  static final Font DEFAULT_LABEL_FONT = new Font(null, Font.PLAIN, 1);
  static final Paint DEFAULT_LABEL_PAINT = Color.black;

  DGraphView m_view;
  final int m_inx; // Positive.
  boolean m_selected;
  Paint m_unselectedPaint;
  Paint m_selectedPaint;
  Paint m_sourceUnselectedPaint;
  Paint m_sourceSelectedPaint;
  Paint m_targetUnselectedPaint;
  Paint m_targetSelectedPaint;
  int m_sourceEdgeEnd; // One of the EdgeView edge end constants.
  int m_targetEdgeEnd; // Ditto.
  ArrayList m_anchors; // A list of Point2D objects.
  int m_lineType;

  String m_toolTipText = null;

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
    m_sourceSelectedPaint = Color.red;
    m_targetUnselectedPaint = m_view.m_edgeDetails.targetArrowPaint(m_inx);
    m_targetSelectedPaint = Color.red;
    m_sourceEdgeEnd = EdgeView.NO_END;
    m_targetEdgeEnd = EdgeView.NO_END;
    m_anchors = null;
    m_lineType = EdgeView.STRAIGHT_LINES;
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
    return m_view.getGraphPerspective().getEdge(~m_inx);
  }

  public GraphView getGraphView()
  {
    return m_view;
  }

  public void setStrokeWidth(float width)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideSegmentThickness(m_inx, width);
      m_view.m_contentChanged = true; }
  }

  public float getStrokeWidth()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.segmentThickness(m_inx); }
  }

  public void setStroke(Stroke stroke)
  {
    if (stroke instanceof BasicStroke) {
      synchronized (m_view.m_lock) {
        final BasicStroke bStroke = (BasicStroke) stroke;
        m_view.m_edgeDetails.overrideSegmentThickness
          (m_inx, bStroke.getLineWidth());
        final float[] dashArr = bStroke.getDashArray();
        if (dashArr != null && dashArr.length > 0) {
          m_view.m_edgeDetails.overrideSegmentDashLength
            (m_inx, dashArr[0]); }
        else {
          m_view.m_edgeDetails.overrideSegmentDashLength(m_inx, -1.0f); }
        m_view.m_contentChanged = true; } }
  }

  public Stroke getStroke()
  {
    synchronized (m_view.m_lock) {
      final float segmentThickness =
        m_view.m_edgeDetails.segmentThickness(m_inx);
      final float segmentDashLength =
        m_view.m_edgeDetails.segmentDashLength(m_inx);
      if (segmentDashLength > 0.0f) {
        final float[] dashes = new float[] { segmentDashLength,
                                             segmentDashLength };
        return new BasicStroke(segmentThickness,
                               BasicStroke.CAP_SQUARE,
                               BasicStroke.JOIN_MITER, 10.0f,
                               dashes, 0.0f); }
      else {
        return new BasicStroke(segmentThickness); } }
  }

  public void setLineType(int lineType)
  {
    if (lineType == EdgeView.CURVED_LINES ||
        lineType == EdgeView.STRAIGHT_LINES) {
      synchronized (m_view.m_lock) {
        m_lineType = lineType;
        m_view.m_contentChanged = true; } }
    else {
      throw new IllegalArgumentException("unrecognized line type"); }
  }

  public int getLineType()
  {
    return m_lineType;
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
            (m_inx, (Color) m_unselectedPaint); }
        m_view.m_contentChanged = true; }
      setSourceEdgeEnd(m_sourceEdgeEnd);
      setTargetEdgeEnd(m_targetEdgeEnd); }
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
            (m_inx, (Color) m_selectedPaint); }
        m_view.m_contentChanged = true; } }
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
          (m_inx, m_sourceSelectedPaint);
        m_view.m_contentChanged = true; } }
  }

  public void setTargetEdgeEndSelectedPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_targetSelectedPaint = paint;
      if (isSelected()) {
        m_view.m_edgeDetails.overrideTargetArrowPaint
          (m_inx, m_targetSelectedPaint);
        m_view.m_contentChanged = true; } }
  }

  public void setSourceEdgeEndStrokePaint(Paint paint)
  {
    // No-op.
  }

  public void setTargetEdgeEndStrokePaint(Paint paint)
  {
    // No-op.
  }

  public void setSourceEdgeEndPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_sourceUnselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_edgeDetails.overrideSourceArrowPaint
          (m_inx, m_sourceUnselectedPaint);
        m_view.m_contentChanged = true; } }
  }

  public void setTargetEdgeEndPaint(Paint paint)
  {
    synchronized (m_view.m_lock) {
      if (paint == null) {
        throw new NullPointerException("paint is null"); }
      m_targetUnselectedPaint = paint;
      if (!isSelected()) {
        m_view.m_edgeDetails.overrideTargetArrowPaint
          (m_inx, m_targetUnselectedPaint);
        m_view.m_contentChanged = true; } }
  }

  public void select()
  {
    final boolean somethingChanged;
    synchronized (m_view.m_lock) {
      somethingChanged = selectInternal(true);
      if (somethingChanged) { m_view.m_contentChanged = true; } }
    if (somethingChanged) {
      final GraphViewChangeListener listener = m_view.m_lis[0];
      if (listener != null) {
        listener.graphViewChanged
          (new GraphViewEdgesSelectedEvent(m_view,
                                           new int[] { ~m_inx })); } }
  }

  // Should synchronize around m_view.m_lock.
  boolean selectInternal(boolean selectAnchors)
  {
    if (m_selected) { return false; }
    m_selected = true;
    m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_selectedPaint);
    m_view.m_edgeDetails.overrideSourceArrowPaint
      (m_inx, m_sourceSelectedPaint);
    m_view.m_edgeDetails.overrideTargetArrowPaint
      (m_inx, m_targetSelectedPaint);
    if (m_selectedPaint instanceof Color) {
      m_view.m_edgeDetails.overrideColorLowDetail
        (m_inx, (Color) m_selectedPaint); }
    m_view.m_selectedEdges.insert(m_inx);
    for (int j = 0; j < numHandles(); j++) {
      getHandleInternal(j, m_view.m_anchorsBuff);
      m_view.m_spacialA.insert
        ((m_inx << 6) | j,
         (float) (m_view.m_anchorsBuff[0] - m_view.getAnchorSize() / 2.0d),
         (float) (m_view.m_anchorsBuff[1] - m_view.getAnchorSize() / 2.0d),
         (float) (m_view.m_anchorsBuff[0] + m_view.getAnchorSize() / 2.0d),
         (float) (m_view.m_anchorsBuff[1] + m_view.getAnchorSize() / 2.0d));
      if (selectAnchors) {
        m_view.m_selectedAnchors.insert((m_inx << 6) | j); } }
    return true;
  }

  public void unselect()
  {
    final boolean somethingChanged;
    synchronized (m_view.m_lock) {
      somethingChanged = unselectInternal();
      if (somethingChanged) { m_view.m_contentChanged = true; } }
    if (somethingChanged) {
      final GraphViewChangeListener listener = m_view.m_lis[0];
      if (listener != null) {
        listener.graphViewChanged
          (new GraphViewEdgesUnselectedEvent(m_view,
                                             new int[] { ~m_inx })); } }
  }

  // Should synchronize around m_view.m_lock.
  boolean unselectInternal()
  {
    if (!m_selected) { return false; }
    m_selected = false;
    m_view.m_edgeDetails.overrideSegmentPaint(m_inx, m_unselectedPaint);
    m_view.m_edgeDetails.overrideSourceArrowPaint
      (m_inx, m_sourceUnselectedPaint);
    m_view.m_edgeDetails.overrideTargetArrowPaint
      (m_inx, m_targetUnselectedPaint);
    if (m_unselectedPaint instanceof Color) {
      m_view.m_edgeDetails.overrideColorLowDetail
        (m_inx, (Color) m_unselectedPaint); }
    m_view.m_selectedEdges.delete(m_inx);
    for (int j = 0; j < numHandles(); j++) {
      m_view.m_selectedAnchors.delete((m_inx << 6) | j);
      m_view.m_spacialA.delete((m_inx << 6) | j); }
    return true;
  }

  public boolean setSelected(boolean state)
  {
    if (state) { select(); }
    else { unselect(); }
    return true;
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

  public void setSourceEdgeEnd(final int type)
  {
    synchronized (m_view.m_lock) {
//       if (type == m_sourceEdgeEnd) { return; }
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
      m_sourceEdgeEnd = type;
      m_view.m_contentChanged = true; }
  }

  public void setTargetEdgeEnd(int type)
  {
    synchronized (m_view.m_lock) {
//       if (type == m_targetEdgeEnd) { return; }
      switch (type) {
      case NO_END:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_NONE);
        break;
      case WHITE_DELTA:
      case WHITE_ARROW:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setTargetEdgeEndPaint(Color.white);
        break;
      case BLACK_DELTA:
      case BLACK_ARROW:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setTargetEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_DELTA:
      case EDGE_COLOR_ARROW:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DELTA);
        setTargetEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_DIAMOND:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setTargetEdgeEndPaint(Color.white);
        break;
      case BLACK_DIAMOND:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setTargetEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_DIAMOND:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DIAMOND);
        setTargetEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_CIRCLE:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setTargetEdgeEndPaint(Color.white);
        break;
      case BLACK_CIRCLE:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setTargetEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_CIRCLE:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_DISC);
        setTargetEdgeEndPaint(getUnselectedPaint());
        break;
      case WHITE_T:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setTargetEdgeEndPaint(Color.white);
        break;
      case BLACK_T:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setTargetEdgeEndPaint(Color.black);
        break;
      case EDGE_COLOR_T:
        m_view.m_edgeDetails.overrideTargetArrow
          (m_inx, GraphGraphics.ARROW_TEE);
        setTargetEdgeEndPaint(getUnselectedPaint());
        break;
      default:
        throw new IllegalArgumentException("unrecognized edge end type"); }
      m_targetEdgeEnd = type;
      m_view.m_contentChanged = true; }
  }

  public int getSourceEdgeEnd()
  {
    return m_sourceEdgeEnd;
  }

  public int getTargetEdgeEnd()
  {
    return m_targetEdgeEnd;
  }

  public void updateLine()
  {
  }

  // This is also a method on giny.view.Bend.
  public void drawSelected()
  {
    select();
  }

  // This is also a method on giny.view.Bend.
  public void drawUnselected()
  {
    unselect();
  }

  public Bend getBend()
  {
    return this;
  }

  public void clearBends()
  {
    removeAllHandles();
  }

  public Label getLabel()
  {
    return this;
  }

  public void setToolTip(String tip)
  {
    m_toolTipText = tip;
  }

  public String getToolTip()
  {
    return m_toolTipText;
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
      m_view.m_edgeDetails.overrideLabelPaint(m_inx, 0, textPaint);
      m_view.m_contentChanged = true; }
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
        m_view.m_edgeDetails.overrideLabelCount(m_inx, 1); }
      m_view.m_contentChanged = true; }
  }

  public Font getFont()
  {
    synchronized (m_view.m_lock) {
      return m_view.m_edgeDetails.labelFont(m_inx, 0); }
  }

  public void setFont(Font font)
  {
    synchronized (m_view.m_lock) {
      m_view.m_edgeDetails.overrideLabelFont(m_inx, 0, font);
      m_view.m_contentChanged = true; }
  }


  // Interface giny.view.Bend:

  public int numHandles()
  {
    synchronized (m_view.m_lock) {
      if (m_anchors == null) { return 0; }
      return m_anchors.size(); }
  }

  public void setHandles(List bendPoints)
  {
    synchronized (m_view.m_lock) {
      removeAllHandles();
      for (int i = 0; i < bendPoints.size(); i++) {
        final Point2D nextPt = (Point2D) bendPoints.get(i);
        addHandle(i, nextPt); }
      m_view.m_contentChanged = true; }
  }

  public List getHandles()
  {
    synchronized (m_view.m_lock) {
      final ArrayList returnThis = new ArrayList();
      if (m_anchors == null) { return returnThis; }
      for (int i = 0; i < m_anchors.size(); i++) {
        final Point2D addThis = new Point2D.Float();
        addThis.setLocation((Point2D) m_anchors.get(i));
        returnThis.add(addThis); }
      return returnThis; }
  }

  public void moveHandle(int inx, Point2D pt)
  {
    synchronized (m_view.m_lock) {
      moveHandleInternal(inx, pt.getX(), pt.getY());
      m_view.m_contentChanged = true; }
  }

  final void moveHandleInternal(int inx, double x, double y)
  {
    final Point2D movePt = (Point2D) m_anchors.get(inx);
    movePt.setLocation(x, y);
    if (m_view.m_spacialA.delete((m_inx << 6) | inx)) {
      m_view.m_spacialA.insert
        ((m_inx << 6) | inx,
         (float) (x - m_view.getAnchorSize() / 2.0d),
         (float) (y - m_view.getAnchorSize() / 2.0d),
         (float) (x + m_view.getAnchorSize() / 2.0d),
         (float) (y + m_view.getAnchorSize() / 2.0d)); }
  }

  final void getHandleInternal(int inx, float[] buff)
  {
    final Point2D.Float pt = (Point2D.Float) m_anchors.get(inx);
    buff[0] = pt.x;
    buff[1] = pt.y;
  }

  public Point2D getSourceHandlePoint()
  {
    synchronized (m_view.m_lock) {
      if (m_anchors == null || m_anchors.size() == 0) { return null; }
      final Point2D returnThis = new Point2D.Float();
      returnThis.setLocation((Point2D) m_anchors.get(0));
      return returnThis; }
  }

  public Point2D getTargetHandlePoint()
  {
    synchronized (m_view.m_lock) {
      if (m_anchors == null || m_anchors.size() == 0) { return null; }
      final Point2D returnThis = new Point2D.Float();
      returnThis.setLocation((Point2D) m_anchors.get(m_anchors.size() - 1));
      return returnThis; }
  }

  public void addHandle(Point2D pt)
  {
    addHandleFoo(pt);
  }

  public int addHandleFoo(Point2D pt)
  {
    synchronized (m_view.m_lock) {
      if (m_anchors == null || m_anchors.size() == 0) {
        addHandle(0, pt);
        return 0; }
      final Point2D sourcePt =
        m_view.getNodeView
        (~((FixedGraph) m_view.m_structPersp).edgeSource(m_inx)).getOffset();
      final Point2D targetPt =
        m_view.getNodeView
        (~((FixedGraph) m_view.m_structPersp).edgeTarget(m_inx)).getOffset();
      double bestDist =
        pt.distance(sourcePt) + pt.distance((Point2D) m_anchors.get(0)) -
        sourcePt.distance((Point2D) m_anchors.get(0));
      int bestInx = 0;
      for (int i = 1; i < m_anchors.size(); i++) {
        final double distCand =
          pt.distance((Point2D) m_anchors.get(i - 1)) +
          pt.distance((Point2D) m_anchors.get(i)) -
          ((Point2D) m_anchors.get(i)).distance
          ((Point2D) m_anchors.get(i - 1));
        if (distCand < bestDist) {
          bestDist = distCand;
          bestInx = i; } }
      final double lastCand =
        pt.distance(targetPt) +
        pt.distance((Point2D) m_anchors.get(m_anchors.size() - 1)) -
        targetPt.distance((Point2D) m_anchors.get(m_anchors.size() - 1));
      if (lastCand < bestDist) {
        bestDist = lastCand;
        bestInx = m_anchors.size(); }
      addHandle(bestInx, pt);
      return bestInx; }
  }

  public void addHandle(int insertInx, Point2D pt)
  {
    synchronized (m_view.m_lock) {
      final Point2D.Float addThis = new Point2D.Float();
      addThis.setLocation(pt);
      if (m_anchors == null) { m_anchors = new ArrayList(); }
      m_anchors.add(insertInx, addThis);
      if (m_selected) {
        for (int j = m_anchors.size() - 1; j > insertInx; j--) {
          m_view.m_spacialA.exists((m_inx << 6) | (j - 1),
                                   m_view.m_extentsBuff, 0);
          m_view.m_spacialA.delete((m_inx << 6) | (j - 1));
          m_view.m_spacialA.insert
            ((m_inx << 6) | j,
             m_view.m_extentsBuff[0], m_view.m_extentsBuff[1],
             m_view.m_extentsBuff[2], m_view.m_extentsBuff[3]);
          if (m_view.m_selectedAnchors.delete((m_inx << 6) | (j - 1))) {
            m_view.m_selectedAnchors.insert((m_inx << 6) | j); } }
        m_view.m_spacialA.insert
          ((m_inx << 6) | insertInx,
           (float) (addThis.x - m_view.getAnchorSize() / 2.0d),
           (float) (addThis.y - m_view.getAnchorSize() / 2.0d),
           (float) (addThis.x + m_view.getAnchorSize() / 2.0d),
           (float) (addThis.y + m_view.getAnchorSize() / 2.0d)); }
      m_view.m_contentChanged = true; }
  }

  public void removeHandle(Point2D pt)
  {
    synchronized (m_view.m_lock) {
      final float x = (float) pt.getX();
      final float y = (float) pt.getY();
      if (m_anchors == null) { return; }
      for (int i = 0; i < m_anchors.size(); i++) {
        final Point2D.Float currPt = (Point2D.Float) m_anchors.get(i);
        if (x == currPt.x && y == currPt.y) {
          removeHandle(i);
          break; } } }
  }

  public void removeHandle(int inx)
  {
    synchronized (m_view.m_lock) {
      m_anchors.remove(inx);
      if (m_selected) {
        m_view.m_spacialA.delete((m_inx << 6) | inx);
        m_view.m_selectedAnchors.delete((m_inx << 6) | inx);
        for (int j = inx; j < m_anchors.size(); j++) {
          m_view.m_spacialA.exists((m_inx << 6) | (j + 1),
                                   m_view.m_extentsBuff, 0);
          m_view.m_spacialA.delete((m_inx << 6) | (j + 1));
          m_view.m_spacialA.insert
            ((m_inx << 6) | j,
             m_view.m_extentsBuff[0], m_view.m_extentsBuff[1],
             m_view.m_extentsBuff[2], m_view.m_extentsBuff[3]);
          if (m_view.m_selectedAnchors.delete((m_inx << 6) | (j + 1))) {
            m_view.m_selectedAnchors.insert((m_inx << 6) | j); } } }
      if (m_anchors.size() == 0) { m_anchors = null; }
      m_view.m_contentChanged = true; }
  }

  public void removeAllHandles()
  {
    synchronized (m_view.m_lock) {
      if (m_anchors == null) { return; }
      if (m_selected) {
        for (int j = 0; j < m_anchors.size(); j++) {
          m_view.m_spacialA.delete((m_inx << 6) | j);
          m_view.m_selectedAnchors.delete((m_inx << 6) | j); } }
      m_anchors = null;
      m_view.m_contentChanged = true; }
  }

  public boolean handleAlreadyExists(Point2D pt)
  {
    synchronized (m_view.m_lock) {
      final float x = (float) pt.getX();
      final float y = (float) pt.getY();
      if (m_anchors == null) { return false; }
      for (int i = 0; i < m_anchors.size(); i++) {
        final Point2D.Float currPt = (Point2D.Float) m_anchors.get(i);
        if (x == currPt.x && y == currPt.y) {
          return true; } }
      return false; }
  }

  public Point2D[] getDrawPoints()
  {
    synchronized (m_view.m_lock) {
      final Point2D[] returnThis =
        new Point2D[m_anchors == null ? 0 : m_anchors.size()];
      for (int i = 0; i < returnThis.length; i++) {
        returnThis[i] = new Point2D.Float();
        returnThis[i].setLocation((Point2D) m_anchors.get(i)); }
      return returnThis; }
  }


  // Interface cytoscape.render.immed.EdgeAnchors:

  public int numAnchors()
  {
    if (m_anchors == null) { return 0; }
    if (m_lineType == EdgeView.CURVED_LINES) {
      return m_anchors.size(); }
    else { // EdgeView.STRAIGHT_LINES.
      return 2 * m_anchors.size(); }
  }

  public void getAnchor(int anchorIndex, float[] anchorArr, int offset)
  {
    final Point2D.Float anchor;
    if (m_lineType == EdgeView.CURVED_LINES) {
      anchor = (Point2D.Float) m_anchors.get(anchorIndex); }
    else { // EdgeView.STRAIGHT_LINES.
      anchor = (Point2D.Float) m_anchors.get(anchorIndex / 2); }
    anchorArr[offset] = anchor.x;
    anchorArr[offset + 1] = anchor.y;
  }

  // Auxillary methods for edge anchors.

}
