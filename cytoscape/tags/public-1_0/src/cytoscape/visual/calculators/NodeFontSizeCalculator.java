//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.visual.Network;
import y.base.Node;
//--------------------------------------------------------------------------
public interface NodeFontSizeCalculator extends Calculator {
    public float calculateNodeFontSize(Node node, Network network);
}
