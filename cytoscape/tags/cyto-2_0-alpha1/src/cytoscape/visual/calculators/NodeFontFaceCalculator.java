//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.data.CyNetwork;
import java.awt.Font;
import giny.model.Node;
//--------------------------------------------------------------------------
public interface NodeFontFaceCalculator extends Calculator {
    public Font calculateNodeFontFace(Node node, CyNetwork network);
}
