package cytoscape.visual.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.vizmap.mappings.DiscreteMapping;

public class GenerateSeriesListener extends DiscreteMappingEditorListener {
	private final static long serialVersionUID = 121374883715581L;
	public GenerateSeriesListener (final VisualPropertySheetPanel panel) {
		super(panel);
	}
	public Map<Object, Object> generateValues(VisualProperty type, DiscreteMapping dm, Set<Object> attrSet) {
		final String start = JOptionPane.showInputDialog(visualPropertySheetPanel.getPSP(),"Please enter start value (1st number in the series)", "0");
		final String increment = JOptionPane.showInputDialog(visualPropertySheetPanel.getPSP(), "Please enter increment", "1");

		if ((increment == null) || (start == null)) return null;

		Float inc; // FIXME: this could be simplified
		Float st;
		try {
			inc = Float.valueOf(increment);
			st = Float.valueOf(start);
		} catch (Exception ex) {
			ex.printStackTrace();
			inc = null;
			st = null;
		}

		if ((inc == null) || (inc < 0) || (st == null) || (st == null)) {
			return null;
		}

		final Map<Object, Object> valueMap = new HashMap<Object, Object>();
		
		if (type.getDataType() == Number.class) {
			for (Object key : attrSet) {
				valueMap.put(key, st);
				st = st + inc;
			}
		}
		return valueMap;
	}
}
