
package browser.ui;

import static browser.DataObjectType.EDGES;
import static browser.DataObjectType.NETWORK;
import static browser.DataObjectType.NODES;
import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import browser.AttributeBrowser;
import browser.AttributeBrowserPlugin;
import browser.DataObjectType;
import browser.DataTableModel;
import browser.SortTableModel;
import browser.util.HyperLinkOut;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.data.Semantics;
import cytoscape.dialogs.NetworkMetaDataDialog;
import cytoscape.logger.CyLogger;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.util.OpenBrowser;
import cytoscape.util.swing.ColumnResizer;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;


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
