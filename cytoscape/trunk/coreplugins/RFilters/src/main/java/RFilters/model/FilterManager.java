
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

package filter.model;

import cern.colt.map.OpenIntObjectHashMap;

import filter.view.*;

import java.beans.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;


/**
 *
 */
public class FilterManager implements ListModel, PropertyChangeListener {
	protected static FilterManager DEFAULT_MANAGER;

	/**
	 * 
	 */
	public static String FILTER_EVENT = "FILTER_EVENT";
	protected Vector filterList;
	protected OpenIntObjectHashMap ID2Filter;
	protected HashMap Filter2ID;
	Object selectedItem;

	/**
	 *  PCS support
	 */
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	/**
	 * Returns the Default Filter Manager
	 */
	public static FilterManager defaultManager() {
		if (DEFAULT_MANAGER == null) {
			DEFAULT_MANAGER = new FilterManager();
		}

		return DEFAULT_MANAGER;
	}

	private FilterManager() {
		filterList = new Vector();
		ID2Filter = new OpenIntObjectHashMap();
		Filter2ID = new HashMap();

		// Add the Select All filter before anything else.
		// This is needed by the NodeTopologyFilter
		addFilter(new filter.cytoscape.SelectAllFilter());
	}

	//----------------------------------------//
	// PCS Methods

	/**
	 * PCS Support
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	/**
	 *Create a new combobox model
	 *that other classes can use to display
	 *the list of filters in a combobox. Note that we
	 *want a separate model for each combobox, because the data
	 *model keeps track of the selected item. The returned combobox will
	 * basically be a wrapper around the list model which they all will share
	 */
	public ComboBoxModel getComboBoxModel() {
		return new ComboBoxModel() {
				Object selectedItem;

				//implements ListModel
				Vector listeners = new Vector();

				public void addListDataListener(ListDataListener l) {
					listeners.add(l);
				}

				public void removeListDataListener(ListDataListener l) {
					listeners.remove(l);
				}

				public Object getSelectedItem() {
					return selectedItem;
				}

				public void setSelectedItem(java.lang.Object anItem) {
					selectedItem = anItem;
				}

				public Object getElementAt(int index) {
					return FilterManager.this.getElementAt(index);
				}

				public int getSize() {
					return FilterManager.this.getSize();
				}
			};
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void fireFilterEvent() {
		pcs.firePropertyChange(FILTER_EVENT, null, null);
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

	/**
	 * Add a filter and assign it the given unique ID
	 */
	public void addFilter(Filter filter, int ID) {
		if (ID < 0) {
			throw new IllegalArgumentException("ID must be greater than 0");
		}

		if (ID2Filter.containsKey(ID)) {
			throw new IllegalArgumentException("ID map already contains that ID");
		}

		ID2Filter.put(ID, filter);
		Filter2ID.put(filter, new Integer(ID));
		filterList.add(filter);
		//System.out.println( "Filter list added: "+filter );
		filter.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		notifyListeners(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, filterList.size(),
		                                  filterList.size()));
		fireFilterEvent();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param filter DOCUMENT ME!
	 */
	public void addFilter(Filter filter) {
		//let's hope we don't have many filters, otherwise
		int ID = 0;

		while (ID2Filter.containsKey(ID)) {
			ID++;
		}

		addFilter(filter, ID);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param filter DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean removeFilter(Filter filter) {
		int index = filterList.indexOf(filter);

		// If it's 0, assume that it's the SelectAllFilter, which we don't want to delete.
		if (index <= 0) {
			return false;
		}

		int ID = ((Integer) Filter2ID.get(filter)).intValue();
		ID2Filter.removeKey(ID);
		Filter2ID.remove(filter);
		filterList.remove(filter);
		notifyListeners(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index));
		filter.getSwingPropertyChangeSupport().removePropertyChangeListener(this);
		fireFilterEvent();

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getFilterID(Filter f) {
		if (f == null) {
			return -1;
		}

		return ((Integer) Filter2ID.get(f)).intValue();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ID DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Filter getFilter(int ID) {
		return (Filter) ID2Filter.get(ID);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator getFilters() {
		return filterList.iterator();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getFilterCount() {
		return filterList.size();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param item DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int indexOf(Object item) {
		return filterList.indexOf(item);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param desc DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Filter createFilterFromString(String desc) {
		String[] array = desc.split("\t");

		if (array[1].equals("class filter.cytoscape.StringPatternFilter")) {
			//System.out.println( "Found String Filter" );
			Filter new_filter = new filter.cytoscape.StringPatternFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue() );

			return new_filter;
		} else if (array[1].equals("class filter.cytoscape.NumericAttributeFilter")) {
			//System.out.println( "Found Numeric Filter" );
			Filter new_filter = new filter.cytoscape.NumericAttributeFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue());

			return new_filter;
		} else if (array[1].equals("class filter.cytoscape.NodeInteractionFilter")) {
			Filter new_filter = new filter.cytoscape.NodeInteractionFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue());

			return new_filter;
		} else if (array[1].equals("class filter.cytoscape.EdgeInteractionFilter")) {
			Filter new_filter = new filter.cytoscape.EdgeInteractionFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue());

			return new_filter;
		} else if (array[1].equals("class filter.cytoscape.NodeTopologyFilter")) {
			Filter new_filter = new filter.cytoscape.NodeTopologyFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue());

			return new_filter;
		} else if (array[1].equals("class filter.cytoscape.BooleanMetaFilter")) {
			Filter new_filter = new filter.cytoscape.BooleanMetaFilter(array[2]);
			addFilter(new_filter); //, (new Integer(array[0])).intValue());

			return new_filter;
		} // end of if ()

		return null;
	}

	//implements PropertyChange
	/**
	 *  DOCUMENT ME!
	 *
	 * @param pce DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent pce) {
		//the only thing we're really listening to here shoudl be the filters,
		//we check which one is sending the event, and then let the list know that
		//the contents of that filter have possibly changed
		Filter filter = (Filter) pce.getSource();
		int index = filterList.indexOf(filter);

		if (index > -1) {
			notifyListeners(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0,
			                                  filterList.size()));
			fireFilterEvent();
		}
	}

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
		return filterList.elementAt(index);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSize() {
		return getFilterCount();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
}
