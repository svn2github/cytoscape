package cytoscape.partitionNetwork;

import java.awt.Color;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

public class PartitionNetworkVisualStyleFactory {

	/**
	 * 
	 */
	public static final String PartitionNetwork_VS = "Partition Network Visual Style";
	


	public static VisualStyle createVisualStyle(CyNetworkView view) {
		

		
		VisualMappingManager vmManager = Cytoscape.getVisualMappingManager();
		NodeAppearanceCalculator nodeAppCalc = new NodeAppearanceCalculator();
		EdgeAppearanceCalculator edgeAppCalc = new EdgeAppearanceCalculator();
		CalculatorCatalog calculatorCatalog = vmManager.getCalculatorCatalog();
		
		VisualStyle currentStyle = view.getVisualStyle();
		
		VisualStyle clone = null;
		try {
			clone = (VisualStyle) currentStyle.clone();
		} catch (CloneNotSupportedException exc) {
			CyLogger.getLogger().warn("Clone not supported exception!");
		}

		// ------------------------------ Set node color ---------------------------//

//		ContinuousMapping colorMapping = new ContinuousMapping(Color.GRAY, ObjectMapping.NODE_MAPPING);
//		colorMapping.setControllingAttributeName(CUM_SPECTRA, network, false);
//		
//		colorMapping.addPoint (CUM_SPECTRA_LOW_POINT, 
//				new BoundaryRangeValues (LOW_COLOR, LOW_COLOR, LOW_COLOR));
//		colorMapping.addPoint(CUM_SPECTRA_MID_POINT, 
//				new BoundaryRangeValues (MID_COLOR, MID_COLOR, MID_COLOR));
//		colorMapping.addPoint(CUM_SPECTRA_HIGH_POINT, 
//				new BoundaryRangeValues (HIGH_COLOR, HIGH_COLOR, HIGH_COLOR));
//		
//		
//		Calculator colorCalculator = new BasicCalculator("Spectrum Mill Color Calculator",
//		                                                            colorMapping,
//	
/*
		ContinuousMapping colorMapping = new ContinuousMapping(Color.GRAY, ObjectMapping.NODE_MAPPING);
		colorMapping.setControllingAttributeName(HEAT_NORMAL_RATIO, network, false);
		
		colorMapping.addPoint (HEAT_NORMAL_RATIO_LOW_POINT, 
				new BoundaryRangeValues (LOW_COLOR, LOW_COLOR, LOW_COLOR));
		colorMapping.addPoint(HEAT_NORMAL_RATIO_MID_POINT, 
				new BoundaryRangeValues (MID_COLOR, MID_COLOR, MID_COLOR));
		colorMapping.addPoint(HEAT_NORMAL_RATIO_HIGH_POINT, 
				new BoundaryRangeValues (HIGH_COLOR, HIGH_COLOR, HIGH_COLOR));
		
		
		Calculator colorCalculator = new BasicCalculator("Scaffold Color Calculator",
		                                                            colorMapping,
																	VisualPropertyType.NODE_FILL_COLOR);
		nodeAppCalc.setCalculator(colorCalculator);
		*/

		//--------------------- Set the size of the nodes --------------------------//

/*
		ContinuousMapping sizeMapping = new ContinuousMapping(MID_SIZE, ObjectMapping.NODE_MAPPING);
		sizeMapping.setControllingAttributeName(CUM_INTENSITY, network, false);
		
		sizeMapping.addPoint (CUM_INTENSITY_LOW_POINT, 
				new BoundaryRangeValues (LOW_SIZE, LOW_SIZE, LOW_SIZE));
		sizeMapping.addPoint(CUM_INTENSITY_MID_POINT, 
				new BoundaryRangeValues (MID_SIZE, MID_SIZE, MID_SIZE));
		sizeMapping.addPoint(CUM_INTENSITY_HIGH_POINT, 
				new BoundaryRangeValues (HIGH_SIZE, HIGH_SIZE, HIGH_SIZE));
		
		
		Calculator sizeCalculator = new BasicCalculator("Spectrum Mill Node Size Calculator",
		                                                            sizeMapping,
																	VisualPropertyType.NODE_SIZE);
		nodeAppCalc.setCalculator(sizeCalculator);

*/
		// ------------------------------ Set node shapes ---------------------------//

		/*
		DiscreteMapping disMapping = new DiscreteMapping(NodeShape.ELLIPSE,
                ObjectMapping.NODE_MAPPING);
		disMapping.setControllingAttributeName(MOLECULAR_SPECIES, network, false);
	
		disMapping.putMapValue(PROTEIN, NodeShape.ELLIPSE);

		Calculator shapeCalculator = new BasicCalculator("Spectrum Mill Node Shape Calculator",
                           disMapping,
							VisualPropertyType.NODE_SHAPE);
		
		nodeAppCalc.setCalculator(shapeCalculator);

*/

		//------------------------- Create a visual style -------------------------------//
//		GlobalAppearanceCalculator gac = vmManager.getVisualStyle().getGlobalAppearanceCalculator();
		GlobalAppearanceCalculator gac = clone.getGlobalAppearanceCalculator();

		// set edge opacity
		gac.setDefaultBackgroundColor(Color.white);
		
//		VisualStyle visualStyle = new VisualStyle(PartitionNetwork_VS, nodeAppCalc, edgeAppCalc, gac);
		VisualPropertyType type = VisualPropertyType.EDGE_OPACITY;
		type.setDefault(clone, new Integer(50));
		
		clone.setName(PartitionNetwork_VS);

		return clone;
	}	
	
}
