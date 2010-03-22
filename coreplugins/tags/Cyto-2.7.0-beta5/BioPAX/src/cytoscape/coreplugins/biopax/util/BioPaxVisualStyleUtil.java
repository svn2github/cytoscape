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
package cytoscape.coreplugins.biopax.util;

import cytoscape.Cytoscape;

import cytoscape.coreplugins.biopax.BiopaxPlugin;
import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.*;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import org.biopax.paxtools.model.BioPAXElement;
import org.biopax.paxtools.model.level2.ControlType;
import org.biopax.paxtools.model.level2.complex;
import org.biopax.paxtools.model.level2.control;
import org.biopax.paxtools.model.level2.interaction;
import org.biopax.paxtools.model.level2.physicalEntity;
import org.biopax.paxtools.model.level3.Complex;
import org.biopax.paxtools.model.level3.Control;
import org.biopax.paxtools.model.level3.Interaction;
import org.biopax.paxtools.model.level3.PhysicalEntity;

import giny.view.NodeView;

import java.awt.*;
import java.util.Iterator;


/**
 * Creates an "out-of-the-box" default Visual Mapper for rendering BioPAX
 * networks.
 *
 * @author Ethan Cerami
 * @author Igor Rodchenkov (re-factoring using PaxTools API)
 */
public class BioPaxVisualStyleUtil {
	public static final CyLogger log = CyLogger.getLogger(BioPaxVisualStyleUtil.class);
	/**
	 * Verion Number String.
	 */
	public static final String VERSION_POST_FIX = " v " + BiopaxPlugin.VERSION_MAJOR_NUM + "_"
	                                              + BiopaxPlugin.VERSION_MINOR_NUM;

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

			NodeAppearanceCalculator nac = bioPaxVisualStyle.getNodeAppearanceCalculator();
			bioPaxVisualStyle.getDependency().set(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED,false);

			EdgeAppearanceCalculator eac = bioPaxVisualStyle.getEdgeAppearanceCalculator();

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
				MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);

		//  map all physical entities to circles
		for (Class<? extends BioPAXElement> claz : BioPaxUtil.getSubclassNames(PhysicalEntity.class, physicalEntity.class)) {
			String name =BioPaxUtil.getTypeInPlainEnglish(claz.getSimpleName());
			discreteMapping.putMapValue(name, NodeShape.ELLIPSE);
		}
		
		// hack for phosphorylated proteins
		discreteMapping.putMapValue(BioPaxUtil.PROTEIN_PHOSPHORYLATED, NodeShape.ELLIPSE);

		// map all interactions
		// - control to triangles
		// - others to square
		for (Class<?> c : BioPaxUtil.getSubclassNames(Interaction.class, interaction.class)) {
			String entityName = BioPaxUtil.getTypeInPlainEnglish(c.getSimpleName());
			if (Control.class.isAssignableFrom(c) || control.class.isAssignableFrom(c)) {
				discreteMapping.putMapValue(entityName, NodeShape.TRIANGLE);
			} else {
				discreteMapping.putMapValue(entityName, NodeShape.RECT);
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
				MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);
		DiscreteMapping discreteMappingHeight = new DiscreteMapping(new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT),
				MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);

		//  map all interactions to required size
		for (Class c : BioPaxUtil.getSubclassNames(Interaction.class, interaction.class)) {
			String entityName = c.getSimpleName();
			discreteMappingWidth.putMapValue(BioPaxUtil.getTypeInPlainEnglish(entityName),
			    new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BIO_PAX_VISUAL_STYLE_INTERACTION_NODE_SIZE_SCALE));
			discreteMappingHeight.putMapValue(BioPaxUtil.getTypeInPlainEnglish(entityName),
			    new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BIO_PAX_VISUAL_STYLE_INTERACTION_NODE_SIZE_SCALE));
		}
		
		
		//  map all complex to required size
		for (Class c: BioPaxUtil.getSubclassNames(complex.class, Complex.class)) {
			String entityName = c.getSimpleName();
			discreteMappingWidth.putMapValue(BioPaxUtil.getTypeInPlainEnglish(entityName),
				new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH * BIO_PAX_VISUAL_STYLE_COMPLEX_NODE_SIZE_SCALE));
			discreteMappingHeight.putMapValue(BioPaxUtil.getTypeInPlainEnglish(entityName),
				new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT * BIO_PAX_VISUAL_STYLE_COMPLEX_NODE_SIZE_SCALE));
		}

		/*
		// hack for phosphorylated proteins - make them large so label fits within node
		// commented out by Ethan Cerami, November 15, 2006
		discreteMappingWidth.putMapValue(BioPaxUtil.PROTEIN_PHOSPHORYLATED,
		     new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH
		                 * BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE));
		discreteMappingHeight.putMapValue(BioPaxUtil.PROTEIN_PHOSPHORYLATED,
		     new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_HEIGHT
		                 * BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_SIZE_SCALE));
		*/

		// create and set node height calculator in node appearance calculator
		Calculator nodeWidthCalculator = new BasicCalculator("BioPAX Node Width" + VERSION_POST_FIX,
		                                                     discreteMappingWidth,
		                                                     VisualPropertyType.NODE_WIDTH);
		nac.setCalculator(nodeWidthCalculator);
		nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_WIDTH,
									   new Double(BIO_PAX_VISUAL_STYLE_PHYSICAL_ENTITY_NODE_WIDTH));

		Calculator nodeHeightCalculator = new BasicCalculator("BioPAX Node Height"
		                                                      + VERSION_POST_FIX,
		                                                      discreteMappingHeight,
		                                                      VisualPropertyType.NODE_HEIGHT);
		nac.setCalculator(nodeHeightCalculator);
		nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_HEIGHT,
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
				MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);

		//  map all complex to black
		discreteMapping.putMapValue("Complex", COMPLEX_NODE_COLOR);

		// create and set node label calculator in node appearance calculator
		Calculator nodeColorCalculator = new BasicCalculator("BioPAX Node Color" + VERSION_POST_FIX,
		                                                     discreteMapping,
		                                                     VisualPropertyType.NODE_FILL_COLOR);
		nac.setCalculator(nodeColorCalculator);

		// set default color
		nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_FILL_COLOR,
									   DEFAULT_NODE_COLOR);
	}

	private static void createNodeBorderColor(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping discreteMapping = new DiscreteMapping(DEFAULT_NODE_BORDER_COLOR,
				MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE, ObjectMapping.NODE_MAPPING);

		//  map all complex to black
		discreteMapping.putMapValue("Complex", COMPLEX_NODE_BORDER_COLOR);

		// create and set node label calculator in node appearance calculator
		Calculator nodeBorderColorCalculator = new BasicCalculator("BioPAX Node Border Color"
		                                                           + VERSION_POST_FIX,
		                                                           discreteMapping,
		                                                           VisualPropertyType.NODE_BORDER_COLOR);
		nac.setCalculator(nodeBorderColorCalculator);

		// set default color
		nac.getDefaultAppearance().set(cytoscape.visual.VisualPropertyType.NODE_BORDER_COLOR,
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
		for (ControlType controlType : ControlType.values()) {
			if(controlType.toString().startsWith("I")) {
				discreteMapping.putMapValue(controlType.toString(), ArrowShape.T);
			}
		}

		//  Activation Edges
		for (ControlType controlType : ControlType.values()) {
			if(controlType.toString().startsWith("A")) {
				discreteMapping.putMapValue(controlType.toString(), ArrowShape.DELTA);
			}
		}

		Calculator edgeTargetArrowCalculator = new BasicCalculator("BioPAX Target Arrows"
		                                                           + VERSION_POST_FIX,
		                                                           discreteMapping,
		                                                           VisualPropertyType.EDGE_TGTARROW_SHAPE);
		eac.setCalculator(edgeTargetArrowCalculator);
	}
	

	@SuppressWarnings("unchecked")
	public static void setNodeToolTips(CyNetworkView networkView) {
		// grab node attributes
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		// iterate through the nodes
		Iterator<NodeView> nodesIt = networkView.getNodeViewsIterator();
		while (nodesIt.hasNext()) {
			NodeView nodeView = nodesIt.next();
			String id = nodeView.getNode().getIdentifier();
			String tip = 
				nodeAttributes.getStringAttribute(id, MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE)
				+ "\n" +
				nodeAttributes.getListAttribute(id, MapBioPaxToCytoscape.BIOPAX_CELLULAR_LOCATIONS);

			nodeView.setToolTip(tip);
			
			if(log.isDebugging())
				log.debug("tooltip set "+ tip + " for node " + id);
		}
		networkView.updateView();
	}
}
