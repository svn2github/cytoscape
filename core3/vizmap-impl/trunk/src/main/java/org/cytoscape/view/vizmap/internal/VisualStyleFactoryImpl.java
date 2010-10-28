package org.cytoscape.view.vizmap.internal;

import java.util.Collection;

import org.cytoscape.event.CyEventHelper;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;

public class VisualStyleFactoryImpl implements VisualStyleFactory {
	
	private final CyEventHelper eventHelper;
	
	public VisualStyleFactoryImpl(final CyEventHelper eventHelper) {
		this.eventHelper = eventHelper;
	}

	@Override
	public VisualStyle createVisualStyle(final VisualStyle original) {
		final VisualStyle copyVS = new VisualStyleImpl(original.getTitle(), original.getVisualLexicon());
		
		// TODO: copy everything! This is incomplete
		Collection<VisualMappingFunction<?, ?>> allMapping = original.getAllVisualMappingFunctions();

		String attrName;
		VisualProperty<?> vp;

		for (VisualMappingFunction<?, ?> mapping : allMapping) {
			attrName = mapping.getMappingAttributeName();
			vp = mapping.getVisualProperty();
		}

		return copyVS;
	}
	

	@Override
	public VisualStyle createVisualStyle(String title, final VisualLexicon lexicon) {
		return new VisualStyleImpl(title, lexicon);
	}

}
