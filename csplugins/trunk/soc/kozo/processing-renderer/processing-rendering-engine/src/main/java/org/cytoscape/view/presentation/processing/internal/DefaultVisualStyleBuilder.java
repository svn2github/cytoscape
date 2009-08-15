package org.cytoscape.view.presentation.processing.internal;

import static org.cytoscape.model.GraphObject.NODE;
import static org.cytoscape.view.presentation.property.ThreeDVisualLexicon.NODE_Z_LOCATION;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.*;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_LABEL;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_OPACITY;

import java.awt.Color;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.mappings.DiscreteMapping;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;

/**
 * Create default Visual Style for Processing Renderer
 * 
 * @author kono
 * 
 */
public class DefaultVisualStyleBuilder {

	// TODO: this attr name should be managed in model!!!
	private static final String NAME = "name";
	private static final String INTERACTION = "interaction";
	

	private static final String STYLE_TITLE = "Processing Default Style";

	private static final Color DEF_NODE_COLOR = new Color(200, 200, 200);
	private static final double DEF_NODE_OPACITY = 100d;

	private static final Color DEF_EDGE_COLOR = new Color(50, 50, 50);
	private static final double DEF_EDGE_OPACITY = 150d;
	
	private static final Color PP_COLOR = new Color(200, 20, 20);
	private static final Color PD_COLOR = new Color(100, 200, 100);

	private static final Color DEF_BACKGROUND_COLOR = Color.white;
	private static final String DEGREE = null;

	private VisualStyle style;

	private final VisualMappingManager vmm;

	public DefaultVisualStyleBuilder(VisualMappingManager vmm) {
		this.vmm = vmm;
		buildStyle();
	}

	private void buildStyle() {
		style = vmm.createVisualStyle(STYLE_TITLE);

		final PassthroughMapping<String, String> labelMapping = new PassthroughMapping<String, String>(
				NAME, String.class, NODE_LABEL);
		
		final DiscreteMapping<Double, Double> randSize = new DiscreteMapping<Double, Double>(
				DEGREE, Double.class, NODE_X_SIZE);
		
		final DiscreteMapping<String, Double> location = new DiscreteMapping<String, Double>(
				NAME, String.class, NODE_Z_LOCATION);
		
		final DiscreteMapping<String, Color> interaction = new DiscreteMapping<String, Color>(
				INTERACTION, String.class, (VisualProperty<Color>) EDGE_COLOR);
		
		randSize.putMapValue(1d, 10d);
		randSize.putMapValue(2d, 100d);
		randSize.putMapValue(3d, 200d);
		location.putMapValue("YMR043W", 500d);
		
		//interaction.putMapValue("pp", PP_COLOR);
		//interaction.putMapValue("pd", PD_COLOR);


		style.addVisualMappingFunction(labelMapping);
		style.addVisualMappingFunction(randSize);
		style.addVisualMappingFunction(location);
		style.addVisualMappingFunction(interaction);
		
		
		style.setDefaultValue(NODE_COLOR, DEF_NODE_COLOR);
		style.setDefaultValue(NODE_SELECTED_COLOR, Color.red);
		style.setDefaultValue(NODE_OPACITY, DEF_NODE_OPACITY);

		style.setDefaultValue(EDGE_COLOR, DEF_EDGE_COLOR);
		style.setDefaultValue(EDGE_SELECTED_COLOR, Color.red);
		style.setDefaultValue(EDGE_OPACITY, DEF_EDGE_OPACITY);
		style.setDefaultValue(EDGE_WIDTH, 3d);

		style.setDefaultValue(NETWORK_BACKGROUND_COLOR, DEF_BACKGROUND_COLOR);

	}

	public VisualStyle getDefaultStyle() {
		return style;
	}

}
