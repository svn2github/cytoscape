//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import giny.model.Node;

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface NodeShapeCalculator extends Calculator {
    
    byte calculateNodeShape(Node node, CyNetwork network);
}

