package org.cytoscape.view.vizmap.gui.editors;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableCellRenderer;

import org.cytoscape.view.model.VisualProperty;

public interface EditorFactory {

	/**
	 * DOCUMENT ME!
	 */
	public static final String EDITOR_WINDOW_OPENED = "EDITOR_WINDOW_OPENED";
	/**
	 * Tell vizMapper main which editor is disabled/enabled.
	 */
	public static final String EDITOR_WINDOW_CLOSED = "EDITOR_WINDOW_CLOSED";

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ed DOCUMENT ME!
	 * @param props DOCUMENT ME!
	 */
	public void addEditorDisplayer(EditorDisplayer ed, Map properties);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ed DOCUMENT ME!
	 * @param props DOCUMENT ME!
	 */
	public void removeEditorDisplayer(EditorDisplayer ed, Map properties);

	/**
	 * Display discrete value editor for this visual property.
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public Object showDiscreteEditor(Component parentComponent,
			VisualProperty type) throws Exception;

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
	public Object showContinuousEditor(Component parentComponent,
			VisualProperty type) throws Exception;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<PropertyEditor> getCellEditors();

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyEditor getDiscreteCellEditor(VisualProperty type);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableCellRenderer getDiscreteCellRenderer(VisualProperty type);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public PropertyEditor getContinuousCellEditor(VisualProperty type);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 * @param w DOCUMENT ME!
	 * @param h DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableCellRenderer getContinuousCellRenderer(VisualProperty type,
			int w, int h);

	public PropertyEditor getDefaultComboBoxEditor(String editorName);

}