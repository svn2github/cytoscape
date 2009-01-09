//----------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------
package cytoscape.visual.calculators;
//----------------------------------------------------------------------------
import java.util.Map;
import javax.swing.JPanel;

import y.base.Node;

import cytoscape.visual.Network;
//----------------------------------------------------------------------------
public interface NodeToolTipCalculator extends Calculator {
    
    String calculateNodeToolTip(Node node, Network network);
}

