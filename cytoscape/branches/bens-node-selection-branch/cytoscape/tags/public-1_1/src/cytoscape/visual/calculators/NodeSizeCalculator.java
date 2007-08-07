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
public interface NodeSizeCalculator extends Calculator {
    
    double calculateNodeSize(Node node, Network network);
}

