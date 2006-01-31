package cytoscape.fung;

import cytoscape.render.stateful.NodeDetails;
import java.awt.Paint;

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

  public Paint fillPaint(int node)
  {
    return m_fung.m_defaultNodeFillPaint;
  }

}
