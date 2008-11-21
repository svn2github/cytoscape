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
package cytoscape.browser;

import cytoscape.Cytoscape;
import cytoscape.browser.ui.AttributeBrowserToolBar;
import cytoscape.browser.ui.CyAttributeBrowserTable;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyRowUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * DataTable class constructs all Panels for the browser.<br>
 * One DataTable contains the table of values and the toolbar above the table
 * within a jpanel. This panel is then added to CytoPanel2. Tabbed browsing
 * between attribute tables is handled by CytoPanel2.
 *
 * @author kono xmas
 *
 * Combine CytoPanel_2 and CytoPanel_3 Peng-Liang wang 9/12/2006 Move advanced
 * panel to AttrMod Dialog Peng-Liang wang 9/28/2006
 *
 */
public class AttributeBrowser implements TableColumnModelListener {
	
    
    protected static Object pcsO = new Object();
    protected static PropertyChangeSupport pcs = new SwingPropertyChangeSupport(pcsO);
    
    
    public static PropertyChangeSupport getPropertyChangeSupport() {
            return pcs;
    }
    
    public static void firePropertyChange(String property_type, Object old_value, Object new_value) {
            final PropertyChangeEvent e = new PropertyChangeEvent(pcsO, property_type, old_value, new_value);
            getPropertyChangeSupport().firePropertyChange(e);
    }
	
	/**
	 *
	 */
	public static final String ID = "ID";

	/**
	 * 
	 */
	public static final String NETWORK_METADATA = "Network Metadata";

	/**
	 * 
	 */
	public static final Color DEFAULT_EDGE_COLOR = Color.RED;

	/**
	 * 
	 */
	public static final Color DEFAULT_NODE_COLOR = Color.YELLOW;

	// Each Attribute Browser operates on one CytoscapeData object, and on
	// either Nodes or Edges.
	private final CyAttributes attrData;

	// Type of attribute
	private final DataObjectType panelType;

	// Main panel for put everything
	private JPanel mainPanel;

	// Table object and its model. 
	private final DataTableModel tableModel;
	private final CyAttributeBrowserTable attributeTable;

	// Toolbar panel on the top of browser
	private final AttributeBrowserToolBar attributeBrowserToolBar;

	// Will be used to keep track of column order.
	private List<String> orderedColumn;

	// Index number for the panels
	int attributePanelIndex;
	int modPanelIndex;
	int tableIndex;
	int browserIndex;

    public final static String RESTORE_COLUMN = "RESTORE_COLUMN";
    public final static String CLEAR_INTERNAL_SELECTION = "CLEAR_INTERNAL_SELECTION";

	/**
	 * Browser object is one per attribute type.
	 * Users should access it through this.
	 *
	 * @param type
	 */
	public static AttributeBrowser getBrowser(final DataObjectType type) {
		return new AttributeBrowser(type);
	}

	protected void addMenuItem(Component newItem) {
		attributeTable.getContextMenu().add(newItem);
	}

	/**
	 * Creates a new DataTable object.
	 *
	 * @param attrData
	 *            DOCUMENT ME!
	 * @param tableObjectType
	 *            DOCUMENT ME!
	 */
	private AttributeBrowser(final DataObjectType panelType) {

		// set up CytoscapeData Object and GraphObject Type
		this.attrData = panelType.getAssociatedAttribute();
		this.panelType = panelType;
		this.orderedColumn = new ArrayList<String>();

		// Create table model.
		tableModel = makeModel();

		// Toolbar for selecting attributes and create new attribute.
		attributeBrowserToolBar = new AttributeBrowserToolBar(tableModel,
		                                                      new AttributeModel(attrData), orderedColumn,
		                                                      panelType);

		// the attribute table display: CytoPanel 2, horizontal SOUTH panel.
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(400, 200));
		mainPanel.setBorder(null);

		attributeTable = new CyAttributeBrowserTable(tableModel, panelType);
		attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	
		// If this is a network attribute browser, do not allow to swap
		// column.
		if (panelType == DataObjectType.NETWORK) {
			attributeTable.getTableHeader().setReorderingAllowed(false);
		}

		attributeTable.getColumnModel().addColumnModelListener(this);
        JScrollPane tp = new JScrollPane(attributeTable);
        tp.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                        getPropertyChangeSupport().firePropertyChange(AttributeBrowser.CLEAR_INTERNAL_SELECTION, null, panelType);
                }
        });
        mainPanel.setName(panelType.getDislayName() + "AttributeBrowser");
        mainPanel.add(tp, BorderLayout.CENTER);		
		mainPanel.add(attributeBrowserToolBar, BorderLayout.NORTH);

		// Add main browser panel to CytoPanel 2 (SOUTH)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		         .add(panelType.getDislayName() + " Attribute Browser", mainPanel);

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.DOCK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public CyAttributeBrowserTable getattributeTable() {
		return attributeTable;
	}

	static class Listener implements CytoPanelListener {
		int WEST;
		int SOUTH;
		int EAST;
		int myIndex;

		Listener(int w, int s, int e, int my) {
			WEST = w;
			SOUTH = s;
			EAST = e;
			myIndex = my;
		}

		public void onComponentAdded(int count) {
		}

		public void onComponentRemoved(int count) {
		}

		public void onComponentSelected(int componentIndex) {
			if (componentIndex == myIndex) {
				if (WEST != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setSelectedIndex(WEST);
				}

				if (SOUTH != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setSelectedIndex(SOUTH);
				}

				if (EAST != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setSelectedIndex(EAST);
				}
			}
		}

		public void onStateChange(CytoPanelState newState) {
		}
	}

	
	private DataTableModel makeModel() {
		final List<String> attributeNames = CyAttributesUtils.getVisibleAttributeNames(attrData);
		final List<GraphObject> graphObjects = getSelectedGraphObjects();
		final DataTableModel model = new DataTableModel(graphObjects, attributeNames, panelType);

		return model;
	}

	private List<GraphObject> getSelectedGraphObjects() {
		if (panelType.equals(DataObjectType.NODES)) {
			return new ArrayList<GraphObject>(Cytoscape.getCurrentNetwork().getSelectedNodes());
		} else if (panelType.equals(DataObjectType.EDGES)){
			return new ArrayList<GraphObject>(Cytoscape.getCurrentNetwork().getSelectedEdges());
		}
		
		return null;
	}

	/**
	 *  Return selected items.
	 *
	 * @return  DOCUMENT ME!
	 */
	public List<String> getSelectedAttributes() {
		orderedColumn.clear();
		for(int i=0; i<attributeTable.getColumnModel().getColumnCount(); i++) {
			orderedColumn.add(attributeTable.getColumnModel().getColumn(i).getHeaderValue().toString());
		}
		return orderedColumn;
	}
	
	public void setSelectedAttributes(List<String> selected) {
		orderedColumn = selected;
		attributeBrowserToolBar.updateList(selected);
		tableModel.setTableData(null, orderedColumn);
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param newModel DOCUMENT ME!
	 */
	public void restoreColumnModel(TableColumnModel newModel) {
		attributeTable.setColumnModel(newModel);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public TableColumnModel getColumnModel() {
		return attributeTable.getColumnModel();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void columnAdded(TableColumnModelEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void columnMarginChanged(ChangeEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void columnMoved(TableColumnModelEvent e) {
		// Ignore if same
		if (e.getFromIndex() == e.getToIndex())
			return;

		final int columnCount = attributeTable.getColumnCount();

		System.out.print("Ordered: " + e.getFromIndex() + " to " + e.getToIndex());
		orderedColumn.clear();

		for (int i = 0; i < columnCount; i++) {
			System.out.print("[" + attributeTable.getColumnName(i) + "] ");
			orderedColumn.add(attributeTable.getColumnName(i));
		}

		System.out.println("");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void columnRemoved(TableColumnModelEvent e) {
		// TODO Auto-generated method stub
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void columnSelectionChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
	}
}
