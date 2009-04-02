package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import javax.annotation.Resource;

import org.cytoscape.view.vizmap.gui.event.VizMapEventHandler;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.view.vizmap.gui.internal.VizMapperMainPanel;
import org.cytoscape.vizmap.VisualMappingManager;

import com.l2fprod.common.propertysheet.PropertySheetPanel;

import cytoscape.CyNetworkManager;

public abstract class AbstractVizMapEventHandler implements VizMapEventHandler {
	
	@Resource
	protected VisualMappingManager vmm;
	
	@Resource
	protected VizMapperMainPanel vizMapperMainPanel;
	
	@Resource
	protected VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	
	@Resource
	protected PropertySheetPanel propertySheetPanel;
	
	@Resource
	protected CyNetworkManager cyNetworkManager;
	
	public AbstractVizMapEventHandler() {
	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.gui.event.VizMapEventHandler#processEvent(java.beans.PropertyChangeEvent)
	 */
	public abstract void processEvent(PropertyChangeEvent e);
}
