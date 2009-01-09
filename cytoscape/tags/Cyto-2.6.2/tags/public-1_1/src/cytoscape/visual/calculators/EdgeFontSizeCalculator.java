//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.visual.Network;
import y.base.Edge;
//--------------------------------------------------------------------------
public interface EdgeFontSizeCalculator extends Calculator {
    public float calculateEdgeFontSize(Edge edge, Network network);
}
