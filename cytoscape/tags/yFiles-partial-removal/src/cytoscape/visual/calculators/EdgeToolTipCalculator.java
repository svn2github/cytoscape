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

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeToolTipCalculator extends Calculator {
    
    String calculateEdgeToolTip(Edge edge, CyNetwork network);
}

