package cytoscape.fung;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.render.stateful.EdgeDetails;
import java.awt.Color;
import java.awt.Paint;

class DefaultEdgeDetails extends EdgeDetails
{

  private final Fung m_fung;

  DefaultEdgeDetails(final Fung fung)
  {
    m_fung = fung;
  }

  public Color colorLowDetail(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_colorLowDetail; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_colorLowDetail; }
  }

  public byte sourceArrow(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_sourceArrow; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_sourceArrow; }
  }

  public float sourceArrowSize(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_sourceArrowSize; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_sourceArrowSize; }
  }

  public Paint sourceArrowPaint(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_sourceArrowPaint; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_sourceArrowPaint; }
  }

  public byte targetArrow(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_targetArrow; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_targetArrow; }
  }

  public float targetArrowSize(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_targetArrowSize; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_targetArrowSize; }
  }

  public Paint targetArrowPaint(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_targetArrowPaint; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_targetArrowPaint; }
  }

  public float segmentThickness(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_segmentThickness; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_segmentThickness; }
  }

  public Paint segmentPaint(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_segmentPaint; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_segmentPaint; }
  }

  public float segmentDashLength(int edge)
  {
    if (m_fung.m_graphModel.m_graph.edgeType(edge) ==
        DynamicGraph.DIRECTED_EDGE) {
      return m_fung.m_directedEdgeDefaults.m_segmentDashLength; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_segmentDashLength; }
  }

}
