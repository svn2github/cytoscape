package org.cytoscape.browser.internal;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.service.util.CyServiceRegistrar;


public class BrowserTable extends JTable {
	private static final TableCellRenderer cellRenderer = new BrowserTableCellRenderer();
	private PropertyChangeListener editorRemover = null;

	public BrowserTable() {
		setCellSelectionEnabled(true);
		setDefaultEditor(Object.class, new MultiLineTableCellEditor());
	}

	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		return cellRenderer;
	}

	@Override
	public boolean isCellEditable(final int row, final int column) {
		return column != 0;
	}

	@Override
	public boolean editCellAt(int row, int column, EventObject e) {
		if ((cellEditor != null) && !cellEditor.stopCellEditing()) {
			return false;
		}

		if ((row < 0) || (row >= getRowCount()) || (column < 0) || (column >= getColumnCount())) {
			return false;
		}

		if (!isCellEditable(row, column))
			return false;

		if (editorRemover == null) {
			KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			editorRemover = new CellEditorRemover(fm);
			fm.addPropertyChangeListener("permanentFocusOwner", editorRemover);
		}

		TableCellEditor editor = getCellEditor(row, column);

		if ((editor != null) && editor.isCellEditable(e)) {

			// Do this first so that the bounds of the JTextArea editor
			// will be correct.
			setEditingRow(row);
			setEditingColumn(column);
			setCellEditor(editor);
			editor.addCellEditorListener(this);

			editorComp = prepareEditor(editor, row, column);

			if (editorComp == null) {
				removeEditor();
				return false;
			}

			Rectangle cellRect = getCellRect(row, column, false);

			if (editor instanceof MultiLineTableCellEditor) {
				Dimension prefSize = editorComp.getPreferredSize();
				((JComponent) editorComp).putClientProperty(MultiLineTableCellEditor.UPDATE_BOUNDS,
				                                            Boolean.TRUE);
				editorComp.setBounds(cellRect.x, cellRect.y,
				                     Math.max(cellRect.width, prefSize.width),
				                     Math.max(cellRect.height, prefSize.height));
				((JComponent) editorComp).putClientProperty(MultiLineTableCellEditor.UPDATE_BOUNDS,
				                                            Boolean.FALSE);
			} else
				editorComp.setBounds(cellRect);

			add(editorComp);
			editorComp.validate();

			return true;
		}

		return false;
	}

	private class CellEditorRemover implements PropertyChangeListener {
		private final KeyboardFocusManager focusManager;

		public CellEditorRemover(final KeyboardFocusManager fm) {
			this.focusManager = fm;
		}

		public void propertyChange(PropertyChangeEvent ev) {
			if (!isEditing() || (getClientProperty("terminateEditOnFocusLost") != Boolean.TRUE)) {
				return;
			}

			Component c = focusManager.getPermanentFocusOwner();

			while (c != null) {
				if (c == BrowserTable.this) {
					// focus remains inside the table
					return;
				} else if (c instanceof Window) {
					if (c == SwingUtilities.getRoot(BrowserTable.this)) {
						if (!getCellEditor().stopCellEditing()) {
							getCellEditor().cancelCellEditing();
						}
					}

					break;
				}

				c = c.getParent();
			}
		}
	}
}

