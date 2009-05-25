/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import cytoscape.Cytoscape;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;

import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.TableSorter;

public class CompoundTable extends JDialog implements ListSelectionListener,
                                                      SelectEventListener,
                                                      ActionListener {
	
	private List<Compound> compoundList;
	private Map<GraphObject,List<Integer>> rowMap;
	private ChemInfoTableModel tableModel;
	private	TableColumnModel columnModel;
	private	ListSelectionModel selectionModel;
	private	JTable table;
	private	TableSorter sorter;
	private	JTableHeader tableHeader;
	private CyNetwork network;
	private CyNetworkView networkView;
	private	boolean modifyingSelection = false;
	private CompoundTable thisDialog;
	private static int DEFAULT_IMAGE_SIZE=80;
	private boolean hasNodes = false;
	private boolean hasEdges = false;
	private	CyLogger logger = CyLogger.getLogger(CompoundTable.class);

	public CompoundTable (List<Compound> compoundList) {
		super(Cytoscape.getDesktop());
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		this.compoundList = compoundList;
		setTitle("2D Structure Table");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.rowMap = new HashMap();

		// Create the table
		initTable();

		// Listen for network selection events
		network.addSelectEventListener(this);

		pack();
		thisDialog = this;
		setVisible(true);
	}

	public void setCompounds(List<Compound> newList) {
		this.compoundList = newList;
		this.rowMap = new HashMap();
		tableModel.fireTableDataChanged();
	}

	private void initTable() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		MouseAdapter mouseAdapter = new MyMouseAdapter();

		tableModel = new ChemInfoTableModel();

		// Create our default columns
		tableModel.addColumn(0, new Column("ID", "", CyAttributes.TYPE_STRING));
		tableModel.addColumn(1, new Column(DescriptorType.ATTRIBUTE));
		tableModel.addColumn(2, new Column(DescriptorType.IDENTIFIER));
		tableModel.addColumn(3, new Column(DescriptorType.WEIGHT));
		tableModel.addColumn(4, new Column(DescriptorType.IMAGE));

		sorter = new TableSorter(tableModel);
		table = new JTable(sorter);
		tableHeader = table.getTableHeader();
		tableHeader.addMouseListener(mouseAdapter);
		sorter.setTableHeader(tableHeader);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setDefaultRenderer(Compound.class, new CompoundRenderer());
		table.setDefaultRenderer(String.class, new StringRenderer());
		table.setRowHeight(DEFAULT_IMAGE_SIZE);

		// Figure out all of our default column widths
		columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(100);
		columnModel.getColumn(1).setPreferredWidth(100);
		columnModel.getColumn(2).setPreferredWidth(200);
		columnModel.getColumn(3).setPreferredWidth(100);
		columnModel.getColumn(4).setPreferredWidth(DEFAULT_IMAGE_SIZE);

		// Add our mouse listener (specific for 2D image popup)
		table.addMouseListener(mouseAdapter);

		// Add our row selection listener
		selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(this);

		JScrollPane pane = new JScrollPane(table);
		pane.setPreferredSize(new Dimension(500+DEFAULT_IMAGE_SIZE+20,520));
		mainPanel.add(pane, BorderLayout.CENTER);

		// Now add our button-box
		JPanel buttonBox = new JPanel();
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		{
			JButton exportButton = new JButton("Export Table...");
			exportButton.addActionListener(this);
			exportButton.setActionCommand("export");
			// exportButton.setEnabled(false);
			buttonBox.add(exportButton);
		}
		{
			JButton printButton = new JButton("Print Table...");
			printButton.addActionListener(this);
			printButton.setActionCommand("print");
			// printButton.setEnabled(false);
			buttonBox.add(printButton);
		}
		{
			JButton closeButton = new JButton("Close");
			closeButton.addActionListener(this);
			closeButton.setActionCommand("close");
			buttonBox.add(closeButton);
		}
		mainPanel.add(buttonBox, BorderLayout.SOUTH);
		add(mainPanel);
	}


	/**
 	 * valueChanged is called when a user changes the selection in the table.
 	 *
 	 * @param e the ListSelectionEvent that tells us what was done.
 	 */
	public void valueChanged(ListSelectionEvent e) {
		if (modifyingSelection) return;
		if (e.getSource() == table.getSelectionModel()) {
			modifyingSelection = true;
			int[] rows = table.getSelectedRows();
			network.unselectAllNodes();
			network.unselectAllEdges();
			for (int i = 0; i < rows.length; i++) {
				Compound c = compoundList.get(sorter.modelIndex(rows[i]));
				GraphObject obj = c.getSource();
				if (obj instanceof CyNode) {
					network.setSelectedNodeState((CyNode)obj, true);
				} else {
					network.setSelectedEdgeState((CyEdge)obj, true);
				}
			}
			modifyingSelection = false;
		}
		networkView.updateView();
	}

	/**
	 * onSelectEvent is called when a user changes the selection
	 * in the network.
	 *
	 * @param event the network selection event
	 */
	public void onSelectEvent(SelectEvent event) {
		if (modifyingSelection) return;
		modifyingSelection = true;
		selectionModel.clearSelection();
		selectObjects(network.getSelectedNodes());
		selectObjects(network.getSelectedEdges());
		modifyingSelection = false;
	}

	private void selectObjects(Set<GraphObject>selectedObjects) {
		for (GraphObject obj: selectedObjects) {
			if (rowMap.containsKey(obj)) {
				for (Integer r: rowMap.get(obj)) {
					int row = sorter.viewIndex(r.intValue());
					selectionModel.addSelectionInterval(row,row);
				}
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("close")) {
			dispose();
		} else if (e.getActionCommand().equals("export")) {
			// Get the file name
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Export Table to File");
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Open the file
				File exportFile = chooser.getSelectedFile();
				try {
					// Output the table
					tableModel.output(exportFile);
				} catch (IOException ioe) {
					logger.error("Unable to export file: "+ioe.getMessage());
				}
			}
		} else if (e.getActionCommand().equals("print")) {
			try {
				table.print();
			} catch (Exception ePrint) {
				logger.error("Unable to print table: "+ePrint.getMessage(), ePrint);
			}
		} else if (e.getActionCommand().equals("export")) {
		} else if (e.getActionCommand().startsWith("removeColumn:")) {
			String columnNumber = e.getActionCommand().substring(13);
			int column = Integer.parseInt(columnNumber);
			tableModel.removeColumn(column);
		} else if (e.getActionCommand().startsWith("addColumn:")) {
			String columnNumber = e.getActionCommand().substring(10);
			int column = Integer.parseInt(columnNumber);
		}
	}

	class ChemInfoTableModel extends AbstractTableModel {
		List<Column> columns;

		ChemInfoTableModel() {
			super();
			columns = new ArrayList();
		}

		public void addColumn(int columnNumber, Column column) {
			columns.add(columnNumber, column);
			fireTableStructureChanged();
		}

		public void removeColumn(int columnNumber) {
			columns.remove(columnNumber);
			fireTableStructureChanged();
		}

		public void removeColumn(Column column) {
			columns.remove(column);
			fireTableStructureChanged();
		}

		public int getColumnCount() { return columns.size(); }
		public int getRowCount() { return compoundList.size(); }

		public String getColumnName(int columnIndex) {
			Column column = columns.get(columnIndex);
			return column.getColumnName();
		}

		public Class getColumnClass(int columnIndex) {
			Column column = columns.get(columnIndex);
			return column.getColumnClass();
		}
		
		public Object getValueAt(int row, int col) {
			Compound cmpd = compoundList.get(row);
			Column column = columns.get(col);
			return column.getValue(cmpd);
		}

		public void output (File file) throws IOException {
			FileWriter writer = new FileWriter(file);
			for (int viewRow = 0; viewRow < compoundList.size(); viewRow++ ) {
				int row = sorter.modelIndex(viewRow);
				Compound cmpd = compoundList.get(row);
				for (int viewCol = 0; viewCol < columns.size(); viewCol++) {
					if (viewCol > 0)
						writer.write("\t");
					int col = table.convertColumnIndexToModel(viewCol);
					columns.get(col).output(writer, cmpd);
				}
				writer.write("\n");
			}
			writer.close();
		}
	}

	enum ColumnType { ATTRIBUTE, DESCRIPTOR };

	class Column {
		private ColumnType columnType;
		private String attributeName;
		private String objectType;
		private byte attributeType;
		private DescriptorType descriptor;

		Column(DescriptorType descriptor) {
			this.columnType = ColumnType.DESCRIPTOR;
			this.descriptor = descriptor;
		}

		Column(String attributeName, String objectType, byte type) {
			this.columnType = ColumnType.ATTRIBUTE;
			this.attributeName = attributeName;
			this.attributeType = type;
			this.objectType = objectType;
		}

		public Object getValue(Compound cmpd) {
			// Get the GraphObject so we can note whether we have nodes
			GraphObject obj = cmpd.getSource();
			if (obj instanceof CyNode) {
				thisDialog.hasNodes = true;
			} else {
				thisDialog.hasEdges = true;
			}

			if (columnType == ColumnType.ATTRIBUTE) {
				CyAttributes attributes;

				// Special case for "ID"
				if (attributeName.equals("ID"))
					return obj.getIdentifier();

				// Get the appropriate attribute
				if (obj instanceof CyNode) {
					if (objectType.equals("edge."))
						return null;
					attributes = Cytoscape.getNodeAttributes();
				} else {
					if (objectType.equals("node."))
						return null;
					attributes = Cytoscape.getEdgeAttributes();
				}
				// Return the value
				switch (attributeType) {
					case CyAttributes.TYPE_BOOLEAN:
						return attributes.getBooleanAttribute(obj.getIdentifier(), attributeName);
					case CyAttributes.TYPE_FLOATING:
						return attributes.getDoubleAttribute(obj.getIdentifier(), attributeName);
					case CyAttributes.TYPE_INTEGER:
						return attributes.getIntegerAttribute(obj.getIdentifier(), attributeName);
					case CyAttributes.TYPE_SIMPLE_LIST:
						List result = attributes.getListAttribute(obj.getIdentifier(), attributeName);
						String retValue = "[";
						for (int index = 0; index < result.size(); index++) {
							if (index > 0) retValue += ", ";
							retValue += result.get(index).toString();
						}
						retValue += "]";
						return retValue;
					case CyAttributes.TYPE_STRING:
						return attributes.getStringAttribute(obj.getIdentifier(), attributeName);
					default:
						return null;
				}
			} else if (columnType == ColumnType.DESCRIPTOR) {
				// Hand it off
				return cmpd.getDescriptor(descriptor);
			}
			return null;
		}

		public Class getColumnClass() {
			if (columnType == ColumnType.DESCRIPTOR)
				return descriptor.getClassType();

			switch (attributeType) {
				case CyAttributes.TYPE_BOOLEAN:
					return Boolean.class;
				case CyAttributes.TYPE_FLOATING:
					return Double.class;
				case CyAttributes.TYPE_INTEGER:
					return Integer.class;
				case CyAttributes.TYPE_SIMPLE_LIST:
				case CyAttributes.TYPE_STRING:
				default:
					return String.class;
			}
		}

		public String getColumnName() {
			if (columnType == ColumnType.DESCRIPTOR)
				return descriptor.toString();
			return attributeName;
		}

		public void output(FileWriter writer, Compound compound) throws IOException {
			Object obj = getValue(compound);
			if (obj != null) {
				// We don't handle the images, yet
				if (obj instanceof Compound) 
					writer.write("[2D Image]");
				else
					writer.write(obj.toString());
			}
			return;
		}
	}

	class CompoundRenderer implements TableCellRenderer {
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                        boolean hasFocus, int viewRow, int viewColumn) {

			int row = sorter.modelIndex(viewRow);
			int column = table.convertColumnIndexToModel(viewColumn);

			adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, viewRow, viewColumn);
			Compound c = compoundList.get(row);
			TableColumn clm = table.getColumnModel().getColumn(viewColumn);
			int width = clm.getPreferredWidth();
			if (width != table.getRowHeight())
				table.setRowHeight(width); // Note, this will trigger a repaint!
			Image resizedImage = c.getImage(width,width);
			if (resizedImage == null) return null;
			JLabel l = new JLabel(new ImageIcon(resizedImage));
			if (!rowMap.containsKey(c.getSource())) {
				rowMap.put(c.getSource(), new ArrayList());
			}

			rowMap.get(c.getSource()).add(Integer.valueOf(row));
			l.setBackground(adaptee.getBackground());
			l.setForeground(adaptee.getForeground());
			return l;
		}
	}

	class StringRenderer extends JTextArea implements TableCellRenderer {
		private final DefaultTableCellRenderer adaptee = new DefaultTableCellRenderer();

		public StringRenderer () {
			setLineWrap(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
		                                        boolean hasFocus, int row, int column) {
			adaptee.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setBackground(adaptee.getBackground());
			setBorder(adaptee.getBorder());
			setFont(adaptee.getFont());
			setText(adaptee.getText());
			setForeground(adaptee.getForeground());
			return this;
		}
	}

	class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2 && e.getComponent() == table)
			{
				Point p = e.getPoint();
				// int row = table.convertRowIndexToModel(table.rowAtPoint(p));
				int row = sorter.modelIndex(table.rowAtPoint(p));
				int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
				final Compound c = compoundList.get(row);
				if (tableModel.getColumnClass(column) == Compound.class) {
					final List<Compound> cList = new ArrayList();
					cList.add(c);
					Runnable t = new Runnable() {
  						public void run() {
   	 					CompoundPopup popup = new CompoundPopup(cList, c.getSource());
							popup.toFront();
						}
					};
					new Thread(t).start();
				}
			} else if (e.getComponent() == tableHeader && 
			           ((e.getButton() == MouseEvent.BUTTON3) ||
			            (e.getButton() == MouseEvent.BUTTON1 && e.isMetaDown()) ||
			            (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()))) {
				// Popup header context menu
				JPopupMenu headerMenu = new JPopupMenu();
				// Get our column title
				Point p = e.getPoint();
				int column = table.convertColumnIndexToModel(table.columnAtPoint(p));
				String name = tableModel.getColumnName(column);
				// Add removeMenu if we have more than 1 column
				if (tableModel.getColumnCount() > 1) {
					JMenuItem removeMenu = new JMenuItem("Remove Column "+name);
					removeMenu.setActionCommand("removeColumn:"+column);
					removeMenu.addActionListener(thisDialog);
					headerMenu.add(removeMenu);
				}
				JMenu addMenu = new JMenu("Add New Column");
				JMenu attrMenu = new JMenu("Cytoscape attributes");
				if (thisDialog.hasNodes) {
					addAttributeMenus(attrMenu, Cytoscape.getNodeAttributes(), "node.", column);
				}
				if (thisDialog.hasEdges) {
					addAttributeMenus(attrMenu, Cytoscape.getEdgeAttributes(), "edge.", column);
				}
				if (attrMenu.getItemCount() > 0) 
					addMenu.add(attrMenu);

				JMenu descMenu = new JMenu("Molecular descriptors");
				addDescriptorMenus(descMenu, column);
				if (descMenu.getItemCount() > 0) 
					addMenu.add(descMenu);

				headerMenu.add(addMenu);
				headerMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		void addAttributeMenus(JMenu addMenu, CyAttributes attributes, String type, int column) {
			String[] attNames = attributes.getAttributeNames();
			for (int i = 0; i < attNames.length; i++) {
				String att = attNames[i];
				if (tableModel.findColumn(att) < 0) {
					addMenu.add(new AddMenu(att, type, column, attributes.getType(att)));
				}
			}
		}

		void addDescriptorMenus(JMenu addMenu, int column) {
			List<DescriptorType> descList = Compound.getDescriptorList();
			for (DescriptorType type: descList) {
				if (tableModel.findColumn(type.toString()) < 0) {
					addMenu.add(new AddMenu(type, column));
				}
			}
		}
	}

	class AddMenu extends JMenuItem implements ActionListener {
		int column;
		Column newColumn;
		
		AddMenu(String name, int column) {
			this(name, "", column, CyAttributes.TYPE_STRING);
		}

		AddMenu(String name, String prefix, int column, byte type) {
			super(prefix+name);
			this.newColumn = new Column(name, prefix, type);
			this.column = column;
			addActionListener(this);
		}

		AddMenu(DescriptorType descriptor, int column) {
			super(descriptor.toString());
			this.newColumn = new Column(descriptor);
			this.column = column;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e) {
			tableModel.addColumn(column, newColumn);
		}
	}

}
