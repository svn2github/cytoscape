/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package SFLDLoader.ui;

// System imports
import java.util.List;
import java.util.Iterator;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.Position;
import javax.swing.border.*;
import javax.swing.WindowConstants.*;

// Cytoscape imports
import cytoscape.*;
import cytoscape.util.CytoscapeAction;

// SFLDLoader imports
import SFLDLoader.model.*;

/**
 * The SFLDQueryDialog provides the user interface for SFLDLoader.  The
 * interface allows the users to browse through the SFLD database in a
 * hierarchical fashion and display certain (limited) information about
 * each superfamily, subgroup, and family.  It also provides the capability
 * to load the XGMML for the entire superfamily or a single subgroup or family
 * within that superfamily.
 *
 * The components of the SFLDQueryDialog include a table with three columns
 * (one for each of superfamily, subgroup, and family).  When we first create
 * the queryDialog, we initialize the table by querying the SFLD.  This 
 * takes quite awhile and so we pop up a progress bar.  There is also a
 * text field with information about the selected group, and two control
 * buttons: one to dismiss the dialog, and one to load the network.
 * 
 */

public class SFLDQueryDialog extends JDialog implements ActionListener {
	// Dialog components
	private JTable queryTable;
	private JTextPane description;
	private JPanel buttonBox;
	private JPanel tableFrame;
	private JButton loadNetworkButton;
	private JButton closeButton;
	private TitledBorder descTitleBorder;
	private BrowseTableModel tableModel;
	private List<Superfamily> superfamilies;

	public SFLDQueryDialog(List<Superfamily> superfamilies) {
		super();	// Create the dialog
		this.superfamilies = superfamilies;

		setTitle("SFLD Browse Interface");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// Create the panel for the main content
		JPanel browsePanel = new JPanel();
		BoxLayout layout = new BoxLayout(browsePanel, BoxLayout.PAGE_AXIS);
		browsePanel.setLayout(layout);

		// Create the panel for the browse table
		JPanel tablePanel = new JPanel();

		// Create the table model
		tableModel = new BrowseTableModel(this);

		// Create the table sorter
		// TableSorter sorter = new TableSorter(tableModel);

		// Create the table
		queryTable = new JTable(tableModel);
		// Customize our table
		queryTable.setRowSelectionAllowed(false);
		queryTable.setColumnSelectionAllowed(false);
		queryTable.setCellSelectionEnabled(true);
		queryTable.setShowHorizontalLines(false);
		queryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queryTable.setDefaultRenderer(Superfamily.class, new SuperFamilyRenderer());
		queryTable.setDefaultRenderer(Subgroup.class, new SubgroupRenderer());
		queryTable.setDefaultRenderer(Family.class, new FamilyRenderer());
		// sorter.setTableHeader(queryTable.getTableHeader());
	
		ListSelectionModel lsm = queryTable.getSelectionModel();
		lsm.addListSelectionListener(tableModel);

		// Put this in a scroll pane
		JScrollPane scrollPane = new JScrollPane(queryTable);
		queryTable.setPreferredScrollableViewportSize(new Dimension(500,500));
		tablePanel.add(scrollPane);

		// Create the border
		Border tableBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(tableBorder, "SFLD Browser");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		tablePanel.setBorder(titleBorder);
		browsePanel.add(tablePanel);

		// Add the description elements
		JPanel infoTextPanel = new JPanel();
		description = new JTextPane();
		description.setEditable(false);
		infoTextPanel.add(description);

		// Border it
		Border descBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		descTitleBorder = BorderFactory.createTitledBorder(descBorder, "Description of selected item");
		descTitleBorder.setTitlePosition(TitledBorder.LEFT);
		descTitleBorder.setTitlePosition(TitledBorder.TOP);
		infoTextPanel.setBorder(descTitleBorder);
		browsePanel.add(infoTextPanel);

		// Last component -- our button box
    JPanel buttonBox = new JPanel();
    JButton doneButton = new JButton("Done");
    doneButton.setActionCommand("done");
    doneButton.addActionListener(this);

    loadNetworkButton = new JButton("Load");
    loadNetworkButton.setActionCommand("load");
    loadNetworkButton.setEnabled(false);
    loadNetworkButton.addActionListener(this);
    buttonBox.add(doneButton);
    buttonBox.add(loadNetworkButton);
    buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    browsePanel.add(buttonBox);
    setContentPane(browsePanel);
	}

	/**
	 * The action listener for the buttons
	 */
	public void actionPerformed(ActionEvent e) {
		if ("done".equals(e.getActionCommand())) {
			setVisible(false);
		}
		else if ("load".equals(e.getActionCommand())) {
			// Get the currently selected item
			// Load it
		}
	}

	public class BrowseTableModel extends AbstractTableModel 
	                              implements ListSelectionListener {
		final String[] columnNames = {"Superfamily","SubGroup","Family"};
		SFLDQueryDialog dialog;
		Superfamily selSuper = null;
		Subgroup selSubgroup = null;
		Family selFamily = null;

		public BrowseTableModel(SFLDQueryDialog dialog) {
			this.dialog = dialog;
		}

		/**
		 * Return the number of rows in the table
		 */
		public int getRowCount() {
			int count = superfamilies.size();
			if (selSuper != null)
				count = Math.max(count, selSuper.getSubgroupCount());
			if (selSubgroup != null)
				count = Math.max(count, selSubgroup.getFamilyCount());
			return count;
		}

		/**
		 * Return the number of columns in the table
		 */
		public int getColumnCount() {
			return 3;
		}

	  /**
		 * This method indicates whether this cell is editable.  We
		 * always return false.
		 *
		 * @param row row number as an integer
		 * @param col column number as an integer
		 * @return false
		 */
	  public boolean isCellEditable(int row, int col) {return false;}

		/**
		 * Return the name of a column.
		 *
		 * @param col column number as an integer
		 * @return column name as a String
		 */
	 	 public String getColumnName(int col) {
	 	   return columnNames[col];
	 	 }
	
		/**
		 * Get the object class of a column.  This is used to determine how
		 * the columns will be displayed
		 *
		 * @param c the column number as an integer
		 * @return object Class of this column
		 */
		public Class getColumnClass(int c) {
			if (c == 0) 
				return Superfamily.class;
			return String.class;
		}

		/**
		 * Return the value at the requested row and column.  In our case
		 * the row provides information about our Structure and the column
		 * indicates the specific data we want.
		 *
		 * @param row the row number
		 * @param col the column number
		 * @return an Object which represents the value at the requested
		 * row and column
		 */
	  public Object getValueAt(int row, int col) {
			if (col == 0 && row < superfamilies.size()) {
				return superfamilies.get(row);
			} else if (col == 1 && selSuper != null && row < selSuper.getSubgroupCount()) {
				return selSuper.getSubgroup(row).getName();
			} else if (col == 2 && selSubgroup != null && row < selSubgroup.getFamilyCount()) {
				return selSubgroup.getFamily(row).getName();
			}
			return null;
		}


		/**
		 * This method is called whenever a value in the table is changed.
 	   * It is used to detect selection and add the selection to the list
 	   * of structures to be used for the alignment
 	   *
 	   * @param e a ListSelectionEvent
 	   */
		public void valueChanged (ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}

			ListSelectionModel lsm = (ListSelectionModel)e.getSource();
			// Get the selected item
			if (lsm.isSelectionEmpty()) {
				loadNetworkButton.setEnabled(false);
			} else {
				// Figure out what is selected
				int selectedCol = queryTable.getSelectedColumn();
				int selectedRow = queryTable.getSelectedRow();
				// Update the text field for this item
				// Fill out next column over (if appropriate)
				if (selectedCol == 0) {
					selSuper = superfamilies.get(selectedRow);
					selSubgroup = null;
					selFamily = null;
					fireTableDataChanged();
				} else if (selectedCol == 1 && selSuper != null) {
					selSubgroup = selSuper.getSubgroup(selectedRow);
					selFamily = null;
					fireTableDataChanged();
				} else if (selectedCol == 2 && selSubgroup != null) {
					selFamily = selSubgroup.getFamily(selectedRow);
				}
				loadNetworkButton.setEnabled(true);
			}
		}
	}

	public class SuperFamilyRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			if (value != null) {
				Superfamily superfamily = (Superfamily) value;
				JLabel label = new JLabel(superfamily.getName());
				if (isSelected) {
					label.setForeground(Color.WHITE);
					label.setBackground(Color.BLACK);
				}
				return label;
			}
			return null;
		}
	}

	public class SubgroupRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			return null;
		}
	}

	public class FamilyRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			return null;
		}
	}
}
