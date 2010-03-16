// VizChooserClient
//-----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//---------------------------------------------------------------------------------------
package cytoscape;
//---------------------------------------------------------------------------------------
import cytoscape.vizmap.*;
//---------------------------------------------------------------------------------------
public interface VizChooserClient {
  public void applyAllVizMappings (NodeViz nodeViz);
  //public void applyAllVizAttributes(NodeViz nodeViz);
  //public void applyDefaultVizAttributes(NodeViz nodeViz);
  //public void applyNodeVizAttributes (NodeViz nodeViz);
  //public void applyBorderVizAttributes (NodeViz nodeViz);
  //public void applyEdgeVizAttributes (NodeViz nodeViz);
  public void applyExpressionVizMappings (NodeViz nodeViz);
  }
