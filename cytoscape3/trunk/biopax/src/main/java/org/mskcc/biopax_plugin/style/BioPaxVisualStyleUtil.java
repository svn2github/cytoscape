// $Id: BioPaxVisualStyleUtil.java,v 1.17 2006/08/23 15:21:07 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.style;

import cytoscape.Cytoscape;
import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.EdgeAppearanceCalculator;
import org.cytoscape.vizmap.NodeAppearanceCalculator;
import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.calculators.BasicCalculator;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassThroughMapping;
import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.plugin.BioPaxPlugIn;
import org.mskcc.biopax_plugin.util.biopax.BioPaxConstants;
import org.mskcc.biopax_plugin.util.biopax.BioPaxPlainEnglish;
import org.mskcc.biopax_plugin.util.biopax.ControlTypeConstants;

import java.awt.*;
import java.util.Iterator;
import java.util.Set;


/**
 * Creates an "out-of-the-box" default Visual Mapper for rendering BioPAX
 * networks.
 *
 * @author Ethan Cerami.
 */
public class BioPaxVisualStyleUtil {
	/**
	 * Verion Number String.
	 */
	public static final String VERSION_POST_FIX = " v " + BioPaxPlugIn.VERSION_MAJOR_NUM + "_"
	                                              + BioPaxPlugIn.VERSION_MINOR_NUM;

	/**
	 * Name of BioPax Visual Style.
	 */
	public static final String BIO_PAX_VISUAL_STYLE = "BioPAX" + VERSION_POST_FIX;

	/**
	 * Node Label Attribute.
	 */
	public static final String BIOPAX_NODE_LABEL = "biopax.node_label";

	/**
	 * size of physical entity node (default node size width)
	 */
	public static final double BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH = 20;

	// taken from DNodeView

	/**
	 * size of physical entity node (default node size height)
	 */
	public static final double BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT = 20;

	// taken from DNodeView

	/**
	 * size of physical entity node scale - (used to scale post tranlational modification nodes)
	 */
	public static final double BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE = 3;

	/**
	 * Size of interaction node
	 */
	private static final double BIO_PAX_VISUAL_STYLE_INTERACTION_NODE_SIZE_SCALE = 0.33;

	/**
	 * Size of complex node
	 */
	private static final double BIO_PAX_VISUAL_STYLE_COMPLEX_NODE_SIZE_SCALE = 0.33;

	/**
	 * Default color of nodes
	 */
	private static final Color DEFAULT_NODE_COLOR = new Color(255, 255, 255);

	/**
	 * Node border color
	 */
	private static final Color DEFAULT_NODE_BORDER_COLOR = new Color(0, 102, 102);

	/**
	 * Complex node color
	 */
	private static final Color COMPLEX_NODE_COLOR = new Color(0, 0, 0);

	/**
	 * Complex node color
	 */
	private static final Color COMPLEX_NODE_BORDER_COLOR = COMPLEX_NODE_COLOR;

	/**
	 * Constructor.
	 * If an existing BioPAX Viz Mapper already exists, we use it.
	 * Otherwise, we create a new one.
	 *
	 * @return VisualStyle Object.
	 */
	public static VisualStyle getBioPaxVisualStyle() {
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		VisualStyle bioPaxVisualStyle = catalog.getVisualStyle(BIO_PAX_VISUAL_STYLE);

		//  If the BioPAX Visual Style already exists, use this one instead.
		//  The user may have tweaked the out-of-the box mapping, and we don't
		//  want to over-ride these tweaks.
		if (bioPaxVisualStyle == null) {
			bioPaxVisualStyle = new VisualStyle(BIO_PAX_VISUAL_STYLE);

			NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
			nac.setNodeSizeLocked(false);

			EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();

			createNodeShape(nac);
			createNodeSize(nac);
			createNodeLabel(nac);
			createNodeColor(nac);
			createNodeBorderColor(nac);
			createTargetArrows(eac);

			bioPaxVisualStyle.setNodeAppearanceCalculator(nac);
			bioPaxVisualStyle.setEdgeAppearanceCalculator(eac);

			//  The visual style must be added to the Global Catalog
			//  in order for it to be written out to vizmap.props upon user exit
			catalog.addVisualStyle(bioPaxVisualStyle);
		}

		return bioPaxVisualStyle;
	}

	private static void createNodeShape(NodeAppearanceCalculator nac) {
		//  create a discrete mapper, for mapping a biopax type to a shape
		DiscreteMapping discreteMapping = new DiscreteMapping(NodeShape.RECT,
		                                                      MapNodeAttributes.BIOPAX_ENTITY_TYPE,
		                                                      ObjectMapping.NODE_MAPPING);

		//  map all physical entities to circles
		BioPaxConstants bpConstants = new BioPaxConstants();
		Set physicalEntitySet = bpConstants.getPhysicalEntitySet();
		Iterator iterator = physicalEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();
			discreteMapping.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
			                            NodeShape.ELLIPSE);
		}

		// hack for phosphorylated proteins
		discreteMapping.putMapValue(BioPaxConstants.PROTEIN_PHOSPHORYLATED, NodeShape.ELLIPSE);

		// map all interactions
		// - control to triangles
		// - others to square
		bpConstants = new BioPaxConstants();

		Set interactionEntitySet = bpConstants.getInteractionSet();
		iterator = interactionEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();

			if (bpConstants.isControlInteraction(entityName)) {
				discreteMapping.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                            NodeShape.TRIANGLE);
			} else {
				discreteMapping.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                            NodeShape.RECT);
			}
		}

		// create and set node shape calculator in node appearance calculator
		Calculator nodeShapeCalculator = new BasicCalculator("BioPAX Node Shape" + VERSION_POST_FIX,
		                                                     discreteMapping,
		                                                     VisualPropertyType.NODE_SHAPE);
		nac.setCalculator(nodeShapeCalculator);
	}

	private static void createNodeSize(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node size.
		DiscreteMapping discreteMappingWidth = new DiscreteMapping(new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH),
		                                                           MapNodeAttributes.BIOPAX_ENTITY_TYPE,
		                                                           ObjectMapping.NODE_MAPPING);
		DiscreteMapping discreteMappingHeight = new DiscreteMapping(new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT),
		                                                            MapNodeAttributes.BIOPAX_ENTITY_TYPE,
		                                                            ObjectMapping.NODE_MAPPING);

		//  map all interactions to required size
		BioPaxConstants bpConstants = new BioPaxConstants();
		Set interactionEntitySet = bpConstants.getInteractionSet();
		Iterator iterator = interactionEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();
			discreteMappingWidth.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
			                                 new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BIO_PAX_VISUAL_STYLE_INTERACTION_NODE_SIZE_SCALE));
			discreteMappingHeight.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
			                                  new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BIO_PAX_VISUAL_STYLE_INTERACTION_NODE_SIZE_SCALE));
		}

		//  map all complex to required size
		Set physicalEntitySet = bpConstants.getPhysicalEntitySet();
		iterator = physicalEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();

			if (entityName.equals(BioPaxConstants.COMPLEX)) {
				discreteMappingWidth.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                                 new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BIO_PAX_VISUAL_STYLE_COMPLEX_NODE_SIZE_SCALE));
				discreteMappingHeight.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                                  new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BIO_PAX_VISUAL_STYLE_COMPLEX_NODE_SIZE_SCALE));
			}
		}

		// hack for phosphorylated proteins - make them large so label fits within node
		// commented out by Ethan Cerami, November 15, 2006
		//        discreteMappingWidth.putMapValue(BioPaxConstants.PROTEIN_PHOSPHORYLATED,
		//                new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH
		//                        * BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE));
		//        discreteMappingHeight.putMapValue(BioPaxConstants.PROTEIN_PHOSPHORYLATED,
		//                new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT
		//                        * BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE));

		// create and set node height calculator in node appearance calculator
		Calculator nodeWidthCalculator = new BasicCalculator("BioPAX Node Width" + VERSION_POST_FIX,
		                                                     discreteMappingWidth,
		                                                     VisualPropertyType.NODE_WIDTH);
		nac.setCalculator(nodeWidthCalculator);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH,
									   new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH));

		Calculator nodeHeightCalculator = new BasicCalculator("BioPAX Node Height"
		                                                      + VERSION_POST_FIX,
		                                                      discreteMappingHeight,
		                                                      VisualPropertyType.NODE_HEIGHT);
		nac.setCalculator(nodeHeightCalculator);
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT,
									   new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT));
	}

	private static void createNodeLabel(NodeAppearanceCalculator nac) {
		// create pass through mapper for node labels
		PassThroughMapping passThroughMapping = new PassThroughMapping("",
		                                                               ObjectMapping.NODE_MAPPING);
		passThroughMapping.setControllingAttributeName(BIOPAX_NODE_LABEL, null, false);

		// create and set node label calculator in node appearance calculator
		Calculator nodeLabelCalculator = new BasicCalculator("BioPAX Node Label" + VERSION_POST_FIX,
		                                                     passThroughMapping,
		                                                     VisualPropertyType.NODE_LABEL);
		nac.setCalculator(nodeLabelCalculator);
	}

	private static void createNodeColor(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping discreteMapping = new DiscreteMapping(DEFAULT_NODE_COLOR,
		                                                      MapNodeAttributes.BIOPAX_ENTITY_TYPE,
		                                                      ObjectMapping.NODE_MAPPING);

		//  map all complex to black
		BioPaxConstants bpConstants = new BioPaxConstants();
		Set physicalEntitySet = bpConstants.getPhysicalEntitySet();
		Iterator iterator = physicalEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();

			if (entityName.equals(BioPaxConstants.COMPLEX)) {
				discreteMapping.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                            COMPLEX_NODE_COLOR);
			}
		}

		// create and set node label calculator in node appearance calculator
		Calculator nodeColorCalculator = new BasicCalculator("BioPAX Node Color" + VERSION_POST_FIX,
		                                                     discreteMapping,
		                                                     VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(nodeColorCalculator);

		// set default color
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,
									   DEFAULT_NODE_COLOR);
	}

	private static void createNodeBorderColor(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping discreteMapping = new DiscreteMapping(DEFAULT_NODE_BORDER_COLOR,
		                                                      MapNodeAttributes.BIOPAX_ENTITY_TYPE,
		                                                      ObjectMapping.NODE_MAPPING);

		//  map all complex to black
		BioPaxConstants bpConstants = new BioPaxConstants();
		Set physicalEntitySet = bpConstants.getPhysicalEntitySet();
		Iterator iterator = physicalEntitySet.iterator();

		while (iterator.hasNext()) {
			String entityName = (String) iterator.next();

			if (entityName.equals(BioPaxConstants.COMPLEX)) {
				discreteMapping.putMapValue(BioPaxPlainEnglish.getTypeInPlainEnglish(entityName),
				                            COMPLEX_NODE_BORDER_COLOR);
			}
		}

		// create and set node label calculator in node appearance calculator
		Calculator nodeBorderColorCalculator = new BasicCalculator("BioPAX Node Border Color"
		                                                           + VERSION_POST_FIX,
		                                                           discreteMapping,
		                                                           VisualPropertyType.NODE_BORDER_COLOR);
		nac.setCalculator(nodeBorderColorCalculator);

		// set default color
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR,
									   DEFAULT_NODE_BORDER_COLOR);
	}

	private static void createTargetArrows(EdgeAppearanceCalculator eac) {
		DiscreteMapping discreteMapping = new DiscreteMapping(ArrowShape.NONE,
		                                                      MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE,
		                                                      ObjectMapping.EDGE_MAPPING);

		discreteMapping.putMapValue(MapBioPaxToCytoscape.RIGHT, ArrowShape.DELTA);
		discreteMapping.putMapValue(MapBioPaxToCytoscape.CONTROLLED, ArrowShape.DELTA);
		discreteMapping.putMapValue(MapBioPaxToCytoscape.COFACTOR, ArrowShape.DELTA);
		discreteMapping.putMapValue(MapBioPaxToCytoscape.CONTAINS, ArrowShape.CIRCLE);

		//  Inhibition Edges
		Set inhibitionSet = ControlTypeConstants.getInhibitionSet();
		Iterator iterator = inhibitionSet.iterator();

		while (iterator.hasNext()) {
			String controlType = (String) iterator.next();
			discreteMapping.putMapValue(controlType, ArrowShape.T);
		}

		//  Activation Edges
		Set activationSet = ControlTypeConstants.getActivationSet();
		iterator = activationSet.iterator();

		while (iterator.hasNext()) {
			String controlType = (String) iterator.next();
			discreteMapping.putMapValue(controlType, ArrowShape.DELTA);
		}

		Calculator edgeTargetArrowCalculator = new BasicCalculator("BioPAX Target Arrows"
		                                                           + VERSION_POST_FIX,
		                                                           discreteMapping,
		                                                           VisualPropertyType.EDGE_TGTARROW_SHAPE);
		eac.setCalculator(edgeTargetArrowCalculator);
	}
}
