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
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import csplugins.brquickfind.util.BRCyAttributesUtil;
import csplugins.brquickfind.util.BRQuickFind;
import csplugins.brquickfind.util.BRQuickFindFactory;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.servers.BioDataServer;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;

/**
 * BubbleRouter Dialog Box.
 * 
 * @author Allan Kuchinsky, Alexander Pico, Kristina Hanspers. 
 * Adapted from code by Ethan Cerami.
 */
public class BRQuickFindConfigDialog extends JDialog {
	/**
	 * Attribute ComboBox
	 */
	private JComboBox attributeComboBox;

	private static ArrayList<Object> selectedValues = new ArrayList<Object>();

	private static String currentAttribute;

	/**
	 * Table of Sample Attribute Values
	 */
	private JSortTable sampleAttributeValuesTable;

	/**
	 * Attribute description text area.
	 */
	private JTextArea attributeDescriptionBox;

	/**
	 * Current Network
	 */
	private CyNetwork currentNetwork;

	/**
	 * Apply Text.
	 */
	private static final String BUTTON_TEXT = "Select";

	/**
	 * Load Attributes Button Text
	 */
	private static final String FILE_SELECT_BUTTON_TEXT = "Load attributes from file";

	/**
	 * Help Text
	 */
	private static final String HELP_BUTTON_TEXT = "Help";

	/**
	 * Apply Button, Attributes Button and Help Button.
	 */
	private JButton applyButton;

	private JButton fileBrowserButton;

	private JButton helpButton;

	private LayoutRegion _region;

	/**
	 * Constructor.
	 */
	public BRQuickFindConfigDialog() {
		this(null);

	}

	public BRQuickFindConfigDialog(LayoutRegion region) {

		this._region = region;

		/**
		 * Initialize, based on currently selected network
		 */
		
		currentNetwork = Cytoscape.getCurrentNetwork();

		Container container = getContentPane();
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		this.setTitle("Select Attribute for Interactive Layout");

		/**
		 * If we are working on Linux, set always on top to true.
		// This is a hack to deal with numerous z-ordering bugs on Linux.
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
		 * Add Sample Attribute Values Panel
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
			private String helpURL = "http://www.genmapp.org/InteractiveLayout/manual.htm";

			public void actionPerformed(ActionEvent e) {
				cytoscape.util.OpenBrowser.openURL(helpURL);
			}
		});

		/**
		 * Cancel Button
		 */
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BRQuickFindConfigDialog.this.setVisible(false);
				BRQuickFindConfigDialog.this.dispose();
			}
		});

		/**
		 * Apply Button
		 */
		applyButton = new JButton(BUTTON_TEXT);
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
	 * Creates a Panel of Sample Attribute Values.
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
		sampleAttributeValuesTable = new JSortTable() {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		addSortTableModel(sampleAttributeValuesTable);
		this.setVisibleRowCount(sampleAttributeValuesTable, 7);
		JScrollPane scrollPane = new JScrollPane(sampleAttributeValuesTable);
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
		if (attributeKey.equals(BRQuickFind.UNIQUE_IDENTIFIER)) {
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
		 * Create column names
		 */
		Vector columnNames = new Vector();
		columnNames.add(attributeKey);		
		/**
		 * Collect attribute values
		 */
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		Iterator nodeIterator = network.nodesIterator();

		String valueSet[] = BRCyAttributesUtil.getDistinctAttributeValues(
				nodeIterator, nodeAttributes, attributeKey, 50);

		ArrayList<String> finalValues = new ArrayList<String>();
		finalValues.add("unassigned");

		String splitValueSet[] = null;

		for (int i = 0; i < (valueSet.length); i++) {
			splitValueSet = valueSet[i].split(", ");
			for (int j = 0; j < (splitValueSet.length); j++) {
				if (!finalValues.contains(splitValueSet[j])) {
					finalValues.add(splitValueSet[j]);
				}
			}
		}

		TableModel model = new DefaultSortTableModel(columnNames,
				finalValues.toArray().length);

		if (finalValues != null && finalValues.toArray().length > 0) {
			for (int i = 0; i < ((finalValues.toArray().length >= 50) ? 50
					: finalValues.toArray().length); i++) {
				model.setValueAt(finalValues.toArray()[i], i, 0);
			}
		}
		/**
		 * Execute Task via TaskManager
		// This automatically pops-open a JTask Dialog Box.
		// This method will block until the JTask Dialog Box
		// is disposed.
		 */
		table.setModel(model);
		table.setAutoscrolls(true);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					selectedValues.clear();
					ListSelectionModel lsm = (ListSelectionModel) e.getSource();
					if (lsm.isSelectionEmpty()) {
						selectedValues.add(sampleAttributeValuesTable
								.getModel().getValueAt(0, 0));
					} else {
						int minIndex = lsm.getMinSelectionIndex();
						int maxIndex = lsm.getMaxSelectionIndex();
						for (int i = minIndex; i <= maxIndex; i++) {
							if (lsm.isSelectedIndex(i)) {
								selectedValues.add(sampleAttributeValuesTable
								.getModel().getValueAt(i, 0));
							}
						}
					}

				}
			}

		});

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					applyAction(selectedValues);
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
		 * Add shortcut button in Bubblerouter window for loading
		// node attribute file
		 */

		fileBrowserButton = new JButton(FILE_SELECT_BUTTON_TEXT);

		fileBrowserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				/**
				 * Use a Default CyFileFilter: enables user to select any file
				// type.
				 */
				CyFileFilter nf = new CyFileFilter();

				/**
				 * get the file name
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
					 * Get the list of attribute names and transform from vector
					// to array
					 */
					String[] forms = new String[getBubbleAttributes().size()];
					getBubbleAttributes().toArray(forms);

					/**
					 * Add latest attribute to already existing
					// attributecombobox
					 */
					String newItem = forms[forms.length - 1];
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
			 *  simply set to first in list for now; later set to
			// current attribute for regions that have been previously assigned.
			 */
			if (currentAttribute == null) {
				currentAttribute = nodeAttributes.getAttributeNames()[0];
			}
			attributeComboBox.setSelectedItem(currentAttribute);

			attributePanel.add(attributeComboBox);
			attributePanel.add(Box.createHorizontalGlue());
			attributePanel.add(fileBrowserButton);

			/**
			 * Add Action Listener
			 */
			attributeComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					applyButton.setText(BUTTON_TEXT);

					addSortTableModel(sampleAttributeValuesTable);
					setAttributeDescription();
					currentAttribute = attributeComboBox.getSelectedItem()
							.toString();
				}
			});
		}
		return attributePanel;
	}

	/**
	 * Sets the Visible Row Count.
	 * 
	 * @param table
	 *            JTable Object.
	 * @param rows
	 *            Number of Visible Rows.
	 */

	/**
	 * Gets list of currently loaded attribute names
	 */
	private Vector getBubbleAttributes() {
		/**
		 * Obtain Node Attributes
		 */
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		String attributeNames[] = nodeAttributes.getAttributeNames();
		Vector attributeList = new Vector();

		/**
		 * Show all attributes, except those of TYPE_COMPLEX
		 */
		for (int i = 0; i < attributeNames.length; i++) {
			int type = nodeAttributes.getType(attributeNames[i]);
			/**
			 * only show user visible attributes
			 */
			if (nodeAttributes.getUserVisible(attributeNames[i])) {
				if (type != CyAttributes.TYPE_COMPLEX) {
					/**
					 * Explicitly filter out CANONICAL_NAME, as it is
					// now deprecated.
					 */
					if (!attributeNames[i].equals(Semantics.CANONICAL_NAME)) {
						attributeList.add(attributeNames[i]);
					}
				}
			}
		}
		Collections.sort(attributeList);

		/**
		 * Add default: Unique Identifier
		 */
		attributeList.insertElementAt(BRQuickFind.UNIQUE_IDENTIFIER, 0);
		return attributeList;
	}

	private void applyAction(ArrayList selectedValues) {
		BRQuickFindConfigDialog.this.setVisible(false);
		BRQuickFindConfigDialog.this.dispose();
		String newAttribute = (String) attributeComboBox.getSelectedItem();
		ReindexQuickFind task = new ReindexQuickFind(currentNetwork,
				newAttribute, _region);
		JTaskConfig config = new JTaskConfig();
		config.setAutoDispose(true);
		config.displayStatus(true);
		config.displayTimeElapsed(false);
		config.displayCloseButton(true);
		config.setOwner(Cytoscape.getDesktop());
		config.setModal(true);

		/**
		 * Execute Task via TaskManager
		// This automatically pops-open a JTask Dialog Box.
		// This method will block until the JTask Dialog Box
		// is disposed.
		 */
		TaskManager.executeTask(task, config);
		_region.setRegionAttributeValue(selectedValues);
	}

	private void setVisibleRowCount(JTable table, int rows) {
		int height = 0;
		for (int row = 0; row < rows; row++) {
			height += table.getRowHeight(row);
		}

		table.setPreferredScrollableViewportSize(new Dimension(table
				.getPreferredScrollableViewportSize().width, height));
	}

	/**
	 * Main method: used for local debugging purposes only.
	 * 
	 * @param args
	 *            No command line arguments expected.
	 */
	public static void main(String[] args) {
		new BRQuickFindConfigDialog();
	}
}

/**
 * Long-term task to Reindex QuickFind.
 * 
 * @author Ethan Cerami.
 */

class ReindexQuickFind implements Task {
	private String newAttributeKey;

	private CyNetwork cyNetwork;

	private TaskMonitor taskMonitor;

	private LayoutRegion _region;

	/**
	 * Constructor.
	 * 
	 * @param newAttributeKey
	 *            New Attribute Key for Indexing.
	 */
	ReindexQuickFind(CyNetwork cyNetwork, String newAttributeKey,
			LayoutRegion region) {
		this.cyNetwork = cyNetwork;
		this.newAttributeKey = newAttributeKey;
		this._region = region;
	}

	/**
	 * Executes Task: Reindex.
	 */
	public void run() {
		BRQuickFind quickFind = BRQuickFindFactory.getGlobalQuickFindInstance();

		/**
		 * Send user-selected attribute name to bubble router
		 */
		_region.setAttributeName(newAttributeKey);
	}

	public void halt() {
	}

	/**
	 * Sets the TaskMonitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
	 * @throws IllegalThreadStateException
	 *             Illegal Thread State.
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets Title of Task.
	 * 
	 * @return Title of Task.
	 */
	public String getTitle() {
		return "ReIndexing";
	}

}

/**
 * Import Node Attributes from file
 *
 */

class ImportAttributesTask implements Task {
	private TaskMonitor taskMonitor;

	private File[] files;

	private int type;

	static final int NODE_ATTRIBUTES = 0;

	static final int EDGE_ATTRIBUTES = 1;

	/**
	 * Constructor.
	 * 
	 * @param file
	 *            File Object.
	 * @param type
	 *            NODE_ATTRIBUTES or EDGE_ATTRIBUTES
	 */
	ImportAttributesTask(File[] files, int type) {
		this.files = files;
		this.type = type;
	}

	/**
	 * Executes Task.
	 */
	public void run() {
		try {
			taskMonitor.setPercentCompleted(-1);
			taskMonitor.setStatus("Reading in Attributes");
			/**
			 * Get Defaults.
			 */
			BioDataServer bioDataServer = Cytoscape.getBioDataServer();
			String speciesName = CytoscapeInit.getProperties().getProperty(
					"defaultSpeciesName");
			boolean canonicalize = CytoscapeInit.getProperties().getProperty(
					"canonicalizeNames").equals("true");

			/**
			 * Read in Data

			// track progress. CyAttributes has separation between
			// reading attributes and storing them
			// so we need to find a different way of monitoring this task:
			// attributes.setTaskMonitor(taskMonitor);
			 */

			for (int i = 0; i < files.length; ++i) {
				taskMonitor.setPercentCompleted(100 * i / files.length);
				if (type == NODE_ATTRIBUTES)
					Cytoscape.loadAttributes(new String[] { files[i]
							.getAbsolutePath() }, new String[] {},
							canonicalize, bioDataServer, speciesName);
				else if (type == EDGE_ATTRIBUTES)
					Cytoscape.loadAttributes(new String[] {},
							new String[] { files[i].getAbsolutePath() },
							canonicalize, bioDataServer, speciesName);
				else
					throw new Exception("Unknown attribute type: "
							+ Integer.toString(type));
			}

			/**
			 * Inform others via property change event
			 */
			taskMonitor.setPercentCompleted(100);
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null,
					null);
			taskMonitor.setStatus("Done");
		} catch (Exception e) {
			taskMonitor.setException(e, e.getMessage());
		}
	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
	}

	/**
	 * Sets the Task Monitor Object.
	 * 
	 * @param taskMonitor
	 * @throws IllegalThreadStateException
	 */
	public void setTaskMonitor(TaskMonitor taskMonitor)
			throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

	/**
	 * Gets the Task Title.
	 * 
	 * @return Task Title.
	 */
	public String getTitle() {
		if (type == NODE_ATTRIBUTES) {
			return new String("Loading Node Attributes");
		} else {
			return new String("Loading Edge Attributes");
		}
	}
}

