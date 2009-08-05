package cytoscape.filters.view;

import cytoscape.Cytoscape;
import cytoscape.filters.AdvancedSetting;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.EdgeInteractionFilter;
import cytoscape.filters.FilterPlugin;
import cytoscape.filters.InteractionFilter;
import cytoscape.filters.NodeInteractionFilter;
import cytoscape.filters.TopologyFilter;
import cytoscape.filters.util.FilterUtil;
import org.cytoscape.util.swing.DropDownMenuButton;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;
import org.cytoscape.model.CyRow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

/**
 * 
 */
public class FilterMainPanel extends JPanel implements ActionListener,
		ItemListener, PropertyChangeListener {

	private static JPopupMenu optionMenu;

	private static JMenuItem newFilterMenuItem;
	private static JMenuItem newTopologyFilterMenuItem;
	private static JMenuItem newNodeInteractionFilterMenuItem;
	private static JMenuItem newEdgeInteractionFilterMenuItem;
	
	private static JMenuItem renameFilterMenuItem;

	private static JMenuItem deleteFilterMenuItem;

	private static JMenuItem duplicateFilterMenuItem;

	private DropDownMenuButton optionButton;
	
	private FilterSettingPanel currentFilterSettingPanel = null;
	private HashMap<CompositeFilter,FilterSettingPanel> filter2SettingPanelMap = new HashMap<CompositeFilter,FilterSettingPanel>();

	private Vector<CompositeFilter> allFilterVect = null;
	/*
	 * Icons used in this panel.
	 */
	private static final ImageIcon optionIcon = new ImageIcon(FilterMainPanel.class
			.getResource("/images/ximian/stock_form-properties.png"));

	private static final ImageIcon delIcon = new ImageIcon(FilterMainPanel.class
			.getResource("/images/ximian/stock_delete-16.png"));

	private static final ImageIcon addIcon = new ImageIcon(
			FilterMainPanel.class.getResource("/images/ximian/stock_data-new-table-16.png"));

	private static final ImageIcon renameIcon = new ImageIcon(FilterMainPanel.class
			.getResource("/images/ximian/stock_redo-16.png"));

	private static final ImageIcon duplicateIcon = new ImageIcon(
			FilterMainPanel.class.getResource("/images/ximian/stock_slide-duplicate.png"));

	private CytoPanelImp cytoPanelWest = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);

	public FilterMainPanel(Vector<CompositeFilter> pAllFilterVect) {
		allFilterVect = pAllFilterVect;
		//Initialize the option menu with menuItems
		setupOptionMenu();

		optionButton = new DropDownMenuButton(new AbstractAction() {
			public void actionPerformed(ActionEvent ae) {
				DropDownMenuButton b = (DropDownMenuButton) ae.getSource();
				optionMenu.show(b, 0, b.getHeight());
			}
		});

		optionButton.setToolTipText("Options...");
		optionButton.setIcon(optionIcon);
		optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		optionButton.setComponentPopupMenu(optionMenu);

		//Initialize the UI components 
		initComponents();

		addEventListeners();
	
		//btnApplyFilter.setVisible(false);
		
		//Update the status of interactionMenuItems if this panel become visible
		MyComponentAdapter cmpAdpt = new MyComponentAdapter();
		addComponentListener(cmpAdpt);
	}


	// Listen to ATTRIBUTES_CHNAGED and NETWORK_VIEW_FOCUSED event
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{	
			refreshAttributeCMB();
			replaceFilterSettingPanel((CompositeFilter)cmbSelectFilter.getSelectedItem());					
		}
		if (e.getPropertyName().equalsIgnoreCase(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{	
			// If FilterPanel is not selected, do nothing
			if (cmbSelectFilter.getSelectedItem() == null) {
				return;
			}
						
			//Refresh indices for UI widgets after network switch			
			CompositeFilter selectedFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();
			selectedFilter.setNetwork(Cytoscape.getCurrentNetwork());
			FilterSettingPanel theSettingPanel= filter2SettingPanelMap.get(selectedFilter);
			theSettingPanel.refreshIndicesForWidgets();
		}
	}


	public void refreshFilterSelectCMB() {
		this.cmbSelectFilter.repaint();
	}
	
	private void refreshAttributeCMB() {
		updateCMBAttributes();
		cmbAttributes.repaint();
	}
		
	/*
	 * Get the list of attribute names for either "node" or "edge". The attribute names will be
	 * prefixed either with "node." or "edge.". Those attributes whose data type is neither
	 * "String" nor "numeric" will be excluded
	 */
	private Vector<Object> getCyAttributesList(String pType) {
		Vector<String> attributeList = new Vector<String>();
		CyAttributes attributes = null;
		
		if (pType.equalsIgnoreCase("node")) {
			attributes = Cytoscape.getNodeAttributes();
			
		}
		else if (pType.equalsIgnoreCase("edge")){
			attributes = Cytoscape.getEdgeAttributes();			
		}
				
		String[] attributeNames = attributes.getAttributeNames();

		if (attributeNames != null) {
			//  Show all attributes, with type of String or Number
			for (int i = 0; i < attributeNames.length; i++) {
				int type = attributes.getType(attributeNames[i]);
				
				//  only show user visible attributes,with type = Number/String/List
				if (!attributes.getUserVisible(attributeNames[i])) {
					continue;
				}
				if ((type == CyAttributes.TYPE_INTEGER)||(type == CyAttributes.TYPE_FLOATING)||(type == CyAttributes.TYPE_STRING)||(type == CyAttributes.TYPE_SIMPLE_LIST)) {
					attributeList.add(pType+"."+attributeNames[i]);
				}
			} //for loop
		
			//  Alphabetical sort
			Collections.sort(attributeList);
		}

		// type conversion
		Vector<Object> retList = new Vector<Object>();

		for (int i=0; i<attributeList.size(); i++) {
			retList.add(attributeList.elementAt(i));
		}
		return retList;
	}
	
	
	/*
	 * Hide the visible filterSettingPanel, if any, and show the new FilterSettingPanel for
	 * the given filter.
	 */
	private void replaceFilterSettingPanel(CompositeFilter pNewFilter) {

		if (pNewFilter == null) {
			  pnlFilterDefinition.setVisible(false);
			  lbPlaceHolder_pnlFilterDefinition.setVisible(true);	
			  return;
		}
		
		//Hide the existing FilterSettingPanel, if any
		if (currentFilterSettingPanel != null) {
			currentFilterSettingPanel.setVisible(false);
		}

		currentFilterSettingPanel = filter2SettingPanelMap.get(pNewFilter);
		
		if (currentFilterSettingPanel == null || currentFilterSettingPanel.hasNullIndexChildFilter()) {
			currentFilterSettingPanel = new FilterSettingPanel(this, pNewFilter);
			//Update the HashMap
			filter2SettingPanelMap.put(pNewFilter, currentFilterSettingPanel);			
		}

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.gridwidth = 3;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
			
			if (pNewFilter instanceof TopologyFilter) {
				lbAttribute.setVisible(false);
				btnAddFilterWidget.setVisible(false);
				cmbAttributes.setVisible(false);	
				pnlFilterDefinition.setBorder(javax.swing.BorderFactory
						.createTitledBorder("Topology Filter Definition"));
			}
			else if (pNewFilter instanceof InteractionFilter) {
				lbAttribute.setVisible(false);
				btnAddFilterWidget.setVisible(false);
				cmbAttributes.setVisible(false);	
				pnlFilterDefinition.setBorder(javax.swing.BorderFactory
						.createTitledBorder("Interaction Filter Definition"));				
			}
			else {
				lbAttribute.setVisible(true);
				btnAddFilterWidget.setVisible(true);
				cmbAttributes.setVisible(true);								
				pnlFilterDefinition.setBorder(javax.swing.BorderFactory
						.createTitledBorder("Filter Definition"));

			}
		pnlFilterDefinition.add(currentFilterSettingPanel, gridBagConstraints);

		pnlFilterDefinition.setVisible(true);
		currentFilterSettingPanel.setVisible(true);
		lbPlaceHolder_pnlFilterDefinition.setVisible(false); 				
			
		this.repaint();
	}
	
	

	private void addEventListeners() {
		btnApplyFilter.addActionListener(this);

		btnAddFilterWidget.addActionListener(this);

		newFilterMenuItem.addActionListener(this);
		newTopologyFilterMenuItem.addActionListener(this);
		newNodeInteractionFilterMenuItem.addActionListener(this);
		newEdgeInteractionFilterMenuItem.addActionListener(this);

		deleteFilterMenuItem.addActionListener(this);
		renameFilterMenuItem.addActionListener(this);
		duplicateFilterMenuItem.addActionListener(this);

		cmbSelectFilter.addItemListener(this);
		cmbAttributes.addItemListener(this);
		
		CytoPanelListener l = new MyCytoPanelListener();
		cytoPanelWest.addCytoPanelListener(l);
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
	}

	public void initCMBSelectFilter(){
		System.out.println("FilterMainPanel.initCMBSelectFilter() ...");
		//if (allFilterVect == null) {
		//	allFilterVect = new Vector<CompositeFilter>();
		//}
		DefaultComboBoxModel theModel = new DefaultComboBoxModel(allFilterVect);
		cmbSelectFilter.setModel(theModel);
		cmbSelectFilter.setRenderer(new FilterRenderer());
		
		if (allFilterVect.size() ==0) {
			this.btnApplyFilter.setEnabled(false);
			this.btnAddFilterWidget.setEnabled(false);
		}
		for (int i=0; i<allFilterVect.size(); i++) {
			filter2SettingPanelMap.put(allFilterVect.elementAt(i), null);
		}
 
		replaceFilterSettingPanel((CompositeFilter)cmbSelectFilter.getSelectedItem());
	}
	
	class MyCytoPanelListener implements CytoPanelListener{
		public void onStateChange(CytoPanelState newState) {}
		public void onComponentSelected(int componentIndex){

			if (componentIndex == cytoPanelWest.indexOfComponent("Filters")) {
				System.out.println("Filter Panel is selected");

				//if (cmbSelectFilter.getModel().getSize() == 0 && allFilterVect.size()>0) {
				if (cmbSelectFilter.getModel().getSize() == 0) {
					// CMBSelectFilter will not be initialize until the Filer Panel is selected
					initCMBSelectFilter();		
				}

				updateCMBAttributes();
			}
		}		
		public void onComponentAdded(int count){}
		public void onComponentRemoved(int count){}
	}

	/*
	 * Update the attribute list in the attribute combobox based on the settings in the 
	 * cuttrent selected filter
	 */
	private void updateCMBAttributes() {
		
		Vector<Object> attributeList = new Vector<Object>();

		attributeList.add("-- Attributes --");
		CompositeFilter selectedFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();

		if (selectedFilter == null) {
			return;
		}

        attributeList.addAll(getCyAttributesList("node"));
        attributeList.addAll(getCyAttributesList("edge"));
        
		attributeList.add("-- Filters --");
		
		if (allFilterVect != null) {
			for (int i=0; i<allFilterVect.size(); i++) {
				attributeList.add(allFilterVect.elementAt(i));
			}
		}
		
		cmbAttributes.setModel(new DefaultComboBoxModel(attributeList));		
	}
	
	/**
	 * Setup menu items.
	 * 
	 */
	private void setupOptionMenu() {
		/*
		 * Option Menu
		 */
		newFilterMenuItem = new JMenuItem("Create new filter...");
		newFilterMenuItem.setIcon(addIcon);
		
		newTopologyFilterMenuItem = new JMenuItem("Create new topology filter...");
		newTopologyFilterMenuItem.setIcon(addIcon);

		newNodeInteractionFilterMenuItem = new JMenuItem("Create new NodeInteraction filter...");
		newNodeInteractionFilterMenuItem.setIcon(addIcon);
		newNodeInteractionFilterMenuItem.setEnabled(false);

		newEdgeInteractionFilterMenuItem = new JMenuItem("Create new EdgeInteraction filter...");
		newEdgeInteractionFilterMenuItem.setIcon(addIcon);
		newEdgeInteractionFilterMenuItem.setEnabled(false);
		
		deleteFilterMenuItem = new JMenuItem("Delete filter...");
		deleteFilterMenuItem.setIcon(delIcon);

		renameFilterMenuItem = new JMenuItem("Rename filter...");
		renameFilterMenuItem.setIcon(renameIcon);

		duplicateFilterMenuItem = new JMenuItem("Copy existing filter...");
		duplicateFilterMenuItem.setIcon(duplicateIcon);
		// Hide copy icon for now, we may need it in the future
		duplicateFilterMenuItem.setVisible(false);

		optionMenu = new JPopupMenu();
		optionMenu.add(newFilterMenuItem);
		optionMenu.add(newTopologyFilterMenuItem);
		optionMenu.add(newNodeInteractionFilterMenuItem);
		optionMenu.add(newEdgeInteractionFilterMenuItem);
		optionMenu.add(deleteFilterMenuItem);
		optionMenu.add(renameFilterMenuItem);
		optionMenu.add(duplicateFilterMenuItem);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		pnlCurrentFilter = new javax.swing.JPanel();
		cmbSelectFilter = new javax.swing.JComboBox();
		// optionButton = new javax.swing.JButton();
		pnlFilterDefinition = new javax.swing.JPanel();
		cmbAttributes = new javax.swing.JComboBox();
		btnAddFilterWidget = new javax.swing.JButton();
		lbAttribute = new javax.swing.JLabel();
		lbPlaceHolder = new javax.swing.JLabel();
		pnlButton = new javax.swing.JPanel();
		btnApplyFilter = new javax.swing.JButton();
		lbPlaceHolder_pnlFilterDefinition = new javax.swing.JLabel();

		setLayout(new java.awt.GridBagLayout());

		pnlCurrentFilter.setLayout(new java.awt.GridBagLayout());

		pnlCurrentFilter.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Current Filter"));
		// cmbSelectFilter.setModel(new javax.swing.DefaultComboBoxModel(new
		// String[] { "My First filter", "My second Filter" }));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.weightx = 0.5;
		gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
		pnlCurrentFilter.add(cmbSelectFilter, gridBagConstraints);

		optionButton.setText("Option");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
		pnlCurrentFilter.add(optionButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipady = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.weightx = 1.0;
		add(pnlCurrentFilter, gridBagConstraints);

		pnlFilterDefinition.setLayout(new java.awt.GridBagLayout());

		pnlFilterDefinition.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Filter Definition"));
		cmbAttributes.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Alias", "Attribute EEE" }));
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
		pnlFilterDefinition.add(cmbAttributes, gridBagConstraints);

		btnAddFilterWidget.setText("Add");
		btnAddFilterWidget.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 5);
		pnlFilterDefinition.add(btnAddFilterWidget, gridBagConstraints);

		lbAttribute.setText("Attribute/Filter");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
		pnlFilterDefinition.add(lbAttribute, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		pnlFilterDefinition.add(lbPlaceHolder, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(pnlFilterDefinition, gridBagConstraints);

		btnApplyFilter.setText("Apply");
		pnlButton.add(btnApplyFilter);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
		add(pnlButton, gridBagConstraints);

		// lbPlaceHolder_pnlFilterDefinition.setText("jLabel1");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		add(lbPlaceHolder_pnlFilterDefinition, gridBagConstraints);

		// Set customized renderer for attributes/filter combobox
		cmbAttributes.setRenderer(new AttributeFilterRenderer());

	}// </editor-fold>

	// Variables declaration - do not modify
	private javax.swing.JButton btnAddFilterWidget;

	private javax.swing.JButton btnApplyFilter;

	private javax.swing.JComboBox cmbAttributes;

	private javax.swing.JComboBox cmbSelectFilter;

	private javax.swing.JLabel lbAttribute;

	private javax.swing.JLabel lbPlaceHolder;

	private javax.swing.JLabel lbPlaceHolder_pnlFilterDefinition;

	// private javax.swing.JButton optionButton;
	private javax.swing.JPanel pnlButton;

	private javax.swing.JPanel pnlCurrentFilter;

	private javax.swing.JPanel pnlFilterDefinition;

	// End of variables declaration
	
	
	public JComboBox getCMBAttributes()
	{
		return 	cmbAttributes;
	}
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		
		//System.out.println("Entering FilterMainPanel.itemStateChnaged() ...");
		
		if (source instanceof JComboBox) {
			JComboBox cmb = (JComboBox) source;
			if (cmb == cmbSelectFilter) {
				CompositeFilter selectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();
				if (selectedFilter == null) {
					this.btnApplyFilter.setEnabled(false);
					this.btnAddFilterWidget.setEnabled(false);
					return;
				}
				else {
					this.btnAddFilterWidget.setEnabled(true);
					this.btnApplyFilter.setEnabled(true);					
				}
				replaceFilterSettingPanel(selectedFilter);

				if (Cytoscape.getCurrentNetwork() != null) {
					Cytoscape.getCurrentNetwork().unselectAllNodes();						
				}

				if (cmbSelectFilter.getSelectedItem() instanceof TopologyFilter || cmbSelectFilter.getSelectedItem() instanceof InteractionFilter) {
					// do not apply TopologyFilter or InteractionFilter automatically
					return;
				}	
								
				// If network size is greater than pre-defined threshold, don't apply it automatically 
				if (FilterUtil.isDynamicFilter(selectedFilter)) {
					FilterUtil.doSelection(selectedFilter);					
				}
				
				refreshAttributeCMB();
			}
			else if (cmb == cmbAttributes) {
                String selectItem = (String) cmbAttributes.getSelectedItem().toString();

				// Disable the Add button if "--Attribute--" or "-- Filter ---" is selected
				if (selectItem.equalsIgnoreCase("-- Filters --") ||selectItem.equalsIgnoreCase("-- Attributes --")) {
					btnAddFilterWidget.setEnabled(false);
				}
				else {
					btnAddFilterWidget.setEnabled(true);
				}
			}
		}	
	}

	
	/**
	 * DOCUMENT ME!
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */

	public void actionPerformed(ActionEvent e) {
		Object _actionObject = e.getSource();

		// handle Button events
		if (_actionObject instanceof JButton) {
			JButton _btn = (JButton) _actionObject;

			if (_btn == btnApplyFilter) {
				//System.out.println("\nApplyButton is clicked!");
				//System.out.println("\tThe Filter to apply is \n" + cmbSelectFilter.getSelectedItem().toString()+"\n");
				
                CompositeFilter theFilterToApply = (CompositeFilter) cmbSelectFilter.getSelectedItem();
                theFilterToApply.setNetwork(Cytoscape.getCurrentNetwork());
                FilterUtil.doSelection(theFilterToApply);
			}
			if (_btn == btnAddFilterWidget) {
				//btnAddFilterWidget is clicked!
				CompositeFilter selectedFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();
				FilterSettingPanel theSettingPanel = filter2SettingPanelMap.get(selectedFilter);

				if (cmbAttributes.getSelectedItem() instanceof String) {
					String selectItem = (String) cmbAttributes.getSelectedItem();
					if (selectItem.equalsIgnoreCase("-- Filters --") ||selectItem.equalsIgnoreCase("-- Attributes --")) {
						return;
					}
				}
				theSettingPanel.addNewWidget((Object)cmbAttributes.getSelectedItem());					
			}
		} // JButton event
		
		if (_actionObject instanceof JMenuItem) {
			JMenuItem _menuItem = (JMenuItem) _actionObject;
			if (_menuItem == newFilterMenuItem || _menuItem == newTopologyFilterMenuItem 
					|| _menuItem == newNodeInteractionFilterMenuItem || _menuItem == newEdgeInteractionFilterMenuItem) {
				String filterType = "Composite";
				//boolean isTopoFilter = false;
				//boolean isInteractionFilter = false;
				if (_menuItem == newTopologyFilterMenuItem) {
					filterType = "Topology";
					if (Cytoscape.getCurrentNetwork() != null) {
						Cytoscape.getCurrentNetwork().unselectAllNodes();						
					}
				}
				if (_menuItem == newNodeInteractionFilterMenuItem) {
					filterType = "NodeInteraction";
					if (Cytoscape.getCurrentNetwork() != null) {
						Cytoscape.getCurrentNetwork().unselectAllNodes();						
					}
				}
				if (_menuItem == newEdgeInteractionFilterMenuItem) {
					filterType = "EdgeInteraction";
					if (Cytoscape.getCurrentNetwork() != null) {
						Cytoscape.getCurrentNetwork().unselectAllNodes();						
					}
				}
				
				String newFilterName = "";
				while (true) {
					newFilterName = javax.swing.JOptionPane.showInputDialog(
							this, "New filter name", "New Filter Name",
							JOptionPane.INFORMATION_MESSAGE);

					if (newFilterName == null) { // user clicked "cancel"
						break;
					}
					if (newFilterName.trim().equals("")) {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(this,
								"Filter name is empty!", "Warning",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[0]);
						continue;
					}
					
					if (cytoscape.filters.util.FilterUtil
							.isFilterNameDuplicated(newFilterName)) {
						Object[] options = { "OK" };
						JOptionPane.showOptionDialog(this,
								"Filter name already existed!", "Warning",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE, null, options,
								options[0]);
						continue;
					}
					break;
				}// while loop

				if ((newFilterName != null)
						&& (!newFilterName.trim().equals(""))) {
					createNewFilter(newFilterName, filterType);
					
					//System.out.println("FilterMainPanel.firePropertyChange() -- NEW_FILTER_CREATED");
					//pcs.firePropertyChange("NEW_FILTER_CREATED", "", "");
					if (FilterPlugin.shouldFireFilterEvent) {
						PropertyChangeEvent evt = new PropertyChangeEvent(this, "NEW_FILTER_CREATED", null, null);
						Cytoscape.getPropertyChangeSupport().firePropertyChange(evt);						
					}
				}
			} else if (_menuItem == deleteFilterMenuItem) {
				CompositeFilter theSelectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();	
				if (theSelectedFilter == null) {
					return;
				}

				Object[] options = { "YES", "CANCEL" };
				int userChoice = JOptionPane.showOptionDialog(this,
						"Are you sure you want to delete " + theSelectedFilter.getName()
								+ "?", "Warning", JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[0]);

				if (userChoice == 1) { // user clicked CANCEL
					return;
				}
				deleteFilter(theSelectedFilter);
				if (FilterPlugin.shouldFireFilterEvent) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_DELETED", null, null);
					Cytoscape.getPropertyChangeSupport().firePropertyChange(evt);
				}
			} else if (_menuItem == renameFilterMenuItem) {
				CompositeFilter theSelectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();				
				if (theSelectedFilter == null) {
					return;
				}
				renameFilter();
				if (FilterPlugin.shouldFireFilterEvent) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_RENAMED", null, null);
					Cytoscape.getPropertyChangeSupport().firePropertyChange(evt);
				}
			} else if (_menuItem == duplicateFilterMenuItem) {
				CompositeFilter theSelectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();				
				if (theSelectedFilter == null) {
					return;
				}
				duplicateFilter();
				if (FilterPlugin.shouldFireFilterEvent) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_DUPLICATED", null, null);
					Cytoscape.getPropertyChangeSupport().firePropertyChange(evt);
				}
			}
		} // JMenuItem event
		
		updateInteractionMenuItemStatus();
	}

	private void updateInteractionMenuItemStatus() {
		//Disable interactionMenuItem if there is no other filters to depend on
		if (FilterPlugin.getAllFilterVect() == null || FilterPlugin.getAllFilterVect().size() == 0) {
			newNodeInteractionFilterMenuItem.setEnabled(false);
			newEdgeInteractionFilterMenuItem.setEnabled(false);
			return;
		}

		// Set newEdgeInteractionFilterMenuItem on only if there are at least one 
		// Node Filter
		if (hasNodeFilter(FilterPlugin.getAllFilterVect())) {
			newEdgeInteractionFilterMenuItem.setEnabled(true);      
		}
		else {
			newEdgeInteractionFilterMenuItem.setEnabled(false);
		}

		// Set newNodeInteractionFilterMenuItem on only if there are at least one 
		// Edge Filter
		if (hasEdgeFilter(FilterPlugin.getAllFilterVect())) {
			newNodeInteractionFilterMenuItem.setEnabled(true);
		}       
		else {
			newNodeInteractionFilterMenuItem.setEnabled(false);
		}
	}
	
	// Check if there are any NodeFilter in the AllFilterVect
	private boolean hasNodeFilter(Vector<CompositeFilter> pAllFilterVect) {
		boolean selectNode = false;

		for (int i=0; i< pAllFilterVect.size(); i++) {
			CompositeFilter curFilter = (CompositeFilter) pAllFilterVect.elementAt(i);
			if (curFilter.getAdvancedSetting().isNodeChecked()) {
				selectNode = true;
			}                       
		}//end of for loop

		return selectNode;
	}

	// Check if there are any NodeFilter in the AllFilterVect
	private boolean hasEdgeFilter(Vector<CompositeFilter> pAllFilterVect) {
		boolean selectEdge = false;

		for (int i=0; i< pAllFilterVect.size(); i++) {
			CompositeFilter curFilter = (CompositeFilter) pAllFilterVect.elementAt(i);
			if (curFilter.getAdvancedSetting().isEdgeChecked()) {
				selectEdge = true;
			}                       
		}//end of for loop

		return selectEdge;
	}
	
    //Each time, the FilterMainPanel become visible, update the status of InteractionMaenuItems
	class MyComponentAdapter extends ComponentAdapter {
		public void componentShown(ComponentEvent e) {
			updateInteractionMenuItemStatus();
		}
	}

	private void duplicateFilter(){
		CompositeFilter theFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();
	
		String tmpName = "Copy of " + theFilter.getName();
		String newFilterName = null;
		
		while (true) {
			Vector<String> nameVect = new Vector<String>();
			nameVect.add(tmpName);
			
			EditNameDialog theDialog = new EditNameDialog("Copy Filter", "Please enter a new Filter name:", nameVect, 300,170);
			theDialog.setLocationRelativeTo(this);
			theDialog.setVisible(true);
			
			newFilterName = (String) nameVect.elementAt(0);

			if ((newFilterName == null)) { // cancel buton is clicked
				return;
			}
			
			if (cytoscape.filters.util.FilterUtil
					.isFilterNameDuplicated(newFilterName)) {
				Object[] options = { "OK" };
				JOptionPane.showOptionDialog(this,
						"Filter name already existed!", "Warning",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options,
						options[0]);
				continue;
			}

			break;
		}// while loop

		CompositeFilter newFilter = (CompositeFilter) theFilter.clone(); 
		newFilter.setName(newFilterName);
		
		allFilterVect.add(newFilter);
		FilterSettingPanel newFilterSettingPanel = new FilterSettingPanel(this, newFilter);
		filter2SettingPanelMap.put(newFilter, newFilterSettingPanel);
		
		// set the new filter in the combobox selected
		cmbSelectFilter.setSelectedItem(newFilter);
	}	
	
	
	private void renameFilter(){
		CompositeFilter theFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();

		String oldFilterName = theFilter.getName();
		String newFilterName = "";
		while (true) {
			Vector<String> nameVect = new Vector<String>();
			nameVect.add(oldFilterName);
			
			EditNameDialog theDialog = new EditNameDialog("Edit Filter Name", "Please enter a new Filter name:", nameVect, 300,170);
			theDialog.setLocationRelativeTo(this);
			theDialog.setVisible(true);
			
			newFilterName = (String) nameVect.elementAt(0);

			if ((newFilterName == null) || newFilterName.trim().equals("") 
					||newFilterName.equals(oldFilterName)) {
				return;
			}
			
			if (cytoscape.filters.util.FilterUtil
					.isFilterNameDuplicated(newFilterName)) {
				Object[] options = { "OK" };
				JOptionPane.showOptionDialog(this,
						"Filter name already existed!", "Warning",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options,
						options[0]);
				continue;
			}

			break;
		}// while loop

		theFilter.setName(newFilterName);

		cmbSelectFilter.setSelectedItem(theFilter);
		cmbSelectFilter.repaint();
	}
		
	private void deleteFilter(CompositeFilter pFilter) {
		 
		filter2SettingPanelMap.remove(pFilter);
		cmbSelectFilter.removeItem(pFilter);
		
		if (allFilterVect == null || allFilterVect.size() == 0) {
			replaceFilterSettingPanel(null);
		}
		this.validate();
		this.repaint();
	}
	
	class AttributeFilterRenderer extends JLabel implements ListCellRenderer {
		public AttributeFilterRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value != null) {
				if (value instanceof String) {
					setText((String)value);
				}
				else if (value instanceof CompositeFilter) {
					CompositeFilter theFilter = (CompositeFilter) value;
					CompositeFilter selectedFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();
					if (value == selectedFilter) {// ignore self
						setText("");						
					}
					else {
						setText(theFilter.getName());
					}
				}				
			}
			else { // value == null
				setText(""); 
			}

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}
	}// AttributeRenderer

	private void createNewFilter(String pFilterName, String pFilterType) {
		// Create an empty filter, add it to the current filter list
		CompositeFilter newFilter = null;
		
		if (pFilterType.equalsIgnoreCase("Topology")) {
			newFilter =  new TopologyFilter();
			newFilter.getAdvancedSetting().setEdge(false);
			newFilter.setName(pFilterName);			
		}
		else if (pFilterType.equalsIgnoreCase("NodeInteraction")) {
			newFilter =  new NodeInteractionFilter();
			//newFilter.getAdvancedSetting().setEdge(false);
			newFilter.setName(pFilterName);			
		}		
		else if (pFilterType.equalsIgnoreCase("EdgeInteraction")) {
			newFilter =  new EdgeInteractionFilter();
			//newFilter.getAdvancedSetting().setEdge(false);
			newFilter.setName(pFilterName);			
		}		
		else {
			newFilter = new CompositeFilter(pFilterName);
		}
		
		newFilter.setNetwork(Cytoscape.getCurrentNetwork());
		
		allFilterVect.add(newFilter);
		FilterSettingPanel newFilterSettingPanel = new FilterSettingPanel(this,newFilter);
		filter2SettingPanelMap.put(newFilter, newFilterSettingPanel);

		// set the new filter in the combobox selected
		cmbSelectFilter.setSelectedItem(newFilter);
		cmbSelectFilter.repaint();

		if (pFilterType.equalsIgnoreCase("Composite")) {
			updateCMBAttributes();
		}
	}

	class FilterRenderer extends JLabel implements ListCellRenderer {
		public FilterRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (value != null) {
				CompositeFilter theFilter = (CompositeFilter) value;
				AdvancedSetting advancedSetting = theFilter
						.getAdvancedSetting();
				String prefix = "";
				if (advancedSetting.isGlobalChecked()) {
					prefix = "global: ";
				}
				if (advancedSetting.isSessionChecked()) {
					prefix += "session: ";
				}
				setText(prefix + theFilter.getName());
			}
			else { // value == null
				setText(""); 
			}

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			return this;
		}
	}// FilterRenderer
}
