package org.idekerlab.PanGIAPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualStyle;

/**
 * Listening to the network creation event and override the visual style.
 * 
 * @author kono
 *
 */
public class VisualStyleObserver implements PropertyChangeListener {
	
	protected static final String NETWORK_TYPE_ATTRIBUTE_NAME = "Network Type";
	
	private static final URL visualStypePropLocation = PanGIAPlugin.class.getResource("/resources/PanGIAVS.props");
	
	private static final String VS_OVERVIEW_NAME = "Complex Overview Style";
	private static final String VS_MODULE_NAME = "Module Style";
	
	private VisualStyle overviewVS;
	private VisualStyle moduleVS;
	
	private Map<String, VisualStyle> styleMap;

		
	VisualStyleObserver() {
		styleMap = new HashMap<String, VisualStyle>();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		restoreVS();
	}
	
	
	private void restoreVS() {
		// Load new styles.
		final VisualStyle currentStyle = Cytoscape.getVisualMappingManager().getVisualStyle();
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, visualStypePropLocation);
		
		overviewVS = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_OVERVIEW_NAME);
		moduleVS = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_MODULE_NAME);
		
		Cytoscape.getVisualMappingManager().setVisualStyle(currentStyle);
		styleMap.put(NetworkType.OVERVIEW.name(), overviewVS);
		styleMap.put(NetworkType.MODULE.name(), moduleVS);
		System.out.println("#### Init VS finished.");
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt.getPropertyName().equals(Cytoscape.SESSION_LOADED)) {
			restoreVS();
			return;
		}
		
		if(evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
			final Object newVal = evt.getNewValue();
			if(newVal == null || newVal instanceof CyNetworkView == false)
				return;
			
			final CyNetworkView view = (CyNetworkView) newVal;
			
			System.out.println("!!!!!!!! View Created for: " + view.getNetwork().getTitle());
			
			Object type = Cytoscape.getNetworkAttributes().getAttribute(view.getNetwork().getIdentifier(), NETWORK_TYPE_ATTRIBUTE_NAME);
			System.out.println("Type is : " + type);
			
			if(type == null)
				return;
			
			final VisualStyle style = styleMap.get(type.toString());
			if(style == null)
				return;
			
			System.out.println("Target Style is : " + style.getName());
			
			
			
			view.setVisualStyle(style.getName());
			if(Cytoscape.getVisualMappingManager().getVisualStyle().equals(style) == false)
				Cytoscape.getVisualMappingManager().setVisualStyle(style);
			view.redrawGraph(false, true);
		}
	}

}
