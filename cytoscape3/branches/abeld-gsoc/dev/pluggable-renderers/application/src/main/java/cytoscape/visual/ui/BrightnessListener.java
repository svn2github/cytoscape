package cytoscape.visual.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cytoscape.view.VisualProperty;
import org.cytoscape.vizmap.mappings.DiscreteMapping;

public class BrightnessListener extends DiscreteMappingEditorListener {
	private final static long serialVersionUID = 121374883775182L;
	protected static final int DARKER = 1;
	protected static final int BRIGHTER = 2;
	private final int functionType;

	public BrightnessListener  (final VisualPropertySheetPanel panel, final int type) {
		super(panel);
		this.functionType = type;
	}

	public Map<Object, Object> generateValues(VisualProperty type, DiscreteMapping dm, Set<Object> attrSet) {
		Map<Object, Object> valueMap = null;
		if (type.getDataType() == Color.class) {
			Object color;
			valueMap = new HashMap<Object, Object>();
			if (functionType == BRIGHTER) {
				for (Object key : attrSet) {
					color = dm.getMapValue(key);

					if ((color != null) && color instanceof Color) {
						valueMap.put(key, ((Color) color).brighter());
					}
				}
			} else if (functionType == DARKER) {
				for (Object key : attrSet) {
					color = dm.getMapValue(key);

					if ((color != null) && color instanceof Color) {
						valueMap.put(key, ((Color) color).darker());
					}
				}
			}
		}
		return valueMap;
	}
}