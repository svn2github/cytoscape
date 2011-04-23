package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

public class VisualStyleBuilder {

    // Default Style
    private static final Color NETWORK_COLOR = Color.BLACK;
    private static final Color NODE_COLOR = new Color(0x00, 0xEE, 0x76);
    private static final Color NODE_LABEL_COLOR = Color.WHITE;
    private static final Color EDGE_COLOR = new Color(200, 200, 200);
    private static final Double EDGE_WIDTH = 2d;
    private static final Double NODE_WIDTH = 35d;
    private static final Double NODE_HEIGHT = 35d;
    private static final Color EDGE_LABEL_COLOR = Color.WHITE;

    private final VisualStyleFactory vsFactory;
    private final VisualMappingFunctionFactory discFactory;

    public VisualStyleBuilder(final VisualStyleFactory vsFactory, final VisualMappingFunctionFactory discFactory) {
	this.vsFactory = vsFactory;
	this.discFactory = discFactory;
    }

    public VisualStyle buildStyle(final String vsName) {
	final VisualStyle newStyle = vsFactory.getInstance(vsName);

	newStyle.setDefaultValue(MinimalVisualLexicon.NETWORK_BACKGROUND_PAINT, NETWORK_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_FILL_COLOR, NODE_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_LABEL_COLOR, NODE_LABEL_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_WIDTH, NODE_WIDTH);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_HEIGHT, NODE_HEIGHT);
	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_WIDTH, EDGE_WIDTH);
	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_PAINT, EDGE_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_LABEL_COLOR, EDGE_LABEL_COLOR);

	VisualMappingFunction<String, Paint> nodeColorMapping = discFactory.createVisualMappingFunction("Gene Type",
		String.class, MinimalVisualLexicon.NODE_FILL_COLOR);

	System.out.println("nodeColorMapping class = " + nodeColorMapping.getClass());
	if (nodeColorMapping instanceof DiscreteMapping) {
	    ((DiscreteMapping) nodeColorMapping).putMapValue("disease", Color.RED);
	}
	newStyle.addVisualMappingFunction(nodeColorMapping);

	return newStyle;
    }

}
