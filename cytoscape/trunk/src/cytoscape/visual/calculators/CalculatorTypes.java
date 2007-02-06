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
	NODE_COLOR("Node Color"), NODE_BORDER_COLOR("Node Border Color"), NODE_LINETYPE(
			"Node Line Type"), NODE_SHAPE("Node Shape"), NODE_WIDTH(
			"Node Width"), NODE_HEIGHT("Node Height"), NODE_LABEL("Node Label"), NODE_LABEL_FONT_FACE(
			"Node Label Font Face"), NODE_LABEL_FONT_SIZE(
			"Node Label Font Size"), NODE_LABEL_COLOR("Node Label Color"), NODE_TOOLTIP(
			"Node Tooltip"), NODE_LABEL_POSITION("Node Label Position"),

	EDGE_COLOR("Edge Color"), EDGE_LINETYPE("Edge Line Type"), EDGE_SRCARROW(
			"Edge Source Arrow"), EDGE_TGTARROW("Edge Target Arrow"), EDGE_LABEL(
			"Edge Label"), EDGE_LABEL_FONT_FACE("Edge Label Font Face"), EDGE_LABEL_FONT_SIZE(
			"Edge Label Font Size"), EDGE_LABEL_COLOR("Edge Label Color"), EDGE_TOOLTIP(
			"Edge Tooltip"), EDGE_LABEL_POSITION("Edge Label Position");

	
	/*
	 * String returned by toString() method.
	 */
	private final String calcName;

	/*
	 * private constructor to put name into this enum.
	 */
	private CalculatorTypes(final String typeName) {
		this.calcName = typeName;
	}

	public String toString() {
		return calcName;
	}

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
