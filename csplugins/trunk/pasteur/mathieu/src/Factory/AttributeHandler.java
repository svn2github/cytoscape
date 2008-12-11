package Factory;
/*
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesFactory;


private CyAttributes attrs = null;

private JComponent getAttributePanel(CyAttributes attributes) {
	// Check and see if the user passed the attributes list in the constructor
	if (attributes == null) {
		// Nope, get it
		if (type == NODEATTRIBUTE)
			attributes = CyAttributesFactory.getCyAttributes("node");
		else if (type == EDGEATTRIBUTE)
			attributes = CyAttributesFactory.getCyAttributes("edge");
	}

	final List<String> list = new ArrayList<String>();

	// See if we have any initial attributes (mapped into lowerBound)
	if (lowerBound != null) {
		list.addAll((List) lowerBound);
	}

	if (attributes != null) {
		final String[] attList = attributes.getAttributeNames();

		for (int i = 0; i < attList.length; i++) {
			// Is this attribute user visible?
			if (!attributes.getUserVisible(attList[i]))
				continue;

			byte type = attributes.getType(attList[i]);

			if (((flag & NUMERICATTRIBUTE) == 0)
			    || ((type == CyAttributes.TYPE_FLOATING) 
			    || (type == CyAttributes.TYPE_INTEGER))) {
				list.add(attList[i]);
			}
		}
	}

	if ((flag & MULTISELECT) != 0) {
		// Set our current value as selected
		JList jList = new JList(list.toArray());
		jList.setSelectedIndices(getSelectedValues(list, decodeArray((String)value)));
		return jList;
	} else {
		// Set our current value as selected
		JComboBox box = new JComboBox(list.toArray());
		box.setSelectedItem((String) value);
		return box;
	}
}












} else if (type == NODEATTRIBUTE || type == EDGEATTRIBUTE) {
	inputField = getAttributePanel(attrs);
	tunablePanel.add(inputField, BorderLayout.LINE_END);


*/