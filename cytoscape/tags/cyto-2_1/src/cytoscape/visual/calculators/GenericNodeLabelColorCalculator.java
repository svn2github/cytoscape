package cytoscape.visual.calculators;

import cytoscape.CyNetwork;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ColorParser;
import giny.model.Node;
import java.awt.Color;
import java.util.Map;
import java.util.Properties;

public class GenericNodeLabelColorCalculator
  extends NodeCalculator
  implements NodeLabelColorCalculator
{

  public GenericNodeLabelColorCalculator(String name, ObjectMapping m)
  {
    super(name, m);
    Class c = Color.class;
    if (!c.isAssignableFrom(m.getRangeClass()))
    {
      String s = "Invalid Calculator: Expected class " +
        c.toString() + ", got " + m.getRangeClass().toString();
      throw new ClassCastException(s);
    }
  }

  /**
   * Constructor for dynamic creation via properties.
   */
  public GenericNodeLabelColorCalculator(String name,
                                         Properties props,
                                         String baseKey)
  {
    super(name, props, baseKey, new ColorParser(), Color.black);
  }
 
  public Color calculateNodeLabelColor(Node node, CyNetwork network)
  {
    String canonicalName = network.getNodeAttributes().getCanonicalName(node);
    Map attrBundle = network.getNodeAttributes().getAttributes(canonicalName);
    return (Color) super.getMapping().calculateRangeValue(attrBundle);
  }

}
