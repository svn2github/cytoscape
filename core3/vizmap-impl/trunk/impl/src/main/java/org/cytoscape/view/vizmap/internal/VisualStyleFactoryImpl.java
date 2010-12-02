package org.cytoscape.view.vizmap.internal;

import java.util.Collection;

import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.vizmap.VisualMappingFunction;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;

public class VisualStyleFactoryImpl implements VisualStyleFactory {

	
	private final VisualLexiconManager lexManager;
	
	public VisualStyleFactoryImpl(final VisualLexiconManager lexManager) {
		this.lexManager = lexManager;
	}

	@Override
	public VisualStyle getInstance(final VisualStyle original) {
		final VisualStyle copyVS = new VisualStyleImpl(original.getTitle(), lexManager);
		
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
	public VisualStyle getInstance(String title) {
		return new VisualStyleImpl(title, lexManager);
	}
}
