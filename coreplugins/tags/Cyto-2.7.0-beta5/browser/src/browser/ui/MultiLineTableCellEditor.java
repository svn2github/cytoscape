
package browser.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.AbstractCellEditor;
import javax.swing.JTextArea;

import java.util.EventObject;

/**
 *
 */
public class MultiLineTableCellEditor extends AbstractCellEditor implements TableCellEditor,
                                                                            ActionListener {
	ResizableTextArea textArea;

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
	                                             int row, int column) {
		String text = (value != null) ? value.toString() : "";
		textArea.setTable(table);
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
