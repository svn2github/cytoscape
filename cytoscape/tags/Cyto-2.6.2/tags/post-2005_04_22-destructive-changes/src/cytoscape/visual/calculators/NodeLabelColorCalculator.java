package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import giny.model.Node;
import java.awt.Color;

public interface NodeLabelColorCalculator extends Calculator
{

  Color calculateNodeLabelColor(Node node, CyNetwork network);

}
