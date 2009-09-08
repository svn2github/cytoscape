package org.genmapp.golayout;

import giny.view.Label;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LabelPosition;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

public class PartitionNetworkVisualStyleFactory {

	/**
	 * 
	 */
	public static final String PartitionNetwork_VS = "MolecularFunction";

	private static LabelPosition lp = new LabelPosition();
	public static DiscreteMapping disMappingLabelPosition = new DiscreteMapping(
			lp, "region_name", ObjectMapping.NODE_MAPPING);

	public static VisualStyle createVisualStyle(CyNetworkView view) {

		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		VisualStyle mfStyle = catalog.getVisualStyle(PartitionNetwork_VS);
		if (mfStyle == null) { // Create the MF visual style
			try {
				mfStyle = (VisualStyle) vmm.getVisualStyle().clone();
			} catch (CloneNotSupportedException e) {
				mfStyle = new VisualStyle(PartitionNetwork_VS);
			}
			mfStyle.setName(PartitionNetwork_VS);
			NodeAppearanceCalculator nac = new MFNodeAppearanceCalculator();
			nac.getDefaultAppearance().setNodeSizeLocked(false);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT,
					MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH,
					MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH);
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_SHAPE,
					NodeShape.RECT);
			nac.getDefaultAppearance().set(
					VisualPropertyType.NODE_BORDER_COLOR, new Color(0, 0, 0));
			nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
					new Color(255, 255, 255));

			final String attributeName = "annotation.GO MOLECULAR_FUNCTION";
			CyAttributes attribs = Cytoscape.getNodeAttributes();
			Map attrMap = CyAttributesUtils
					.getAttribute(attributeName, attribs);
			Collection values = attrMap.values();
			List uniqueValueList = new ArrayList();

			/*
			 * key will be a List attribute value, so we need to pull out
			 * individual list items
			 */
			if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
				for (Object o : values) {
					List oList = (List) o;
					for (int j = 0; j < oList.size(); j++) {
						Object jObj = oList.get(j);
						if (jObj != null) {
							if (!uniqueValueList.contains(jObj)) {
								uniqueValueList.add(jObj);
							}
						}
					}
				}
			}
			PassThroughMapping passMappingLabel = new PassThroughMapping("",
					"canonicalName");
			Calculator labelCalculator = new BasicCalculator(
					PartitionNetwork_VS, passMappingLabel,
					VisualPropertyType.NODE_LABEL);
			nac.setCalculator(labelCalculator);
			DiscreteMapping disMappingNodeW = new DiscreteMapping(
					MFNodeAppearanceCalculator.FEATURE_NODE_WIDTH,
					"register_node", ObjectMapping.NODE_MAPPING);
			disMappingNodeW.putMapValue(Boolean.TRUE, 0.1);
			Calculator widthCalculator = new BasicCalculator(
					PartitionNetwork_VS, disMappingNodeW,
					VisualPropertyType.NODE_WIDTH);
			nac.setCalculator(widthCalculator);
			DiscreteMapping disMappingNodeH = new DiscreteMapping(
					MFNodeAppearanceCalculator.FEATURE_NODE_HEIGHT,
					"register_node", ObjectMapping.NODE_MAPPING);
			disMappingNodeH.putMapValue(Boolean.TRUE, 0.1);
			Calculator heightCalculator = new BasicCalculator(
					PartitionNetwork_VS, disMappingNodeH,
					VisualPropertyType.NODE_HEIGHT);
			nac.setCalculator(heightCalculator);

			DiscreteMapping disMappingFont = new DiscreteMapping(12,
					"register_node", ObjectMapping.NODE_MAPPING);
			disMappingFont.putMapValue(Boolean.TRUE, 24);
			Calculator fontCalculator = new BasicCalculator(
					PartitionNetwork_VS, disMappingFont,
					VisualPropertyType.NODE_FONT_SIZE);
			nac.setCalculator(fontCalculator);

			/*
			 * disMappingLabelPosition is dynamically defined in
			 * CellAlgorithm.construct()
			 */
			Calculator posCalculator = new BasicCalculator(PartitionNetwork_VS,
					disMappingLabelPosition,
					VisualPropertyType.NODE_LABEL_POSITION);
			nac.setCalculator(posCalculator);

			DiscreteMapping disMappingNodeFill = new DiscreteMapping(
					Color.white, ObjectMapping.NODE_MAPPING);
			disMappingNodeFill.setControllingAttributeName(attributeName, view
					.getNetwork(), false);
			/*
			 * Create random colors
			 */
			final float increment = 1f / ((Number) uniqueValueList.size())
					.floatValue();
			float hue = 0;
			float sat = 0;
			float br = 0;
			int i = 0;
			for (Object key : uniqueValueList) {
				hue = hue + increment;
				sat = (Math.abs(((Number) Math.cos((8 * i) / (2 * Math.PI)))
						.floatValue()) * 0.7f) + 0.3f;
				br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI))
						+ (Math.PI / 2))).floatValue()) * 0.7f) + 0.3f;
				disMappingNodeFill.putMapValue(key, new Color(Color.HSBtoRGB(
						hue, sat, br)));
				i++;
			}
			Calculator colorCalculator = new BasicCalculator(
					PartitionNetwork_VS, disMappingNodeFill,
					VisualPropertyType.NODE_FILL_COLOR);

			nac.setCalculator(colorCalculator);

			mfStyle.setNodeAppearanceCalculator(nac);

			GlobalAppearanceCalculator gac = mfStyle
					.getGlobalAppearanceCalculator();
			// set edge opacity
			gac.setDefaultBackgroundColor(Color.white);
			VisualPropertyType type = VisualPropertyType.EDGE_OPACITY;
			type.setDefault(mfStyle, new Integer(150));
			type = VisualPropertyType.NODE_SHAPE;
			type.setDefault(mfStyle, NodeShape.ELLIPSE);
			mfStyle.setGlobalAppearanceCalculator(gac);

			catalog.addVisualStyle(mfStyle);
		}
		CyNetworkView myView = Cytoscape.getCurrentNetworkView();
		vmm.setNetworkView(myView);
		vmm.setVisualStyle(mfStyle);
		myView.setVisualStyle(PartitionNetwork_VS);

		Cytoscape.getVisualMappingManager().setNetworkView(myView);
		Cytoscape.getVisualMappingManager().applyAppearances();

		return mfStyle;
	}

}
