package org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;

public class AttributeComboBoxPropertyEditor extends CyComboBoxPropertyEditor
		implements ColumnDeletedListener, ColumnCreatedListener {

	private Class<?> filter;
	private CyDataTable attributes;

	public AttributeComboBoxPropertyEditor(final CyDataTable attributes) {
		this(attributes, Object.class);
	}

	public AttributeComboBoxPropertyEditor(final CyDataTable attributes,
			final Class<?> filter) {
		super();
		this.attributes = attributes;
		this.filter = filter;

		initialize();
	}

	public void handleEvent(ColumnDeletedEvent e) {
		JComboBox box = (JComboBox) editor;
		box.removeItem(e.getColumnName());
	}

	public void handleEvent(ColumnCreatedEvent e) {
		// TODO: implement this.
	}

	private void initialize() {
		if (attributes == null)
			return;

		final List<Object> attrList = new ArrayList<Object>();
		final Map<String, Class<?>> attrTypes = attributes.getColumnTypeMap();
		final List<String> names = new ArrayList<String>(attrTypes.keySet());
		Collections.sort(names);

		Class<?> dataClass;
		for (String name : names) {
			dataClass = attrTypes.get(name);

			if (dataClass == filter)
				attrList.add(name);
		}

		setAvailableValues(attrList.toArray());
	}

}
