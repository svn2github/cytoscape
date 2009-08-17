package org.cytoscape.phylotree.visualstyle;

import cytoscape.data.CyAttributes;
import cytoscape.visual.VisualStyle;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
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


public class DepthwiseColor implements PhyloVisualStyle {
	
	public String getName()
	{
		return "phylotree_DepthwiseColor";
	}
	
	public VisualStyle createStyle(CyNetwork network)
	{
		// Add attributes defining node depth
		addDepthAttributes(network);
		
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(); 
		
		// Set the background
		globalAppCalc.setDefaultBackgroundColor(Color.BLACK);
		

		// Passthrough Mapping - set node label 
		PassThroughMapping pm = new PassThroughMapping(new String(), "Name");
		Calculator nlc = new BasicCalculator("Node Label Calculator",pm, VisualPropertyType.NODE_LABEL);
		
		nodeAppCalc.setCalculator(nlc);

		// Add node appearance specific settings
		NodeAppearance nodeApp = new NodeAppearance();
		nodeApp.set(VisualPropertyType.NODE_SHAPE, NodeShape.ELLIPSE);
		nodeApp.set(VisualPropertyType.NODE_LABEL_COLOR, Color.WHITE);
		nodeApp.set(VisualPropertyType.NODE_LABEL_POSITION, new LabelPosition(5,3,2,0,0));
		nodeApp.set(VisualPropertyType.NODE_FONT_SIZE,20);
		
		
		nodeAppCalc.setDefaultAppearance(nodeApp);
		
		// Add edge appearance specific settings
		EdgeAppearance edgeApp = new EdgeAppearance();
		edgeApp.set(VisualPropertyType.EDGE_LABEL_COLOR, Color.WHITE);
		edgeApp.set(VisualPropertyType.EDGE_FONT_SIZE,20);
		edgeApp.set(VisualPropertyType.EDGE_LINE_WIDTH,10);
		edgeAppCalc.setDefaultAppearance(edgeApp);
		
		
		nodeAppCalc.setCalculator(createNodeColorCalculator(network));
		edgeAppCalc.setCalculator(createEdgeColorCalculator(network));

		// Create the visual style 
		VisualStyle visualStyle = new VisualStyle(getName(), nodeAppCalc, edgeAppCalc, globalAppCalc);

		return visualStyle;
	}

	/**
	 * Calculates the depth of each node and assigns as a hidden attribute to the node and the edge
	 * @param network - the network to be manipulated
	 */
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
		
		//	System.out.println(node.getIdentifier());
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
	
	/**
	 * Creates the calculator that assigns node color on a continuous gradient based on node depth
	 * @param network - the network containing the nodes to be colored
	 * @return - the node color calculator
	 */
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
//		Color underColor = new Color(87,12,12);
//		Color minColor = new Color(91,33,15);
//		Color midColor = new Color(121,67,44);
//		Color maxColor = new Color(134,91,39);
//		Color overColor = new Color(151,136,53);
				
		// Dark Knight
		Color underColor = new Color(8,33,75);
		Color minColor = new Color(30,55,98);
		Color midColor = new Color(55,82,127);
		Color maxColor = new Color(105,126,160);
		Color overColor = new Color(131,149,177);
		
		
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

	/**
	 * Creates a calculator for edge color
	 * @param network - the network containing the edges
	 * @return - the calculator for edge color
	 */
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

		Color underColor = new Color(8,33,75);
		Color minColor = new Color(30,55,98);
		Color midColor = new Color(55,82,127);
		Color maxColor = new Color(105,126,160);
		Color overColor = new Color(131,149,177);
		
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
