package org.cytoscape.phylotree.visualstyle;

import cytoscape.visual.VisualStyle;

import cytoscape.CyNetwork;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import java.awt.Color;


public class LevelColor implements PhyloVisualStyle {
	
	public String getName()
	{
		return "LevelColor";
	}
	
	public VisualStyle createStyle(CyNetwork network)
	{
		
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(); 


		// Passthrough Mapping - set node label 
		// PassThroughMapping pm = new PassThroughMapping(new String(), "attr2");
		PassThroughMapping pm = new PassThroughMapping(new String(), "ID");
		Calculator nlc = new BasicCalculator("Example Node Label Calculator",pm, VisualPropertyType.NODE_LABEL);
		
		nodeAppCalc.setCalculator(nlc);


		// Discrete Mapping - set node shapes 
		DiscreteMapping disMapping = new DiscreteMapping(NodeShape.RECT,
		                                                 ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName("ID", network, false);
		disMapping.putMapValue(new Integer(1), NodeShape.DIAMOND);
		disMapping.putMapValue(new Integer(2), NodeShape.ELLIPSE);
		disMapping.putMapValue(new Integer(3), NodeShape.TRIANGLE);

		Calculator shapeCalculator = new BasicCalculator("Example Node Shape Calculator",
		                                                  disMapping,VisualPropertyType.NODE_SHAPE);
		nodeAppCalc.setCalculator(shapeCalculator);


		// Continuous Mapping - set node color 
		ContinuousMapping continuousMapping = new ContinuousMapping(Color.WHITE, 
                                                            ObjectMapping.NODE_MAPPING);
		continuousMapping.setControllingAttributeName("ID", network, false);

        Interpolator numToColor = new LinearNumberToColorInterpolator();
        continuousMapping.setInterpolator(numToColor);

		Color underColor = Color.GRAY;
		Color minColor = Color.RED;
		Color midColor = Color.WHITE;
		Color maxColor = Color.GREEN;
		Color overColor = Color.BLUE;

		// Create boundary conditions                     less than,   equals,  greater than
		BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
		BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
		BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);

        // Set the attribute point values associated with the boundary values 
		continuousMapping.addPoint(0.0, bv0);
		continuousMapping.addPoint(1.0, bv1);
		continuousMapping.addPoint(2.0, bv2);
		
		Calculator nodeColorCalculator = new BasicCalculator("Example Node Color Calc", 
		                                                continuousMapping, 
													 VisualPropertyType.NODE_FILL_COLOR);
		nodeAppCalc.setCalculator(nodeColorCalculator);


		// Discrete Mapping - Set edge target arrow shape	
		DiscreteMapping arrowMapping = new DiscreteMapping(ArrowShape.NONE,
		                                                   ObjectMapping.EDGE_MAPPING);
		arrowMapping.setControllingAttributeName("interaction", network, false);
		arrowMapping.putMapValue("pp", ArrowShape.ARROW);
		arrowMapping.putMapValue("pd", ArrowShape.CIRCLE);

		Calculator edgeArrowCalculator = new BasicCalculator("Example Edge Arrow Shape Calculator",
                                              arrowMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
		edgeAppCalc.setCalculator(edgeArrowCalculator);


		// Create the visual style 
		VisualStyle visualStyle = new VisualStyle("LevelColor", nodeAppCalc, edgeAppCalc, globalAppCalc);

		return visualStyle;
	}

	

}
