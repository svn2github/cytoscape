package src;

/*
 * Steve Federowicz 
 * Google Summer of Code
 * 
 * This class contains all of the methods responsible for coloring nodes in the graph.  It also contains methods for coloring node border color.
 * The methods in this class are called by BooleanSettingsDialog and CriteriaTablePanel.  The coloring methods are called by BooleanSettingsDialog 
 * user hits 'apply' and by CriteriaTablePanel any time a selection in the table is made.  
 * 
 */
import giny.model.*;


import cytoscape.visual.mappings.*;
import cytoscape.visual.calculators.*;
import cytoscape.visual.properties.*;
import cytoscape.visual.ui.*;
import cytoscape.visual.*;


import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle; 
import cytoscape.CyNode;

import cytoscape.visual.mappings.ObjectMapping; 

import java.awt.Color;
import java.util.List;
import java.util.Set;

public class ColorMapper {
	CyNetwork network;
	CyNetworkView networkView;
	String vsName;
	String attributeLabel;
	Color color;
	List<Node> nodes = null;
	//VisualMappingManager manager;
	//CalculatorCatalog catalog;
	//VisualStyle vs;
	
	public ColorMapper(){

	}
	
	/*
	 * This method creates the CompositeMapping, or mapping of multiple colors at once in the graph.
	 * It takes as arguments a compositeLabel or colon separated String and an array of Colors.  Each of the
	 * values in the comma separated list is the name of an attribute that holds the values of the outcome of an individually
	 * evaluated criteria.  Each of the comma separated values is then a label name in the order it appears
	 * in the table in the actual GUI.  The Colors are in an array parallel to this and are subsequently mapped.
	 */
	public VisualStyle createCompositeMapping(String vsName, String compositeLabel, Color[] colors){
		boolean newStyle = false;
		boolean update = true;
		
		if(!compositeLabel.contains(":")){
			return createDiscreteMapping(compositeLabel+"discrete", compositeLabel, colors[0]);
		}
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		
		VisualStyle vs = Cytoscape.getCurrentNetworkView().getVisualStyle();
		//VisualStyle visualStyle = null;
		
		//System.out.println("make Composite mapping");
		
		
		Set<String> styles = catalog.getVisualStyleNames();
		if (!(vs == null) && styles.contains(vs.getName())){
			vs = catalog.getVisualStyle(vs.getName());
		} else {
			//System.out.println("apply new style");
			vs = new VisualStyle(vs, vsName);
			newStyle = true;
		}
	
		
		DiscreteMapping disMapping = new DiscreteMapping(new Color(0), compositeLabel, ObjectMapping.NODE_MAPPING);
		
		disMapping.putMapValue(new Integer(-1), Color.WHITE);
		
		for(int i=0; i<colors.length; i++){
			disMapping.putMapValue(new Integer(i), colors[i]);
			System.out.println("COLORED: "+colors[i]);
		}
		
		
		NodeAppearanceCalculator nodeAppCalc = vs.getNodeAppearanceCalculator();
   		
		Calculator nodeColorCalculator = new BasicCalculator("Single Node Color Calc",
                disMapping,
                VisualPropertyType.NODE_FILL_COLOR);
		
		nodeAppCalc.setCalculator(nodeColorCalculator);
		
		vs.setNodeAppearanceCalculator(nodeAppCalc);
		
		// Create the visual style
		if (newStyle) {
			catalog.addVisualStyle(vs);
			if (update) {
				Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
				manager.setVisualStyle(vs);
			}
		} else if (update) {
			Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
		} 
		
		
		networkView.redrawGraph(true,true);

		return vs;
	}
	
	
	
	
	
	
	/*
	 * This method creates a single discreteMapping taking the label of an evaluated criteria and the color
	 * it should be mapped to.
	 */
	public VisualStyle createDiscreteMapping(String vsName, String label, Color currentColor){
		
		boolean newStyle = false;
		boolean update = true;
		
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		
		VisualStyle vs = Cytoscape.getCurrentNetworkView().getVisualStyle();
		
		Set<String> styles = catalog.getVisualStyleNames();
		if (!(vs == null) && styles.contains(vs.getName())){
			vs = catalog.getVisualStyle(vs.getName());
			System.out.println("apply style");
		} else {
			System.out.println("apply new style");
			vs = new VisualStyle(vs, vsName);
			newStyle = true;
		}
		
		
		DiscreteMapping disMapping = new DiscreteMapping(new Color(0), label, ObjectMapping.NODE_MAPPING);
		
		
		disMapping.putMapValue(Boolean.TRUE, currentColor);
		disMapping.putMapValue(Boolean.FALSE, Color.WHITE);
		
		NodeAppearanceCalculator nodeAppCalc = vs.getNodeAppearanceCalculator();
   		//EdgeAppearanceCalculator edgeAppCalc = vs.getEdgeAppearanceCalculator();
   		//GlobalAppearanceCalculator globalAppCalc = vs.getGlobalAppearanceCalculator();
		
		Calculator nodeColorCalculator = new BasicCalculator("Single Node Color Calc",
                disMapping,
                VisualPropertyType.NODE_FILL_COLOR);
		
		nodeAppCalc.setCalculator(nodeColorCalculator);
		
		vs.setNodeAppearanceCalculator(nodeAppCalc);
		// Create the visual style
			  			
		
		if (newStyle) {
			catalog.addVisualStyle(vs);
			if (update) {
				Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
				manager.setVisualStyle(vs);
			}
		} else if (update) {
			Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
		} 
		
		
		networkView.redrawGraph(true,true);

		return vs;
	}
	
	/*
	 * This method creates a single discreteMapping of the border color taking the label of an evaluated criteria and the color
	 * it should be mapped to.
	 */
	public VisualStyle createDiscreteBorderMapping(String vsName, String label, Color currentColor){
		
		
		boolean newStyle = false;
		boolean update = true;
		
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		
		VisualStyle vs = Cytoscape.getCurrentNetworkView().getVisualStyle();
		//VisualStyle visualStyle = null;
		
		
		
		
		Set<String> styles = catalog.getVisualStyleNames();
		if (!(vs == null) && styles.contains(vs.getName())){
			vs = catalog.getVisualStyle(vs.getName());
		} else {
			System.out.println("apply new style");
			vs = new VisualStyle(vs, vsName);
			newStyle = true;
		}
	
		
	     

		
		
		DiscreteMapping disMapping = new DiscreteMapping(Color.WHITE, label,ObjectMapping.NODE_MAPPING);
		
		//disMapping.setControllingAttributeName(label, network, false);
		System.out.println("hey "+currentColor);
		
		disMapping.putMapValue(Boolean.TRUE, currentColor);
		disMapping.putMapValue(Boolean.FALSE, Color.WHITE);
		
		NodeAppearanceCalculator nodeAppCalc = vs.getNodeAppearanceCalculator();
   		//EdgeAppearanceCalculator edgeAppCalc = vs.getEdgeAppearanceCalculator();
   		//GlobalAppearanceCalculator globalAppCalc = vs.getGlobalAppearanceCalculator();
		
		
		Calculator nodeColorCalculator = new BasicCalculator("Single Node Border Color Calc",
                disMapping,
                VisualPropertyType.NODE_BORDER_COLOR);
		
		nodeAppCalc.setCalculator(nodeColorCalculator);
		
		vs.setNodeAppearanceCalculator(nodeAppCalc);
		// Create the visual style
		
		  	
		
		//VisualStyle visualStyle = new VisualStyle(vsName, nodeAppCalc, edgeAppCalc, globalAppCalc);
		 
		
		
		
		if (newStyle) {
			catalog.addVisualStyle(vs);
			if (update) {
				Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
				manager.setVisualStyle(vs);
			}
		} else if (update) {
			Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
		} 
		
		 //catalog.addVisualStyle(visualStyle);
	    
		
		
		//manager.setVisualStyle(visualStyle);
		networkView.redrawGraph(true,true);

		return vs;
	}
	
	
	
	public VisualStyle createContinuousMapping(CyNetwork network, String label, Color currentColor, boolean update){
		boolean newStyle = false;
		boolean edge = false;
		// Get our current vizmap
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog calculatorCatalog = manager.getCalculatorCatalog();

		// Get the current style
		VisualStyle style = Cytoscape.getCurrentNetworkView().getVisualStyle();
		// Get our colors
		//Color missingColor = currentColor;
		// Color zeroColor = colorExtractor.getColor(0.0f);
		// Color upColor = colorExtractor.getColor(maxValue);
		// Color downColor = colorExtractor.getColor(minValue);

		// Adjust for contrast.  Since we really don't have contrast control,
		// we try to provide the same basic idea by adding extra points
		// in the continuous mapper
		// Color downDeltaColor = colorExtractor.getColor(minValue/100.0);
		// Color upDeltaColor = colorExtractor.getColor(maxValue/100.0);

		//if (!style.getName().endsWith(suffix)) {
			// Create a new vizmap
			Set<String> styles = calculatorCatalog.getVisualStyleNames();
			if (styles.contains(style.getName()))
				style = calculatorCatalog.getVisualStyle(style.getName());
			else {
				style = new VisualStyle(style, style.getName());
				newStyle = true;
			}
		//}

		// Get the right mapping, depending on whether we are mapping an edge or a node
		byte mapping = ObjectMapping.NODE_MAPPING;
		VisualPropertyType vizType = VisualPropertyType.NODE_FILL_COLOR;
		if (edge) {
			mapping = ObjectMapping.EDGE_MAPPING;
			vizType = VisualPropertyType.EDGE_COLOR;
		}

		// Create the new continuous mapper
		ContinuousMapping colorMapping = new ContinuousMapping(currentColor, mapping);
		colorMapping.setControllingAttributeName(label, Cytoscape.getCurrentNetwork(), false);
		colorMapping.setInterpolator(new LinearNumberToColorInterpolator());
		
		
/*
		colorMapping.addPoint (minValue,
       new BoundaryRangeValues (downColor, downColor, downColor));
		colorMapping.addPoint (minValue/100.0,
       new BoundaryRangeValues (downDeltaColor, downDeltaColor, downDeltaColor));
   	colorMapping.addPoint(0,
       new BoundaryRangeValues (zeroColor, zeroColor, zeroColor));
   	colorMapping.addPoint(maxValue/100.0,
       new BoundaryRangeValues (upDeltaColor, upDeltaColor, upDeltaColor));
   	colorMapping.addPoint(maxValue,
       new BoundaryRangeValues (upColor, upColor, upColor));
*/


   	Calculator colorCalculator = new BasicCalculator("TreeView Color Calculator", 
		                                                 colorMapping, vizType);


		// Apply it
	
		if (edge) {
			EdgeAppearanceCalculator edgeAppCalc = style.getEdgeAppearanceCalculator();
   		edgeAppCalc.setCalculator(colorCalculator);
			style.setEdgeAppearanceCalculator(edgeAppCalc);
		} else {
			NodeAppearanceCalculator nodeAppCalc = style.getNodeAppearanceCalculator();
   		nodeAppCalc.setCalculator(colorCalculator);
			style.setNodeAppearanceCalculator(nodeAppCalc);
		}
		if (newStyle) {
			calculatorCatalog.addVisualStyle(style);
			if (update) {
				Cytoscape.getCurrentNetworkView().setVisualStyle(style.getName());
				manager.setVisualStyle(style);
			}
		} else if (update) {
			Cytoscape.getCurrentNetworkView().applyVizmapper(style);
		} 
		return style;
	}


		
}

