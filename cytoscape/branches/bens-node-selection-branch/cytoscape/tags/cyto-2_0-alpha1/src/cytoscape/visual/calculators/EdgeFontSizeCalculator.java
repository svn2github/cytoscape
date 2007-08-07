//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.data.CyNetwork;
import giny.model.Edge;
//--------------------------------------------------------------------------
public interface EdgeFontSizeCalculator extends Calculator {
    public float calculateEdgeFontSize(Edge edge, CyNetwork network);
}
