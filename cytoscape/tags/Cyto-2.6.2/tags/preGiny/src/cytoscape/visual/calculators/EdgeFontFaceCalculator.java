//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.data.CyNetwork;
import java.awt.Font;
import y.base.Edge;
//--------------------------------------------------------------------------
public interface EdgeFontFaceCalculator extends Calculator {
    public Font calculateEdgeFontFace(Edge edge, CyNetwork network);
}
