
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

package cytoscape.filter.cytoscape;

import cytoscape.Cytoscape;
import org.cytoscape.model.CyRow;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;


/**
 *
 */
public abstract class AttributeComboBoxModel implements ComboBoxModel, PropertyChangeListener {
	protected Object selectedObject;
	protected Vector attributeList;
	protected Class[] type2Class = new Class[] {
	                                   Boolean.class, Double.class, Integer.class, String.class
	                               };

	protected AttributeComboBoxModel() {
		attributeList = new Vector();
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}

	/**
	    * This function will map from a type in the CyAttributes class to an actual
	    * class instance
	    */
	protected Class type2Class(int type) {
		switch (type) {
			case CyAttributes.TYPE_BOOLEAN:
				return Boolean.class;

			case CyAttributes.TYPE_COMPLEX:
				return Object.class;

			case CyAttributes.TYPE_FLOATING:
				return Double.class;

			case CyAttributes.TYPE_INTEGER:
				return Integer.class;

			case CyAttributes.TYPE_SIMPLE_LIST:
				return java.util.List.class;

			case CyAttributes.TYPE_SIMPLE_MAP:
				return java.util.Map.class;

			case CyAttributes.TYPE_STRING:
				return String.class;

			case CyAttributes.TYPE_UNDEFINED:
				return Object.class;

			default:
				return Object.class;
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void notifyListeners() {
		for (Iterator listenIt = listeners.iterator(); listenIt.hasNext();) {
			((ListDataListener) listenIt.next()).contentsChanged(new ListDataEvent(this,
			                                                                       ListDataEvent.CONTENTS_CHANGED,
			                                                                       0,
			                                                                       attributeList
			                                                                                                    .size()));
		}
	}

	//implements PropertyChange
	/**
	 *  DOCUMENT ME!
	 *
	 * @param pce DOCUMENT ME!
	 */
	public abstract void propertyChange(PropertyChangeEvent pce);

	//implements ListModel
	Vector listeners = new Vector();

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
	 * @param index DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getElementAt(int index) {
		return attributeList.elementAt(index);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSize() {
		return attributeList.size();
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
	 * @param item DOCUMENT ME!
	 */
	public void setSelectedItem(Object item) {
		selectedObject = item;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getSelectedItem() {
		return selectedObject;
	}
}


class NodeAttributeComboBoxModel extends AttributeComboBoxModel {
	Class attributeClass;
	CyAttributes nodeAttributes;

	/**
	 * Creates a new NodeAttributeComboBoxModel object.
	 *
	 * @param attributeClass  DOCUMENT ME!
	 */
	public NodeAttributeComboBoxModel(Class attributeClass) {
		super();
		nodeAttributes = Cytoscape.getNodeAttributes();
		this.attributeClass = attributeClass;
		updateAttributes();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pce DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent pce) {
		updateAttributes();
	}

	protected void updateAttributes() {
		/*byte type;
		if ( attributeClass == Double.class )
		  type = CyAttributes.TYPE_FLOATING;
		else if ( attributeClass == Integer.class )
		  type = CyAttributes.TYPE_INTEGER;
		else if ( attributeClass == String.class )
		  type = CyAttributes.TYPE_STRING;
		else
		  return;
		                    */
		String[] na = Cytoscape.getNodeAttributes().getAttributeNames();
		attributeList = new Vector();

		for (int idx = 0; idx < na.length; idx++) {
			if (attributeClass.isAssignableFrom(type2Class(nodeAttributes.getType(na[idx])))) {
				attributeList.add(na[idx]);
			}

			notifyListeners();
		}
	}
}


class EdgeAttributeComboBoxModel extends AttributeComboBoxModel {
	Class attributeClass;
	CyAttributes edgeAttributes;

	/**
	 * Creates a new EdgeAttributeComboBoxModel object.
	 *
	 * @param attributeClass  DOCUMENT ME!
	 */
	public EdgeAttributeComboBoxModel(Class attributeClass) {
		super();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		this.attributeClass = attributeClass;
		updateAttributes();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pce DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent pce) {
		updateAttributes();
	}

	protected void updateAttributes() {
		/*
		                * This part isn't really necessary  anymore
		                * now that we have the class lookup
		                * table
		            byte type;
		if ( attributeClass == String.class )
		  type = CyAttributes.TYPE_STRING;
		else if ( attributeClass == Double.class )
		  type = CyAttributes.TYPE_FLOATING;
		else if ( attributeClass == Integer.class )
		  type = CyAttributes.TYPE_INTEGER;
		else
		  return;
		                    */
		String[] ea = Cytoscape.getEdgeAttributes().getAttributeNames();
		attributeList = new Vector();

		for (int idx = 0; idx < ea.length; idx++) {
			if (attributeClass.isAssignableFrom(type2Class(edgeAttributes.getType(ea[idx])))) {
				attributeList.add(ea[idx]);
			}

			notifyListeners();
		}
	}
}
