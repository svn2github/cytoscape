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
public enum CalculatorType {
	NODE_COLOR("Node Color", "nodeFillColorCalculator", "node.fillColor", "defaultNodeFillColor"), 
	NODE_BORDER_COLOR("Node Border Color", "nodeBorderColorCalculator", "node.borderColor", "defaultNodeBorderColor"), 
	NODE_LINETYPE("Node Line Type", "nodeLineTypeCalculator", "node.lineType", "defaultNodeLineType"), 
	NODE_SHAPE("Node Shape", "nodeShapeCalculator", "node.shape", "defaultNodeShape"), 
	NODE_SIZE("Node Size","nodeUniformSizeCalculator", "node.size", "defaultNodeSize"), 
	NODE_WIDTH("Node Width","nodeWidthCalculator", "node.width", "defaultNodeWidth"), 
	NODE_HEIGHT("Node Height","nodeHeightCalculator", "node.height", "defaultNodeHight"), 
	NODE_LABEL("Node Label","nodeLabelCalculator", "node.label", "defaultNodeLabel"), 
	NODE_FONT_FACE("Node Font Face","nodeFontFaceCalculator", "node.font", "defaultNodeFont"), 
	NODE_FONT_SIZE("Node Font Size","nodeFontSizeCalculator", "node.fontSize", "defaultNodeFontSize"), 
	NODE_LABEL_COLOR("Node Label Color","nodeLabelColor", "node.labelColor", "defaultNodeLabelColor"), 
	NODE_TOOLTIP("Node Tooltip","nodeTooltipCalculator", "node.toolTip", "defaultNodeToolTip"), 
	NODE_LABEL_POSITION("Node Label Position", "nodeLabelPositionCalculator", "node.labelPosition", "defaultNodeLabelPosition"),

	EDGE_COLOR("Edge Color", "edgeColorCalculator", "edge.color", "defaultEdgeColor"), 
	EDGE_LINETYPE("Edge Line Type", "edgeLineTypeCalculator", "edge.lineType", "defaultEdgeLineType"), 
	EDGE_SRCARROW("Edge Source Arrow", "edgeSourceArrowCalculator", "edge.sourceArrow", "defaultEdgeSourceArrow"), 
	EDGE_TGTARROW("Edge Target Arrow", "edgeTargetArrowCalculator", "edge.targetArrow", "defaultEdgeTargetArrow"), 
	EDGE_LABEL("Edge Label", "edgeLabelCalculator", "edge.label", "defaultEdgeLabel"), 
	EDGE_FONT_FACE("Edge Font Face", "edgeFontFaceCalculator", "edge.font", "defaultEdgeFont"), 
	EDGE_FONT_SIZE("Edge Font Size", "edgeFontSizeCalculator", "edge.fontSize", "defaultEdgeFontSize"), 
	EDGE_LABEL_COLOR("Edge Label Color", "edgeLabelColorCalculator", "edge.labelColor", "defaultEdgeLabelColor"), 
	EDGE_TOOLTIP("Edge Tooltip", "edgeTooltipCalculator", "edge.toolTip", "defaultEdgeToolTip"), 
	// Will be used in future.
	EDGE_LABEL_POSITION("Edge Label Position", "edgeLabelPositionCalculator", "edge.labelPosition", "defaultEdgeLabelPosition");

	/*
	 * String returned by toString() method.
	 */
	private final String calcName;

	/*
	 * Property label in prop file.
	 */
	private String propertyLabel;

	/*
	 * Attribute name for vizmap bypass function (right-click bypass) 
	 */
	private String bypassAttrName;
	
	private String defaultPropertyLabel;

	/*
	 * private constructor to put name into this enum.
	 */
	private CalculatorType(final String calcName, final String propertyLabel,
			final String bypassAttrName, final String defaultPropertyLabel) {
		this.calcName = calcName;
		this.propertyLabel = propertyLabel;
		this.bypassAttrName = bypassAttrName;
		this.defaultPropertyLabel = defaultPropertyLabel;
	}

	/**
	 * Returns name of calculator.
	 */
	@Override
	public String toString() {
		return calcName;
	}

	public String getName() {
		return calcName;
	}

	/**
	 * Returns string used as property label in VS prop file.
	 * 
	 * @return
	 */
	public String getPropertyLabel() {
		return propertyLabel;
	}

	public String getBypassAttrName() {
		return bypassAttrName;
	}
	
	public String getDefaultPropertyLabel() {
		return defaultPropertyLabel;
	}
	
	public String getDefaultPropertyKey(final String baseKey) {
		return baseKey + "." + defaultPropertyLabel;
	}

	/**
	 * Returns number of available calculators
	 * 
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

	public static CalculatorType getCalculatorTypes(byte type) {
		/*
		 * Type is always equal to ordinal.
		 */
		return values()[type];

	}

}
