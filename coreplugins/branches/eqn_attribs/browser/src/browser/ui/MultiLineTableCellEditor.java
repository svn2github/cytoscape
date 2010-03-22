/*
 Copyright (c) 2006, 2007, 2010 The Cytoscape Consortium (www.cytoscape.org)

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
package browser.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Dimension;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import java.util.EventObject;

import browser.ValueAndEquation;


/**
 *
 */
public class MultiLineTableCellEditor extends AbstractCellEditor implements TableCellEditor,
                                                                            ActionListener
{
	private ResizableTextArea textArea;

	/**
	 * Creates a new MultiLineTableCellEditor object.
	 */
	public MultiLineTableCellEditor() {
		textArea = new ResizableTextArea();
		textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getCellEditorValue() {
		return textArea.getText();
	}

	protected int clickCountToStart = 2;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getClickCountToStart() {
		return clickCountToStart;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param clickCountToStart DOCUMENT ME!
	 */
	public void setClickCountToStart(int clickCountToStart) {
		this.clickCountToStart = clickCountToStart;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isCellEditable(EventObject e) {
		return !(e instanceof MouseEvent)
		       || (((MouseEvent) e).getClickCount() >= clickCountToStart);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ae DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent ae) {
		stopCellEditing();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param table DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 * @param isSelected DOCUMENT ME!
	 * @param row DOCUMENT ME!
	 * @param column DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
	                                             int row, int column)
	{
		textArea.setTable(table);

		final ValueAndEquation valAndEq = (ValueAndEquation)value;
		final String text;
		if (valAndEq == null)
			text = "";
		else {
			final String equation = valAndEq.getEquation();
			if (isSelected && equation != null)
				text = equation;
			else
				text = valAndEq.getValue().toString();
		}
		textArea.setText(text);

		return textArea;
	}

	/**
	 * 
	 */
	public static final String UPDATE_BOUNDS = "UpdateBounds";

	class ResizableTextArea extends JTextArea {
		JTable table;

		public void setTable(JTable t) {
			table = t;
		}

		public void setText(String text) {
			super.setText(text);
			updateBounds();
		}

		public void setBounds(int x, int y, int width, int height) {
			if (Boolean.TRUE.equals(getClientProperty(UPDATE_BOUNDS)))
				super.setBounds(x, y, width, height);
		}

		public void addNotify() {
			super.addNotify();
			getDocument().addDocumentListener(listener);
		}

		public void removeNotify() {
			getDocument().removeDocumentListener(listener);
			super.removeNotify();
		}

		DocumentListener listener = new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateBounds();
			}

			public void removeUpdate(DocumentEvent e) {
				updateBounds();
			}

			public void changedUpdate(DocumentEvent e) {
				updateBounds();
			}
		};

		private void updateBounds() {
			if ( table == null ) {
				System.out.println("table is null");
				return;
			}
				
			if (table.isEditing()) {
				Rectangle cellRect = table.getCellRect(table.getEditingRow(),
				                                       table.getEditingColumn(), false);
				Dimension prefSize = getPreferredSize();
				putClientProperty(UPDATE_BOUNDS, Boolean.TRUE);
				setBounds(getX(), getY(), Math.max(cellRect.width, prefSize.width),
				          Math.max(cellRect.height + prefSize.height, prefSize.height));
				putClientProperty(UPDATE_BOUNDS, Boolean.FALSE);
				validate();
			} 
		}
	}
}
