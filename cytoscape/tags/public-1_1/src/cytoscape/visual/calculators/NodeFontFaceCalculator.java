//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.visual.Network;
import java.awt.Font;
import y.base.Node;
//--------------------------------------------------------------------------
public interface NodeFontFaceCalculator extends Calculator {
    public Font calculateNodeFontFace(Node node, Network network);
}
