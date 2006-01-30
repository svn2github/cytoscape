package cytoscape.fung;

import cytoscape.render.stateful.NodeDetails;

class DefaultNodeDetails extends NodeDetails
{

  private final Fung m_fung;

  DefaultNodeDetails(final Fung fung)
  {
    m_fung = fung;
  }

  public byte shape(int node)
  {
    return m_fung.m_defaultNodeShape;
  }

}
