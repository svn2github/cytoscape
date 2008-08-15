
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
import cytoscape.filter.model.FilterEditorManager;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


/**
 * Provides a tabbed Interface for creating filters of all available
 * filter editors that have been provided.
 */
public class FilterEditorPanel extends JPanel implements PropertyChangeListener {
	/**
	 * 
	 */
	public FilterEditor nullEdit;

	/**
	 * 
	 */
	public static String ACTIVE_PANEL_CHANGED = "Active Panel Changed";
	JPanel currentEditor;
	JPanel defaultPanel;
	PropertyChangeSupport pcs;

	/**
	 * Creates a new FilterEditorPanel object.
	 */
	public FilterEditorPanel() {
		super();
		initialize();
		pcs = new PropertyChangeSupport(this);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void initialize() {
		defaultPanel = new DefaultPanel();
		currentEditor = defaultPanel;
		nullEdit = new NullFilterEditor();
		//Change the layout manager,to let the panel stretch to fill the available space
		setLayout(new BorderLayout());
		add(currentEditor);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcs;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param editor DOCUMENT ME!
	 */
	public void setActivePanel(JPanel editor) {
		remove(currentEditor);
		add(editor);
		validate();
		paint(getGraphics());
		currentEditor = editor;
		pcs.firePropertyChange(ACTIVE_PANEL_CHANGED, null, null);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == FilterListPanel.FILTER_SELECTED) {
			Filter f = ((FilterListPanel) e.getSource()).getSelectedFilter();
			FilterEditor editor = FilterEditorManager.defaultManager().getEditorForFilter(f);

			if (editor == null) {
				editor = nullEdit;
			}

			editor.editFilter(f);
			setActivePanel(editor);
		} else if (e.getPropertyName() == FilterListPanel.NO_SELECTION) {
			setActivePanel(defaultPanel);
		}
	}
}


class DefaultPanel extends JPanel {
	/**
	 * Creates a new DefaultPanel object.
	 */
	public DefaultPanel() {
		JTextArea text = new JTextArea();
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEditable(false);
		text.setText("There is no filter currently selected. To edit a filter, select it from the \"Available filters\" list. If the list is empty, you can create a new filter with the \"Create new filter\" button.");
		text.setColumns(25);
		text.setBackground(this.getBackground());
		setLayout(new BorderLayout());
		add(text, BorderLayout.CENTER);
	}
}
