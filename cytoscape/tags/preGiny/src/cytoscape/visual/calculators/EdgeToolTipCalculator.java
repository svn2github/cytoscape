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

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeToolTipCalculator extends Calculator {
    
    String calculateEdgeToolTip(Edge edge, CyNetwork network);
}

