//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import java.awt.Color;
import javax.swing.JPanel;

import y.base.Edge;

import cytoscape.data.CyNetwork;
//----------------------------------------------------------------------------
public interface EdgeColorCalculator extends Calculator{
    
    public Color calculateEdgeColor(Edge edge, CyNetwork network);
}

