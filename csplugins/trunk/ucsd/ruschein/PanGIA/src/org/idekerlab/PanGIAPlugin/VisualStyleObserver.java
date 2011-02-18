package org.idekerlab.PanGIAPlugin;

import giny.model.Node;
import giny.view.NodeView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
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
import cytoscape.visual.mappings.PassThroughMapping;
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
	public static final String PARENT_MODULE_ATTRIBUTE_NAME = "Parent Module";
	
	private static final URL visualStypePropLocation = PanGIAPlugin.class.getResource("/resources/PanGIAVS.props");
	
	public static final String VS_OVERVIEW_NAME = "PanGIA Overview Style";
	public static final String VS_MODULE_NAME = "PanGIA Module Style";
	
	private VisualStyle overviewVS;
	private VisualStyle moduleVS;
	
	private Map<String, VisualStyle> styleMap;

	private static CyNetworkView overviewView;
	
	VisualStyleObserver() {
		styleMap = new HashMap<String, VisualStyle>();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		restoreVS();
	}
	
	public static void setOverviewView(CyNetworkView overviewView)
	{
		VisualStyleObserver.overviewView = overviewView;
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
		styleMap.put(NetworkType.DETAILED.name(), moduleVS);
		System.out.println("#### Init VS finished.");
	}
	
	public void setModuleLabels(String nattr)
	{
		BasicCalculator calcLabel = (BasicCalculator)moduleVS.getNodeAppearanceCalculator().getCalculator(VisualPropertyType.NODE_LABEL);
	
		if (calcLabel.getMapping(0) instanceof cytoscape.visual.mappings.PassThroughMapping)
		{
			PassThroughMapping pm = (PassThroughMapping)(calcLabel.getMapping(0));
			
			pm.setControllingAttributeName(nattr);
		}
			
	}
	
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public void propertyChange(PropertyChangeEvent evt) {
		
		if(evt.getPropertyName().equals(Cytoscape.SESSION_LOADED)) {
			restoreVS();
			return;
		}
		
		if(evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED))
		{
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
				overviewView = view;
				
				double min = Float.MAX_VALUE;
				double max = Float.MIN_VALUE;
				
				for (double f : (Collection<Double>)CyAttributesUtils.getAttribute(NestedNetworkCreator.EDGE_SCORE, Cytoscape.getEdgeAttributes()).values())
				{
					if (f<min) min = f;
					if (f>max) max = f;
				}
				
				EdgeAppearanceCalculator eac = style.getEdgeAppearanceCalculator();
				
				// Set EDGE_OPACITY
				BasicCalculator cal = (BasicCalculator) eac.getCalculator(VisualPropertyType.EDGE_OPACITY);
				if (cal.getMapping(0) instanceof ContinuousMapping){
					ContinuousMapping c_m = (ContinuousMapping)cal.getMapping(0);
					if (c_m.getControllingAttributeName().equalsIgnoreCase(NestedNetworkCreator.EDGE_SCORE)){
						// Clear points
						for (int i=c_m.getPointCount()-1; i>=0; i--){
							c_m.removePoint(i);
						}
						// Add new points
						c_m.addPoint(min, new BoundaryRangeValues(30,30,30));
						c_m.addPoint(max, new BoundaryRangeValues(255,255,255));
					}
					
				}
				
				//Set EDGE_LINE_WIDTH
				cal = (BasicCalculator) eac.getCalculator(VisualPropertyType.EDGE_LINE_WIDTH);
				if (cal.getMapping(0) instanceof ContinuousMapping){
					ContinuousMapping c_m = (ContinuousMapping)cal.getMapping(0);
					if (c_m.getControllingAttributeName().equalsIgnoreCase(NestedNetworkCreator.EDGE_SCORE)){
						// Clear points
						for (int i=c_m.getPointCount()-1; i>=0; i--){
							c_m.removePoint(i);
						}
						// Add new points
						c_m.addPoint(min, new BoundaryRangeValues(5,5,5));
						c_m.addPoint(max, new BoundaryRangeValues(20,20,20));
					}
				}				
					
				min = Float.MAX_VALUE;
				max = Float.MIN_VALUE;
				
				for (double f : (Collection<Double>)CyAttributesUtils.getAttribute(NestedNetworkCreator.GENE_COUNT_SQRT, Cytoscape.getNodeAttributes()).values())
				{
					if (f<min) min = f;
					if (f>max) max = f;
				}

				NodeAppearanceCalculator nac = style.getNodeAppearanceCalculator();
				
				// Set Node Size
				cal = (BasicCalculator) nac.getCalculator(VisualPropertyType.NODE_SIZE);
				if (cal.getMapping(0) instanceof ContinuousMapping){
					ContinuousMapping c_m = (ContinuousMapping)cal.getMapping(0);
					if (c_m.getControllingAttributeName().equalsIgnoreCase(NestedNetworkCreator.GENE_COUNT_SQRT)){
						// Clear points
						for (int i=c_m.getPointCount()-1; i>=0; i--){
							c_m.removePoint(i);
						}
						// Add new points
						c_m.addPoint(min, new BoundaryRangeValues(20,20,20));						
						double fs = Math.max(10*max,20);
						c_m.addPoint(max, new BoundaryRangeValues(fs,fs,fs));						
					}
				}
				
				//Set NODE_FONT_SIZE
				cal = (BasicCalculator) nac.getCalculator(VisualPropertyType.NODE_FONT_SIZE);
				if (cal.getMapping(0) instanceof ContinuousMapping){
					ContinuousMapping c_m = (ContinuousMapping)cal.getMapping(0);
					if (c_m.getControllingAttributeName().equalsIgnoreCase(NestedNetworkCreator.GENE_COUNT_SQRT)){
						// Clear points
						for (int i=c_m.getPointCount()-1; i>=0; i--){
							c_m.removePoint(i);
						}
						// Add new points
						c_m.addPoint(min, new BoundaryRangeValues(10,10,10));
						double fs = Math.max(max,10); 
						c_m.addPoint(max, new BoundaryRangeValues(fs,fs,fs));
					}
				}
			}
			
			view.setVisualStyle(style.getName());
			if(Cytoscape.getVisualMappingManager().getVisualStyle().equals(style) == false)
				Cytoscape.getVisualMappingManager().setVisualStyle(style);
			
			if (type.toString().equals(NetworkType.MODULE.name()))
			{
				CyLayoutAlgorithm alg = cytoscape.layout.CyLayouts.getLayout("force-directed");
				view.applyLayout(alg);	
				
				view.redrawGraph(true, true);
			}else if (type.toString().equals(NetworkType.DETAILED.name()))
			{
				//DetailedViewLayout.layout(view, overviewView);
				
			}else view.redrawGraph(false, true);
		}
	}
}
