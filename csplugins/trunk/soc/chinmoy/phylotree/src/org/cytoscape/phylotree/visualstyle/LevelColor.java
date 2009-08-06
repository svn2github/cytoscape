package org.cytoscape.phylotree.visualstyle;

import cytoscape.data.CyAttributes;
import cytoscape.visual.VisualStyle;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.visual.Appearance;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.NodeAppearance;
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
import giny.model.Edge;
import giny.model.Node;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import org.cytoscape.phylotree.layout.CommonFunctions;


public class LevelColor implements PhyloVisualStyle {
	
	public String getName()
	{
		return "LevelColor";
	}
	
	public VisualStyle createStyle(CyNetwork network)
	{
		
		// Add attributes defining node level
		addDepthAttributes(network);
		
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(); 
		
		// Set the background
		globalAppCalc.setDefaultBackgroundColor(Color.BLACK);
	//	globalAppCalc.setDefaultBackgroundColor(Color.WHITE);
		

		// Passthrough Mapping - set node label 
		PassThroughMapping pm = new PassThroughMapping(new String(), "ID");
		Calculator nlc = new BasicCalculator("Node Label Calculator",pm, VisualPropertyType.NODE_LABEL);
		
		nodeAppCalc.setCalculator(nlc);

		// Set node label color
//		DiscreteMapping disColorMapping = new DiscreteMapping(Color.WHITE, ObjectMapping.NODE_MAPPING);
//		disColorMapping.setControllingAttributeName("Level", network, false);
//		Calculator labelColorCalculator = new BasicCalculator("Node Label Color Calculator", disColorMapping, VisualPropertyType.NODE_LABEL_COLOR);
//		nodeAppCalc.setCalculator(labelColorCalculator);

		
		NodeAppearance nodeApp = new NodeAppearance();
		nodeApp.set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
		nodeApp.set(VisualPropertyType.NODE_LABEL_COLOR, Color.WHITE);
		nodeApp.set(VisualPropertyType.NODE_LABEL_POSITION, new LabelPosition(5,3,2,0,0));
		nodeApp.set(VisualPropertyType.NODE_FONT_SIZE,20);
		
		
		nodeAppCalc.setDefaultAppearance(nodeApp);
		
		
		EdgeAppearance edgeApp = new EdgeAppearance();
		edgeApp.set(VisualPropertyType.EDGE_LABEL_COLOR, Color.WHITE);
		edgeApp.set(VisualPropertyType.EDGE_FONT_SIZE,20);
		edgeApp.set(VisualPropertyType.EDGE_LINE_WIDTH,10);
		edgeAppCalc.setDefaultAppearance(edgeApp);
		
		
		// Discrete Mapping - set node shapes 
//		DiscreteMapping disMapping = new DiscreteMapping(NodeShape.ELLIPSE, ObjectMapping.NODE_MAPPING);
//		disMapping.setControllingAttributeName("Level", network, false);
//		disMapping.putMapValue(new Integer(0), NodeShape.ELLIPSE);
//		disMapping.putMapValue(new Integer(1), NodeShape.ELLIPSE);
//		disMapping.putMapValue(new Integer(2), NodeShape.ELLIPSE);

//		Calculator shapeCalculator = new BasicCalculator("Node Shape Calculator",
	//	                                                  disMapping,VisualPropertyType.NODE_SHAPE);
		//nodeAppCalc.setCalculator(shapeCalculator);


//		// Continuous Mapping - set node color 
//		ContinuousMapping continuousMapping = new ContinuousMapping(Color.WHITE, 
//                                                            ObjectMapping.NODE_MAPPING);
//		continuousMapping.setControllingAttributeName("ID", network, false);
//
//        Interpolator numToColor = new LinearNumberToColorInterpolator();
//        continuousMapping.setInterpolator(numToColor);
//
//		Color underColor = Color.GRAY;
//		Color minColor = Color.RED;
//		Color midColor = Color.WHITE;
//		Color maxColor = Color.GREEN;
//		Color overColor = Color.BLUE;
//
//		// Create boundary conditions                     less than,   equals,  greater than
//		BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
//		BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
//		BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);
//
//        // Set the attribute point values associated with the boundary values 
//		continuousMapping.addPoint(0.0, bv0);
//		continuousMapping.addPoint(1.0, bv1);
//		continuousMapping.addPoint(2.0, bv2);
//		
//		Calculator nodeColorCalculator = new BasicCalculator("Example Node Color Calc", 
//		                                                continuousMapping, 
//													 VisualPropertyType.NODE_FILL_COLOR);
		nodeAppCalc.setCalculator(createNodeColorCalculator(network));
		

		// Discrete Mapping - Set edge target arrow shape	
//		DiscreteMapping arrowMapping = new DiscreteMapping(ArrowShape.NONE,
//		                                                   ObjectMapping.EDGE_MAPPING);
//		arrowMapping.setControllingAttributeName("interaction", network, false);
//		arrowMapping.putMapValue("pp", ArrowShape.ARROW);
//		arrowMapping.putMapValue("pd", ArrowShape.CIRCLE);
//
//		Calculator edgeArrowCalculator = new BasicCalculator("Example Edge Arrow Shape Calculator",
//                                              arrowMapping, VisualPropertyType.EDGE_TGTARROW_SHAPE);
//		edgeAppCalc.setCalculator(edgeArrowCalculator);
		edgeAppCalc.setCalculator(createEdgeColorCalculator(network));

		// Create the visual style 
		VisualStyle visualStyle = new VisualStyle("LevelColor", nodeAppCalc, edgeAppCalc, globalAppCalc);

		return visualStyle;
	}

	private void addDepthAttributes(CyNetwork network)
	{
		// Get all nodes
		List<Node> nodeList = network.nodesList();
		Iterator<Node> nodeListIterator = nodeList.iterator();
		
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CommonFunctions cf = new CommonFunctions();
		
		// For each node, find level and set the level
		while(nodeListIterator.hasNext())
		{
			Node node = nodeListIterator.next();
			
			int depth = cf.getDepth(network, node);
			
			nodeAttributes.setAttribute(node.getIdentifier(), "Depth", depth);
			
			int [] edgeIndicesArray =network.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(), false, false, true); 
			for(int i = 0; i<edgeIndicesArray.length; i++)
			{
				Edge edge = network.getEdge(edgeIndicesArray[i]);
				edgeAttributes.setAttribute(edge.getIdentifier(),"Depth", depth);
				
			}
		}
		
	}
	
	private Calculator createNodeColorCalculator(CyNetwork network) {
		// Determine the min and max for degree
		CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();

		int min = 0;
		int max =0;

		Iterator<Node> it = network.nodesIterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			Integer value = cyNodeAttrs.getIntegerAttribute(node.getIdentifier(), "Depth");
			if (value.intValue() < min) {
				min = value.intValue();
			}
			else if (value.intValue() > max) {
				max = value.intValue();
			}
		}
		
		// pick 3 points within (min~max)
		double p1 = min + (max-min)/3.0;
		double p2 = p1 + (max-min)/3.0;
		double p3 = p2 + (max-min)/3.0;
		
		// Create a calculator for "Degree" attribute
		VisualPropertyType type = VisualPropertyType.NODE_FILL_COLOR;
		final Object defaultObj = type.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
		
		ContinuousMapping cm = new ContinuousMapping(defaultObj, ObjectMapping.NODE_MAPPING);
		// Set controlling Attribute
		cm.setControllingAttributeName("Depth", Cytoscape.getCurrentNetwork(), false);

		Interpolator numToColor = new LinearNumberToColorInterpolator();
		cm.setInterpolator(numToColor);
		
		// Power of the Sun
		Color underColor = new Color(87,12,12);
		Color minColor = new Color(91,33,15);
		Color midColor = new Color(121,67,44);
		Color maxColor = new Color(134,91,39);
		Color overColor = new Color(151,136,53);
				
		// Dark Knight
//		Color underColor = new Color(8,33,75);
//		Color minColor = new Color(30,55,98);
//		Color midColor = new Color(55,82,127);
//		Color maxColor = new Color(105,126,160);
//		Color overColor = new Color(131,149,177);
		
		
		// Froggy electric
//		Color underColor = new Color(43,166,189);
//		Color minColor = new Color(43,166,156);
//		Color midColor = new Color(0,236,141);
//		Color maxColor = new Color(0,236,120);
//		Color overColor = new Color(140,201,77);
		
		
		BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
		BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
		BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);
		
		// Set the attribute point values associated with the boundary values
		cm.addPoint(p1, bv0);
		cm.addPoint(p2, bv1);
		cm.addPoint(p3, bv2);
		
		// Create a calculator
		return new BasicCalculator("Node Color calcualtor", cm, VisualPropertyType.NODE_FILL_COLOR);			
	}

	private Calculator createEdgeColorCalculator(CyNetwork network) {
		// Determine the min and max for degree
		CyAttributes cyEdgeAttrs = Cytoscape.getEdgeAttributes();

		int min = 0;
		int max =0;

		Iterator<Edge> it = network.edgesIterator();
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			Integer value = cyEdgeAttrs.getIntegerAttribute(edge.getIdentifier(), "Depth");
			if (value.intValue() < min) {
				min = value.intValue();
			}
			else if (value.intValue() > max) {
				max = value.intValue();
			}
		}
		
		// pick 3 points within (min~max)
		double p1 = min + (max-min)/3.0;
		double p2 = p1 + (max-min)/3.0;
		double p3 = p2 + (max-min)/3.0;
		
		// Create a calculator for "Degree" attribute
		VisualPropertyType type = VisualPropertyType.EDGE_COLOR;
		final Object defaultObj = type.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
		
		ContinuousMapping cm = new ContinuousMapping(defaultObj, ObjectMapping.EDGE_MAPPING);
		// Set controlling Attribute
		cm.setControllingAttributeName("Depth", Cytoscape.getCurrentNetwork(), false);

		Interpolator numToColor = new LinearNumberToColorInterpolator();
		cm.setInterpolator(numToColor);

		Color underColor = new Color(87,12,12);
		Color minColor = new Color(91,33,15);
		Color midColor = new Color(121,67,44);
		Color maxColor = new Color(134,91,39);
		Color overColor = new Color(151,136,53);
		
		BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
		BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
		BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);
		
		// Set the attribute point values associated with the boundary values
		cm.addPoint(p1, bv0);
		cm.addPoint(p2, bv1);
		cm.addPoint(p3, bv2);
		
		// Create a calculator
		return new BasicCalculator("Edge Color calcualtor", cm, VisualPropertyType.EDGE_COLOR);			
	}
}
