package org.cytoscape.task.internal.quickstart.subnetworkbuilder;

import java.awt.Color;
import java.awt.Paint;

import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.view.presentation.property.RichVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;

import static org.cytoscape.task.internal.quickstart.subnetworkbuilder.CreateSubnetworkTask.*;

/**
 * Builder for Visual Style
 * 
 */
public class VisualStyleBuilder {

    // Default Style
    private static final Color BACKGROUND_COLOR = Color.BLACK;

    private static final Color NODE_COLOR = new Color(0x00, 0xEE, 0x76);
    private static final Color NODE_LABEL_COLOR = Color.WHITE;
    private static final Color EDGE_COLOR = new Color(200, 200, 200);
    private static final Double EDGE_WIDTH = 2d;
    private static final Double NODE_WIDTH = 55d;
    private static final Double NODE_HEIGHT = 35d;
    private static final Color EDGE_LABEL_COLOR = Color.WHITE;

    private static final int NODE_LABEL_SIZE_REGULAR = 12;
    private static final int NODE_LABEL_SIZE_LARGE = 30;

    private final VisualStyleFactory vsFactory;
    private final VisualMappingFunctionFactory discFactory;
    private final VisualMappingFunctionFactory ptFactory;

    public VisualStyleBuilder(final VisualStyleFactory vsFactory, final VisualMappingFunctionFactory discFactory,
	    final VisualMappingFunctionFactory ptFactory) {
	this.vsFactory = vsFactory;
	this.discFactory = discFactory;
	this.ptFactory = ptFactory;
    }

    public VisualStyle buildStyle(final String vsName) {
	final VisualStyle newStyle = vsFactory.getInstance(vsName);

	newStyle.setDefaultValue(MinimalVisualLexicon.NETWORK_BACKGROUND_PAINT, BACKGROUND_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_FILL_COLOR, NODE_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_LABEL_COLOR, NODE_LABEL_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_WIDTH, NODE_WIDTH);
	newStyle.setDefaultValue(MinimalVisualLexicon.NODE_HEIGHT, NODE_HEIGHT);

	newStyle.setDefaultValue(RichVisualLexicon.NODE_LABEL_FONT_SIZE, NODE_LABEL_SIZE_REGULAR);

	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_WIDTH, EDGE_WIDTH);
	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_PAINT, EDGE_COLOR);
	newStyle.setDefaultValue(MinimalVisualLexicon.EDGE_LABEL_COLOR, EDGE_LABEL_COLOR);

	// Node Color mapping
	VisualMappingFunction<String, Paint> nodeColorMapping = discFactory.createVisualMappingFunction(QUERY_GENE_ATTR_NAME,
		String.class, MinimalVisualLexicon.NODE_FILL_COLOR);

	System.out.println("nodeColorMapping class = " + nodeColorMapping.getClass());
	if (nodeColorMapping instanceof DiscreteMapping) {
	    ((DiscreteMapping<String, Paint>) nodeColorMapping).putMapValue("disease", Color.RED);
	    ((DiscreteMapping<String, Paint>) nodeColorMapping).putMapValue("query and disease", Color.ORANGE);
	    ((DiscreteMapping<String, Paint>) nodeColorMapping).putMapValue("query", Color.BLUE);
	}
	newStyle.addVisualMappingFunction(nodeColorMapping);
	
	// Node Label Size mapping
	VisualMappingFunction<String, Integer> nodeLabelSizeMapping = discFactory.createVisualMappingFunction(QUERY_GENE_ATTR_NAME,
		String.class, RichVisualLexicon.NODE_LABEL_FONT_SIZE);

	System.out.println("nodeLabelSizeMapping class = " + nodeLabelSizeMapping.getClass());
	if (nodeLabelSizeMapping instanceof DiscreteMapping) {
	    ((DiscreteMapping<String, Integer>) nodeLabelSizeMapping).putMapValue("disease", NODE_LABEL_SIZE_LARGE);
	    ((DiscreteMapping<String, Integer>) nodeLabelSizeMapping).putMapValue("query and disease", NODE_LABEL_SIZE_LARGE);
	    ((DiscreteMapping<String, Integer>) nodeLabelSizeMapping).putMapValue("query", NODE_LABEL_SIZE_LARGE);
	}
	newStyle.addVisualMappingFunction(nodeLabelSizeMapping);
	
	// Label Mapping.
	final VisualMappingFunction<String, String> nodeLabelMapping = ptFactory.createVisualMappingFunction(CyTableEntry.NAME,
		String.class, MinimalVisualLexicon.NODE_LABEL);
	System.out.println("nodeLabelMapping class = " + nodeLabelMapping.getClass());
	newStyle.addVisualMappingFunction(nodeLabelMapping);

	return newStyle;
    }

}
