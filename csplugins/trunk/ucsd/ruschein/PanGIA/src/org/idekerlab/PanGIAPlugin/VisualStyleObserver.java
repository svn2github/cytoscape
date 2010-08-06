package org.idekerlab.PanGIAPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.BoundaryRangeValues;
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
			
			
			if (style.getName().equals(VS_OVERVIEW_NAME))
			{
				double min = Float.MAX_VALUE;
				double max = Float.MIN_VALUE;
				
				for (double f : (Collection<Double>)CyAttributesUtils.getAttribute("PanGIA.edge score", Cytoscape.getEdgeAttributes()).values())
				{
					if (f<min) min = f;
					if (f>max) max = f;
				}
					
				
				EdgeAppearanceCalculator eac = style.getEdgeAppearanceCalculator();
				
				ContinuousMapping cm = new ContinuousMapping(0.0, ObjectMapping.EDGE_MAPPING);
				cm.setControllingAttributeName("PanGIA.edge score", view.getNetwork(), true);
				cm.addPoint(min, new BoundaryRangeValues(30,30,30));
				cm.addPoint(max, new BoundaryRangeValues(255,255,255));
				Calculator edgeCalc = new BasicCalculator(VS_OVERVIEW_NAME+"-EdgeOpacityMapping", cm, VisualPropertyType.EDGE_OPACITY);
				eac.setCalculator(edgeCalc);
				
				cm = new ContinuousMapping(0.0, ObjectMapping.EDGE_MAPPING);
				cm.setControllingAttributeName("PanGIA.edge score", view.getNetwork(), true);
				cm.addPoint(min, new BoundaryRangeValues(5,5,5));
				cm.addPoint(max, new BoundaryRangeValues(20,20,20));
				edgeCalc = new BasicCalculator(VS_OVERVIEW_NAME+"-EdgeWidthMapping", cm, VisualPropertyType.EDGE_LINE_WIDTH);
				eac.setCalculator(edgeCalc);
				
				
				min = Float.MAX_VALUE;
				max = Float.MIN_VALUE;
				
				for (double f : (Collection<Double>)CyAttributesUtils.getAttribute("PanGIA.SQRT of member count", Cytoscape.getNodeAttributes()).values())
				{
					if (f<min) min = f;
					if (f>max) max = f;
				}
				
				NodeAppearanceCalculator nac = style.getNodeAppearanceCalculator();
				
				cm = new ContinuousMapping(0.0, ObjectMapping.NODE_MAPPING);
				cm.setControllingAttributeName("PanGIA.SQRT of member count", view.getNetwork(), true);
				cm.addPoint(min, new BoundaryRangeValues(20,20,20));
				double fs = Math.max(10*max,20);
				cm.addPoint(max, new BoundaryRangeValues(fs,fs,fs));
				Calculator nodeCalc = new BasicCalculator(VS_OVERVIEW_NAME+"-NodeSizeMapping", cm, VisualPropertyType.NODE_SIZE);
				nac.setCalculator(nodeCalc);
				
				cm = new ContinuousMapping(0.0, ObjectMapping.NODE_MAPPING);
				cm.setControllingAttributeName("PanGIA.SQRT of member count", view.getNetwork(), true);
				cm.addPoint(min, new BoundaryRangeValues(10,10,10));
				fs = Math.max(max,10); 
				cm.addPoint(max, new BoundaryRangeValues(fs,fs,fs));
				nodeCalc = new BasicCalculator(VS_OVERVIEW_NAME+"-NodeFontSizeMapping", cm, VisualPropertyType.NODE_FONT_SIZE);
				nac.setCalculator(nodeCalc);
			}
			
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
