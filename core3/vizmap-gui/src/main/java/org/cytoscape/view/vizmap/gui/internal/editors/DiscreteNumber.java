
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

package org.cytoscape.view.vizmap.gui.internal.editors;

import org.cytoscape.view.vizmap.gui.editors.EditorDisplayer;
import org.cytoscape.view.vizmap.gui.internal.editors.discrete.CyDoublePropertyEditor;
import org.cytoscape.viewmodel.VisualProperty;

import java.awt.Component;

import java.beans.PropertyEditor;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 *
 */
public class DiscreteNumber implements EditorDisplayer {
	private final CyDoublePropertyEditor numberCellEditor;
	private final DefaultTableCellRenderer numberCellRenderer;

	/**
	 * Creates a new DiscreteNumber object.
	 */
	public DiscreteNumber() {
		numberCellEditor = new CyDoublePropertyEditor(null);
		numberCellRenderer = new DefaultTableCellRenderer();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class<?> getDataType() {
		return Number.class;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public EditorDisplayer.Type getEditorType() {
		return EditorDisplayer.Type.DISCRETE;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parentComponent DOCUMENT ME!
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object showEditor(Component parentComponent, VisualProperty type) {
		return JOptionPane.showInputDialog(parentComponent, "Please enter a new numeric value:");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyEditor getCellEditor() {
		return numberCellEditor;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 * @param width DOCUMENT ME!
	 * @param height DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableCellRenderer getCellRenderer(VisualProperty type, int width, int height) {
		return numberCellRenderer;
	}
}
