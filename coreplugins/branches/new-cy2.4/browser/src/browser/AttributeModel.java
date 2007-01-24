package browser;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.attr.MultiHashMapDefinitionListener;

public class AttributeModel implements ListModel, ComboBoxModel,
		MultiHashMapDefinitionListener {

	private Vector listeners = new Vector();
	private CyAttributes attributes;

	private List<String> attributeNames;
	private Object selection = null;

	public AttributeModel(final CyAttributes data) {
		this.attributes = data;
		data.getMultiHashMapDefinition().addDataDefinitionListener(this);
		sortAtttributes();
	}

	protected void sortAtttributes() {
		attributeNames = CyAttributesUtils.getVisibleAttributeNames(attributes);
		Collections.sort(attributeNames);
		notifyListeners(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
				0, attributeNames.size()));
	}

	// implements ListModel

	public Object getElementAt(int i) {
		if (i > attributeNames.size())
			return null;

		return attributeNames.get(i);
	}

	public int getSize() {
		return attributeNames.size();
	}

	// implements ComboBoxModel

	public void setSelectedItem(Object anItem) {
		selection = anItem;
	}

	public Object getSelectedItem() {
		return selection;
	}

	// implements CyDataDefinitionListener

	public void attributeDefined(String attributeName) {
		sortAtttributes();
	}

	public void attributeUndefined(String attributeName) {
		sortAtttributes();
	}

	// implements ListModel

	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public void notifyListeners(ListDataEvent e) {
		for (Iterator listenIt = listeners.iterator(); listenIt.hasNext();) {
			if (e.getType() == ListDataEvent.CONTENTS_CHANGED) {
				((ListDataListener) listenIt.next()).contentsChanged(e);
			} else if (e.getType() == ListDataEvent.INTERVAL_ADDED) {
				((ListDataListener) listenIt.next()).intervalAdded(e);
			} else if (e.getType() == ListDataEvent.INTERVAL_REMOVED) {
				((ListDataListener) listenIt.next()).intervalRemoved(e);
			}
		}
	}

}
