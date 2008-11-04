package cytoscape.visual.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesUtils;
import org.cytoscape.attributes.MultiHashMapListener;

import cytoscape.Cytoscape;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;

public class MultiHashMapListenerAdapter implements MultiHashMapListener {

	// ref to members
	private final JPanel container;
	private final CyAttributes attr;
	private final CyComboBoxPropertyEditor attrEditor;
	private final CyComboBoxPropertyEditor numericalAttrEditor;
	private final List<String> attrEditorNames;
	private final List<String> numericalAttrEditorNames;

	/**
	 * Constructor.
	 *
	 * @param cyAttributes CyAttributes
	 */
	public MultiHashMapListenerAdapter(JPanel container, CyAttributes cyAttributes, CyComboBoxPropertyEditor attrEditor, CyComboBoxPropertyEditor numericalAttrEditor) {
		
		// init some members
		this.attr = cyAttributes;
		this.container = container;
		this.attrEditor = attrEditor;
		this.numericalAttrEditor = numericalAttrEditor;
		this.attrEditorNames = new ArrayList<String>();
		this.numericalAttrEditorNames = new ArrayList<String>();

		// populate our lists
		populateLists();
	}

	/**
	 *  Our implementation of MultiHashMapListener.attributeValueAssigned().
	 *
	 * @param objectKey String
	 * @param attributeName String
	 * @param keyIntoValue Object[]
	 * @param oldAttributeValue Object
	 * @param newAttributeValue Object
	 */
	public void attributeValueAssigned(String objectKey, String attributeName,
									   Object[] keyIntoValue, Object oldAttributeValue,
									   Object newAttributeValue) {

		// we do not process network attributes
		if (attr == Cytoscape.getNetworkAttributes()) return;

		// conditional repaint container
		boolean repaint = false;

		// this code gets called a lot
		// so i've decided to keep the next two if statements as is, 
		// rather than create a shared general routine to call

		// if attribute is not in attrEditorNames, add it if we support its type
		if (!attrEditorNames.contains(attributeName)) {
			byte type = attr.getType(attributeName);
			if (attr.getUserVisible(attributeName) && (type != CyAttributes.TYPE_UNDEFINED) && (type != CyAttributes.TYPE_COMPLEX)) {
				attrEditorNames.add(attributeName);
				Collections.sort(attrEditorNames);
				attrEditor.setAvailableValues(attrEditorNames.toArray());
				repaint = true;
			}
		}

		// if attribute is not contained in numericalAttrEditorNames, add it if we support its class
		if (!numericalAttrEditorNames.contains(attributeName)) {
			Class dataClass = CyAttributesUtils.getClass(attributeName, attr);
			if ((dataClass == Integer.class) || (dataClass == Double.class) || (dataClass == Float.class)) {
				numericalAttrEditorNames.add(attributeName);
				Collections.sort(numericalAttrEditorNames);
				numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
				repaint = true;
			}
		}
		
		if (repaint) container.repaint();
	}

	/**
	 *  Our implementation of MultiHashMapListener.attributeValueRemoved().
	 *
	 * @param objectKey String
	 * @param attributeName String
	 * @param keyIntoValue Object[]
	 * @param attributeValue Object
	 */
	public void attributeValueRemoved(String objectKey, String attributeName,
									  Object[] keyIntoValue, Object attributeValue) {
		allAttributeValuesRemoved(objectKey, attributeName);
	}

	/**
	 *  Our implementation of MultiHashMapListener.allAttributeValuesRemoved()
	 *
	 * @param objectKey String
	 * @param attributeName String
	 */
	public void allAttributeValuesRemoved(String objectKey, String attributeName) {

		// we do not process network attributes
		if (attr == Cytoscape.getNetworkAttributes()) return;

		// conditional repaint container
		boolean repaint = false;

		// this code gets called a lot
		// so i've decided to keep the next two if statements as is, 
		// rather than create a shared general routine to call

		// if attribute is in attrEditorNames, remove it
		if (attrEditorNames.contains(attributeName)) {
			attrEditorNames.remove(attributeName);
			Collections.sort(attrEditorNames);
			attrEditor.setAvailableValues(attrEditorNames.toArray());
			repaint = true;
		}

		// if attribute is in numericalAttrEditorNames, remove it
		if (numericalAttrEditorNames.contains(attributeName)) {
			numericalAttrEditorNames.remove(attributeName);
			Collections.sort(numericalAttrEditorNames);
			numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
			repaint = true;
		}

		if (repaint) container.repaint();
	}

	/**
	 * Method to populate attrEditorNames & numericalAttrEditorNames on object instantiation.
	 */
	private void populateLists() {

		// get attribute names & sort
		String[] nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);

		// populate attrEditorNames & numericalAttrEditorNames
		attrEditorNames.add("ID");
		byte type;
		Class dataClass;
		for (String name : nameArray) {
			type = attr.getType(name);
			if (attr.getUserVisible(name) && (type != CyAttributes.TYPE_UNDEFINED) && (type != CyAttributes.TYPE_COMPLEX)) {
				attrEditorNames.add(name);
			}
			dataClass = CyAttributesUtils.getClass(name, attr);
			if ((dataClass == Integer.class) || (dataClass == Double.class) || (dataClass == Float.class)) {
				numericalAttrEditorNames.add(name);
			}
		}
	}
}
