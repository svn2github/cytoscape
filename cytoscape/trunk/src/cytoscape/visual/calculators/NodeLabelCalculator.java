//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import y.base.Node;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface NodeLabelCalculator extends Calculator {
    
    String calculateNodeLabel(Node node, Network network);
}

