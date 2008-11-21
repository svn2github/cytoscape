
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

package cytoscape.filter.view;


import cytoscape.filter.model.Filter;
import cytoscape.filter.model.FilterManager;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;


/**
 * A FilterListPanel will
 * be able to search through its contents based on the Filter title using
 * wildcard search
 */
public class FilterListBox extends JPanel implements PropertyChangeListener, ListSelectionListener,
                                                     ListDataListener {
	JComboBox filterBox;

	/**
	 * 
	 */
	public static String FILTER_SELECTED = "FILTER_SELECTED";

	/**
	 * 
	 */
	public static String NO_SELECTION = "NO_SELECTION";
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	/**
	 * Creates a new FilterListBox object.
	 */
	public FilterListBox() {
		super();
		initialize();
	}

	protected void initialize() {
		FilterManager.defaultManager().getSwingPropertyChangeSupport()
		             .addPropertyChangeListener(this);

		JPanel listPanel = new JPanel();
		//listPanel.setBorder( new TitledBorder( "Available Filters" ) );

		//FilterManager.defaultManager().addListDataListener(this);
		filterBox = new JComboBox(FilterManager.defaultManager().getComboBoxModel());
		//filterBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//filterBox.addListSelectionListener( this );

		//JScrollPane scroll = new JScrollPane( filterBox );
		//listPanel.add( scroll, BorderLayout.CENTER);
		//setLayout( new BorderLayout() );
		//add( listPanel, BorderLayout.CENTER );
		//scroll.setPreferredSize(new Dimension(250,100));
		add(filterBox);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	protected void fireFilterSelected() {
		pcs.firePropertyChange(FILTER_SELECTED, null, null);
	}

	protected void handleEvent(EventObject e) {
		if (filterBox.getSelectedItem() == null) {
			pcs.firePropertyChange(NO_SELECTION, null, null);
		} else {
			pcs.firePropertyChange(FILTER_SELECTED, null, null);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void valueChanged(ListSelectionEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void contentsChanged(ListDataEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void intervalAdded(ListDataEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void intervalRemoved(ListDataEvent e) {
		handleEvent(e);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Filter getSelectedFilter() {
		return (Filter) filterBox.getSelectedItem();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		//updateLists();
	}
}
