/*
 * $Archive: SourceJammer$
 * $FileName: JSortTable.java$
 * $FileID: 3984$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2007-07-11 17:47:31 -0700 (水, 11 7 2007) $
 * $Comment: $
 *
 * $KeyWordsOff: $
 */

/*
 =====================================================================

 JSortTable.java

 Created by Claude Duguay
 Copyright (c) 2002

 =====================================================================
 */
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
 * Cell renderer for preview table.<br>
 * Coloring function is added. This will sync node color and cell colors.<br>
 *
 * @version 0.6
 * @since Cytoscape 2.3
 *
 * @author kono
 *
 */
class BrowserTableCellRenderer extends JLabel implements TableCellRenderer {
	private static final String HTML_BEG = "<html><body topmargin=\"5\" leftmargin=\"0\" marginheight=\"5\" marginwidth=\"5\" "
	                                       + "bgcolor=\"#ffffff\" text=\"#595959\" link=\"#0000ff\" vlink=\"#800080\" alink=\"#ff0000\">";
	private static final String HTML_STYLE = "<div style=\"width: 200px; background-color: #ffffff; padding: 3px;\"> ";

	// Define fonts & colors for the cells
	private Font labelFont = new Font("Sans-serif", Font.BOLD, 12);
	private Font normalFont = new Font("Sans-serif", Font.PLAIN, 12);
	private final Color metadataBackground = new Color(255, 210, 255);
	private static final Color NON_EDITABLE_COLOR = new Color(235, 235, 235, 100);
	private static final Color SELECTED_CELL_COLOR = new Color(0, 100, 255, 40);
	private static final Color SELECTED_LABEL_COLOR = Color.black.brighter();
	private DataObjectType type = DataObjectType.NODES;
	private boolean coloring;
	private Object vl;

	/**
	 * Creates a new BrowserTableCellRenderer object.
	 *
	 * @param coloring  DOCUMENT ME!
	 * @param type  DOCUMENT ME!
	 */
	public BrowserTableCellRenderer(boolean coloring, DataObjectType type) {
		super();
		this.type = type;
		this.coloring = coloring;
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param table DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 * @param isSelected DOCUMENT ME!
	 * @param hasFocus DOCUMENT ME!
	 * @param row DOCUMENT ME!
	 * @param column DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
	                                               boolean hasFocus, int row, int column) {
		vl = value;

		final String colName = table.getColumnName(column);

		// First, set values
		setHorizontalAlignment(JLabel.LEFT);
		setText((value == null) ? "" : value.toString());

		if (value != null) {
			// Set HTML style tooltip
			setToolTipText(getFormattedToolTipText(colName, value));
		} else {
			setToolTipText(null);
		}

		// If selected, return
		if (isSelected) {
			setFont(labelFont);
			setForeground(SELECTED_LABEL_COLOR);
			setBackground(SELECTED_CELL_COLOR);

			return this;
		}

		// set default colorings
		setForeground(table.getForeground());
		setFont(normalFont);
		setBackground(table.getBackground());

		final CyAttributes data = type.getAssociatedAttribute();

		if (data == null)
			return this;

		// check for non-editable columns
		if (data.getUserEditable(colName) == false) {
			setBackground(NON_EDITABLE_COLOR);
		}

		// If ID, return default.
		if (colName.equals(AttributeBrowser.ID)) {
			setFont(labelFont);
			setBackground(NON_EDITABLE_COLOR);
		}

		// handle special NETWORK coloring
		if ((type == NETWORK) && (value != null)) {
			if (colName.equals("Network Attribute Name") && !value.equals("Network Metadata")) {
				setFont(labelFont);
				setBackground(NON_EDITABLE_COLOR);
			} else if (value.equals("Network Metadata")) {
				setBackground(metadataBackground);
				setFont(labelFont);
			}
		}

		// if we're not coloring the ID column we're done
		if ((coloring == false) || !colName.equals(AttributeBrowser.ID))
			return this;

		// handle colors for the the ID column
		CyNetworkView netview = Cytoscape.getCurrentNetworkView();

		if (type == NODES) {
			if (netview != Cytoscape.getNullNetworkView()) {
				NodeView nodeView = netview.getNodeView(Cytoscape.getCyNode((String) table.getValueAt(row, column)));

				if (nodeView != null) {
					Color nodeColor = (Color) nodeView.getUnselectedPaint();
					setBackground(nodeColor);
				}
			}
		} else if (type == EDGES) {
			if (netview != Cytoscape.getNullNetworkView()) {
				final String edgeName = (String) table.getValueAt(row, column);
				final EdgeView edgeView = netview.getEdgeView(((CyAttributeBrowserTable) table).getEdge(edgeName));

				if (edgeView != null) {
					Color edgeColor = (Color) edgeView.getUnselectedPaint();
					setBackground(edgeColor);
				}
			}
		}

		return this;
	}

	/**
	 * Returns organized & readable tooltip text.
	 * @param value
	 * @return
	 */
	private String getFormattedToolTipText(final String colName, final Object value) {
		StringBuilder html = new StringBuilder();

		html.append(HTML_BEG + "<strong text=\"#4169E1\" >" + colName + "</strong><br><hr>"
		            + HTML_STYLE);

		if ((value instanceof List == false) && (value instanceof Map == false)) {
			html.append(value.toString());
		} else if (value instanceof List) {
			html.append("<ul leftmargin=\"0\">");

			for (Object item : (List<Object>) value) {
				html.append("<li type=\"square\">" + item.toString() + "</li>");
			}

			html.append("</ul>");
		}

		html.append("</div></body></html>");

		return html.toString();
	}
}


