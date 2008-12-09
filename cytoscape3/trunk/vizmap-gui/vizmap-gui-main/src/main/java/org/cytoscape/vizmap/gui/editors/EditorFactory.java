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
package org.cytoscape.vizmap.gui.editors;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.vizmap.VisualPropertyType;


/**
 *
 */
public class EditorFactory {
	private Set<EditorDisplayer> displayers;

	/**
	 * DOCUMENT ME!
	 */
	public static final String EDITOR_WINDOW_OPENED = "EDITOR_WINDOW_OPENED";

	/**
	 * Tell vizMapper main which editor is disabled/enabled.
	 */
	public static final String EDITOR_WINDOW_CLOSED = "EDITOR_WINDOW_CLOSED";

	/**
	 * Creates a new EditorFactory object.
	 */
	public EditorFactory() {
		displayers = new HashSet<EditorDisplayer>();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ed DOCUMENT ME!
	 * @param props DOCUMENT ME!
	 */
	public void addEditorDisplayer(EditorDisplayer ed, Map properties) {
		displayers.add(ed);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ed DOCUMENT ME!
	 * @param props DOCUMENT ME!
	 */
	public void removeEditorDisplayer(EditorDisplayer ed, Map properties) {
		displayers.remove(ed);
	}

	private EditorDisplayer findEditor(VisualPropertyType type, EditorDisplayer.Type edType) {
		final Class<?> dataType = type.getDataType();

		for (EditorDisplayer disp : displayers)
			if ((dataType == disp.getDataType()) && (edType == disp.getEditorType()))
				return disp;

		throw new NullPointerException("no editor displayer found for: " + type.toString());
	}

	/**
	 * Display discrete value editor for this visual property.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public Object showDiscreteEditor(Component parentComponent, VisualPropertyType type)
	    throws Exception {
		return findEditor(type, EditorDisplayer.Type.DISCRETE).showEditor(parentComponent, type);
	}

	/**
	 * Display continuous value editor.
	 *
	 * <p>
	 * Continuous editor always update mapping automatically, so there is no
	 * return value.
	 * </p>
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public Object showContinuousEditor(Component parentComponent, VisualPropertyType type)
	    throws Exception {
		return findEditor(type, EditorDisplayer.Type.CONTINUOUS).showEditor(parentComponent, type);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<PropertyEditor> getCellEditors() {
		List<PropertyEditor> ret = new ArrayList<PropertyEditor>();

		for (EditorDisplayer disp : displayers)
			ret.add(disp.getCellEditor());

		return ret;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyEditor getDiscreteCellEditor(VisualPropertyType type) {
		return findEditor(type, EditorDisplayer.Type.DISCRETE).getCellEditor();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableCellRenderer getDiscreteCellRenderer(VisualPropertyType type) {
		return findEditor(type, EditorDisplayer.Type.DISCRETE).getCellRenderer(type, 0, 0);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyEditor getContinuousCellEditor(VisualPropertyType type) {
		return findEditor(type, EditorDisplayer.Type.CONTINUOUS).getCellEditor();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableCellRenderer getContinuousCellRenderer(VisualPropertyType type, int w, int h) {
		return findEditor(type, EditorDisplayer.Type.CONTINUOUS).getCellRenderer(type, w, h);
	}
}
