package org.idekerlab.PanGIAPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.properties.EdgeOpacityProp;

/**
 * Listening to the network creation event and override the visual style.
 * 
 * @author kono
 *
 */
public class VisualStyleObserver implements PropertyChangeListener {
	
	// Name of the network attribute for checking network type.
	protected static final String NETWORK_TYPE_ATTRIBUTE_NAME = "Network Type";
	
	private static final URL visualStypePropLocation = PanGIAPlugin.class.getResource("/resources/PanGIAVS.props");
	
	public static final String VS_OVERVIEW_NAME = "PanGIA Overview Style";
	public static final String VS_MODULE_NAME = "PanGIA Module Style";
	
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
		
		if(Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_MODULE_NAME) == null ||
				Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(VS_OVERVIEW_NAME) == null) {
			Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, visualStypePropLocation);
		}
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
			
			Object type = Cytoscape.getNetworkAttributes().getAttribute(view.getNetwork().getIdentifier(), NETWORK_TYPE_ATTRIBUTE_NAME);
			
			if(type == null)
				return;
			
			final VisualStyle style = styleMap.get(type.toString());
			if(style == null)
				return;
			
			/*
			if (style.getName().equals(VS_OVERVIEW_NAME))
			{
				EdgeAppearanceCalculator eac = style.getEdgeAppearanceCalculator();
				
				ContinuousMapping cm = new ContinuousMapping(new EdgeOpacityProp(), ObjectMapping.EDGE_MAPPING);
				cm.setControllingAttributeName("PanGIA.edge score", view.getNetwork(), true);
				cm.getPoint(0).setValue(77);
				cm.getPoint(1).setValue(177);
				
				Calculator edgeOpacityCalc = new BasicCalculator(VS_OVERVIEW_NAME+"-EdgeOpacityMapping", cm, VisualPropertyType.EDGE_OPACITY);

				eac.setCalculator(edgeOpacityCalc);

			}*/
			
			view.setVisualStyle(style.getName());
			if(Cytoscape.getVisualMappingManager().getVisualStyle().equals(style) == false)
				Cytoscape.getVisualMappingManager().setVisualStyle(style);
			
			if (style.getName().equals(VS_MODULE_NAME))
			{
				CyLayoutAlgorithm alg = cytoscape.layout.CyLayouts.getLayout("force-directed");
				view.applyLayout(alg);	
				
				view.redrawGraph(true, true);
			}else view.redrawGraph(false, true);
		}
	}
}
