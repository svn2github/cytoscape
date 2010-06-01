package cytoscape.visual.ui;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;

import cytoscape.visual.VisualPropertyType;

public class DiscretePropertySetter {

	private final PropertyRendererRegistry rendReg;
	private final PropertyEditorRegistry editorReg;

	private final CellRendererFactory rendFactory;
	private final CellEditorFactory editorFactory;

	DiscretePropertySetter(final PropertyChangeListener pcl,
			final PropertyRendererRegistry rendReg,
			final PropertyEditorRegistry editorReg,
			final CellRendererFactory rendFactory,
			final CellEditorFactory editorFactory) {
		
		this.rendReg = rendReg;
		this.editorReg = editorReg;

		this.rendFactory = rendFactory;
		this.editorFactory = editorFactory;
	}

	protected final <K, V> void setDiscreteProps(final VisualPropertyType type,
			final Map<K, V> discMapping, Set<K> attrKeys, DefaultProperty parent) {
		if (attrKeys == null)
			return;

		V val = null;
		VizMapperProperty valProp;
		String strVal;

		final List<VizMapperProperty> children = new ArrayList<VizMapperProperty>();

		for (final K key : attrKeys) {
			valProp = new VizMapperProperty();
			strVal = key.toString();
			valProp.setDisplayName(strVal);
			valProp.setName(strVal + "-" + type.toString());
			valProp.setParentProperty(parent);

			val = discMapping.get(key);
	

			if (val != null)
				valProp.setType(val.getClass());

			children.add(valProp);
			
			System.out.println(valProp.getDisplayName() + ": editor>>>>>>>>>>>>>>>>" + editorFactory
					.getPropertyEditor(type.getDataType()));
			rendReg.registerRenderer(valProp, rendFactory.getCellRenderer(type.getDataType()));
			editorReg.registerEditor(valProp, editorFactory
					.getPropertyEditor(type.getDataType()));

			valProp.setValue(val);
		}

		// Add all children.
		parent.addSubProperties(children);
	}

}
