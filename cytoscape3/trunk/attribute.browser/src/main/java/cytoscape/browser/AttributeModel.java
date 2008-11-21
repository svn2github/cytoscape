/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.browser;

import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyRowUtils;
import org.cytoscape.attributes.MultiHashMapDefinitionListener;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


/**
 *
 */
public class AttributeModel implements ListModel, ComboBoxModel, MultiHashMapDefinitionListener {
	private Vector listeners = new Vector();
	private final CyAttributes attributes;
	private List<String> attributeNames;
	private Object selection = null;

	/**
	 * Creates a new AttributeModel object.
	 *
	 * @param data  DOCUMENT ME!
	 */
	public AttributeModel(final CyAttributes data) {
		this.attributes = data;
		data.getMultiHashMapDefinition().addDataDefinitionListener(this);
		sortAtttributes();
	}

	/**
	 *  DOCUMENT ME!
	 */
    public void sortAtttributes() {
        attributeNames = new ArrayList<String>();
        for(String attrName: CyAttributesUtils.getVisibleAttributeNames(attributes)) {
                if(attributes.getUserVisible(attrName)) {
                        attributeNames.add(attrName);
                }
        }
        Collections.sort(attributeNames);
        notifyListeners(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0,
                                          attributeNames.size()));
}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param i DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getElementAt(int i) {
		if (i > attributeNames.size())
			return null;

		return attributeNames.get(i);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSize() {
		return attributeNames.size();
	}

	// implements ComboBoxModel
	/**
	 *  DOCUMENT ME!
	 *
	 * @param anItem DOCUMENT ME!
	 */
	public void setSelectedItem(Object anItem) {
		selection = anItem;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getSelectedItem() {
		return selection;
	}

	// implements CyDataDefinitionListener
	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeName DOCUMENT ME!
	 */
	public void attributeDefined(String attributeName) {
		sortAtttributes();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeName DOCUMENT ME!
	 */
	public void attributeUndefined(String attributeName) {
		sortAtttributes();
	}

	// implements ListModel
	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
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
