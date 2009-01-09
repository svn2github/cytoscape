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

import y.base.Node;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface NodeColorCalculator extends Calculator {
    
    Color calculateNodeColor(Node node, Network network);
}

