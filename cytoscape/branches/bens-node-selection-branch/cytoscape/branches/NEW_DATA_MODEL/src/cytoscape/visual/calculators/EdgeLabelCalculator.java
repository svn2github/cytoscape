//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import giny.model.Edge;

import cytoscape.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeLabelCalculator extends Calculator {
    
    public String calculateEdgeLabel(Edge edge, CyNetwork network);
}

