package org.cytoscape.view.vizmap.gui.internal.util;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.EDGE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.EDGE_LABEL;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.EDGE_LABEL_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_LABEL;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_LABEL_COLOR;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_X_SIZE;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NODE_Y_SIZE;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.view.vizmap.mappings.PassthroughMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Builder object for default Visual Style.
 * 
 * <p>
 * If default visual style does not exists, create one by this builder.
 * <p>
 * Rendering-engine specific style can be created from this style by adding 
 * extra properties, such as Shape.
 * 
 * @author kono
 *
 */
public class DefaultVisualStyleBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultVisualStyleBuilder.class);
	
	// Name of default visual style.
	private static final String DEFAULT_VS_NAME = "default";
	
	// TODO: replace these!!
	private static final String NAME = "name";
	private static final String INTERACTION = "interaction";
	
	// Preset parameters for default style
	private static final Color DEFAULT_NODE_COLOR = new Color(0x79CDCD);
	private static final Color DEFAULT_NODE_LABEL_COLOR = new Color(0x1C1C1C);

	private static final Color DEFAULT_EDGE_COLOR = new Color(0x80, 0x80, 0x80);
	private static final Color DEFAULT_EDGE_LABEL_COLOR = new Color(50, 50, 255);
	
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.white;
	
	private static final double DEFAULT_NODE_WIDTH = 55.0d;
	private static final double DEFAULT_NODE_HEIGHT = 30.0d;
		
	
	// This should be injected
	private final VisualStyleFactory vsFactory;
	
	// Each lexicon has its own defaults.
	private final Map<VisualLexicon, VisualStyle> styleMap;
	
	
	public DefaultVisualStyleBuilder(final VisualStyleFactory vsFactory) {
		this.vsFactory = vsFactory;
		this.styleMap = new HashMap<VisualLexicon, VisualStyle>();
	}
	
	public VisualStyle getDefaultStyle(final VisualLexicon lexicon) {
		
		VisualStyle defStyle = styleMap.get(lexicon);
		if(defStyle == null) {
			defStyle = buildDefaultStyle(lexicon);
			styleMap.put(lexicon, defStyle);
		}
		
		logger.debug("Default Style Created: " + defStyle.getTitle());
		
		return defStyle;
	}
	
	
	private VisualStyle buildDefaultStyle(final VisualLexicon lexicon) {

		// Create new style 
		final VisualStyle newStyle = vsFactory.createVisualStyle(DEFAULT_VS_NAME, lexicon);
		
		// Set node appearance
		newStyle.setDefaultValue(NODE_COLOR, DEFAULT_NODE_COLOR );
		newStyle.setDefaultValue(NODE_LABEL_COLOR, DEFAULT_NODE_LABEL_COLOR );
		newStyle.setDefaultValue(NODE_X_SIZE, DEFAULT_NODE_WIDTH );
		newStyle.setDefaultValue(NODE_Y_SIZE, DEFAULT_NODE_HEIGHT );
		
		// Set edge appearance
		newStyle.setDefaultValue(EDGE_COLOR, DEFAULT_EDGE_COLOR );
		newStyle.setDefaultValue(EDGE_LABEL_COLOR, DEFAULT_EDGE_LABEL_COLOR );
		
		// Set network appearance
		newStyle.setDefaultValue(NETWORK_BACKGROUND_PAINT, DEFAULT_BACKGROUND_COLOR );
		
		// Create label mappings
		final PassthroughMapping<String, String> labelMapping = new PassthroughMapping<String, String>(NAME, String.class, NODE_LABEL);
		final PassthroughMapping<String, String> edgeLabelMapping = new PassthroughMapping<String, String>(INTERACTION, String.class, EDGE_LABEL);
		
		newStyle.addVisualMappingFunction(labelMapping);
		newStyle.addVisualMappingFunction(edgeLabelMapping);
		
		return newStyle;
	}

}
