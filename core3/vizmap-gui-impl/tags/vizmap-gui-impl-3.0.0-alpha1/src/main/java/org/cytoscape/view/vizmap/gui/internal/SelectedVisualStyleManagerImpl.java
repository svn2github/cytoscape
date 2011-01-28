package org.cytoscape.view.vizmap.gui.internal;

import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEvent;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectedVisualStyleManagerImpl implements
		SelectedVisualStyleManager, SelectedVisualStyleSwitchedListener {
	
	private static final Logger logger = LoggerFactory.getLogger(SelectedVisualStyleManagerImpl.class);
	
	private VisualStyle selectedStyle;
	
	protected final VisualStyle defaultVS;
	
	public SelectedVisualStyleManagerImpl(final VisualMappingManager vmm) {
		if(vmm == null)
			throw new NullPointerException("Visual Mapping Manager is missing.");
		
		this.defaultVS = vmm.getDefaultVisualStyle();
		this.selectedStyle = this.defaultVS;
	}

	public VisualStyle getDefaultVisualStyle() {
		return defaultVS;
	}
	

	@Override
	public void handleEvent(SelectedVisualStyleSwitchedEvent e) {
		final VisualStyle style = e.getNewVisualStyle();
		if(style == null)
			throw new NullPointerException("Tried to set selected Visual Style to null.");
		
		this.selectedStyle = style;
		logger.debug("========= Selected Style Switched to " + selectedStyle.getTitle());

	}

	@Override
	public VisualStyle getCurrentVisualStyle() {
		return selectedStyle;
	}

	@Override
	public VisualStyle getDefaultStyle() {
		return defaultVS;
	}

}
