//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import y.base.Edge;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface EdgeLabelCalculator extends Calculator {
    
    public String calculateEdgeLabel(Edge edge, Network network);
}

