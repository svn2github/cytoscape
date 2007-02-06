package cytoscape.visual.calculators;

/**
 * Enum for calculator types.<br>
 * 
 * This will replace public constants defined in VizMapperUI class.
 * 
 * @since Cytoscape 2.5
 * @author kono
 * 
 */
public enum CalculatorTypes {
	NODE_COLOR("Node Color", "nodeColorCalculator"), NODE_BORDER_COLOR(
			"Node Border Color", "nodeBorderColorCalculator"), NODE_LINETYPE(
			"Node Line Type", "nodeLineTypeCalculator"), NODE_SHAPE(
			"Node Shape", "nodeShapeCalculator"), NODE_WIDTH("Node Width",
			"nodeWidthCalculator"), NODE_HEIGHT("Node Height",
			"nodeHeightCalculator"), NODE_LABEL("Node Label",
			"nodeLabelCalculator"), NODE_FONT_FACE("Node Font Face",
			"nodeFontFaceCalculator"), NODE_FONT_SIZE("Node Font Size",
			"nodeFontSizeCalculator"), NODE_LABEL_COLOR("Node Label Color",
			"nodeLabelColor"), NODE_TOOLTIP("Node Tooltip",
			"nodeTooltipCalculator"), NODE_LABEL_POSITION(
			"Node Label Position", "nodeLabelPositionCalculator"),

	EDGE_COLOR("Edge Color", "edgeColorCalculator"), EDGE_LINETYPE(
			"Edge Line Type", "edgeLineTypeCalculator"), EDGE_SRCARROW(
			"Edge Source Arrow", "edgeSourceArrowCalculator"), EDGE_TGTARROW(
			"Edge Target Arrow", "edgeTargetArrowCalculator"), EDGE_LABEL(
			"Edge Label", "edgeLabelCalculator"), EDGE_FONT_FACE(
			"Edge Font Face", "edgeFontFaceCalculator"), EDGE_FONT_SIZE(
			"Edge Font Size", "edgeFontSizeCalculator"), EDGE_LABEL_COLOR(
			"Edge Label Color", "edgeLabelColorCalculator"), EDGE_TOOLTIP(
			"Edge Tooltip", "edgeTooltipCalculator"), EDGE_LABEL_POSITION(
			"Edge Label Position", "edgeLabelPositionCalculator");

	/*
	 * String returned by toString() method.
	 */
	private final String calcName;

	/*
	 * Property label in prop file.
	 */
	private String propertyLabel;

	/*
	 * private constructor to put name into this enum.
	 */
	private CalculatorTypes(final String calcName, final String propertyLabel) {
		this.calcName = calcName;
		this.propertyLabel = propertyLabel;
	}

	/**
	 * Returns name of calculator.
	 */
	public String toString() {
		return calcName;
	}
	
	/**
	 * Returns string used as property label in VS prop file.
	 * @return
	 */
	public String getPropertyLabel() {
		return propertyLabel;
	}

	/**
	 * Returns number of available calculators
	 * @return
	 */
	public int totalCalululatorsCount() {
		return values().length;
	}

	/**
	 * Return position in thie enum as byte.<br>
	 * Will be used as type.
	 * 
	 * @return byte type
	 */
	public byte getType() {
		return (byte) ordinal();
	}

}
