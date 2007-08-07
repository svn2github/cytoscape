//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.calculators;
//------------------------------------------------------------------------------
import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import cytoscape.dialogs.MiscGB;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.MappingFactory;
import cytoscape.CyNetwork;
import cytoscape.visual.parsers.ValueParser;
//------------------------------------------------------------------------------
/**
 * EdgeCalculator implements some UI features for calculators lower in the
 * object tree.
 */
public abstract class EdgeCalculator extends AbstractCalculator {

    public EdgeCalculator(String name, ObjectMapping m) {
	super(name, m);
    }
    /**
     * Constructor that calls {@link MappingFactory} to construct a new
     * ObjectMapping based on the supplied arguments.
     */
    public EdgeCalculator(String name, Properties props, String baseKey,
                          ValueParser parser, Object defObj) {
        super(name, MappingFactory.newMapping(props, baseKey + ".mapping", parser,
                                              defObj, ObjectMapping.EDGE_MAPPING) );
    }

    /**
     * Get the UI for edge calculators. Display a JPanel with a JPanel from
     * AbstractCalculator {@link AbstractCalculator#getUI} and the underlying
     * mapper's UI JPanel in a FlowLayout.
     *
     * @param	parent	Parent dialog for the child UI
     * @param	n	CyNetwork representing the graph
     *
     * @return	JPanel containing JComboBox
     */
    public JPanel getUI(JDialog parent, CyNetwork n) {
	return super.getUI(n.getEdgeAttributes(), parent, n);
	/*
	// attribute select combo box - delivered complete from the superclass
	JPanel selectAttributePanel = super.getUI(n.getEdgeAttributes());
	
	// underlying mapper's UI
	JPanel mapperUI = super.getMapping().getUI(parent, n);

	// stick them together
	GridBagGroup g = new GridBagGroup();
	MiscGB.insert(g, selectAttributePanel, 0, 0);
	MiscGB.insert(g, mapperUI, 0, 1, 1, 1, 5, 5, GridBagConstraints.BOTH);
	return g.panel;
	*/
    }
}
