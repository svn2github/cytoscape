//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.CyNetwork;
import java.awt.Font;
import giny.model.Edge;
//--------------------------------------------------------------------------
public interface EdgeFontFaceCalculator extends Calculator {
    public Font calculateEdgeFontFace(Edge edge, CyNetwork network);
}
