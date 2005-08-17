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
import cytoscape.visual.LineType;

import cytoscape.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeLineTypeCalculator extends Calculator{
    
    LineType calculateEdgeLineType(Edge edge, CyNetwork network);
}

