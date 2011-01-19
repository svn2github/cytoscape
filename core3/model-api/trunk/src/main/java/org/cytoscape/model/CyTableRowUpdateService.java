package org.cytoscape.model;


import org.cytoscape.model.events.CyTableRowUpdateMicroListener;


/** This service provides aggregated row creation/update events.  It guarantees that
 *  the handleRowCreations() methods of the listeners will be called before
 *  handleRowSets() methods of the listeners will be called.
 */
public interface CyTableRowUpdateService {
	void startTracking(CyTableRowUpdateMicroListener listener, CyTable table);
	void stopTracking(CyTableRowUpdateMicroListener listener, CyTable table);
}