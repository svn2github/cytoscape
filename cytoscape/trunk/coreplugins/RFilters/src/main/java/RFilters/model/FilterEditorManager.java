
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

import filter.view.*;

import java.util.*;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;


/**
 *
 */
public class FilterEditorManager {
	protected static FilterEditorManager DEFAULT_MANAGER;
	protected Vector editorList;
	protected HashMap class2Editor;

	/**
	 *  PCS support
	 */
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);

	/**
	 * Returns the Default Filter Manager
	 */
	public static FilterEditorManager defaultManager() {
		if (DEFAULT_MANAGER == null) {
			DEFAULT_MANAGER = new FilterEditorManager();
		}

		return DEFAULT_MANAGER;
	}

	private FilterEditorManager() {
		editorList = new Vector();
		class2Editor = new HashMap();
	}

	/**
	 * PCS Support
	 */
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	// Editor Methods
	/**
	 *  DOCUMENT ME!
	 *
	 * @param editor DOCUMENT ME!
	 */
	public void addEditor(FilterEditor editor) {
		editorList.add(editor);
		class2Editor.put(editor.getFilterClass(), editor);

		//    fireEditorEvent();
		//pcs.firePropertyChange( EDITOR_ADDED, null, editor );
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param editor DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean removeEditor(FilterEditor editor) {
		return editorList.remove(editor);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Iterator getEditors() {
		return editorList.iterator();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getEditorCount() {
		return editorList.size();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param f DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public FilterEditor getEditorForFilter(Filter f) {
		return (FilterEditor) class2Editor.get(f.getClass());
	}
}
