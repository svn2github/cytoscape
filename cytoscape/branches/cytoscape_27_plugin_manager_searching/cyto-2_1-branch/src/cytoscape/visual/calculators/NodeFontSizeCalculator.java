//--------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//--------------------------------------------------------------------------
package cytoscape.visual.calculators;
//--------------------------------------------------------------------------
import cytoscape.CyNetwork;
import giny.model.Node;
//--------------------------------------------------------------------------
public interface NodeFontSizeCalculator extends Calculator {
    public float calculateNodeFontSize(Node node, CyNetwork network);
}
