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
import y.view.Arrow;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface EdgeArrowCalculator extends Calculator {
    
    public Arrow calculateEdgeArrow(Edge edge, Network network);
}

