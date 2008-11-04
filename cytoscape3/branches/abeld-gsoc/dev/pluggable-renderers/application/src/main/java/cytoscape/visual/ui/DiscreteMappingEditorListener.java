package cytoscape.visual.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

import cytoscape.Cytoscape;

public abstract class DiscreteMappingEditorListener extends AbstractAction {
	protected VisualPropertySheetPanel visualPropertySheetPanel;

	public DiscreteMappingEditorListener (final VisualPropertySheetPanel panel) {
		visualPropertySheetPanel = panel;
	}
	
	abstract public Map<Object, Object>generateValues(VisualProperty type, DiscreteMapping dm, Set<Object> attrSet);
	
	public void actionPerformed(ActionEvent e) {
		final VizMapperProperty prop = visualPropertySheetPanel.getSelectedProperty();

		if ((prop != null) && (prop.getHiddenObject() instanceof VisualProperty)) {
			final VisualProperty type = (VisualProperty) prop.getHiddenObject();

			final ObjectMapping oMap;
			final CyAttributes attr;

			Calculator calculator = visualPropertySheetPanel.getVMMP().getCurrentlyEditedVS().getCalculator(type);
			oMap = calculator.getMapping(0);
			if (type.isNodeProp()) {
				attr = Cytoscape.getNodeAttributes();
			} else {
				attr = Cytoscape.getEdgeAttributes();
			}

			// This function is for discrete mapping only.
			if ((oMap instanceof DiscreteMapping) == false)
				return;
			
			DiscreteMapping dm = (DiscreteMapping) oMap;
			
			Set<Object> attrSet = visualPropertySheetPanel.loadKeys(oMap.getControllingAttributeName(), attr, oMap, type.isNodeProp());
			Map<Object, Object> valueMap = generateValues(type, (DiscreteMapping) oMap, attrSet);
			if (valueMap == null) return;
			dm.putAll(valueMap);
			visualPropertySheetPanel.getPSP().removeProperty(prop);
			final VizMapperProperty newRootProp = new VizMapperProperty();
			
			if (type.isNodeProp())
				visualPropertySheetPanel.buildProperty(calculator, newRootProp);
			else
				visualPropertySheetPanel.buildProperty(calculator, newRootProp);
			
			visualPropertySheetPanel.expandLastSelectedItem(type.getName());
			
		} else {
			System.out.println("Invalid.");
		}
		return;
	}
}
