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
import cytoscape.visual.Arrow;

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeArrowCalculator extends Calculator {
    
    public Arrow calculateEdgeArrow(Edge edge, CyNetwork network);
}

