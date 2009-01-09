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
public interface EdgeToolTipCalculator extends Calculator {
    
    String calculateEdgeToolTip(Edge edge, Network network);
}

