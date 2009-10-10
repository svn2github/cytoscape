package cytoscape.coreplugins.biopax.style;

import cytoscape.Cytoscape;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.mappings.*;

import cytoscape.coreplugins.biopax.mapping.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.mapping.MapNodeAttributes;
import cytoscape.coreplugins.biopax.util.BioPax3Constants;

import java.awt.*;

import org.biopax.paxtools.model.level2.ControlType;


public class BioPax3VisualStyleUtil {
	public static final String DASHED = "dashed";

	public static final String SOLID = "solid";

	/**
	 * Version Number String.
	 */
	public static final String VERSION_SUFFIX = "x";
//	public static final String VERSION_SUFFIX = " v "
//			+ BiopaxPlugin.VERSION_MAJOR_NUM + "_"
//			+ BiopaxPlugin.VERSION_MINOR_NUM;

	/**
	 * Name of BioPax Visual Style.
	 */
	public static final String BIO_PAX_VISUAL_STYLE = "BioPAX3";
	//public static final String BIO_PAX_VISUAL_STYLE = "BioPAX" + VERSION_POST_FIX;

	/**
	 * Names of biopax attributes in cytoscape.
	 */
	public static final String BIOPAX_NODE_LABEL = "biopax.node_label";
	public static final String BIOPAX_NAME = "biopax.name";
	public static final String BIOPAX_EDGE_MARK = "biopax.edge_mark";
	public static final String BIOPAX_EDGE_TIP = "biopax.edge_tip";
	public static final String BIOPAX_EDGE_STYLE = "biopax.edge_style";

	// Should be in org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape
	public static final String TEMPLATE = "TEMPLATE";
	public static final String PRODUCT = "PRODUCT";
	public static final String CATALYSIS = "CATALYSIS";
	public static final String CONTROL = "CONTROL";

	
	private static final Double SMALL_SIZE = new Double(15);
	private static final Double MEDIUM_SIZE = new Double(25);
	private static final Double LARGE_SIZE = new Double(35);

	/**
	 * Default color of nodes
	 */
	private static final Color DEFAULT_NODE_COLOR = new Color(255, 255, 255);
	private static final Color DEFAULT_NODE_BORDER_COLOR = new Color(0, 102,102);
	/**
	 * Constructor. If an existing BioPAX Viz Mapper already exists, we use it.
	 * Otherwise, we create a new one.
	 * 
	 * @return VisualStyle Object.
	 */
	
	public static VisualStyle getBioPaxVisualStyle() {
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();

		VisualStyle bioPaxVisualStyle = catalog.getVisualStyle(BIO_PAX_VISUAL_STYLE);

		// If the BioPAX Visual Style already exists, use this one instead.
		// The user may have tweaked the out-of-the box mapping, and we don't
		// want to over-ride these tweaks.
		if (bioPaxVisualStyle == null) {
			bioPaxVisualStyle = new VisualStyle(BIO_PAX_VISUAL_STYLE);

			NodeAppearanceCalculator nac = new NodeAppearanceCalculator();
			nac.setNodeSizeLocked(false);
			createNodeShape(nac);
			createNodeSize(nac);
			createNodeLabel(nac);
			createNodeToolTips(nac);
			createNodeColor(nac);
			createNodeBorderColor(nac);
			bioPaxVisualStyle.setNodeAppearanceCalculator(nac);

			
			EdgeAppearanceCalculator eac = new EdgeAppearanceCalculator();
			createEdgeArrows(eac);
			createEdgeLabels(eac);
			createEdgeStyles(eac);
			createEdgeToolTips(eac);
			bioPaxVisualStyle.setEdgeAppearanceCalculator(eac);

			// The visual style must be added to the Global Catalog
			// in order for it to be written out to vizmap.props upon user exit
			catalog.addVisualStyle(bioPaxVisualStyle);
		}

		return bioPaxVisualStyle;
	}

	private static void createNodeShape(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping a biopax type to a shape
		DiscreteMapping shapeMap = new DiscreteMapping(NodeShape.OCTAGON,
				MapNodeAttributes.BIOPAX_ENTITY_TYPE,
				ObjectMapping.NODE_MAPPING);
		
		// Default for all entities is stop sign: there is probably an error...
		shapeMap.putMapValue(unCamel(BioPax3Constants.ENTITY),NodeShape.ELLIPSE);
		
		// Map most physical entities to circles...
		shapeMap.putMapValue(unCamel(BioPax3Constants.PHYSICAL_ENTITY),NodeShape.ELLIPSE);
		// ... but map "templates" to parallelograms.
		shapeMap.putMapValue(unCamel(BioPax3Constants.RNA),NodeShape.PARALLELOGRAM);
		shapeMap.putMapValue(unCamel(BioPax3Constants.DNA),NodeShape.PARALLELOGRAM);
		shapeMap.putMapValue(unCamel(BioPax3Constants.COMPLEX),NodeShape.ROUND_RECT);

		// Generic interactions are rectangles...
		shapeMap.putMapValue(unCamel(BioPax3Constants.INTERACTION),NodeShape.RECT);
		// ... but controls are (<) triangles.
		shapeMap.putMapValue(unCamel(BioPax3Constants.CONTROL),NodeShape.TRIANGLE);
		// Interactions with "products" are diamonds.
		shapeMap.putMapValue(unCamel(BioPax3Constants.CONVERSION),NodeShape.DIAMOND);
		shapeMap.putMapValue(unCamel(BioPax3Constants.TEMPLATE_REACTION),NodeShape.DIAMOND);
		shapeMap.putMapValue(unCamel(BioPax3Constants.GENE),NodeShape.ELLIPSE);
		
		propagateDownward(shapeMap,BioPax3Constants.ENTITY,NodeShape.OCTAGON);
		
		// create and set node shape calculator in node appearance calculator
		Calculator nodeShapeCalculator = new BasicCalculator(
				"BioPAX3 Node Shape", shapeMap,	VisualPropertyType.NODE_SHAPE);
		nac.setCalculator(nodeShapeCalculator);
	}

	private static void propagateDownward(DiscreteMapping dMap, String key, Object defalt) {
		String eKey = unCamel(key);
		Object image = dMap.getMapValue(eKey);
		if (image == null) dMap.putMapValue(eKey, defalt);
		else defalt = image;
		System.err.println(key+" ("+eKey+") is mapped to "+ dMap.getMapValue(eKey));
		
		for (String child: BioPax3Constants.getBiopaxSubclasses(key)) {
			propagateDownward(dMap,child,defalt);
		}
	}

	private static void createNodeSize(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node size.
		DiscreteMapping widthMap = new DiscreteMapping(
				MEDIUM_SIZE,MapNodeAttributes.BIOPAX_ENTITY_TYPE,ObjectMapping.NODE_MAPPING);
		DiscreteMapping heightMap = new DiscreteMapping(
				MEDIUM_SIZE,MapNodeAttributes.BIOPAX_ENTITY_TYPE,ObjectMapping.NODE_MAPPING);
		
		// Default for all entities is stop sign: there is probably an error...
		widthMap.putMapValue(unCamel(BioPax3Constants.PHYSICAL_ENTITY),LARGE_SIZE);
		heightMap.putMapValue(unCamel(BioPax3Constants.PHYSICAL_ENTITY),MEDIUM_SIZE);
		widthMap.putMapValue(unCamel(BioPax3Constants.SMALL_MOLECULE),SMALL_SIZE);
		heightMap.putMapValue(unCamel(BioPax3Constants.SMALL_MOLECULE),SMALL_SIZE);
		widthMap.putMapValue(unCamel(BioPax3Constants.INTERACTION),MEDIUM_SIZE);
		heightMap.putMapValue(unCamel(BioPax3Constants.INTERACTION),MEDIUM_SIZE);
		widthMap.putMapValue(unCamel(BioPax3Constants.CONTROL),SMALL_SIZE);
		heightMap.putMapValue(unCamel(BioPax3Constants.CONTROL),SMALL_SIZE);
		
		// create and set node height calculator in node appearance calculator
		propagateDownward(widthMap,BioPax3Constants.ENTITY,LARGE_SIZE);
		nac.setCalculator(new BasicCalculator(
				"BioPAX Node Width" + VERSION_SUFFIX, widthMap, VisualPropertyType.NODE_WIDTH));
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_WIDTH, MEDIUM_SIZE);

		propagateDownward(heightMap,BioPax3Constants.ENTITY,LARGE_SIZE);
		nac.setCalculator(new BasicCalculator(
				"BioPAX Node Height" + VERSION_SUFFIX, heightMap, VisualPropertyType.NODE_HEIGHT));
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_HEIGHT, MEDIUM_SIZE);
	}

	private static void createNodeLabel(NodeAppearanceCalculator nac) {
		// create pass through mapper for node labels
		PassThroughMapping passMap = new PassThroughMapping("",	ObjectMapping.NODE_MAPPING);
		passMap.setControllingAttributeName(BIOPAX_NODE_LABEL, null, false);

		// create and set node label calculator in node appearance calculator
		nac.setCalculator(new BasicCalculator(
				"BioPAX Node Label" + VERSION_SUFFIX, passMap, VisualPropertyType.NODE_LABEL));
	}
	private static void createNodeToolTips(NodeAppearanceCalculator nac) {
		// create pass through mapper for node labels
		PassThroughMapping passMap = new PassThroughMapping("",	ObjectMapping.NODE_MAPPING);
		passMap.setControllingAttributeName(BIOPAX_NAME, null, false);

		// create and set node label calculator in node appearance calculator
		nac.setCalculator(new BasicCalculator(
				"BioPAX Node Tooltip" + VERSION_SUFFIX, passMap, VisualPropertyType.NODE_TOOLTIP));
	}

	
	private static void createNodeColor(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping colorMap = new DiscreteMapping(
				DEFAULT_NODE_COLOR, MapNodeAttributes.BIOPAX_ENTITY_TYPE,
				ObjectMapping.NODE_MAPPING);

		Color brightRed = new Color(1.0F,0.0F,0.0F,0.0F);
		Color paleRed = new Color(1.0F,0.8F,0.8F,0.0F);
		Color paleBlue = new Color(0.8F,0.8F,1.0F,0.0F);
		Color brightBlue = new Color(0.0F,0.0F,1.0F,0.0F);
		Color paleGreen = new Color(0.8F,1.0F,0.8F,0.0F);
		Color brightGreen = new Color(0.0F,1.0F,0.0F,0.0F);
		Color black = new Color(0.0F,.0F,0.0F,0.0F);
		colorMap.putMapValue(unCamel(BioPax3Constants.ENTITY),black);
		colorMap.putMapValue(unCamel(BioPax3Constants.PHYSICAL_ENTITY),paleBlue);
		colorMap.putMapValue(unCamel(BioPax3Constants.DNA),paleGreen);
		colorMap.putMapValue(unCamel(BioPax3Constants.RNA),paleGreen);
		colorMap.putMapValue(unCamel(BioPax3Constants.TEMPLATE_REACTION),brightGreen);
		colorMap.putMapValue(unCamel(BioPax3Constants.TEMPLATE_REACTION_REGULATION),paleGreen);
		colorMap.putMapValue(unCamel(BioPax3Constants.PHYSICAL_ENTITY),paleBlue);
		//colorMap.putMapValue(unCamel(BioPax3Constants.CONTROL),paleRed);
		colorMap.putMapValue(unCamel(BioPax3Constants.CONVERSION),brightBlue);
		colorMap.putMapValue(unCamel(BioPax3Constants.CATALYSIS),paleBlue);
		
		propagateDownward(colorMap,BioPax3Constants.ENTITY,LARGE_SIZE);

		// create and set node label calculator in node appearance calculator
		nac.setCalculator(new BasicCalculator(
				"BioPAX Node Color" + VERSION_SUFFIX, colorMap,	VisualPropertyType.NODE_FILL_COLOR));

		// set default color
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_FILL_COLOR,DEFAULT_NODE_COLOR);
	}

	private static void createNodeBorderColor(NodeAppearanceCalculator nac) {
		// create a discrete mapper, for mapping biopax node type
		// to a particular node color
		DiscreteMapping borderMap = new DiscreteMapping(
				DEFAULT_NODE_BORDER_COLOR,
				MapNodeAttributes.BIOPAX_ENTITY_TYPE,
				ObjectMapping.NODE_MAPPING);

		// map all complex to black
		propagateDownward(borderMap,BioPax3Constants.ENTITY,	DEFAULT_NODE_BORDER_COLOR);


		// create and set node label calculator in node appearance calculator
		nac.setCalculator(new BasicCalculator("BioPAX Node Border Color" + VERSION_SUFFIX, 
				borderMap,VisualPropertyType.NODE_BORDER_COLOR));

		// set default color
		nac.getDefaultAppearance().set(VisualPropertyType.NODE_BORDER_COLOR, DEFAULT_NODE_BORDER_COLOR);
	}

	private static void createEdgeToolTips(EdgeAppearanceCalculator eac) {
		// create pass through mapper for node labels
		PassThroughMapping passMap = new PassThroughMapping("",	ObjectMapping.EDGE_MAPPING);
		passMap.setControllingAttributeName(BioPax3VisualStyleUtil.BIOPAX_EDGE_TIP, null, false);

		// create and set node label calculator in node appearance calculator
		eac.setCalculator(new BasicCalculator(
				"BioPAX Edge Tooltip" + VERSION_SUFFIX, passMap, VisualPropertyType.EDGE_TOOLTIP));
	}

	private static void createEdgeStyles(EdgeAppearanceCalculator eac) {
		// create a discrete mapper, for mapping a biopax type to a shape
		DiscreteMapping styleMap = new DiscreteMapping(LineStyle.SOLID,
				BIOPAX_EDGE_STYLE,
				ObjectMapping.EDGE_MAPPING);
		
		styleMap.putMapValue(SOLID, LineStyle.SOLID);
		styleMap.putMapValue(DASHED, LineStyle.LONG_DASH);
		// create pass through mapper for node labels


		// create and set node label calculator in node appearance calculator
		eac.setCalculator(new BasicCalculator(
				"BioPAX Edge Line Style" + VERSION_SUFFIX, styleMap, VisualPropertyType.EDGE_LINE_STYLE));
	}
	
	private static void createEdgeLabels(EdgeAppearanceCalculator eac) {
		// create pass through mapper for node labels
		PassThroughMapping passMap = new PassThroughMapping("",	ObjectMapping.EDGE_MAPPING);
		passMap.setControllingAttributeName(BIOPAX_EDGE_MARK, null, false);

		// create and set node label calculator in node appearance calculator
		eac.setCalculator(new BasicCalculator(
				"BioPAX Edge Tooltip" + VERSION_SUFFIX, passMap, VisualPropertyType.EDGE_LABEL));
	}
	private static void createEdgeArrows(EdgeAppearanceCalculator eac) {
		DiscreteMapping srcMap = new DiscreteMapping(ArrowShape.NONE,
				MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE,
				ObjectMapping.EDGE_MAPPING);
		DiscreteMapping tgtMap = new DiscreteMapping(ArrowShape.NONE,
				MapBioPaxToCytoscape.BIOPAX_EDGE_TYPE,
				ObjectMapping.EDGE_MAPPING);

		tgtMap.putMapValue(MapBioPaxToCytoscape.RIGHT,     ArrowShape.DELTA);
		srcMap.putMapValue(MapBioPaxToCytoscape.LEFT,      ArrowShape.DELTA);

		tgtMap.putMapValue(PRODUCT,     ArrowShape.DELTA);
		srcMap.putMapValue(TEMPLATE,      ArrowShape.DELTA);

		srcMap.putMapValue(MapBioPaxToCytoscape.CONTROLLER,ArrowShape.DIAMOND);
		tgtMap.putMapValue(MapBioPaxToCytoscape.CONTROLLED,ArrowShape.DIAMOND);
		tgtMap.putMapValue(CATALYSIS,ArrowShape.DIAMOND);
		tgtMap.putMapValue(CONTROL,ArrowShape.DIAMOND);

		tgtMap.putMapValue(MapBioPaxToCytoscape.COFACTOR,  ArrowShape.DELTA);

		//srcMap.putMapValue(MapBioPaxToCytoscape.CONTAINS,  ArrowShape.CIRCLE);
		tgtMap.putMapValue(MapBioPaxToCytoscape.CONTAINS,  ArrowShape.CIRCLE);

		// Inhibition Edges
		for (ControlType controlType : ControlType.values()) {
			if(controlType.toString().startsWith("I")) {
				tgtMap.putMapValue(controlType.toString(), ArrowShape.T);
			}
		}
		// Activation Edges
		for (ControlType controlType : ControlType.values()) {
			if(controlType.toString().startsWith("A")) {
				tgtMap.putMapValue(controlType.toString(), ArrowShape.DIAMOND);
			}
		}

		eac.setCalculator(new BasicCalculator(
				"BioPAX Source Arrows" + VERSION_SUFFIX, srcMap,
				VisualPropertyType.EDGE_SRCARROW_SHAPE));
		eac.setCalculator(new BasicCalculator(
				"BioPAX Target Arrows" + VERSION_SUFFIX, tgtMap,
				VisualPropertyType.EDGE_TGTARROW_SHAPE));
	}

	public static String unCamel(String s) {
		if (s.matches("[dr]na")) return s.toUpperCase();
		s = s.replaceAll("(\\S)([A-Z])", "$1 $2");
		s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
		return s;
	}
}
