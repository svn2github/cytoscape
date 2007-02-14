package cytoscape.visual.calculators;

import static cytoscape.visual.VisualPropertyType.EDGE_TGTARROW_COLOR;
import giny.model.Edge;

import java.util.Properties;

import cytoscape.CyNetwork;
import cytoscape.visual.Arrow;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.ArrowParser;

public class GenericEdgeTargetArrowColorCalculator extends EdgeCalculator {

	public GenericEdgeTargetArrowColorCalculator(String name, ObjectMapping m) {
		super(name, m, Arrow.class, EDGE_TGTARROW_COLOR);
	}

	public GenericEdgeTargetArrowColorCalculator(String name, Properties props,
			String baseKey) {
		super(name, props, baseKey, new ArrowParser(), Arrow.NONE,
				EDGE_TGTARROW_COLOR);
	}

	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
		Arrow a = (Arrow) getRangeValue(edge);

		// default has already been set - no need to do anything
		if (a == null)
			return;

		appr.setTargetArrow(a);
	}

	public Arrow calculateEdgeArrow(Edge e, CyNetwork n) {
		final EdgeAppearance ea = new EdgeAppearance();
		apply(ea, e, n);

		return ea.getTargetArrow();
	}
}
