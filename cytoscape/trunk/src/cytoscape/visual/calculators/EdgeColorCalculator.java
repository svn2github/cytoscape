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

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface EdgeColorCalculator extends Calculator{
    
    public Color calculateEdgeColor(Edge edge, Network network);
}

