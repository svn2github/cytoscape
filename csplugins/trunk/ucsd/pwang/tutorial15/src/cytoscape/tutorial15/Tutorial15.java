package cytoscape.tutorial15;

import giny.model.Node;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.BasicCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.Interpolator;
import cytoscape.visual.mappings.LinearNumberToColorInterpolator;
import cytoscape.visual.mappings.BoundaryRangeValues;
import java.util.Iterator;
/**
 * 
 */
public class Tutorial15 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial15() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		public MyPluginAction(Tutorial15 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial15");
			setPreferredMenu("Plugins");
		}

		
		public void actionPerformed(ActionEvent e) {
			
			String styleName = "myVisualStyle";

			if (Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(styleName) != null) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),styleName + " already existed!");	
				return;
			}
			
			//Check if attribute "Degree" exists
			boolean degree_exist = false;
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();
			String[] names = cyNodeAttrs.getAttributeNames();
			for (String name: names){
				if (name.equalsIgnoreCase("Degree")) {
					degree_exist = true;
					break;
				}
			}
			if (!degree_exist) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Degree attribute does not exist!");	
				return;
			}
			
			// Create a new visual style
			VisualStyle vs = new VisualStyle(styleName);

			// Create a node color calculator for "Degree" attribute
			Calculator nodeColorCalculator = createCalculator();

			// Set the calculator to the visualStyle			
			vs.getNodeAppearanceCalculator().setCalculator(nodeColorCalculator);
			
			// register the visual style with the visual mapper manager
			Cytoscape.getVisualMappingManager().getCalculatorCatalog().addVisualStyle(vs);
			Cytoscape.getVisualMappingManager().setVisualStyle(vs);
			
			// Appy the vsualStyle
			Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
		}

		private Calculator createCalculator() {
			// Determine the min and max for degree
			CyAttributes cyNodeAttrs = Cytoscape.getNodeAttributes();

			int min = 0;
			int max =0;

			Iterator<Node> it = Cytoscape.getCurrentNetwork().nodesIterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				Integer value = cyNodeAttrs.getIntegerAttribute(node.getIdentifier(), "Degree");
				if (value.intValue() < min) {
					min = value.intValue();
				}
				else if (value.intValue() > max) {
					max = value.intValue();
				}
			}
			
			// pick 3 points within (min~max)
			double p1 = min + (max-min)/3.0;
			double p2 = p1 + (max-min)/3.0;
			double p3 = p2 + (max-min)/3.0;
			
			// Create a calculator for "Degree" attribute
			VisualPropertyType type = VisualPropertyType.NODE_FILL_COLOR;
			final Object defaultObj = type.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle());
			
			ContinuousMapping cm = new ContinuousMapping(defaultObj, ObjectMapping.NODE_MAPPING);
			// Set controlling Attribute
			cm.setControllingAttributeName("Degree", Cytoscape.getCurrentNetwork(), false);

			Interpolator numToColor = new LinearNumberToColorInterpolator();
			cm.setInterpolator(numToColor);
			
			Color underColor = Color.GRAY;
			Color minColor = Color.RED;
			Color midColor = Color.WHITE;
			Color maxColor = Color.GREEN;
			Color overColor = Color.BLUE;
			
			BoundaryRangeValues bv0 = new BoundaryRangeValues(underColor, minColor, minColor);
			BoundaryRangeValues bv1 = new BoundaryRangeValues(midColor, midColor, midColor);
			BoundaryRangeValues bv2 = new BoundaryRangeValues(maxColor, maxColor, overColor);
			
			// Set the attribute point values associated with the boundary values
			cm.addPoint(p1, bv0);
			cm.addPoint(p2, bv1);
			cm.addPoint(p3, bv2);
			
			// Create a calculator
			return new BasicCalculator("My degree calcualtor", cm, VisualPropertyType.NODE_FILL_COLOR);			
		}
	}
}
