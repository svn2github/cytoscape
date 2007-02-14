package cytoscape.visual.calculators;

//import static cytoscape.visual.VisualPropertyType.EDGE_LINE_WIDTH;
import giny.model.Edge;

import java.util.Properties;

import cytoscape.CyNetwork;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.Line;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.parsers.LineTypeParser;

public class GenericEdgeLineWidthCalculator  {

//	public GenericEdgeLineWidthCalculator(String name, ObjectMapping m, Class c) {
//		super(name, m, c, EDGE_LINE_WIDTH);
//	}
//
//	public GenericEdgeLineWidthCalculator(String name, Properties props,
//			String baseKey) {
//		super(name, props, baseKey, new LineTypeParser(), new Line(LineTypeDef.SOLID, 1.0f),
//				EDGE_LINE_WIDTH);
//	}
//	
//	public void apply(EdgeAppearance appr, Edge edge, CyNetwork network) {
//		final Line line = (Line) getRangeValue(edge);
//
//		// default has already been set - no need to do anything
//		if (line == null)
//			return;
//
//		appr.setLine(line);
//	}
//
//	public Line calculateEdgeLineWidth(Edge e, CyNetwork n) {
//		final EdgeAppearance ea = new EdgeAppearance();
//		apply(ea, e, n);
//		return ea.getLine();
//	}
}
