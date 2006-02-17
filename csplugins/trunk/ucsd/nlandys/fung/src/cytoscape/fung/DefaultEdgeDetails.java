package cytoscape.fung;

import java.awt.Color;

import cytoscape.render.stateful.EdgeDetails;

class DefaultEdgeDetails extends EdgeDetails
{

  private final Fung m_fung;

  DefaultEdgeDetails(final Fung fung)
  {
    m_fung = fung;
  }

  public Color colorLowDetail(int edge)
  {
    if (m_fung.getGraphModel().edgeType(edge) > 0) {
      return m_fung.m_directedEdgeDefaults.m_colorLowDetail; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_colorLowDetail; }
  }

  public byte sourceArrow(int edge)
  {
    if (m_fung.getGraphModel().edgeType(edge) > 0) {
      return m_fung.m_directedEdgeDefaults.m_sourceArrow; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_sourceArrow; }
  }

  public float sourceArrowSize(int edge)
  {
    if (m_fung.getGraphModel().edgeType(edge) > 0) {
      return m_fung.m_directedEdgeDefaults.m_sourceArrowSize; }
    else {
      return m_fung.m_undirectedEdgeDefaults.m_sourceArrowSize; }
  }

}
