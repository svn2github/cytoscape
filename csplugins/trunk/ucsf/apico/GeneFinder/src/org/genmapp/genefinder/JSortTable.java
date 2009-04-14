/*
 * $Archive: SourceJammer$
 * $FileName: JSortTable.java$
 * $FileID: 3984$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2005/12/13 00:42:44 $
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

import giny.model.GraphObject;

import java.awt.Cursor;
import java.awt.Dimension;
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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.cytopanels.CytoPanelState;

public class JSortTable extends JTable implements MouseListener, ActionListener {
	protected int sortedColumnIndex = -1;
	protected boolean sortedColumnAscending = true;

	// For right-click menu
	private JPopupMenu rightClickPopupMenu;

	private JPopupMenu cellMenu;

	private JMenuItem jMenuItem = null;
	private JMenuItem jMenuItem1 = null;
	private JMenu jMenu2 = null;
	private JMenuItem jMenuItem3 = null;
	private JMenuItem jMenuItem4 = null;
	private JMenuItem jMenuItem5 = null;

	private JCheckBoxMenuItem showAdvancedWindow = null;

	CopyToExcel excelHandler;
	private Clipboard systemClipboard;

	StringSelection stsel;

	MultiDataEditAction edit;

	SortTableModel tableModel;

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

			jMenuItem = new JMenuItem("Copy");
			// jMenuItem1 = new JMenuItem("Clear This Row");

			jMenu2 = new JMenu("Export...");
			jMenuItem3 = new JMenuItem("Selected Cells");
			jMenuItem4 = new JMenuItem("Entire Table");
			jMenuItem5 = new JMenuItem("Select All");
			showAdvancedWindow = new JCheckBoxMenuItem("Show Advanced Window");
			jMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.out.println("Cells copied to clipboard.");
					copyToClipBoard();
				}
			});

			showAdvancedWindow
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							// System.out.println("actionPerformed()"); // TODO
							// Auto-generated Event stub actionPerformed()
							if (showAdvancedWindow.isSelected() == true) {
								Cytoscape.getDesktop().getCytoPanel(
										SwingConstants.EAST).setState(
										CytoPanelState.FLOAT);
							} else {
								Cytoscape.getDesktop().getCytoPanel(
										SwingConstants.EAST).setState(
										CytoPanelState.HIDE);
							}
						}
					});

			jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// System.out.println("actionPerformed()"); // TODO
					// Auto-generated Event stub actionPerformed()
					export(false);
				}
			});

			// jMenuItem1.addActionListener(new java.awt.event.ActionListener()
			// {
			// public void actionPerformed(java.awt.event.ActionEvent e) {
			// // System.out.println("actionPerformed()"); // TODO
			// // Auto-generated Event stub actionPerformed()
			// clearRow();
			//
			// }
			// });

			jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// System.out.println("actionPerformed()"); // TODO
					// Auto-generated Event stub actionPerformed()
					export(true);
				}
			});

			jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// System.out.println("actionPerformed()"); // TODO
					// Auto-generated Event stub actionPerformed()
					selectAll();
				}
			});

			jMenu2.add(jMenuItem3);
			jMenu2.add(jMenuItem4);

			rightClickPopupMenu.add(jMenuItem);
			// rightClickPopupMenu.add(jMenuItem1);
			rightClickPopupMenu.add(jMenuItem5);
			rightClickPopupMenu.add(jMenu2);
			// rightClickPopupMenu.add(jMenuItem3);
			rightClickPopupMenu.addSeparator();
			rightClickPopupMenu.add(showAdvancedWindow);
			// rightClickPopupMenu1.add(getJMenuItem4());
		}
		return rightClickPopupMenu;
	}

	private JPopupMenu getPopupMenu2() {
		if (cellMenu == null) {
			cellMenu = new JPopupMenu();

			JList listContents = new JList();

		}
		return cellMenu;
	}

	private void clearRow() {
		// edit = new MultiDataEditAction( null,
		// "Delete",
		// this.tableModel.getO
		// tableModel.getObjects(),
		// (String)attributeDeleteBox.getSelectedItem(),
		// null,
		// null,
		// graphObjectType,
		// tableModel );
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
				} else {
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
						//System.out.println("##### " + urle.getMessage());
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
			}
		});

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

	public void showListContents(MouseEvent e) {
		int column = 0;
		int row = 0;
		row = this.getSelectedRow();
		column = this.getSelectedColumn();
		DataTableModel model = (DataTableModel) tableModel;

		// List tester = new ArrayList();
		Class tester = model.getObjectTypeAt(this.getColumnName(column));

		if (tester != null && tester.equals(List.class)) {
			int idCol = 0;
			for (int i = 0; i < this.getColumnCount(); i++) {
				if (this.getColumnName(i).equals("ID")) {
					idCol = i;
					break;
				}
			}

			String idField = (String) this.getValueAt(row, idCol);
			// System.out.println("ID is col number: " + idCol + " name = " +
			// idField );

			List contents = (List) model.getAttributeValue(
					CyAttributes.TYPE_SIMPLE_LIST, idField, this
							.getColumnName(column));
			cellMenu = new JPopupMenu();
			List arrayList = new ArrayList();
			arrayList = contents;
			Object[] listItems = arrayList.toArray();
			if (listItems.length != 0) {
				JList listContents = new JList(listItems);
				listContents.setSize(new Dimension(180, 50));
				listContents
						.setBorder(javax.swing.BorderFactory
								.createTitledBorder(
										null,
										idField,
										javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
										javax.swing.border.TitledBorder.DEFAULT_POSITION,
										null, null));

				cellMenu.add(listContents);
				cellMenu.show(e.getComponent(), e.getX(), e.getY());
			}
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
