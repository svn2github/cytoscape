package cytoscape.filters.view;

import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import ViolinStrings.Strings;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import csplugins.quickfind.util.QuickFind;
import cytoscape.util.swing.DropDownMenuButton;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.filters.util.FilterUtil;
import cytoscape.filters.AdvancedSetting;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.CyFilter;
import cytoscape.filters.TopologyFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import cytoscape.filters.AtomicFilter;
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.view.cytopanels.CytoPanelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * 
 */
public class FilterMainPanel extends JPanel implements ActionListener,
		ItemListener, PropertyChangeListener {

	private static JPopupMenu optionMenu;

	private static JMenuItem newFilterMenuItem;
	private static JMenuItem newTopologyFilterMenuItem;
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
	private static final ImageIcon optionIcon = new ImageIcon(Cytoscape.class
			.getResource("/cytoscape/images/ximian/stock_form-properties.png"));

	private static final ImageIcon delIcon = new ImageIcon(Cytoscape.class
			.getResource("/cytoscape/images/ximian/stock_delete-16.png"));

	private static final ImageIcon addIcon = new ImageIcon(
			Cytoscape.class
					.getResource("/cytoscape/images/ximian/stock_data-new-table-16.png"));

	private static final ImageIcon renameIcon = new ImageIcon(Cytoscape.class
			.getResource("/cytoscape/images/ximian/stock_redo-16.png"));

	private static final ImageIcon duplicateIcon = new ImageIcon(
			Cytoscape.class
					.getResource("/cytoscape/images/ximian/stock_slide-duplicate.png"));

	private CytoPanelImp cytoPanelWest = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);

	
	private SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	
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

	}

	//public Vector<CompositeFilter> getAllFilterVect() {
	//	return allFilterVect;
	//}
	
	//public void setAllFilterVect(Vector<CompositeFilter> pAllFilterVect) {
	//	allFilterVect = pAllFilterVect;
	//}

	// Listen to ATTRIBUTES_CHNAGED event
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equalsIgnoreCase(Cytoscape.ATTRIBUTES_CHANGED))
		{	
			//refreshFilterSettingPanels();
			refreshAttributeCMB();
			//updateIndexForWidget();
			replaceFilterSettingPanel((CompositeFilter)cmbSelectFilter.getSelectedItem());
		}
	}
	
	public void refreshFilterSelectCMB() {
		this.cmbSelectFilter.repaint();
	}
	
	public void refreshAttributeCMB() {
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

		if (currentFilterSettingPanel == null) {
			currentFilterSettingPanel = new FilterSettingPanel(this, pNewFilter, allFilterVect);
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
		deleteFilterMenuItem.addActionListener(this);
		renameFilterMenuItem.addActionListener(this);
		duplicateFilterMenuItem.addActionListener(this);

		cmbSelectFilter.addItemListener(this);
		
		CytoPanelListener l = new MyCytoPanelListener();
		cytoPanelWest.addCytoPanelListener(l);
		
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
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

				//initTestData();
				//initTestFilters();

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
	 * Update the attriuute list in the attribute combobox based on the settings in the 
	 * cuttrent selected filter
	 */
	private void updateCMBAttributes() {
		
		System.out.println("Entering updateCMBAttributes() ...");
		
		Vector<Object> attributeList = new Vector<Object>();

		attributeList.add("-- Attributes --");
		CompositeFilter selectedFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();

		if (selectedFilter == null) {
			return;
		}

		//boolean debug = true;
		//if (debug) {
		//	selectedFilter = allFilterVect.elementAt(0);
		//}

		if (selectedFilter.getAdvancedSetting().isNodeChecked() 
				&& !selectedFilter.getAdvancedSetting().isEdgeChecked())
		{
			//System.out.println("Only Node is checked");
			attributeList.addAll(getCyAttributesList("node"));
			//attributeList = getCyAttributesList("node");
		}
		else if (selectedFilter.getAdvancedSetting().isEdgeChecked()
				&& !selectedFilter.getAdvancedSetting().isNodeChecked())
		{
			//System.out.println("Only Edge is checked");
			attributeList.addAll(getCyAttributesList("edge"));
			//attributeList = getCyAttributesList("edge");
		}
		else if (selectedFilter.getAdvancedSetting().isNodeChecked()
				&& selectedFilter.getAdvancedSetting().isEdgeChecked())
		{
			//System.out.println("Both Node and edge are checked");
			attributeList.addAll(getCyAttributesList("node"));
			//attributeList = getCyAttributesList("node");
			attributeList.addAll(getCyAttributesList("edge"));
		}
		//else {
		//	System.out.println("Neither Node nore edge is checked");
		//}

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
		//topologyFilterMenuItem.setIcon(addIcon);

		deleteFilterMenuItem = new JMenuItem("Delete filter...");
		deleteFilterMenuItem.setIcon(delIcon);

		renameFilterMenuItem = new JMenuItem("Rename filter...");
		renameFilterMenuItem.setIcon(renameIcon);

		duplicateFilterMenuItem = new JMenuItem("Copy existing filter...");
		duplicateFilterMenuItem.setIcon(duplicateIcon);

		optionMenu = new JPopupMenu();
		optionMenu.add(newFilterMenuItem);
		optionMenu.add(newTopologyFilterMenuItem);
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
		
		System.out.println("Entering FilterMainPanel.itemStateChnaged() ...");
		
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
				FilterUtil.doSelection(selectedFilter);
				refreshAttributeCMB();
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

				System.out.println("\nApplyButton is clicked!");
				
				System.out.println("\n\tThere are " + allFilterVect.size() + " comositeFilter in allFilterVect\n");
				
				System.out.println("\tThe Filter to apply is \n\t\t" + cmbSelectFilter.getSelectedItem().toString()+"\n");

				FilterUtil.doSelection((CompositeFilter) cmbSelectFilter.getSelectedItem());
				
				//CompositeFilter tmpFilter = (CompositeFilter) cmbSelectFilter.getSelectedItem();
				
				//System.out.println("\tfilter.getNodebits().toString() = \n" + tmpFilter.getNodeBits().toString()+"\n");
				//tmpFilter.apply();
				//tmpFilter.doSelection();
				//System.out.println(" After filter.doSelection()\n");

				//initCMBSelectFilter(); // for debug only
				//updateCMBAttributes(); // for debug only
				
				//CompositeFilter theFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();
				
				//if (theFilter == null) {
				//	return;
				//}
				
				//System.out.println("\nThe Filter to apply:\n" + theFilter.toString()+"\n");
				//quickFind.selectRange(cyNetwork, lowValue, highValue);
				//QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
				//quickFind.selectHit(currentNetwork, hit);
				
				 // We have to run "Apply filter" in a seperate thread, becasue
				 //we want to monitor the progress
				
				//FilterUtil.applyFilter(theFilter);
				
				//ApplyFilterThread applyFilterThread = new ApplyFilterThread(theFilter);
				//applyFilterThread.start();
			}
			if (_btn == btnAddFilterWidget) {

				System.out.println("\nFilterMainPanel: btnAddFilterWidget is clicked!");

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
			if (_menuItem == newFilterMenuItem || _menuItem == newTopologyFilterMenuItem) {
				boolean isTopoFilter = false;
				if (_menuItem == newTopologyFilterMenuItem) {
					isTopoFilter = true;
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
							.isFilterNameDuplicated(allFilterVect, newFilterName)) {
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
					createNewFilter(newFilterName, isTopoFilter);
					
					//System.out.println("FilterMainPanel.firePropertyChange() -- NEW_FILTER_CREATED");
					//pcs.firePropertyChange("NEW_FILTER_CREATED", "", "");
					PropertyChangeEvent evt = new PropertyChangeEvent(this, "NEW_FILTER_CREATED", null, null);
					pcs.firePropertyChange(evt);
					
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
				PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_DELETED", null, null);
				pcs.firePropertyChange(evt);
			} else if (_menuItem == renameFilterMenuItem) {
				CompositeFilter theSelectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();				
				if (theSelectedFilter == null) {
					return;
				}
				renameFilter();
				PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_RENAMED", null, null);
				pcs.firePropertyChange(evt);
			} else if (_menuItem == duplicateFilterMenuItem) {
				CompositeFilter theSelectedFilter = (CompositeFilter)cmbSelectFilter.getSelectedItem();				
				if (theSelectedFilter == null) {
					return;
				}
				duplicateFilter();
				PropertyChangeEvent evt = new PropertyChangeEvent(this, "FILTER_DUPLICATED", null, null);
				pcs.firePropertyChange(evt);
			}
		} // JMenuItem event
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
					.isFilterNameDuplicated(allFilterVect, newFilterName)) {
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
		FilterSettingPanel newFilterSettingPanel = new FilterSettingPanel(this, newFilter, allFilterVect);
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
					.isFilterNameDuplicated(allFilterVect, newFilterName)) {
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

	private void createNewFilter(String pFilterName, boolean isTopoFilter) {
		// Create an empty filter, add it to the current filter list
		
		System.out.println("Entering FilterMainPanel.createNewFilter() ...");
		CompositeFilter newFilter = null;
		
		if (isTopoFilter) {
			System.out.println("\tCreate a topology filter");

			newFilter =  new TopologyFilter();
			newFilter.setName(pFilterName);			
		}
		else {
			newFilter = new CompositeFilter(pFilterName);
		}
		
		allFilterVect.add(newFilter);
		FilterSettingPanel newFilterSettingPanel = new FilterSettingPanel(this,newFilter, allFilterVect);
		filter2SettingPanelMap.put(newFilter, newFilterSettingPanel);

		// set the new filter in the combobox selected
		cmbSelectFilter.setSelectedItem(newFilter);
		cmbSelectFilter.repaint();

		if (!isTopoFilter) {
			updateCMBAttributes();
		}
		
		System.out.println("Leaving FilterMainPanel.createNewFilter() ...");
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
/*
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
*/
			return this;
		}
	}// FilterRenderer

	//============================== for debug only ========================

	private void initTestFilters() {
		//allFilterVect = new Vector<CompositeFilter>();
		
		//StringFilter myStringFilter = new StringFilter("myStringFilter",LOCATION,"cy*");
		//myStringFilter.setTextIndex(index_by_location);
		//myStringFilter.setNetwork(cyNetwork);
		
		//NumericFilter myNumericFilter = new NumericFilter<Integer>("myNumericFilter",RANK, 2, 3);
		//rankFilter.setNumberIndex(integerIndex_rank);
		//rankFilter.setNetwork(cyNetwork);
		
		CompositeFilter compositeFilter1 = new CompositeFilter("firstCompositeFilter");
		CompositeFilter compositeFilter2 = new CompositeFilter("secondCompositeFilter");
		CompositeFilter topoFilter = new TopologyFilter("ThirdTopoFilter");

		allFilterVect.add(compositeFilter1);
		allFilterVect.add(compositeFilter2);
		allFilterVect.add(topoFilter);

	}
}
