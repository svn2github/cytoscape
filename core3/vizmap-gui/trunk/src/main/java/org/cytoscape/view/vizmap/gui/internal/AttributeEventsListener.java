package org.cytoscape.view.vizmap.gui.internal;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.view.vizmap.gui.internal.editor.propertyeditor.CyComboBoxPropertyEditor;

import org.cytoscape.session.CyNetworkManager;

public class AttributeEventsListener {

	private CyComboBoxPropertyEditor propEditor;
	private Class<?> filter;
	private final CyDataTable attr;
	private CyNetworkManager cyNetworkManager;

	/**
	 * Constructor.
	 * 
	 * @param cyAttributes
	 *            CyDataTable
	 */
	public AttributeEventsListener(CyComboBoxPropertyEditor propEditor, Class<?> filter, 
			CyDataTable attributes, CyNetworkManager cyNetworkManager) {

		this.attr = attributes;
		this.filter = filter;
		this.propEditor = propEditor;
		this.cyNetworkManager = cyNetworkManager;

		// populate our lists
		//updateAttrList();
	}

//	/**
//	 * Our implementation of MultiHashMapListener.attributeValueAssigned().
//	 * 
//	 * @param objectKey
//	 *            String
//	 * @param attributeName
//	 *            String
//	 * @param keyIntoValue
//	 *            Object[]
//	 * @param oldAttributeValue
//	 *            Object
//	 * @param newAttributeValue
//	 *            Object
//	 */
//	public void handleEvent(RowSetEvent e) {
//		CyRow row = e.getSource();
//		String attributeName = e.getColumnName();
//
//		// we do not process network attributes
//		if (attr == cyNetworkManager.getCurrentNetwork()
//				.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS))
//			return;
//
//		// conditional repaint container
//		boolean repaint = false;
//
//		// this code gets called a lot
//		// so i've decided to keep the next two if statements as is,
//		// rather than create a shared general routine to call
//
//		// if attribute is not in attrEditorNames, add it if we support its
//		// type
//		if (!attrEditorNames.contains(attributeName)) {
//			attrEditorNames.add(attributeName);
//			Collections.sort(attrEditorNames);
//			// attrEditor.setAvailableValues(attrEditorNames.toArray());
//			spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "attrEditor",
//					attrEditorNames.toArray());
//			repaint = true;
//		}
//
//		// if attribute is not contained in numericalAttrEditorNames, add it
//		// if we support its class
//		if (!numericalAttrEditorNames.contains(attributeName)) {
//			Class<?> dataClass = attr.getColumnTypeMap().get(attributeName);
//
//			if ((dataClass == Integer.class) || (dataClass == Double.class)) {
//				numericalAttrEditorNames.add(attributeName);
//				Collections.sort(numericalAttrEditorNames);
//				// numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
//				spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
//						"numericalAttrEditorNames", numericalAttrEditorNames
//								.toArray());
//				repaint = true;
//			}
//		}
//
//		if (repaint)
//			targetComponent.repaint();
//	}
//
//	/**
//	 * Our implementation of MultiHashMapListener.allAttributeValuesRemoved()
//	 * 
//	 * @param objectKey
//	 *            String
//	 * @param attributeName
//	 *            String
//	 */
//	public void handleEvent(ColumnDeletedEvent e) {
//		String attributeName = e.getColumnName();
//
//		// we do not process network attributes
//		if (attr == cyNetworkManager.getCurrentNetwork()
//				.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS))
//			return;
//
//		// conditional repaint container
//		boolean repaint = false;
//
//		// this code gets called a lot
//		// so i've decided to keep the next two if statements as is,
//		// rather than create a shared general routine to call
//
//		// if attribute is in attrEditorNames, remove it
//		if (attrEditorNames.contains(attributeName)) {
//			attrEditorNames.remove(attributeName);
//			Collections.sort(attrEditorNames);
//			// attrEditor.setAvailableValues(attrEditorNames.toArray());
//			spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "attrEditor",
//					attrEditorNames.toArray());
//			repaint = true;
//		}
//
//		// if attribute is in numericalAttrEditorNames, remove it
//		if (numericalAttrEditorNames.contains(attributeName)) {
//			numericalAttrEditorNames.remove(attributeName);
//			Collections.sort(numericalAttrEditorNames);
//			// numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
//			spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
//					"numericalAttrEditor", numericalAttrEditorNames.toArray());
//			repaint = true;
//		}
//
//		if (repaint)
//			targetComponent.repaint();
//	}
//
//	/**
//	 * Method to populate attrEditorNames & numericalAttrEditorNames on object
//	 * instantiation.
//	 */
//	private void updateAttrList() {
//
//		// Attribute Names
//		if(attr== null) {
//			// Add ID only.
//			
//			
//		}
//		final List<String> names = new ArrayList<String>(attr.getColumnTypeMap().keySet());
//		Collections.sort(names);
//		
//		attrEditorNames.add("ID");
//
//		byte type;
//		Class<?> dataClass;
//
//		for (String name : names) {
//			attrEditorNames.add(name);
//			dataClass = attr.getColumnTypeMap().get(name);
//
//			if ((dataClass == Integer.class) || (dataClass == Double.class)) {
//				numericalAttrEditorNames.add(name);
//			}
//		}
//	}
//
//	public void handleEvent(ColumnCreatedEvent e) {
//		// TODO Auto-generated method stub
//		
//	}
}