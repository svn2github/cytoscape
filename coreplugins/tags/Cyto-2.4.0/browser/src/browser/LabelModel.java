package browser;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import cytoscape.data.CyAttributes;
import exesto.AttributeTags;
import exesto.TagListener;

public class LabelModel implements ListModel, ComboBoxModel, TagListener {

	Vector listeners = new Vector();
	CyAttributes data;

	Vector tags;
	Object selection = null;

	public LabelModel(CyAttributes data) {
		this.data = data;
		AttributeTags.addTagListener(this);
		sortTags();
	}

	protected void sortTags() {

		tags = new Vector(AttributeTags.getTagNames(data));
		Collections.sort(tags);
		notifyListeners(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED,
				0, tags.size()));
	}

	// implements ListModel

	public Object getElementAt(int i) {
		if (i > tags.size())
			return null;

		return tags.get(i);
	}

	public int getSize() {
		return tags.size();
	}

	// implements ComboBoxModel

	public void setSelectedItem(Object anItem) {
		selection = anItem;
	}

	public Object getSelectedItem() {
		return selection;
	}

	// implements CyDataDefinitionListener

	public void tagStateChange() {
		sortTags();
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
