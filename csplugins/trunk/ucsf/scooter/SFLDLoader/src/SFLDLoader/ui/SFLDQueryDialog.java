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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import java.net.URL;

// Cytoscape imports
import cytoscape.util.CytoscapeAction;
import cytoscape.actions.LoadNetworkTask;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;
import cytoscape.logger.CyLogger;
import cytoscape.Cytoscape;

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

public class SFLDQueryDialog extends JDialog implements ActionListener, ChangeListener {
	// Dialog components
	private JTable queryTable;
	private	JPanel infoTextPanel;
	private	JPanel settingsPanel;
	private JPanel buttonBox;
	private JPanel tableFrame;
	private JButton loadNetworkButton;
	private JButton closeButton;
	private TitledBorder descTitleBorder;
	private TitledBorder settingsTitleBorder;
	private JSlider slider;
	private JEditorPane sliderLabel;
	private BrowseTableModel tableModel;
	private List<Superfamily> superfamilies;
	private Superfamily selSuper = null;
	private List<Subgroup> selSubgroups = null;
	private List<Family> selFamilies = null;
	private String URLBase = null;
	private String backgroundColor = null;
	private boolean automaticLayout = true;
	private CyLogger logger;

	static final int MAX_CUTOFF = 2;
	static final int MIN_CUTOFF = -150;

	public SFLDQueryDialog(List<Superfamily> superfamilies, String URLBase, CyLogger logger) {
		super(Cytoscape.getDesktop(), "SFLD Browse Interface");	// Create the dialog
		this.superfamilies = superfamilies;
		this.URLBase = URLBase;
		this.logger = logger;

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
		queryTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		queryTable.setDefaultRenderer(Superfamily.class, new SuperFamilyRenderer());
		queryTable.setDefaultRenderer(Subgroup.class, new SubgroupRenderer());
		queryTable.setDefaultRenderer(Family.class, new FamilyRenderer());
		// sorter.setTableHeader(queryTable.getTableHeader());
	
		ListSelectionModel lsm = queryTable.getSelectionModel();
		lsm.addListSelectionListener(tableModel);

		// Put this in a scroll pane
		JScrollPane scrollPane = new JScrollPane(queryTable);
		scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scrollPane.setPreferredSize(new Dimension(700, 200));
		scrollPane.setMinimumSize(new Dimension(700, 10));
		tablePanel.add(scrollPane);

		// Create the border
		Border tableBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(tableBorder, "SFLD Browser");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		tablePanel.setBorder(titleBorder);
		browsePanel.add(tablePanel);

		// Add the description elements
		infoTextPanel = new JPanel();
		JEditorPane description = new JEditorPane();
		JScrollPane descScrollPane = new JScrollPane(description);
		descScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		descScrollPane.setPreferredSize(new Dimension(800, 300));
		descScrollPane.setMinimumSize(new Dimension(700, 10));
		infoTextPanel.add(descScrollPane);

		// Border it
		Border descBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		descTitleBorder = BorderFactory.createTitledBorder(descBorder, "Description of selected item");
		descTitleBorder.setTitlePosition(TitledBorder.LEFT);
		descTitleBorder.setTitlePosition(TitledBorder.TOP);
		infoTextPanel.setBorder(descTitleBorder);
		browsePanel.add(infoTextPanel);

		// Add our settings panel.
		settingsPanel = new JPanel();
		slider = new JSlider(MIN_CUTOFF, MAX_CUTOFF, MAX_CUTOFF);
		slider.setLabelTable(getLabels());
		slider.setPaintLabels(true);
		slider.setPreferredSize(new Dimension(600, 40));
		slider.addChangeListener(this);
		settingsPanel.add(slider);

		sliderLabel = new JEditorPane("text/html",createLabel(MAX_CUTOFF));
		sliderLabel.setEditable(false);
		settingsPanel.add(sliderLabel);

		// Border it
		Border settingsBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		settingsTitleBorder = BorderFactory.createTitledBorder(descBorder, "Blast E-Value Cutoff");
		settingsTitleBorder.setTitlePosition(TitledBorder.LEFT);
		settingsTitleBorder.setTitlePosition(TitledBorder.TOP);
		settingsPanel.setBorder(settingsTitleBorder);
		browsePanel.add(settingsPanel);

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
		buttonBox.setMinimumSize(new Dimension(700, 43));
		buttonBox.setMaximumSize(new Dimension(1000, 43));
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
			int id = 0;

			String loadURL = URLBase+"?query=pairs&scores=pairwise_blast&level=";
			if (selFamilies != null && selFamilies.size() > 0) {
				loadURL = loadURL+"family&id=";
				for (int i = 0; i < selFamilies.size(); i++) {
					id = selFamilies.get(i).getId();
					if (i > 0)
						loadURL = loadURL+","+id;
					else
						loadURL = loadURL+id;
				}
			} else if (selSubgroups != null && selSubgroups.size() > 0) {
				loadURL = loadURL+"subgroup&id=";
				for (int i = 0; i < selSubgroups.size(); i++) {
					id = selSubgroups.get(i).getId();
					if (i > 0)
						loadURL = loadURL+","+id;
					else
						loadURL = loadURL+id;
				}
			} else if (selSuper != null) {
				id = selSuper.getId();
				loadURL = loadURL+"superfamily&id="+id;
			} else {
				return;
			}

			loadURL = loadURL+"&cutoff=1e"+slider.getValue();
			// Load it
			try {
				logger.info("Loading "+loadURL);
				if (!automaticLayout) {
					LoadNetworkTask.loadURL(new URL(loadURL), false);
				} else {
					LoadNetworkTask.loadURL(new URL(loadURL), false, configureLayout());
				}
			} catch (Exception ex) {
				logger.error("Unable to load URL '"+loadURL+"': "+ex.getMessage());
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		// Get the value
		int val = slider.getValue();

		// Update the label
		sliderLabel.setText(createLabel(val));
	}

	public void getDescription(Object group) {
		String URI = URLBase+"?query=description&";
		if (group != null && group.getClass() == Superfamily.class) {
			Superfamily sf = (Superfamily)group;
			URI = URI + "level=superfamily&id="+sf.getId();
		} else if (group != null && group.getClass() == Subgroup.class) {
			Subgroup sg = (Subgroup)group;
			if (sg.getId() < 0) {
				URI = URI + "level=family&id="+(-sg.getId());
			} else {
				URI = URI + "level=subgroup&id="+sg.getId();
			}
		} else if (group != null && group.getClass() == Family.class) {
			Family fam = (Family)group;
			URI = URI + "level=family&id="+fam.getId();
		}
		infoTextPanel.removeAll();
		JEditorPane description = null;
		try {
			if (group == null) 
				description = new JEditorPane("text/plain",null);
			else
				description = new JEditorPane(URI);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		JScrollPane scrollPane = new JScrollPane(description);
		scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scrollPane.setPreferredSize(new Dimension(800, 300));
		scrollPane.setMinimumSize(new Dimension(700, 10));
		infoTextPanel.add(scrollPane);
		pack();
		setVisible(true);
		infoTextPanel.repaint();
	}
			
	private Hashtable<Integer,Component> getLabels() {
		Hashtable <Integer, Component> labelTable = new Hashtable(100);
		JEditorPane label1 = new JEditorPane("text/html", createLabel(MIN_CUTOFF));
		label1.setEditable(false);
		labelTable.put(new Integer(MIN_CUTOFF), label1);

		JEditorPane label2 = new JEditorPane("text/html", createLabel(MAX_CUTOFF));
		label2.setEditable(false);
		labelTable.put(new Integer(MAX_CUTOFF), label2);
		return labelTable;
	}

	private String createLabel(int val) {
		if (backgroundColor == null) {
			Color bg = settingsPanel.getBackground();
			backgroundColor = "\"#"+Integer.toHexString(bg.getRed())
			                       +Integer.toHexString(bg.getGreen())
			                       +Integer.toHexString(bg.getBlue())+"\"";
		}
		return "<html bgcolor="+backgroundColor+">1x10<sup>"+val+"</sup></html>";
	}

	private CyLayoutAlgorithm configureLayout() {
		// Get the force-directed layout (if available)
		CyLayoutAlgorithm layoutAlgorithm = CyLayouts.getLayout("force-directed");
		if (layoutAlgorithm != null) {
    	LayoutProperties propertyList = layoutAlgorithm.getSettings();
    	if (propertyList == null)
     		return CyLayouts.getDefaultLayout();
			{
				Tunable tunable = propertyList.get("edge_attribute");
				tunable.setValue("BlastProbability");
				layoutAlgorithm.updateSettings();
			}
			{
				Tunable tunable = propertyList.get("defaultSpringCoefficient");
				tunable.setValue("5");
				layoutAlgorithm.updateSettings();
			}
			{
				Tunable tunable = propertyList.get("defaultSpringLength");
				tunable.setValue("30");
				layoutAlgorithm.updateSettings();
			}
			{
				Tunable tunable = propertyList.get("defaultNodeMass");
				tunable.setValue("30");
				layoutAlgorithm.updateSettings();
			}
		} else {
			return CyLayouts.getDefaultLayout();
		}
		return layoutAlgorithm;
	}

	public class BrowseTableModel extends AbstractTableModel 
	                              implements ListSelectionListener {
		final String[] columnNames = {"Superfamily","SubGroup","Family"};
		SFLDQueryDialog dialog;

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
			if (selSubgroups != null && selSubgroups.size() == 1)
				count = Math.max(count, selSubgroups.get(0).getFamilyCount());
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
			else if (c == 1)
				return Subgroup.class;
			else if (c == 2)
				return Family.class;
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
				return selSuper.getSubgroup(row);
			} else if (col == 2 
			           && selSubgroups != null 
			           && selSubgroups.size() == 1 
			           && row < selSubgroups.get(0).getFamilyCount()) {
				return selSubgroups.get(0).getFamily(row);
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
				int[] selectedRows = queryTable.getSelectedRows();
				// Update the text field for this item
				// Fill out next column over (if appropriate)
				if (selectedCol == 0) {
					selSuper = superfamilies.get(selectedRows[0]);
					selSubgroups = new ArrayList<Subgroup>();
					lsm.clearSelection();
					descTitleBorder.setTitle("Description of superfamily "+selSuper.getName());
					getDescription(selSuper);
					fireTableDataChanged();
				} else if (selectedCol == 1 && selSuper != null) {
					// Handle subgroup selection
					selFamilies = new ArrayList<Family>();
					if (selectedRows.length == 1) {
						Subgroup sg = selSuper.getSubgroup(selectedRows[0]);
						if (selSubgroups.size() > 1 && selSubgroups.contains(sg)) {
							// Deselect
							selSubgroups.remove(sg);
						} else {
							selSubgroups.add(sg);
						}
					} else {
						for (int i = 0; i < selectedRows.length; i++) {
							selSubgroups.add(selSuper.getSubgroup(selectedRows[i]));
						}
					}
					if (selSubgroups.size() == 1 && selFamilies.size() == 0) {
						getDescription(selSubgroups.get(0));
						descTitleBorder.setTitle("Description of subgroup "+selSubgroups.get(0).getName());
					} else {
						descTitleBorder.setTitle("Multiple subgroups");
						getDescription(null);
					}
					fireTableDataChanged();
				} else if (selectedCol == 2 && selSubgroups != null && selSubgroups.size() == 1) {
					// Handle family selection
					Subgroup selSubgroup = selSubgroups.get(0);
					if (selectedRows.length == 1) {
						Family fam = selSubgroup.getFamily(selectedRows[0]);
						if (selFamilies.size() > 1 && selFamilies.contains(fam)) {
							// Deselect
							selFamilies.add(fam);
						} else {
							selFamilies.add(fam);
						}
					} else {
						for (int i = 0; i < selectedRows.length; i++) {
							selFamilies.add(selSubgroup.getFamily(selectedRows[i]));
						}
					}

					if (selFamilies.size() == 1) {
						getDescription(selFamilies.get(0));
						descTitleBorder.setTitle("Description of family "+selFamilies.get(0).getName());
					} else {
						descTitleBorder.setTitle("Multiple families");
						getDescription(null);
					}
				}
				loadNetworkButton.setEnabled(true);
				repaint();
			}
		}
	}

	public class SuperFamilyRenderer extends DefaultTableCellRenderer {
		public SuperFamilyRenderer() {
			super();
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			if (value != null) {
				Superfamily superfamily = (Superfamily) value;
				String label = null;
				if (isSelected || superfamily == selSuper) {
					label = "<html><b>"+superfamily.getName()+"</b>    &#8594;</html>";
					setBackground(table.getSelectionBackground());
				} else {
					label = "<html>" + superfamily.getName()+"</html>";
					setBackground(table.getBackground());
				}

				Component cell = super.getTableCellRendererComponent(table, label, 
				                                                     isSelected, hasFocus,
				                                                     row, col);
				return cell;
			}
			return null;
		}
	}

	public class SubgroupRenderer extends DefaultTableCellRenderer {
		public SubgroupRenderer() {
			super();
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			if (value != null) {
				Subgroup subgroup = (Subgroup) value;
				String label = null;
				if (isSelected || (selSubgroups != null && selSubgroups.contains(subgroup))) {
					setBackground(table.getSelectionBackground());
					if (selSubgroups != null && selSubgroups.size() == 1) {
						label = "<html><b>"+subgroup.getName()+"</b>   &#8594;</html>";
					} else
						label = "<html><b>"+subgroup.getName()+"</b></html>";
				} else {
					label = "<html>" + subgroup.getName()+"</html>";
					setBackground(table.getBackground());
				}

				Component cell = super.getTableCellRendererComponent(table, label, 
				                                                     isSelected, hasFocus,
				                                                     row, col);
				return cell;
			}
			return null;
		}
	}

	public class FamilyRenderer extends DefaultTableCellRenderer {
		public FamilyRenderer() {super();}

		public Component getTableCellRendererComponent(JTable table, Object value,
		                                               boolean isSelected, boolean hasFocus,
		                                               int row, int col) {
			if (value != null) {
				Family family = (Family) value;
				String label = null;
				if (isSelected || (selFamilies != null && selFamilies.contains(family))) {
					setBackground(table.getSelectionBackground());
					label = "<html><b>"+family.getName()+"</b></html>";
				} else {
					label = "<html>" + family.getName()+"</html>";
					setBackground(table.getBackground());
				}

				Component cell = super.getTableCellRendererComponent(table, label, 
				                                                     isSelected, hasFocus,
				                                                     row, col);
				return cell;
			}
			return null;
		}
	}
}
