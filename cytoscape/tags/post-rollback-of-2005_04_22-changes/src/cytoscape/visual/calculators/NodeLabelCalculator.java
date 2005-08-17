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

import cytoscape.CyNetwork;
//----------------------------------------------------------------------------
public interface NodeLabelCalculator extends Calculator {
    
    String calculateNodeLabel(Node node, CyNetwork network);
}

