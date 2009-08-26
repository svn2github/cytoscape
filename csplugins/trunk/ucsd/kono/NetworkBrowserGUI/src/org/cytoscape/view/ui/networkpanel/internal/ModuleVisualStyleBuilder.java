package org.cytoscape.view.ui.networkpanel.internal;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import static cytoscape.visual.VisualPropertyType.NODE_LABEL;
import static org.cytoscape.view.ui.networkpanel.MetaNetworkGenerator.*;


/**
 * Build visual styles for Module Overview and others.
 * 
 * @author kono
 *
 */
public class ModuleVisualStyleBuilder {

	private static final String STYLE_NAME = "Module Overview";

	private VisualStyle moduleStyle = null;

	public VisualStyle getMosuleVisualStyle() {
		if (moduleStyle == null) {
			moduleStyle = new VisualStyle(STYLE_NAME);

			// Set globals
			GlobalAppearanceCalculator gac = moduleStyle
					.getGlobalAppearanceCalculator();
			gac.setDefaultBackgroundColor(new Color(230, 230, 230, 200));

			PassThroughMapping labelMapping = new PassThroughMapping("",
					AbstractCalculator.ID);
			NodeCalculator calc = new NodeCalculator(STYLE_NAME + "-"
					+ "NodeLabelMapping", labelMapping, null, NODE_LABEL);

			NodeAppearanceCalculator nac = moduleStyle
					.getNodeAppearanceCalculator();
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
					Color.DARK_GRAY);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
					NodeShape.ROUND_RECT);
			nac.getDefaultAppearance()
					.set(VisualPropertyType.NODE_OPACITY, 100);
			nac.getDefaultAppearance().set(
					VisualPropertyType.NODE_BORDER_OPACITY, 150);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_LINE_WIDTH,
					2);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, 210);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, 45);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_LABEL_COLOR,
					new Color(50, 50, 50, 200));
			nac.setNodeSizeLocked(false);

			DiscreteMapping nodeShape = new DiscreteMapping(
					NodeShape.ROUND_RECT, NODE_TYPE, ObjectMapping.NODE_MAPPING);

			nodeShape.putMapValue(NETWORK_NODE, NodeShape.DIAMOND);
			nodeShape.putMapValue(MODULE_NODE, NodeShape.RECT);
			
			NodeCalculator nodeShapeCalc = new NodeCalculator(STYLE_NAME + "-"
                    + "NodeShapeMapping",
                    nodeShape, null,
                    VisualPropertyType.NODE_SHAPE);
			nac.setCalculator(nodeShapeCalc);
			
			
			DiscreteMapping nodeColor = new DiscreteMapping(
					Color.blue, NODE_TYPE, ObjectMapping.NODE_MAPPING);

			nodeColor.putMapValue(NETWORK_NODE, Color.green);
			nodeColor.putMapValue(MODULE_NODE, Color.gray);
			
			NodeCalculator nodeColorCalc = new NodeCalculator(STYLE_NAME + "-"
                    + "NodeColorMapping",
                    nodeColor, null,
                    VisualPropertyType.NODE_FILL_COLOR);
			nac.setCalculator(nodeColorCalc);
			
			// Border Color Mapping
			DiscreteMapping nodeBorderColor = new DiscreteMapping(
					Color.blue, EXEC_COUNTER, ObjectMapping.NODE_MAPPING);

			try {
			int r = 80;
			int g = 50; 
			int b = 50;
			Integer run = 1;
			while(true) {
				if(run%3 == 0) {
					r+=30;
				} else if(run%2 == 0) {
					g+=30;
				} else {
					b+=30;
				}
				nodeBorderColor.putMapValue(run, new Color(r, g, b));
				run++;
				if(run >10)
					break;
					
			}
			
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			NodeCalculator nodeBorderColorCalc = new NodeCalculator(STYLE_NAME + "-"
                    + "NodeBorderColorMapping",
                    nodeBorderColor, null,
                    VisualPropertyType.NODE_BORDER_COLOR);
			nac.setCalculator(nodeBorderColorCalc);
			
			

			moduleStyle.getNodeAppearanceCalculator().setCalculator(calc);
			Cytoscape.getVisualMappingManager().getCalculatorCatalog()
					.addVisualStyle(moduleStyle);
		}

		return moduleStyle;
	}
}
