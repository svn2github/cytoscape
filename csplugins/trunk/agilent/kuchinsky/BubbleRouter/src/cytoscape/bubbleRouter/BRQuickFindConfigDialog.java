package cytoscape.bubbleRouter;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/**
 * BubbleRouter Dialog is the primary user interface, providing attribute
 * selection, description and values. You can also load an attribute file from
 * within the dialog.
 * 
 * @author Allan Kuchinsky, Alexander Pico, Kristina Hanspers. Adapted from code
 *         by Ethan Cerami.
 */
@SuppressWarnings("serial")
public class BRQuickFindConfigDialog extends JDialog {

	/**
	 * Pull down list of attributes associated with network.
	 */
	private JComboBox attributeComboBox;

	/**
	 * Currently selected attribute from combobox.
	 */
	private static String currentAttribute;

	/**
	 * Attribute description text area.
	 */
	private JTextArea attributeDescriptionBox;

	/**
	 * Table of attribute values (maximum of 50 values collected).
	 */
	private JSortTable attributeValuesTable;

	/**
	 * List of one or more attribute values selected from table.
	 */
	private static ArrayList<Object> selectedValues = new ArrayList<Object>();

	/**
	 * Layout Region Dimensions
	 */
	Double x, y; // Top left corner of the rectangle.

	Double w, h; // Width and height of the rectangle.

	/**
	 * Buttons and Text Labels
	 */
	private JButton applyButton;

	private JButton fileSelectButton;

	private JButton helpButton;

	private JButton cancelButton;

	private static final String APPLY_BUTTON_TEXT = "Select";

	private static final String FILE_SELECT_BUTTON_TEXT = "Load attributes from file";

	private static final String HELP_BUTTON_TEXT = "Help";

	private static final String CANCEL_BUTTON_TEXT = "Cancel";

	/**
	 * URL for BubbleRouter manual.
	 */
	private String helpURL = "http://www.genmapp.org/BubbleRouter/manual.htm";

	/**
	 * Constructor.
	 * 
	 * Returns region attribute name and values.
	 */
	public BRQuickFindConfigDialog(double x, double y, double width,
			double height) {

		/**
		 * Set layout region dimension variables
		 */
		this.x = x;
		this.y = y;
		this.w = width;
		this.h = height;

		/**
		 * Initialize, based on current network
		 */
		Container container = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setTitle("Select Attribute for Interactive Layout");

		/**
		 * If we are working on Linux, set always on top to true. // This is a
		 * hack to deal with numerous z-ordering bugs on Linux.
		 */
		String os = System.getProperty("os.name");
		if (os != null) {
			if (os.toLowerCase().startsWith("linux")) {
				this.setAlwaysOnTop(true);
			}
		}

		/**
		 * Create Master Panel
		 */
		JPanel masterPanel = new JPanel();
		masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

		/**
		 * Add Attribute ComboBox Panel
		 */
		JPanel attributePanel = createAttributeSelectionPanel();
		masterPanel.add(attributePanel);

		/**
		 * Add Attribute Description Panel
		 */
		JPanel attributeDescriptionPanel = createAttributeDescriptionPanel();
		masterPanel.add(attributeDescriptionPanel);

		/**
		 * Add Attribute Values Panel
		 */
		JPanel attributeValuePanel = createAttributeValuePanel();
		masterPanel.add(attributeValuePanel);

		/**
		 * Add Button Panel
		 */
		masterPanel.add(Box.createVerticalGlue());
		JPanel buttonPanel = createButtonPanel();
		masterPanel.add(buttonPanel);
		container.add(masterPanel);

		/**
		 * Pack, set modality, and center on screen
		 */
		pack();
		setModal(true);
		setLocationRelativeTo(Cytoscape.getDesktop());
		setVisible(true);
	}

	/**
	 * Enable / Disable Apply Button.
	 * 
	 * @param enable
	 *            Enable flag;
	 */
	void enableApplyButton(boolean enable) {
		if (applyButton != null) {
			applyButton.setEnabled(enable);
		}
	}

	/**
	 * Creates Button Panel.
	 * 
	 * @return JPanel Object.
	 */
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		/**
		 * Help Button
		 */
		helpButton = new JButton(HELP_BUTTON_TEXT);
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cytoscape.util.OpenBrowser.openURL(helpURL);
			}
		});

		/**
		 * Cancel Button
		 */
		cancelButton = new JButton(CANCEL_BUTTON_TEXT);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BRQuickFindConfigDialog.this.setVisible(false);
				BRQuickFindConfigDialog.this.dispose();
			}
		});

		/**
		 * Apply Button
		 */
		applyButton = new JButton(APPLY_BUTTON_TEXT);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyAction(selectedValues);
			}
		});

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(helpButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
		buttonPanel.add(cancelButton);
		buttonPanel.add(applyButton);

		return buttonPanel;
	}

	/**
	 * Creates a Panel to show the currently selected attribute description.
	 * 
	 * @return JPanel Object.
	 */
	private JPanel createAttributeDescriptionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Attribute Description:"));
		panel.setLayout(new BorderLayout());
		attributeDescriptionBox = new JTextArea(5, 40);
		attributeDescriptionBox.setEditable(false);
		attributeDescriptionBox.setLineWrap(true);
		attributeDescriptionBox.setWrapStyleWord(true);
		JScrollPane scrollPane = new JScrollPane(attributeDescriptionBox);
		panel.add(scrollPane, BorderLayout.CENTER);
		setAttributeDescription();
		return panel;
	}

	/**
	 * Creates a Panel of Attribute Values.
	 * 
	 * @return JPanel Object.
	 */
	private JPanel createAttributeValuePanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder("Attribute Values:"));
		panel.setLayout(new GridLayout(1, 0));

		/**
		 * Table Cells are not editable
		 */
		attributeValuesTable = new JSortTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		/**
		 * Add Action Listener to Table for Double-Click Selection
		 */
		attributeValuesTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					applyAction(selectedValues);
				}
			}
		});

		addSortTableModel(attributeValuesTable);
		this.setVisibleRowCount(attributeValuesTable, 7);
		JScrollPane scrollPane = new JScrollPane(attributeValuesTable);
		panel.add(scrollPane);
		return panel;
	}

	/**
	 * Sets Text for Attribute Description Box.
	 */
	private void setAttributeDescription() {
		Object selectedAttribute = attributeComboBox.getSelectedItem();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attributeKey;
		if (selectedAttribute != null) {
			attributeKey = selectedAttribute.toString();
		} else {
			attributeKey = "NO_SELECTION";
		}
		String description;
		if (attributeKey.equals(Semantics.CANONICAL_NAME)) {
			description = "Each node and edge in Cytoscape is assigned a "
					+ "unique identifier.  This is an alphanumeric value.";
		} else if (attributeKey.equals("NO_SELECTION")) {
			description = "Select an attribute above.";

		} else {
			description = nodeAttributes.getAttributeDescription(attributeKey);
		}
		if (description == null) {
			description = "No description available.";
		}
		attributeDescriptionBox.setText(description);
		attributeDescriptionBox.setCaretPosition(0);
	}

	/**
	 * Creates TableModel consisting of Distinct Attribute Values.
	 */
	private void addSortTableModel(JSortTable table) {
		Object selectedAttribute = attributeComboBox.getSelectedItem();
		
		// reset value selection whenever a new attribute is selection
		selectedValues.clear();
		
		/**
		 * Determine current attribute key
		 */
		String attributeKey;
		if (selectedAttribute != null) {
			attributeKey = selectedAttribute.toString();
		} else {
			attributeKey = "NO_SELECTION";
		}

		/**
		 * Create column names.
		 */
		Vector<Object> columnNames = new Vector<Object>();
		columnNames.add(attributeKey);

		/**
		 * Collect node attributes.
		 */
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator nodeIterator = network.nodesIterator();

		/**
		 * Set maximum number of collected values here (e.g., 50).
		 */
		String valueSet[] = BRCyAttributesUtil.getDistinctAttributeValues(
				nodeIterator, nodeAttributes, attributeKey, 50);

		/**
		 * Add "unassigned" to list.
		 */
		ArrayList<String> finalValues = new ArrayList<String>();
		finalValues.add("unassigned");

		/**
		 * Split up comma-separated lists into individual values.
		 */
		String splitValueSet[] = null;
		for (int i = 0; i < (valueSet.length); i++) {
			splitValueSet = valueSet[i].split(", ");
			for (int j = 0; j < (splitValueSet.length); j++) {
				if (!finalValues.contains(splitValueSet[j])) {
					finalValues.add(splitValueSet[j]);
				}
			}
		}

		/**
		 * Populate table model with first 50 values.
		 */
		TableModel model = new DefaultSortTableModel(columnNames, finalValues
				.toArray().length);

		if (finalValues != null && finalValues.toArray().length > 0) {
			for (int i = 0; i < ((finalValues.toArray().length >= 50) ? 50
					: finalValues.toArray().length); i++) {
				model.setValueAt(finalValues.toArray()[i], i, 0);
			}
		}

		table.setModel(model);
		table.setAutoscrolls(true);

		/**
		 * Define default selection and allow multiple selection.
		 */
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					selectedValues.clear();
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						selectedValues.add(attributeValuesTable.getModel()
								.getValueAt(0, 0));
					} else {
						int minIndex = lsm.getMinSelectionIndex();
						int maxIndex = lsm.getMaxSelectionIndex();
						for (int i = minIndex; i <= maxIndex; i++) {
							if (lsm.isSelectedIndex(i)) {
								selectedValues.add(attributeValuesTable
										.getModel().getValueAt(i, 0));
							}
						}
					}

				}
			}

		});
	}

	/**
	 * Creates the Attribute Selection Panel.
	 * 
	 * @return JPanel Object.
	 */
	private JPanel createAttributeSelectionPanel() {
		JPanel attributePanel = new JPanel();
		attributePanel
				.setLayout(new BoxLayout(attributePanel, BoxLayout.X_AXIS));

		/**
		 * Add shortcut button in BubbleRouter dialog for loading node attribute
		 * file
		 */
		fileSelectButton = new JButton(FILE_SELECT_BUTTON_TEXT);
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/**
				 * Use a Default CyFileFilter: enables user to select any file
				 * type.
				 */
				CyFileFilter nf = new CyFileFilter();

				/**
				 * Get the file name
				 */
				File[] files = FileUtil.getFiles("Import Node Attributes",
						FileUtil.LOAD, new CyFileFilter[] { nf });
				

				
				if (files != null) {
					
					/**
					 * Create Load Attributes Task
					 */
					ImportAttributesTask task = new ImportAttributesTask(files,
							ImportAttributesTask.NODE_ATTRIBUTES);

					/**
					 * Configure JTask Dialog Pop-Up Box
					 */
					JTaskConfig jTaskConfig = new JTaskConfig();
					jTaskConfig.setOwner(Cytoscape.getDesktop());
					jTaskConfig.displayCloseButton(true);
					jTaskConfig.displayStatus(true);
					jTaskConfig.setAutoDispose(false);

					/**
					 * Execute Task in New Thread; pop open JTask Dialog Box.
					 */
					TaskManager.executeTask(task, jTaskConfig);

					/**
					 * Identify newly added attribute by comparing current
					 * attributeComboBox items (oldItems) against current node
					 * attributes (newItems), then add newItem to combo box.
					 */
					Vector<String> oldList = new Vector<String>();
					for (int i = 0; i < attributeComboBox.getItemCount(); i++) {
						Object item = attributeComboBox.getItemAt(i);
						oldList.add(item.toString());
					}

					Vector<String> newList = getBubbleAttributes();
					String newItem = null;

					Iterator<String> it = newList.iterator();
					while (it.hasNext()) {
						String item = it.next();
						if (!oldList.contains(item)) {
							newItem = item;
						}
					}
					attributeComboBox.addItem(newItem);
				}

			}

		});

		/**
		 * Obtain Node Attributes
		 */
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

		if (nodeAttributes.getAttributeNames() != null) {
			JLabel label = new JLabel("Layout by Attribute:  ");
			label.setBorder(new EmptyBorder(5, 5, 5, 5));
			attributePanel.add(label);

			/**
			 * Create ComboBox
			 */
			attributeComboBox = new JComboBox(getBubbleAttributes());
			/**
			 * simply set to first in list for now; later set to // current
			 * attribute for regions that have been previously assigned.
			 */
			if (currentAttribute == null) {
				currentAttribute = attributeComboBox.getItemAt(0).toString();
			}
			attributeComboBox.setSelectedItem(currentAttribute);

			attributePanel.add(attributeComboBox);
			attributePanel.add(Box.createHorizontalGlue());
			attributePanel.add(fileSelectButton);

			/**
			 * Add Action Listener to Attribute Combo Box
			 */
			attributeComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applyButton.setText(APPLY_BUTTON_TEXT);
					addSortTableModel(attributeValuesTable);
					setAttributeDescription();
					currentAttribute = attributeComboBox.getSelectedItem()
							.toString();
				}
			});
		}
		return attributePanel;
	}

	/**
	 * Gets list of currently loaded attribute names
	 */
	private Vector<String> getBubbleAttributes() {
		/**
		 * Obtain Node Attributes
		 */
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attributeNames[] = nodeAttributes.getAttributeNames();
		Vector<String> attributeList = new Vector<String>();

		/**
		 * Show attributes associated with network
		 */
		for (int i = 0; i < attributeNames.length; i++) {
			int type = nodeAttributes.getType(attributeNames[i]);
			/**
			 * only show user visible attributes
			 */
			if (nodeAttributes.getUserVisible(attributeNames[i])) {
				/**
				 * and skip the __unknown attribute (whatever that is!?)
				 */
				if (attributeNames[i] != "__unknown") {
					/**
					 * and skip attributes of TYPE_COMPLEX
					 */
					if (type != CyAttributes.TYPE_COMPLEX) {
						attributeList.add(attributeNames[i]);

					}
				}
			}
		}
		Collections.sort(attributeList);

		return attributeList;
	}

	/**
	 * Set the number of visible rows for table.
	 * 
	 * @param table
	 * @param rows
	 */
	private void setVisibleRowCount(JTable table, int rows) {
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += table.getRowHeight(row);
		}

		table.setPreferredScrollableViewportSize(new Dimension(table
				.getPreferredScrollableViewportSize().width, height));
	}

	/**
	 * Apply Button Action.
	 * 
	 * @param selectedValuesForRegion
	 */
	private void applyAction(ArrayList<Object> selectedValuesForRegion) {

		/**
		 * Check for valid selection
		 */
		if (selectedValuesForRegion == null
				|| selectedValuesForRegion.toString().contentEquals("[]")) {
			JOptionPane.showMessageDialog(this,
					"Invalid selection. Please try again.");
			return;
		}

		/**
		 * Check for unique selection per view.
		 */
		List<LayoutRegion> regionListForView = LayoutRegionManager
				.getRegionListForView(Cytoscape.getCurrentNetworkView());
		if (regionListForView != null) {
			Iterator<LayoutRegion> it = regionListForView.iterator();
			while (it.hasNext()) {
				LayoutRegion lr = it.next();
				if (lr.getRegionAttributeValue()
						.equals(selectedValuesForRegion)) {
					JLabel values = new JLabel(selectedValuesForRegion
							.toString());
					values.setForeground(lr.getColor());
					JLabel label = new JLabel(
							"A region for "
									+ selectedValuesForRegion
									+ " already exists. \nPlease make another selection.");
					JPanel p = new JPanel(new java.awt.GridLayout(1, 1));
					p.add(label);
					JOptionPane.showMessageDialog(this, p);
					return;
				}
			}
		}

		// Close dialog
		BRQuickFindConfigDialog.this.setVisible(false);
		BRQuickFindConfigDialog.this.dispose();

		// Get selected attribute
		String newAttribute = (String) attributeComboBox.getSelectedItem();

		/**
		 * Create LayoutRegion object
		 */
		new LayoutRegion(x, y, w, h, newAttribute, selectedValuesForRegion);
	}

}
