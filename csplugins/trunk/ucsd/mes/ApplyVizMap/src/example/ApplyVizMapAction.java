
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package example;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cytoscape.util.CytoscapeAction;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class ApplyVizMapAction extends CytoscapeAction {

	public static final String vsName = "Example Visual Style";

	public ApplyVizMapAction() {
		super("Apply Test VizMap");
		setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {
		// get the network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView networkView = Cytoscape.getCurrentNetworkView();

		// get the VisualMappingManager and CalculatorCatalog
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		// check to see if a visual style with this name already exists
		VisualStyle vs = catalog.getVisualStyle(vsName);
		if (vs == null) {
			// if not, create it and add it to the catalog
			vs = createVisualStyle(network);
			catalog.addVisualStyle(vs);
		}
		
		networkView.setVisualStyle(vs.getName()); // not strictly necessary

		// actually apply the visual style
		manager.setVisualStyle(vs);
		networkView.redrawGraph(true,true);
	}

	VisualStyle createVisualStyle(CyNetwork network) {

		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		GlobalAppearanceCalculator globalAppCalc = new GlobalAppearanceCalculator(); 


		// Passthrough Mapping - set node label 
		//PassThroughMapping pm = new PassThroughMapping(new String(), "attr2");
		PassThroughMapping pm = new PassThroughMapping(new String(), "attr2asdf");
		Calculator nlc = new BasicCalculator("Example Node Label Calculator", 
		                                     pm, VisualPropertyType.NODE_LABEL);
		nodeAppCalc.setCalculator(nlc);


		// Discrete Mapping - set node shapes 
		DiscreteMapping disMapping = new DiscreteMapping(NodeShape.RECT,
		                                                 ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName("attr1", network, false);
		disMapping.putMapValue(new Integer(1), NodeShape.DIAMOND);
		disMapping.putMapValue(new Integer(2), NodeShape.ELLIPSE);
		disMapping.putMapValue(new Integer(3), NodeShape.TRIANGLE);

		Calculator shapeCalculator = new BasicCalculator("Example Node Shape Calculator",
		                                                  disMapping,
														  VisualPropertyType.NODE_SHAPE);
		nodeAppCalc.setCalculator(shapeCalculator);


		// Continuous Mapping - set node color 
		ContinuousMapping continuousMapping = new ContinuousMapping(Color.WHITE, 
                                                            ObjectMapping.NODE_MAPPING);
		continuousMapping.setControllingAttributeName("attr3", network, false);

        Interpolator numToColor = new LinearNumberToColorInterpolator();
        continuousMapping.setInterpolator(numToColor);

		Color underColor = Color.GRAY;
		Color minColor = Color.RED;
		Color midColor = Color.WHITE;
		Color maxColor = Color.GREEN;
		Color overColor = Color.BLUE;

		// Create boundary conditions                  less than,   equals,  greater than
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
		VisualStyle visualStyle = new VisualStyle(vsName, nodeAppCalc, edgeAppCalc, globalAppCalc);

		return visualStyle;
	}
}
