package org.cytoscape.vizmap.gui.event;

import java.beans.PropertyChangeEvent;

import javax.annotation.Resource;

import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.gui.VizMapPropertySheetBuilder;
import org.cytoscape.vizmap.gui.VizMapperMainPanel;

import com.l2fprod.common.propertysheet.PropertySheetPanel;

public abstract class VizMapEventHandler {
	
	@Resource
	protected VisualMappingManager vmm;
	
	@Resource
	protected VizMapperMainPanel vizMapperMainPanel;
	
	@Resource
	protected VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	
	@Resource
	protected PropertySheetPanel propertySheetPanel;
	
	public VizMapEventHandler() {
	}

	public abstract void processEvent(PropertyChangeEvent e);
}
