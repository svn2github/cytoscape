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
import java.util.LinkedList;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
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

import chemViz.commands.ValueUtils;
import chemViz.model.ChemInfoTableModel;
import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.model.CompoundColumn;
import chemViz.model.CompoundColumn.ColumnType;
import chemViz.model.TableSorter;

import chemViz.ui.renderers.CompoundRenderer;
import chemViz.ui.renderers.StringRenderer;

public class CompoundTable extends JDialog implements ListSelectionListener,
                                                      SelectEventListener,
                                                      ActionListener {
	
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
	private boolean hasNodes = false;
	private boolean hasEdges = false;
	private	CyLogger logger = CyLogger.getLogger(CompoundTable.class);

	private	List<CompoundColumn> columns;
	private	List<Compound> compoundList;

	public CompoundTable (List<Compound> compoundList, List<String> columnList) {
		super(Cytoscape.getDesktop());
		network = Cytoscape.getCurrentNetwork();
		networkView = Cytoscape.getCurrentNetworkView();
		this.compoundList = compoundList;
		setTitle("2D Structure Table");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.rowMap = new HashMap();

		GraphObject obj = compoundList.get(0).getSource();
		String objType = "node.";
		CyAttributes attributes = Cytoscape.getNodeAttributes();

		if (obj instanceof CyEdge) {
			objType = "edge.";
			attributes = Cytoscape.getEdgeAttributes();
		}

		if (columnList != null && columnList.size() > 0) {
			columns = new ArrayList<CompoundColumn>();
			for (String s: columnList) {
				String[] tokens = s.trim().split("[:;]");
				if (tokens.length != 3 && tokens.length != 2)
					throw new RuntimeException("Illegal column specification: "+s);
				if (tokens[0].equalsIgnoreCase("descriptor") || tokens[0].equalsIgnoreCase("desc")) {
					DescriptorType type = ValueUtils.getDescriptor(Compound.getDescriptorList(),tokens[1]);
					int columnWidth = -1;
					if (tokens.length == 3) columnWidth = Integer.parseInt(tokens[2]);
					columns.add(new CompoundColumn(type, columnWidth));
				} else if (tokens[0].equalsIgnoreCase("attribute") || tokens[0].equalsIgnoreCase("attr")) {
					String attribute = tokens[1];
					byte type = CyAttributes.TYPE_STRING;
					if (!attribute.equals("ID")) {
						type = attributes.getType(attribute);
						if (type == CyAttributes.TYPE_UNDEFINED)
							continue;
					}
					int columnWidth = -1;
					if (tokens.length == 3) columnWidth = Integer.parseInt(tokens[2]);

					if (attribute.equals("ID"))
						columns.add(new CompoundColumn("ID", "", CyAttributes.TYPE_STRING, columnWidth));
					else
						columns.add(new CompoundColumn(attribute, objType, type, columnWidth));
				}
			}
		} else {
			// See if we have any table attributes stored
			columns = TableAttributeHandler.getAttributes(Cytoscape.getCurrentNetwork());
		}

		// Create the table
		initTable();

		// Listen for network selection events
		network.addSelectEventListener(this);

		pack();

		// Now, see if we need to adjust the width and height
		int height = TableAttributeHandler.getHeightAttribute(Cytoscape.getCurrentNetwork());
		int width = TableAttributeHandler.getWidthAttribute(Cytoscape.getCurrentNetwork());
		if (height != -1 && width != -1) {
			this.setSize(width, height);
		}

		thisDialog = this;
		setVisible(true);
	}

	public void setCompounds(List<Compound> newList) {
		this.rowMap = new HashMap();
		tableModel.setCompoundList(newList);
		tableModel.fireTableDataChanged();
	}

	private void initTable() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		// create our table model
		tableModel = new ChemInfoTableModel(compoundList);
		sorter = new TableSorter(tableModel);

		// Create our default columns
		int column = 0;
		for (CompoundColumn c: columns) {
			tableModel.addColumn(column++, c);
		}

		table = new JTable(sorter);

		MouseAdapter mouseAdapter = new TableMouseAdapter(table, tableModel, sorter);
		tableHeader = table.getTableHeader();
		tableHeader.addMouseListener(mouseAdapter);
		sorter.setTableHeader(tableHeader);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setDefaultRenderer(Compound.class, new CompoundRenderer(sorter, rowMap));
		table.setDefaultRenderer(String.class, new StringRenderer());

		// Figure out all of our default column widths
		columnModel = table.getColumnModel();
		int rowHeight = TableAttributeHandler.DEFAULT_IMAGE_SIZE;
		column = 0;

		for (CompoundColumn c: columns) {
			columnModel.getColumn(column++).setPreferredWidth(c.getWidth());
			// See if we've got an image -- if so, use it to set the default row height
			if (c.getColumnType() == ColumnType.DESCRIPTOR && c.getDescriptor() == DescriptorType.IMAGE) {
				rowHeight = c.getWidth();
			}
		}

		table.setRowHeight(rowHeight);

		// Add our mouse listener (specific for 2D image popup)
		table.addMouseListener(mouseAdapter);

		// Add our row selection listener
		selectionModel = table.getSelectionModel();
		selectionModel.addListSelectionListener(this);

		JScrollPane pane = new JScrollPane(table);
		pane.setPreferredSize(new Dimension(500+TableAttributeHandler.DEFAULT_IMAGE_SIZE+20,520));
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
			TableAttributeHandler.setTableAttributes(table, tableModel, Cytoscape.getCurrentNetwork());
			TableAttributeHandler.setSizeAttributes(this, Cytoscape.getCurrentNetwork());
			dispose();
		} else if (e.getActionCommand().equals("export")) {
			// Get the file name
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Text file formats", "txt", "tsv");
			chooser.setFileFilter(filter);
			chooser.setDialogTitle("Export Table to File");
			int returnVal = chooser.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// Open the file
				File exportFile = chooser.getSelectedFile();
				try {
					// Output the table
					outputTable(exportFile);
				} catch (IOException ioe) {
					logger.error("Unable to export file: "+ioe.getMessage());
				}
			}
		} else if (e.getActionCommand().equals("print")) {
			try {
				((JTable)table).print();
			} catch (Exception ePrint) {
				logger.error("Unable to print table: "+ePrint.getMessage(), ePrint);
			}
		}
	}

	private void outputTable(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		for (int viewRow = 0; viewRow < compoundList.size(); viewRow++ ) {
			int row = sorter.modelIndex(viewRow);
			Compound cmpd = compoundList.get(row);
			for (int viewCol = 0; viewCol < columns.size(); viewCol++) {
				int col = table.convertColumnIndexToModel(viewCol);
				CompoundColumn c = columns.get(col);

				// warning -- if the image column is the first column, then we'll get a
				// leading tab
				if (c.getColumnType() != ColumnType.DESCRIPTOR || c.getDescriptor() != DescriptorType.IMAGE) {
					if (viewCol > 0)
						writer.write("\t");
					c.output(writer, cmpd);
				}
			}
			writer.write("\n");
		}
		writer.close();
	}
}
