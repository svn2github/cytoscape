/*
 * $Archive: SourceJammer$
 * $FileName: JSortTable.java$
 * $FileID: 3984$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date$
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

package browser;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.dialogs.NetworkMetaDataDialog;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

public class JSortTable extends JTable implements MouseListener, ActionListener {
	protected int sortedColumnIndex = -1;
	protected boolean sortedColumnAscending = true;

	// For right-click menu
	private JPopupMenu rightClickPopupMenu;

	private JPopupMenu cellMenu;
	private JScrollPane cellContentListPane;

	private JMenuItem copyMenuItem = null;
	private JMenu exportMenu = null;
	private JMenuItem exportCellsMenuItem = null;
	private JMenuItem exportTableMenuItem = null;
	private JMenuItem selectAllMenuItem = null;
	private JMenuItem newSelectionMenuItem = null;

	private JCheckBoxMenuItem coloringMenuItem = null;

	CopyToExcel excelHandler;
	private Clipboard systemClipboard;

	StringSelection stsel;

	MultiDataEditAction edit;

	SortTableModel tableModel;
	private int objectType;

	private boolean colorSwitch = false;

	public static final String LS = System.getProperty("line.separator");

	public JSortTable() {
		this(new DefaultSortTableModel());
		initialize();
	}

	public JSortTable(int rows, int cols) {
		this(new DefaultSortTableModel(rows, cols));
		initialize();
	}

	public JSortTable(Object[][] data, Object[] names) {
		this(new DefaultSortTableModel(data, names));
		initialize();
	}

	public JSortTable(Vector data, Vector names) {
		this(new DefaultSortTableModel(data, names));
		initialize();
	}

	public JSortTable(SortTableModel model) {
		super(model);
		initSortHeader();

		this.tableModel = model;

		initialize();
	}

	public JSortTable(SortTableModel model, int objectType) {
		super(model);
		initSortHeader();

		this.tableModel = model;
		this.objectType = objectType;

		initialize();
	}

	public JSortTable(SortTableModel model, TableColumnModel colModel) {
		super(model, colModel);
		initSortHeader();
		this.tableModel = model;
		initialize();
	}

	public JSortTable(SortTableModel model, TableColumnModel colModel,
			ListSelectionModel selModel) {
		super(model, colModel, selModel);
		initSortHeader();

		this.tableModel = model;

		initialize();
	}

	// Initialize some attributes of this table
	private void initialize() {
		this.setSize(400, 200);
		this.setCellSelectionEnabled(true);
		this.getPopupMenu();

		setKeyStroke();

		this.setDefaultRenderer(Object.class, new BrowserTableCellRenderer(
				false, objectType));
		/**
		 * @param args
		 *            the command line arguments
		 */

		// excelHandler = new CopyToExcel(this);
	}

	private void setKeyStroke() {
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,
				ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.

		this
				.registerKeyboardAction(this, "Copy", copy,
						JComponent.WHEN_FOCUSED);

		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	}

	// Create pop-up menu for right-click

	/**
	 * This method initializes jPopupMenu1
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getPopupMenu() {
		if (rightClickPopupMenu == null) {
			rightClickPopupMenu = new JPopupMenu();

			copyMenuItem = new JMenuItem("Copy");
			// jMenuItem1 = new JMenuItem("Clear This Row");
			newSelectionMenuItem = new JMenuItem("Select from table");
			exportMenu = new JMenu("Export...");
			exportCellsMenuItem = new JMenuItem("Selected Cells");
			exportTableMenuItem = new JMenuItem("Entire Table");
			selectAllMenuItem = new JMenuItem("Select All");

			coloringMenuItem = new JCheckBoxMenuItem("On/Off Coloring");

			// showAdvancedWindow = new JCheckBoxMenuItem("Show Advanced
			// Window");

			copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Cells copied to clipboard.");
					copyToClipBoard();
				}
			});

			exportCellsMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							export(false);
						}
					});

			exportTableMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							export(true);
						}
					});

			selectAllMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							selectAll();
						}
					});

			newSelectionMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							int[] rowsSelected = getSelectedRows();

							int columnCount = getColumnCount();
							int idLocation = 0;

							// First, find the location of the ID column
							for (int idx = 0; idx < columnCount; idx++) {
								if (getColumnName(idx).equals(DataTable.ID)) {
									idLocation = idx;
									break;
								}
							}

							HashMap selectedMap = new HashMap();
							String selectedName = null;
							for (int idx = 0; idx < rowsSelected.length; idx++) {
								selectedName = (String) getValueAt(
										rowsSelected[idx], idLocation);

								if (objectType == DataTable.NODES) {
									// Change node color
									Node selectedNode = Cytoscape
											.getCyNode(selectedName);

									selectedMap.put(selectedName, selectedNode);
									if (Cytoscape.getCurrentNetworkView() != Cytoscape
											.getNullNetworkView()) {
										NodeView nv = Cytoscape
												.getCurrentNetworkView()
												.getNodeView(selectedNode);
										if (nv != null) {
											nv.setSelectedPaint(Color.GREEN);
										}
									}
								} else {
									// Edge selectedEdge =
									// Cytoscape.getCyEdge((String)
									String[] edgeNameParts = selectedName
											.split(" ");
									String interaction = edgeNameParts[1]
											.substring(1, edgeNameParts[1]
													.length() - 1);
									Node source = Cytoscape
											.getCyNode(edgeNameParts[0]);
									Node target = Cytoscape
											.getCyNode(edgeNameParts[2]);
									Edge selectedEdge = Cytoscape.getCyEdge(
											source, target,
											Semantics.INTERACTION, interaction,
											false);
									selectedMap.put(selectedName, selectedEdge);
									if (Cytoscape.getCurrentNetworkView() != Cytoscape
											.getNullNetworkView()) {
										EdgeView ev = Cytoscape
												.getCurrentNetworkView()
												.getEdgeView(selectedEdge);
										if (ev != null) {
											ev.setSelectedPaint(Color.GREEN);
										}
									}
								}
							}

							Iterator it = null;
							List nonSelectedObjects = new ArrayList();

							if (objectType == DataTable.NODES) {
								it = Cytoscape.getCurrentNetwork()
										.getSelectedNodes().iterator();
								while (it.hasNext()) {
									Node curNode = (Node) it.next();
									Node fromMap = (Node) selectedMap
											.get(curNode.getIdentifier());
									if (fromMap == null) {
										nonSelectedObjects.add(curNode);
									}
								}
								resetObjectColor(idLocation);

								Cytoscape.getCurrentNetwork()
										.setSelectedNodeState(
												nonSelectedObjects, false);
							} else {
								it = Cytoscape.getCurrentNetwork()
										.getSelectedEdges().iterator();
								while (it.hasNext()) {
									Edge curEdge = (Edge) it.next();
									Edge fromMap = (Edge) selectedMap
											.get(curEdge.getIdentifier());
									if (fromMap == null) {
										nonSelectedObjects.add(curEdge);
									}

								}
								resetObjectColor(idLocation);

								Cytoscape.getCurrentNetwork()
										.setSelectedEdgeState(
												nonSelectedObjects, false);
							}
							if (Cytoscape.getCurrentNetworkView() != Cytoscape
									.getNullNetworkView()) {
								Cytoscape.getCurrentNetworkView().updateView();
							}
						}
					});

			coloringMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {

							if (Cytoscape.getCurrentNetworkView() != Cytoscape
									.getNullNetworkView()) {
								if (coloringMenuItem.isSelected() == true) {
									System.out.println("color ON");
									setNewRenderer(true);
								} else {
									System.out.println("color OFF");
									setNewRenderer(false);
								}
							}
						}
					});

			exportMenu.add(exportCellsMenuItem);
			exportMenu.add(exportTableMenuItem);

			if (objectType != DataTable.NETWORK) {
				rightClickPopupMenu.add(newSelectionMenuItem);
			}

			rightClickPopupMenu.add(copyMenuItem);
			rightClickPopupMenu.add(selectAllMenuItem);
			rightClickPopupMenu.add(exportMenu);
			if (objectType != DataTable.NETWORK) {
				rightClickPopupMenu.addSeparator();
				rightClickPopupMenu.add(coloringMenuItem);
			}
		}
		return rightClickPopupMenu;
	}

	private void setNewRenderer(boolean colorSwitch) {
		this.setDefaultRenderer(Object.class, new BrowserTableCellRenderer(
				colorSwitch, objectType));
		this.repaint();
	}

	public void export(final boolean all) {
		// Do this in the GUI Event Dispatch thread...
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final String name;
				try {
					name = FileUtil.getFile("Export Table", FileUtil.SAVE,
							new CyFileFilter[] {}).toString();
				} catch (Exception exp) {
					// this is because the selection was canceled
					return;
				}
				String export = exportTable(all);
				// write to file
				System.out.println("Write to: " + name + " " + export);
				try {
					File file = new File(name);
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							file));
					writer.write(export);
					writer.close();
				} catch (Exception ex) {
					System.out.println("Table Export Write error");
					ex.printStackTrace();
				}

			}
		});
	}

	public String exportTable() {
		return exportTable("\t", LS, false);
	}

	public String exportTable(boolean all) {
		return exportTable("\t", LS, all);
	}

	public String exportTable(String element_delim, String eol_delim,
			boolean all) {

		String attributeNames = "";

		if (all == true) {
			this.selectAll();
		}

		int[] selectedCols = this.getSelectedColumns();
		for (int i = 0; i < selectedCols.length; i++) {
			attributeNames = attributeNames
					+ this.getColumnName(selectedCols[i]) + "\t";
		}
		attributeNames = attributeNames + (LS);
		return attributeNames + copyToClipBoard();

	}

	protected void initSortHeader() {
		JTableHeader header = getTableHeader();
		header.setDefaultRenderer(new SortHeaderRenderer());
		header.addMouseListener(this);

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		//
		// Event handler. Define actions when mouse is clicked.
		//
		addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {

				// If action is right click, then show edit pop-up menu
				if (javax.swing.SwingUtilities.isRightMouseButton(e)) {

					rightClickPopupMenu.show(e.getComponent(), e.getX(), e
							.getY());
				} else if (javax.swing.SwingUtilities.isLeftMouseButton(e)
						&& getSelectedRows().length != 0) {

					showListContents(e);
					// Otherwise, do cell editing.

					TableColumnModel columnModel = getColumnModel();
					int column = columnModel.getColumnIndexAtX(e.getX());
					int row = e.getY() / getRowHeight();
					if (row >= getRowCount() || row < 0
							|| column >= getColumnCount() || column < 0)
						return;

					Object cellValue = getValueAt(row, column);
					try {
						if (cellValue != null
								&& cellValue.getClass() == Class
										.forName("java.lang.String")) {
							// see if the String representation is a URL
							// if it is not, a MalformedURLException gets
							// thrown,
							// and we
							// ignore it
							java.net.URL url = new java.net.URL(
									(String) cellValue);
							cytoscape.util.OpenBrowser.openURL(url.toString());
						}

					} catch (Exception urle) {
						// System.out.println("##### " + urle.getMessage());
					}
				}

			} // mouseClicked

			public void mouseExited(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {

			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseReleased(MouseEvent e) {
				// When the mouse is released, fire signal to pass the selected
				// objects in the table.
				// Get selected object names
				int[] rowsSelected = getSelectedRows();

				if (rowsSelected.length == 0) {
					return;
				}

				int columnCount = getColumnCount();
				int idLocation = 0;

				// First, find the location of the ID column
				for (int idx = 0; idx < columnCount; idx++) {
					if (getColumnName(idx).equals(DataTable.ID)) {
						idLocation = idx;
						break;
					}
				}

				// Initialize internal selection table
				((DataTableModel) dataModel).resetSelectionFlags();
				resetObjectColor(idLocation);

				String selectedName = null;
				for (int idx = 0; idx < rowsSelected.length; idx++) {
					selectedName = (String) getValueAt(rowsSelected[idx],
							idLocation);
					if (objectType == DataTable.NODES) {

						// Flip the internal flag

						((DataTableModel) dataModel).setSelectionArray(
								selectedName, true);
						Node selectedNode = Cytoscape.getCyNode(selectedName);

						if (Cytoscape.getCurrentNetworkView() != Cytoscape
								.getNullNetworkView()) {
							NodeView nv = Cytoscape.getCurrentNetworkView()
									.getNodeView(selectedNode);
							if (nv != null) {
								nv.setSelectedPaint(Color.GREEN);
							}

						}

					} else if (objectType == DataTable.EDGES) {
						// Edge selectedEdge = Cytoscape.getCyEdge((String)
						String[] edgeNameParts = selectedName.split(" ");
						String interaction = edgeNameParts[1].substring(1,
								edgeNameParts[1].length() - 1);
						Node source = Cytoscape.getCyNode(edgeNameParts[0]);
						Node target = Cytoscape.getCyNode(edgeNameParts[2]);
						Edge selectedEdge = Cytoscape.getCyEdge(source, target,
								Semantics.INTERACTION, interaction, false);
						if (Cytoscape.getCurrentNetworkView() != Cytoscape
								.getNullNetworkView()) {
							EdgeView ev = Cytoscape.getCurrentNetworkView()
									.getEdgeView(selectedEdge);
							if (ev != null) {
								ev.setSelectedPaint(Color.GREEN);
							}
						}
					} else {
						// For network, do nothing.
					}
				}
				if (Cytoscape.getCurrentNetworkView() != Cytoscape
						.getNullNetworkView()) {
					Cytoscape.getCurrentNetworkView().updateView();
				}
			}
		});

	}

	private void resetObjectColor(int idLocation) {
		for (int idx = 0; idx < dataModel.getRowCount(); idx++) {

			if (objectType == DataTable.NODES) {
				Node selectedNode = Cytoscape.getCyNode((String) dataModel
						.getValueAt(idx, idLocation));
				// Set to the original color
				if (Cytoscape.getCurrentNetworkView() != Cytoscape
						.getNullNetworkView()) {
					NodeView nv = Cytoscape.getCurrentNetworkView()
							.getNodeView(selectedNode);
					if (nv != null) {
						nv.setSelectedPaint(Color.YELLOW);
					}
				}
			} else if (objectType == DataTable.EDGES) {
				String selectedEdgeName = (String) dataModel.getValueAt(idx,
						idLocation);
				String[] edgeNameParts = selectedEdgeName.split(" ");
				String interaction = edgeNameParts[1].substring(1,
						edgeNameParts[1].length() - 1);
				Node source = Cytoscape.getCyNode(edgeNameParts[0]);
				Node target = Cytoscape.getCyNode(edgeNameParts[2]);
				Edge selectedEdge = Cytoscape.getCyEdge(source, target,
						Semantics.INTERACTION, interaction, false);
				if (Cytoscape.getCurrentNetworkView() != Cytoscape
						.getNullNetworkView()) {
					EdgeView ev = Cytoscape.getCurrentNetworkView()
							.getEdgeView(selectedEdge);
					if (ev != null) {
						ev.setSelectedPaint(Color.RED);
					}
				}
			} else {
				// For network attr, we do not have to do anything.
			}
		}
		// Cytoscape.getCurrentNetworkView().updateView();
	}

	public int getSortedColumnIndex() {
		return sortedColumnIndex;
	}

	public boolean isSortedColumnAscending() {
		return sortedColumnAscending;
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void mousePressed(MouseEvent event) {
	}

	public void mouseClicked(MouseEvent event) {

		int cursorType = getTableHeader().getCursor().getType();
		if (event.getButton() == MouseEvent.BUTTON1
				&& cursorType != Cursor.E_RESIZE_CURSOR
				&& cursorType != Cursor.W_RESIZE_CURSOR) {
			TableColumnModel colModel = getColumnModel();
			int index = colModel.getColumnIndexAtX(event.getX());
			int modelIndex = colModel.getColumn(index).getModelIndex();

			SortTableModel model = (SortTableModel) getModel();
			if (model.isSortable(modelIndex)) {
				// toggle ascension, if already sorted
				if (sortedColumnIndex == index) {
					sortedColumnAscending = !sortedColumnAscending;
				}
				sortedColumnIndex = index;

				model.sortColumn(modelIndex, sortedColumnAscending);
			}
		}

	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if (event.getActionCommand().compareTo("Copy") == 0) {
			System.out.println("Cells copied to clipboard.");
			copyToClipBoard();
		}

	}

	// Display elements in te list & map objects
	public void showListContents(MouseEvent e) {
		int column = 0;
		int row = 0;
		row = this.getSelectedRow();
		column = this.getSelectedColumn();
		DataTableModel model = (DataTableModel) tableModel;

		// List tester = new ArrayList();
		Class tester = model.getObjectTypeAt(this.getColumnName(column));
		// System.out.println("Obj type = " + tester.toString());

		Object value = model.getValueAt(row, column);

		if (tester != null && tester.equals(List.class)) {
			int idCol = 0;
			for (int i = 0; i < this.getColumnCount(); i++) {
				if (this.getColumnName(i).equals(DataTable.ID)) {
					idCol = i;
					break;
				}
			}

			String idField = (String) this.getValueAt(row, idCol);

			List contents = (List) model.getAttributeValue(
					CyAttributes.TYPE_SIMPLE_LIST, idField, this
							.getColumnName(column));
			cellMenu = new JPopupMenu();
			List arrayList = new ArrayList();
			arrayList = contents;
			Object[] listItems = arrayList.toArray();
			if (listItems.length != 0) {

				cellContentListPane = new JScrollPane();
				JList listContents = new JList(listItems);
				listContents.addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						// hide if right click
						if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
							cellMenu.setVisible(false);
						} else if (javax.swing.SwingUtilities
								.isLeftMouseButton(e)
								&& getSelectedRows().length != 0) {
						}

					}

					public void mouseExited(MouseEvent event) {
					}

					public void mouseReleased(MouseEvent event) {
					}

					public void mousePressed(MouseEvent event) {
					}

					public void mouseEntered(MouseEvent event) {
					}
				});
				cellContentListPane.setPreferredSize(new Dimension(200, 100));
				cellContentListPane
						.setBorder(javax.swing.BorderFactory
								.createTitledBorder(
										null,
										idField,
										javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
										javax.swing.border.TitledBorder.DEFAULT_POSITION,
										null, null));
				cellContentListPane.setViewportView(listContents);

				cellMenu.add(cellContentListPane);
				cellMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		} else if (value.getClass() == HashMap.class
				&& model.getValueAt(row, 0).equals(DataTable.NETWORK_METADATA)) {
			if(model.getValueAt(row, column) == null) {
				System.out.println("Metadata not available for this network.  Creating new one...");
			}
			NetworkMetaDataDialog mdd = new NetworkMetaDataDialog(Cytoscape
					.getDesktop(), false, Cytoscape.getCurrentNetwork());
			mdd.setVisible(true);
		}
	}

	private String copyToClipBoard() {

		StringBuffer sbf = new StringBuffer();
		// Check to ensure we have selected only a contiguous block of
		// cells
		int numcols = this.getSelectedColumnCount();
		int numrows = this.getSelectedRowCount();

		int[] rowsselected = this.getSelectedRows();
		int[] colsselected = this.getSelectedColumns();

		// Return if no cell is selected.
		if (numcols == 0 && numrows == 0) {
			return null;
		}

		if (!((numrows - 1 == rowsselected[rowsselected.length - 1]
				- rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
				- colsselected[0] && numcols == colsselected.length))) {
			JOptionPane.showMessageDialog(null, "Invalid Copy Selection",
					"Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				sbf.append(this.getValueAt(rowsselected[i], colsselected[j]));
				if (j < numcols - 1)
					sbf.append("\t");
			}
			sbf.append(LS);
		}
		stsel = new StringSelection(sbf.toString());
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(stsel, stsel);

		return sbf.toString();
	}

}

/*
 * Cell renderer for preview table.
 * 
 * Coloring function is added. This will sync node color and cell colors.
 * 
 */
class BrowserTableCellRenderer extends JLabel implements TableCellRenderer {
	private Font labelFont = new Font("Sans-serif", Font.BOLD, 14);
	private Font normalFont = new Font("Sans-serif", Font.PLAIN, 12);
	private final Color metadataBackground = new Color(255, 210, 255);

	private static final String METADATA_ATTR_NAME = "Network Metadata";

	private int type = DataTable.NODES;
	private boolean coloring;

	public BrowserTableCellRenderer(boolean coloring, int type) {
		super();
		this.type = type;
		this.coloring = coloring;
		setOpaque(true);
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		// First, set values
		setHorizontalAlignment(LEFT);
		setText((value == null) ? "" : value.toString());

		// If selected, return
		if (isSelected) {
			setFont(labelFont);
			setForeground(Color.WHITE);
			setBackground(Color.BLUE);
			return this;
		}

		if (coloring == false || type == DataTable.NETWORK) {
			if (table.getColumnName(column).equals(DataTable.ID)
					|| (table.getColumnName(column).equals(
							"Network Attribute Name") && !value
							.equals("Network Metadata"))) {
				setFont(labelFont);
				setBackground(DataTable.NON_EDITIBLE_COLOR);

			} else if (type == DataTable.NETWORK
					&& value.equals("Network Metadata")) {
				setBackground(metadataBackground);
				setFont(labelFont);
			} else {
				setFont(normalFont);
				setBackground(table.getBackground());
			}
			setForeground(table.getForeground());
			return this;
		} else {

			setForeground(table.getForeground());

			if (type == DataTable.NODES) {
				/*
				 * Render color node cells
				 */

				if (table.getColumnName(column).equals(DataTable.ID)) {
					if (Cytoscape.getCurrentNetworkView() != Cytoscape
							.getNullNetworkView()) {
						NodeView nodeView = Cytoscape.getCurrentNetworkView()
								.getNodeView(
										Cytoscape.getCyNode((String) table
												.getValueAt(row, column)));
						if (nodeView != null) {
							Color nodeColor = (Color) nodeView
									.getUnselectedPaint();
							super.setBackground(nodeColor);
						}
					}
					setFont(labelFont);
				} else {
					setFont(normalFont);
					setBackground(table.getBackground());
				}

			} else if (type == DataTable.EDGES) {
				/*
				 * Render color edge cells
				 */

				if (table.getColumnName(column).equals(DataTable.ID)) {
					setFont(labelFont);
					String edgeName = (String) table.getValueAt(row, column);
					String[] parts = edgeName.split(" ");

					CyNode source = Cytoscape.getCyNode(parts[0].trim());
					CyNode target = Cytoscape.getCyNode(parts[2].trim());
					String interaction = parts[1].trim().substring(1,
							parts[1].trim().length() - 1);

					// System.out.println("Source = " + source + ", Target = " +
					// target + ", Itr = " + interaction);
					if (Cytoscape.getCurrentNetworkView() != Cytoscape
							.getNullNetworkView()) {
						EdgeView edgeView = Cytoscape.getCurrentNetworkView()
								.getEdgeView(
										Cytoscape.getCyEdge(source, target,
												Semantics.INTERACTION,
												interaction, false));
						if (edgeView != null) {
							Color edgeColor = (Color) edgeView
									.getUnselectedPaint();
							super.setBackground(edgeColor);
						}
					}
				} else {
					setFont(normalFont);
					setBackground(table.getBackground());
				}
			} else {
				/*
				 * Render Network cells
				 */

				// for now, no special scheme available.
			}
		}
		return this;
	}

}
