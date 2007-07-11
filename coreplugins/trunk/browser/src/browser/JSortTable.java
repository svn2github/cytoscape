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

import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.data.Semantics;

import cytoscape.dialogs.NetworkMetaDataDialog;

import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;

import giny.model.Edge;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


/**
 *
 */
public class JSortTable extends JTable implements MouseListener, ActionListener,
                                                  PropertyChangeListener, SelectEventListener {
	
	private VisualMappingManager vmm = Cytoscape.getVisualMappingManager();
	private GlobalAppearanceCalculator gac;
	
	protected int sortedColumnIndex = -1;
	protected boolean sortedColumnAscending = true;
	private Color selectedNodeColor;
	private Color selectedEdgeColor;
	private Color reverseSelectedNodeColor;
	private Color reverseSelectedEdgeColor;
	protected static final int SELECTED_NODE = 1;
	protected static final int REV_SELECTED_NODE = 2;
	protected static final int SELECTED_EDGE = 3;
	protected static final int REV_SELECTED_EDGE = 4;

	// For right-click menu
	private JPopupMenu rightClickPopupMenu;
	private JPopupMenu cellMenu;
	private JMenuItem copyMenuItem = null;
	private JMenu exportMenu = null;
	private JMenuItem exportCellsMenuItem = null;
	private JMenuItem exportTableMenuItem = null;
	private JMenuItem selectAllMenuItem = null;
	private JMenuItem newSelectionMenuItem = null;
	private JCheckBoxMenuItem coloringMenuItem = null;
	private Clipboard systemClipboard;
	private StringSelection stsel;
	private SortTableModel tableModel;
	private int objectType;

	/**
	 * 
	 */
	public static final String LS = System.getProperty("line.separator");

	/**
	 * Creates a new JSortTable object.
	 */
	public JSortTable() {
		this(new DefaultSortTableModel());
		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param rows  DOCUMENT ME!
	 * @param cols  DOCUMENT ME!
	 */
	public JSortTable(int rows, int cols) {
		this(new DefaultSortTableModel(rows, cols));
		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public JSortTable(Object[][] data, Object[] names) {
		this(new DefaultSortTableModel(data, names));
		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public JSortTable(Vector data, Vector names) {
		this(new DefaultSortTableModel(data, names));
		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param model  DOCUMENT ME!
	 */
	public JSortTable(SortTableModel model) {
		super(model);
		initSortHeader();

		this.tableModel = model;

		initialize();
	}

	// this is the only one that's actually used
	/**
	 * Creates a new JSortTable object.
	 *
	 * @param model  DOCUMENT ME!
	 * @param objectType  DOCUMENT ME!
	 */
	public JSortTable(SortTableModel model, int objectType) {
		super(model);
		initSortHeader();

		this.tableModel = model;
		this.objectType = objectType;

		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param model  DOCUMENT ME!
	 * @param colModel  DOCUMENT ME!
	 */
	public JSortTable(SortTableModel model, TableColumnModel colModel) {
		super(model, colModel);
		initSortHeader();
		this.tableModel = model;
		initialize();
	}

	/**
	 * Creates a new JSortTable object.
	 *
	 * @param model  DOCUMENT ME!
	 * @param colModel  DOCUMENT ME!
	 * @param selModel  DOCUMENT ME!
	 */
	public JSortTable(SortTableModel model, TableColumnModel colModel, ListSelectionModel selModel) {
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
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		setSelectedColor(SELECTED_NODE);
		setSelectedColor(REV_SELECTED_NODE);
		setSelectedColor(SELECTED_EDGE);
		setSelectedColor(REV_SELECTED_EDGE);

		this.setDefaultRenderer(Object.class, new BrowserTableCellRenderer(false, objectType));
		this.getColumnModel().addColumnModelListener(this);
	}

	private void setKeyStroke() {
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		this.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);

		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	protected void setSelectedColor(final int type) {
		gac = vmm.getVisualStyle().getGlobalAppearanceCalculator();

		switch (type) {
			case SELECTED_NODE:
				selectedNodeColor = gac.getDefaultNodeSelectionColor();

				break;

			case REV_SELECTED_NODE:
				reverseSelectedNodeColor = gac.getDefaultNodeReverseSelectionColor();

				break;

			case SELECTED_EDGE:
				selectedEdgeColor = gac.getDefaultEdgeSelectionColor();

				break;

			case REV_SELECTED_EDGE:
				reverseSelectedEdgeColor = gac.getDefaultEdgeReverseSelectionColor();

				break;

			default:
				break;
		}
	}

	protected Color getSelectedColor(final int type) {
		Color newColor;
		gac = vmm.getVisualStyle()
		                                          .getGlobalAppearanceCalculator();

		switch (type) {
			case SELECTED_NODE:
				newColor = gac.getDefaultNodeSelectionColor();

				break;

			case REV_SELECTED_NODE:
				newColor = gac.getDefaultNodeReverseSelectionColor();

				break;

			case SELECTED_EDGE:
				newColor = gac.getDefaultEdgeSelectionColor();

				break;

			case REV_SELECTED_EDGE:
				newColor = gac.getDefaultEdgeReverseSelectionColor();

				break;

			default:
				newColor = null;

				break;
		}

		return newColor;
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

			exportCellsMenuItem.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						export(false);
					}
				});

			exportTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						export(true);
					}
				});

			selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						selectAll();
					}
				});

			newSelectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
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
							selectedName = (String) getValueAt(rowsSelected[idx], idLocation);

							if (objectType == DataTable.NODES) {
								// Change node color
								Node selectedNode = Cytoscape.getCyNode(selectedName);

								selectedMap.put(selectedName, selectedNode);

								if (Cytoscape.getCurrentNetworkView() != Cytoscape
								                                                                                                                                                                                                                                                                                                                                                                                    .getNullNetworkView()) {
									NodeView nv = Cytoscape.getCurrentNetworkView()
									                       .getNodeView(selectedNode);

									if (nv != null) {
										nv.setSelectedPaint(Color.GREEN);
									}
								}
							} else {
								// Edge selectedEdge =
								// Cytoscape.getCyEdge((String)
								String[] edgeNameParts = selectedName.split(" ");
								String interaction = edgeNameParts[1].substring(1,
								                                                edgeNameParts[1]
								                                                                                                                                                                                                                                                                                                                                                                                                  .length()
								                                                - 1);
								Node source = Cytoscape.getCyNode(edgeNameParts[0]);
								Node target = Cytoscape.getCyNode(edgeNameParts[2]);
								Edge selectedEdge = Cytoscape.getCyEdge(source, target,
								                                        Semantics.INTERACTION,
								                                        interaction, false);
								selectedMap.put(selectedName, selectedEdge);

								if (Cytoscape.getCurrentNetworkView() != Cytoscape
								                                                                                                                                                                                                                                                                                                                                                                                                            .getNullNetworkView()) {
									EdgeView ev = Cytoscape.getCurrentNetworkView()
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
							it = Cytoscape.getCurrentNetwork().getSelectedNodes().iterator();

							while (it.hasNext()) {
								Node curNode = (Node) it.next();
								Node fromMap = (Node) selectedMap.get(curNode.getIdentifier());

								if (fromMap == null) {
									nonSelectedObjects.add(curNode);
								}
							}

							resetObjectColor(idLocation);

							Cytoscape.getCurrentNetwork()
							         .setSelectedNodeState(nonSelectedObjects, false);
						} else {
							it = Cytoscape.getCurrentNetwork().getSelectedEdges().iterator();

							while (it.hasNext()) {
								Edge curEdge = (Edge) it.next();
								Edge fromMap = (Edge) selectedMap.get(curEdge.getIdentifier());

								if (fromMap == null) {
									nonSelectedObjects.add(curEdge);
								}
							}

							resetObjectColor(idLocation);

							Cytoscape.getCurrentNetwork()
							         .setSelectedEdgeState(nonSelectedObjects, false);
						}

						if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
							Cytoscape.getCurrentNetworkView().updateView();
						}
					}
				});

			coloringMenuItem.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent e) {
						if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
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

			rightClickPopupMenu.add(new HyperLinkOut(null));
		}

		return rightClickPopupMenu;
	}

	private void setNewRenderer(boolean colorSwitch) {
		this.setDefaultRenderer(Object.class, new BrowserTableCellRenderer(colorSwitch, objectType));
		this.repaint();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param all DOCUMENT ME!
	 */
	public void export(final boolean all) {
		// Do this in the GUI Event Dispatch thread...
		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					final String name;

					try {
						name = FileUtil.getFile("Export Table", FileUtil.SAVE,
						                        new CyFileFilter[] {  }).toString();
					} catch (Exception exp) {
						// this is because the selection was canceled
						return;
					}

					String export = exportTable(all);
					export = export.replace("[", "\"");
					export = export.replace("]", "\"");

					try {
						final BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name)));
						writer.write(export);
						writer.close();
						export = null;
					} catch (Exception ex) {
						System.out.println("Table Export Write error");
						ex.printStackTrace();
					}
				}
			});
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String exportTable() {
		return exportTable("\t", LS, false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param all DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String exportTable(boolean all) {
		return exportTable("\t", LS, all);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param element_delim DOCUMENT ME!
	 * @param eol_delim DOCUMENT ME!
	 * @param all DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String exportTable(final String element_delim, final String eol_delim, boolean all) {
		if (all == true) {
			this.selectAll();
		}

		final int[] selectedCols = this.getSelectedColumns();
		final StringBuffer buf = new StringBuffer();

		for (int i = 0; i < selectedCols.length; i++) {
			buf.append(this.getColumnName(selectedCols[i]) + "\t");
		}

		buf.append(LS);

		return buf.toString() + copyToClipBoard();
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
					final int column = getColumnModel().getColumnIndexAtX(e.getX());
					final int row = e.getY() / getRowHeight();
					final Object value = getValueAt(row, column);

					// If action is right click, then show edit pop-up menu
					if (SwingUtilities.isRightMouseButton(e)) {
						if (value != null) {
							rightClickPopupMenu.remove(rightClickPopupMenu.getComponentCount() - 1);
							rightClickPopupMenu.add(new HyperLinkOut(value.toString()));
							rightClickPopupMenu.show(e.getComponent(), e.getX(), e.getY());
						}
					} else if (SwingUtilities.isLeftMouseButton(e)
					           && (getSelectedRows().length != 0)) {
						showListContents(e);

						if ((row >= getRowCount()) || (row < 0) || (column >= getColumnCount())
						    || (column < 0))
							return;

						// Object cellValue = getValueAt(row, column);
						if ((value != null) && (value.getClass() == String.class)) {
							URL url = null;

							try {
								url = new URL((String) value);
							} catch (MalformedURLException e1) {
							}

							if (url != null) {
								cytoscape.util.OpenBrowser.openURL(url.toString());
							}
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

					setSelectedColor(SELECTED_NODE);
					setSelectedColor(REV_SELECTED_NODE);
					setSelectedColor(SELECTED_EDGE);
					setSelectedColor(REV_SELECTED_EDGE);

					resetObjectColor(idLocation);

					String selectedName = null;

					for (int idx = 0; idx < rowsSelected.length; idx++) {
						selectedName = (String) getValueAt(rowsSelected[idx], idLocation);

						if (objectType == DataTable.NODES) {
							// Flip the internal flag
							((DataTableModel) dataModel).setSelectionArray(selectedName, true);

							Node selectedNode = Cytoscape.getCyNode(selectedName);

							if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
								NodeView nv = Cytoscape.getCurrentNetworkView()
								                       .getNodeView(selectedNode);

								if (nv != null) {
									nv.setSelectedPaint(reverseSelectedNodeColor);
								}
							}
						} else if (objectType == DataTable.EDGES) {
							// Edge selectedEdge = Cytoscape.getCyEdge((String)
							String[] edgeNameParts = selectedName.split(" ");
							String interaction = edgeNameParts[1].substring(1,
							                                                edgeNameParts[1].length()
							                                                - 1);
							Node source = Cytoscape.getCyNode(edgeNameParts[0]);
							Node target = Cytoscape.getCyNode(edgeNameParts[2]);
							Edge selectedEdge = Cytoscape.getCyEdge(source, target,
							                                        Semantics.INTERACTION,
							                                        interaction, false);

							if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
								EdgeView ev = Cytoscape.getCurrentNetworkView()
								                       .getEdgeView(selectedEdge);

								if (ev != null) {
									ev.setSelectedPaint(reverseSelectedEdgeColor);
								}
							}
						} else {
							// For network, do nothing.
						}
					}

					if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
						Cytoscape.getCurrentNetworkView().updateView();
					}
				}
			});
	}
	
	private Edge getEdge(final String edgeName) {
		
		String[] edgeNameParts = edgeName.split(" (");
		String interaction = edgeNameParts[1].split(") ")[0];
		Node source = Cytoscape.getCyNode(edgeNameParts[0]);
		Node target = Cytoscape.getCyNode(edgeNameParts[2]);
		
		return null;
	}
	

	private void resetObjectColor(int idLocation) {
		for (int idx = 0; idx < dataModel.getRowCount(); idx++) {
			if (objectType == DataTable.NODES) {
				Node selectedNode = Cytoscape.getCyNode((String) dataModel.getValueAt(idx,
				                                                                      idLocation));

				// Set to the original color
				if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
					NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(selectedNode);

					if (nv != null) {
						nv.setSelectedPaint(selectedNodeColor);
					}
				}
			} else if (objectType == DataTable.EDGES) {
				String selectedEdgeName = (String) dataModel.getValueAt(idx, idLocation);
				String[] edgeNameParts = selectedEdgeName.split(" ");
				String interaction = edgeNameParts[1].substring(1, edgeNameParts[1].length() - 1);
				Node source = Cytoscape.getCyNode(edgeNameParts[0]);
				Node target = Cytoscape.getCyNode(edgeNameParts[2]);
				Edge selectedEdge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION,
				                                        interaction, false);

				if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
					EdgeView ev = Cytoscape.getCurrentNetworkView().getEdgeView(selectedEdge);

					if (ev != null) {
						ev.setSelectedPaint(selectedEdgeColor);
					}
				}
			} else {
				// For network attr, we do not have to do anything.
			}
		}

		// Cytoscape.getCurrentNetworkView().updateView();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSortedColumnIndex() {
		return sortedColumnIndex;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSortedColumnAscending() {
		return sortedColumnAscending;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void mouseReleased(MouseEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void mousePressed(MouseEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void mouseClicked(MouseEvent event) {
		int cursorType = getTableHeader().getCursor().getType();

		if ((event.getButton() == MouseEvent.BUTTON1) && (cursorType != Cursor.E_RESIZE_CURSOR)
		    && (cursorType != Cursor.W_RESIZE_CURSOR)) {
			final int index = getColumnModel().getColumnIndexAtX(event.getX());

			if (index >= 0) {
				final int modelIndex = getColumnModel().getColumn(index).getModelIndex();

				final SortTableModel model = (SortTableModel) getModel();

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
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void mouseEntered(MouseEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void mouseExited(MouseEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent event) {
		// TODO Auto-generated method stub
		if (event.getActionCommand().compareTo("Copy") == 0) {
			System.out.println("Cells copied to clipboard.");
			copyToClipBoard();
		}
	}

	// Display elements in te list & map objects
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void showListContents(MouseEvent e) {
		int column = 0;
		int row = 0;
		row = this.getSelectedRow();
		column = this.getSelectedColumn();

		DataTableModel model = (DataTableModel) tableModel;

		// List tester = new ArrayList();
		Class tester = model.getObjectTypeAt(this.getColumnName(column));

		Object value = model.getValueAt(row, column);

		if ((tester != null) && tester.equals(List.class)) {
			int idCol = 0;

			for (int i = 0; i < this.getColumnCount(); i++) {
				if (this.getColumnName(i).equals(DataTable.ID)) {
					idCol = i;

					break;
				}
			}

			String idField = (String) this.getValueAt(row, idCol);

			List contents = (List) model.getAttributeValue(CyAttributes.TYPE_SIMPLE_LIST, idField,
			                                               this.getColumnName(column));
			cellMenu = new JPopupMenu();

			Object[] listItems = contents.toArray();

			if (listItems.length != 0) {
				getCellContentView(CyAttributes.TYPE_SIMPLE_LIST, listItems, idField, e);
			}
		} else if ((value != null) && (value.getClass() == HashMap.class)
		           && model.getValueAt(row, 0).equals(DataTable.NETWORK_METADATA)) {
			NetworkMetaDataDialog mdd = new NetworkMetaDataDialog(Cytoscape.getDesktop(), false,
			                                                      Cytoscape.getCurrentNetwork());
			mdd.setLocationRelativeTo(Cytoscape.getDesktop());
			mdd.setVisible(true);
		} else if ((tester != null) && tester.equals(Map.class)) {
			int idCol = 0;

			for (int i = 0; i < this.getColumnCount(); i++) {
				if (this.getColumnName(i).equals(DataTable.ID)) {
					idCol = i;

					break;
				}
			}

			String idField = (String) this.getValueAt(row, idCol);

			Map<String, Object> contents = (Map) model.getAttributeValue(CyAttributes.TYPE_SIMPLE_MAP,
			                                                             idField,
			                                                             this.getColumnName(column));

			if ((contents != null) && (contents.size() != 0)) {
				Object[] listItems = new Object[contents.size()];
				Object[] keySet = contents.keySet().toArray();

				for (int i = 0; i < contents.keySet().size(); i++) {
					// System.out.println("Key = " + key + ", Val = " +
					// contents.get(key));
					listItems[i] = keySet[i] + " = " + contents.get(keySet[i]);
				}

				cellMenu = new JPopupMenu();
				getCellContentView(CyAttributes.TYPE_SIMPLE_MAP, listItems, idField, e);
			}
		}
	}

	private void getCellContentView(Byte type, Object[] listItems, String idField, MouseEvent e) {
		JMenu curItem = null;

		for (Object item : listItems) {
			curItem = new JMenu(item.toString());
			curItem.add(getPopupMenu());
			curItem.add(new JMenuItem("Copy"));
			curItem.add(new JMenuItem("Edit"));
			// Not yet implemented.
			curItem.getMenuComponent(1).setEnabled(false);
			curItem.getMenuComponent(2).setEnabled(false);

			if (type == CyAttributes.TYPE_SIMPLE_LIST) {
				curItem.add(new HyperLinkOut(item.toString()));
			} else {
				curItem.add(new HyperLinkOut(item.toString().split("=")[1]));
			}

			cellMenu.add(curItem);
		}

		final Border popupBorder = BorderFactory.createTitledBorder(null, idField,
		                                                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                            javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                            null, Color.BLUE);
		cellMenu.setBorder(popupBorder);
		cellMenu.setBackground(Color.WHITE);
		cellMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private String copyToClipBoard() {
		final StringBuffer sbf = new StringBuffer();

		/*
		 * Check to ensure we have selected only a contiguous block of cells.
		 */
		final int numcols = this.getSelectedColumnCount();
		final int numrows = this.getSelectedRowCount();

		final int[] rowsselected = this.getSelectedRows();
		final int[] colsselected = this.getSelectedColumns();

		// Return if no cell is selected.
		if ((numcols == 0) && (numrows == 0)) {
			return null;
		}

		if (!((((numrows - 1) == (rowsselected[rowsselected.length - 1] - rowsselected[0]))
		      && (numrows == rowsselected.length))
		    && (((numcols - 1) == (colsselected[colsselected.length - 1] - colsselected[0]))
		       && (numcols == colsselected.length)))) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Invalid Copy Selection",
			                              "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);

			return null;
		}

		Object tempCell = null;
		String oneCell = null;

		for (int i = 0; i < numrows; i++) {
			for (int j = 0; j < numcols; j++) {
				tempCell = this.getValueAt(rowsselected[i], colsselected[j]);

				sbf.append(tempCell);

				if (j < (numcols - 1))
					sbf.append("\t");
			}

			sbf.append(LS);
		}

		stsel = new StringSelection(sbf.toString());
		systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		systemClipboard.setContents(stsel, stsel);

		return sbf.toString();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if ((e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS)
		    || e.getPropertyName().equals(Cytoscape.SESSION_LOADED)
		    || e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)
		    || e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			
			setSelectedColor(SELECTED_NODE);
			setSelectedColor(REV_SELECTED_NODE);
			setSelectedColor(SELECTED_EDGE);
			setSelectedColor(REV_SELECTED_EDGE);

//			if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
//				System.out.println("############ calling rd @@@@@@@@@@@@@@@@@@@@@@@@@@@");
//				Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
//			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param arg0 DOCUMENT ME!
	 */
	public void onSelectEvent(SelectEvent arg0) {
		setSelectedColor(SELECTED_NODE);
		setSelectedColor(REV_SELECTED_NODE);
		setSelectedColor(SELECTED_EDGE);
		setSelectedColor(REV_SELECTED_EDGE);

		if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
			Cytoscape.getCurrentNetworkView().redrawGraph(false, true);
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void editingStopped(ChangeEvent e) {
		super.editingStopped(e);
		Cytoscape.getVisualMappingManager().getNetworkView().redrawGraph(false, true);
	}
}


/**
 *
 * Cell renderer for preview table.<br>
 * Coloring function is added. This will sync node color and cell colors.<br>
 *
 * @version 0.5
 * @since Cytoscape 2.3
 *
 * @author kono
 *
 */
class BrowserTableCellRenderer extends JLabel implements TableCellRenderer {
	// Define fonts & colors for the cells
	private Font labelFont = new Font("Sans-serif", Font.BOLD, 14);
	private Font normalFont = new Font("Sans-serif", Font.PLAIN, 12);
	private final Color metadataBackground = new Color(255, 210, 255);
	private int type = DataTable.NODES;
	private boolean coloring;

	/**
	 * Creates a new BrowserTableCellRenderer object.
	 *
	 * @param coloring  DOCUMENT ME!
	 * @param type  DOCUMENT ME!
	 */
	public BrowserTableCellRenderer(boolean coloring, int type) {
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

		// set default colorings
		setForeground(table.getForeground());
		setFont(normalFont);
		setBackground(table.getBackground());

		CyAttributes data = null;

		if (type == DataTable.NODES)
			data = Cytoscape.getNodeAttributes();
		else if (type == DataTable.EDGES)
			data = Cytoscape.getEdgeAttributes();
		else if (type == DataTable.NETWORK)
			data = Cytoscape.getNetworkAttributes();
		else

			return this;

		String colName = table.getColumnName(column);

		// check for non-editable columns
		if (!data.getUserEditable(colName)) {
			setBackground(DataTable.NON_EDITIBLE_COLOR);
		}

		if (colName.equals(DataTable.ID)) {
			setFont(labelFont);
			setBackground(DataTable.NON_EDITIBLE_COLOR);
		}

		// handle special NETWORK coloring
		if ((type == DataTable.NETWORK) && (value != null)) {
			if (colName.equals("Network Attribute Name") && !value.equals("Network Metadata")) {
				setFont(labelFont);
				setBackground(DataTable.NON_EDITIBLE_COLOR);
			} else if (value.equals("Network Metadata")) {
				setBackground(metadataBackground);
				setFont(labelFont);
			}
		}

		// if we're not coloring the ID column we're done
		if ((coloring == false) || !colName.equals(DataTable.ID))
			return this;

		// handle colors for the the ID column
		CyNetworkView netview = Cytoscape.getCurrentNetworkView();

		if (type == DataTable.NODES) {
			if (netview != Cytoscape.getNullNetworkView()) {
				NodeView nodeView = netview.getNodeView(Cytoscape.getCyNode((String) table
				                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           .getValueAt(row,
				                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       column)));

				if (nodeView != null) {
					Color nodeColor = (Color) nodeView.getUnselectedPaint();
					setBackground(nodeColor);
				}
			}
		} else if (type == DataTable.EDGES) {
			String edgeName = (String) table.getValueAt(row, column);
			String[] parts = edgeName.split(" ");

			CyNode source = Cytoscape.getCyNode(parts[0].trim());
			CyNode target = Cytoscape.getCyNode(parts[2].trim());
			String interaction = parts[1].trim().substring(1, parts[1].trim().length() - 1);

			if (netview != Cytoscape.getNullNetworkView()) {
				EdgeView edgeView = netview.getEdgeView(Cytoscape.getCyEdge(source, target,
				                                                            Semantics.INTERACTION,
				                                                            interaction, false));

				if (edgeView != null) {
					Color edgeColor = (Color) edgeView.getUnselectedPaint();
					setBackground(edgeColor);
				}
			}
		}

		return this;
	}
}
