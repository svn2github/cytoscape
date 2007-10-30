package cytoscape.filters.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.filters.AtomicFilter;
import cytoscape.filters.CompositeFilter;
import cytoscape.filters.CyFilter;
import cytoscape.filters.TopologyFilter;
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.AdvancedSetting;
import cytoscape.filters.Relation;
import cytoscape.filters.util.FilterUtil;

import java.util.List;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import javax.swing.JSlider;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;

import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;
import csplugins.widgets.autocomplete.index.GenericIndex;
import csplugins.widgets.autocomplete.index.IndexFactory;
import csplugins.widgets.autocomplete.index.NumberIndex;
import csplugins.widgets.autocomplete.index.TextIndex;
import csplugins.widgets.autocomplete.view.ComboBoxFactory;
import csplugins.widgets.autocomplete.view.TextIndexComboBox;
import csplugins.widgets.autocomplete.index.Hit;
import csplugins.widgets.slider.JRangeSliderExtended;
import prefuse.data.query.NumberRangeModel;
import prefuse.util.ui.JRangeSlider;
import csplugins.test.quickfind.test.TaskMonitorBase;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.event.ChangeEvent;
import java.awt.Component;
import javax.swing.JRadioButton;



public class FilterSettingPanel extends JPanel {
	
	private static final ImageIcon plusIcon = new ImageIcon(
			Cytoscape.class.getResource("/cytoscape/images/ximian/plus.gif"));
	private static final ImageIcon minusIcon = new ImageIcon(
			Cytoscape.class.getResource("/cytoscape/images/ximian/minus.gif"));
	private static final ImageIcon delIcon = new ImageIcon(Cytoscape.class
			.getResource("/cytoscape/images/ximian/stock_delete-16.png"));

	private CompositeFilter theFilter;
	private FilterMainPanel parentPanel;
	private CyNetwork currentNetwork = null;

	private Vector<CompositeFilter> allFilterVect = null;
	
	public FilterSettingPanel(FilterMainPanel pParent, Object pFilterObj, Vector<CompositeFilter> pAllFilterVect) {
		theFilter = (CompositeFilter) pFilterObj;
		parentPanel = pParent;
		allFilterVect = pAllFilterVect;
		initComponents();
		
		initAdvancedSetting();
		
		initCustomSetting();	
		
		if (pFilterObj instanceof TopologyFilter) {
			System.out.println("FilterSettingPanl: it is a topologyFilter");

			java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
	        gridBagConstraints.gridy = 2;
	        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        gridBagConstraints.weightx = 1.0;

			pnlCustomSettings.removeAll();
			TopoFilterPanel topoPanel = new TopoFilterPanel((TopologyFilter)theFilter, allFilterVect);
			pnlCustomSettings.add(topoPanel, gridBagConstraints);
			addBlankLabelToCustomPanel();

			// Hide un-used components in AdvancedPanel
			lbRelation.setVisible(false);
			rbtAND.setVisible(false);
			rbtOR.setVisible(false);
			
			this.validate();
		}

	}
	
	private void initCustomSetting() {
		List<CyFilter> theCustomFilterList = theFilter.getChildren();
		boolean isLast = false;
		for (int i=0; i <theCustomFilterList.size();i++) {
			//if (i == (theCustomFilterList.size()-1)) {
			//	isLast = true;
			//}
			addWidgetRow((CyFilter)theCustomFilterList.get(i),i*2);
		}	
		addBlankLabelToCustomPanel();
		
		//Restore initial values for RangerSliders 
		//Note: rangerSlider can not be set to their initial value until they are visible on screen
		
		//restoreRangeSliderModel();
		this.validate();
		//this.repaint();
	}
	
	
	private void restoreRangeSliderModel(){
		List<CyFilter> theFilterList = theFilter.getChildren();
		if (theFilterList == null)
			return;

		for (int i=0; i<theFilterList.size(); i++ ) {
			
			if (theFilterList.get(i) instanceof NumericFilter) {
				if (theFilterList.get(i) == null) {
					return;
				}
				
				NumericFilter theNumericFilter = (NumericFilter) theFilterList.get(i);
				
				int componentIndex = i*3 +1;
				JRangeSliderExtended theSlider = (JRangeSliderExtended) pnlCustomSettings.getComponent(componentIndex);

				NumberRangeModel rangeModel = (NumberRangeModel) theSlider.getModel();
								
				final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
				
				currentNetwork = Cytoscape.getCurrentNetwork();
				
				int indexType = theNumericFilter.getIndexType();
				quickFind.reindexNetwork(currentNetwork, indexType, 
						theNumericFilter.getControllingAttribute().substring(5), new TaskMonitorBase());

				GenericIndex currentIndex = quickFind.getIndex(currentNetwork);

				//System.out.println("FilterSettingPanel.restoreRangeSliderModel()...");
				//System.out.println("\tInstance of NumericFilter: componentIndex = "+ componentIndex + "\n");
				try {
					NumberIndex numberIndex = (NumberIndex) currentIndex;
					
					rangeModel.setMinValue(numberIndex.getMinimumValue());
					rangeModel.setMaxValue(numberIndex.getMaximumValue());

					//System.out.println("\t here 1: " + theSlider.isShowing());

					if (theSlider.isShowing()) {
						rangeModel.setLowValue(theNumericFilter.getLowBound());
						rangeModel.setHighValue(theNumericFilter.getHighBound());									
					}

				}
				catch (Exception e) {					
					System.out.println("Exception caught: The slider is shown on screen: " + theSlider.isShowing());
				}			
			}// for loop
		}

	}
	
	/*
	//Return value could be, QuickFind.INDEX_NODES,QuickFind.INDEX_EDGES, or -1 (unknown)  
	private int getIndexTypeForAttribute(String pAttributeName) {
		int indexType = -1;
		if (pAttributeName.startsWith("node.")){
			indexType = QuickFind.INDEX_NODES;
			//System.out.println("indexType = QuickFind.INDEX_NODES");
		}
		else if (pAttributeName.startsWith("edge.")){
			indexType = QuickFind.INDEX_EDGES;	
			//System.out.println("indexType = QuickFind.INDEX_EDGES");
		}

		return indexType;
	}
	*/
	
	private TextIndexComboBox getTextIndexComboBox(StringFilter pFilter){
		TextIndexComboBox comboBox = null;

		try {
			comboBox = ComboBoxFactory.createTextIndexComboBox((TextIndex)pFilter.getIndex(), 2.0);

			//  Set Size of ComboBox Display, based on # of specific chars
			comboBox.setPrototypeDisplayValue("01234567");

			//  Set Max Size of ComboBox to match preferred size
			comboBox.setMaximumSize(comboBox.getPreferredSize());
		} catch (Exception e) {
			System.out.println("Exception in FilterSettingpanel.getTextIndexComboBox()");
		}

		comboBox.setName(pFilter.getControllingAttribute());
		try {
			if (pFilter.getSearchStr() != null) {
				comboBox.setSelectedItem(pFilter.getSearchStr());							
			}
		}
		catch (Exception e) {
		}

		ActionListener listener = new UserSelectionListener(comboBox);
		comboBox.addFinalSelectionListener(listener);

		return comboBox;
	}
	
	
	private JRangeSliderExtended getRangerSlider(NumericFilter pFilter) {
		System.out.println("Exception in FilterSettingpanel.getRangerSlider()...");
		//NumberIndex theIndex = createNumberIndex(pNumericFilter);

		NumberRangeModel rangeModel = null;
		if (pFilter.getIndex() == null) {
			rangeModel = new NumberRangeModel(0,0,0,0);			
		}
		else {
			//Initialize the search values, lowValue and highValue	
			NumberIndex tmpIndex = (NumberIndex) pFilter.getIndex();
			pFilter.setLowBound(tmpIndex.getMinimumValue());
			pFilter.setHighBound(tmpIndex.getMaximumValue());
				
			rangeModel = new NumberRangeModel
						(tmpIndex.getMinimumValue(), tmpIndex.getMinimumValue(), tmpIndex.getMinimumValue(), tmpIndex.getMaximumValue());						
		}
		
		JRangeSliderExtended rangeSlider = new JRangeSliderExtended(rangeModel, JRangeSlider.HORIZONTAL,
	                JRangeSlider.LEFTRIGHT_TOPBOTTOM);		
				
		rangeSlider.setMinimumSize(new Dimension(100,20));
		rangeSlider.setPreferredSize(new Dimension(100,20));

		RangeSelectionListener rangeSelectionListener = new RangeSelectionListener(rangeSlider);
		rangeSlider.addChangeListener(rangeSelectionListener);
		rangeSlider.setName(pFilter.getControllingAttribute());
		
		RangeSlideMouseAdapter l = new RangeSlideMouseAdapter(); 
		rangeSlider.addMouseListener(l);
		
		return rangeSlider;
	}
	
	/**
	 * Inner class Mouse listener for double click events on rangeSlider.
	 */
	public class RangeSlideMouseAdapter extends MouseAdapter
	{
		public void mouseClicked(MouseEvent pMouseEvent)
		{
			if (pMouseEvent.getClickCount() >= 2)
			{
				System.out.println("User double clickeded on rangeSlider");	
			}
		}
	}

	private AtomicFilter getAtomicFilterFromStr(String pCtrlAttribute, int pIndexType) {
		AtomicFilter retFilter = null;
		
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
		//quickFind.addNetwork(cyNetwork, new TaskMonitorBase());
		//index_by_UniqueIdentification = (TextIndex) quickFind.getIndex(cyNetwork);
		
		quickFind.reindexNetwork(Cytoscape.getCurrentNetwork(), pIndexType, pCtrlAttribute, new TaskMonitorBase());

		int attributeType = -1;
		
		if (pIndexType == QuickFind.INDEX_NODES) {
			attributeType = Cytoscape.getNodeAttributes().getType(pCtrlAttribute);
		}
		else if (pIndexType == QuickFind.INDEX_EDGES) {
			attributeType = Cytoscape.getEdgeAttributes().getType(pCtrlAttribute);
		}
		//
		if ((attributeType == CyAttributes.TYPE_INTEGER)
				||(attributeType == CyAttributes.TYPE_FLOATING)||(attributeType == CyAttributes.TYPE_BOOLEAN)) {
				retFilter = new NumericFilter();
				retFilter.setControllingAttribute(pCtrlAttribute);
				retFilter.setIndexType(pIndexType);		

				//NumberIndex index_by_thisAttr = (NumberIndex) quickFind.getIndex(Cytoscape.getCurrentNetwork());

				retFilter.setIndex(quickFind.getIndex(Cytoscape.getCurrentNetwork()));
				
		}
		else if ((attributeType == CyAttributes.TYPE_STRING)||(attributeType == CyAttributes.TYPE_SIMPLE_LIST)) {
				retFilter = new StringFilter();	
				retFilter.setControllingAttribute(pCtrlAttribute);
				retFilter.setIndexType(pIndexType);
				
				System.out.println("FilterSettingPanel.getAtomicFilterFromStr() ...");		
				
				//TextIndex index_by_thisAttr = (TextIndex) quickFind.getIndex(Cytoscape.getCurrentNetwork());

				retFilter.setIndex(quickFind.getIndex(Cytoscape.getCurrentNetwork()));
		}
		else {
				System.out.println("AttributeType is not numeric/string/list!");
		}

		return retFilter;
	}
	
	// Update the relation label after user click radio button "AND" or "OR" in AdvancedPanel
	private void updateRelationLabel(){
		Component[] allComponents = pnlCustomSettings.getComponents();
		
    	String relationStr = "AND";
    	if (theFilter.getAdvancedSetting().getRelation()== Relation.OR) {
    		relationStr = "OR";
    	}
    	
    	for (int i=0; i<allComponents.length; i++) {
			if (allComponents[i] instanceof JLabel) {
				JLabel theLabel = (JLabel) allComponents[i]; 
				String labelName = theLabel.getName();
				if ((labelName != null) &&(labelName.equalsIgnoreCase("RelationLabel"))) {
					theLabel.setText(relationStr);
					theLabel.repaint();
				}
			}
		}
	}
	
	//user Clicked CheckBox_Not left-side of the child filter
	private void updateNegationStatus(MouseEvent e) {
		// Determine the child index in theFilter, 
		// then update the negation value for that child filter
		Object _actionObject = e.getSource();
		
		if (_actionObject instanceof JCheckBox) {
			JCheckBox _chk = (JCheckBox) _actionObject;
			int widgetGridY = (new Integer(_chk.getName())).intValue();
			int childIndex =widgetGridY/2;
			
			CyFilter childFilter = theFilter.getChildren().get(childIndex);
			if (childFilter instanceof CompositeFilter) {
				CompositeFilter tmpFilter = (CompositeFilter)childFilter;
				theFilter.setNotTable(tmpFilter, new Boolean(_chk.isSelected()));
			}
			else { // it is an AtomiCFilter
				childFilter.setNegation(_chk.isSelected());				
			}
			//Update the selection on screen
			FilterUtil.doSelection(theFilter);
		}
	}
	
	// select node/edge on current network based on the filter defined
	//private void doSelection() {
		//System.out.println("Entering FilterSettingPanel.doSelection()...");		
	//	theFilter.doSelection();
	//}

	
	
	private void addWidgetRow(CyFilter pFilter, int pGridY) {
		//System.out.println("Entering FilterSettingPanel: addWidgetRow_atomic() ...");
		
		//System.out.println("pFilter =" + pFilter.toString());
		//System.out.println("pFilter.getControllingAttribute() = "+pFilter.getControllingAttribute());
		//System.out.println("pGridY = "+ pGridY);
		
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        if (pGridY > 0) {
        	// add a row to indicate the relationship between the widgets
        	String relationStr = "AND";
        	if (theFilter.getAdvancedSetting().getRelation()== Relation.OR) {
        		relationStr = "OR";
        	}

            //Col 2 ---> Label to indicate relationship between widgets
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = pGridY-1;
            JLabel lbRelation = new JLabel(relationStr);
            lbRelation.setName("RelationLabel");
            pnlCustomSettings.add(lbRelation, gridBagConstraints);        	
        }
    
        // Col 0 -- label with attributeName/Filter
		JLabel theLabel_col0 = new JLabel();

		if (pFilter instanceof AtomicFilter) {
			AtomicFilter atomicFilter = (AtomicFilter) pFilter;
			theLabel_col0.setText(atomicFilter.getControllingAttribute());
		}
		else {
			theLabel_col0.setText("Filter");
		}

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = pGridY;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        //gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        pnlCustomSettings.add(theLabel_col0, gridBagConstraints);
    	
		//Col 1 ---> chk box -- NOT
        JCheckBox chkNot = new JCheckBox("Not");
        chkNot.setName(Integer.toString(pGridY));
        chkNot.setSelected(pFilter.getNegation());
        chkNot.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {         	
                    	updateNegationStatus(e);
                    }
                }
            );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = pGridY;
        //gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        pnlCustomSettings.add(chkNot, gridBagConstraints);

        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        //Col 2 ---> Widget if atomicfilter
		if (pFilter instanceof StringFilter) {
	        pnlCustomSettings.add(getTextIndexComboBox((StringFilter)pFilter), gridBagConstraints);		
		}
		else if (pFilter instanceof NumericFilter) {
			JRangeSliderExtended theSlider = getRangerSlider((NumericFilter) pFilter);
			pnlCustomSettings.add(theSlider, gridBagConstraints);						
		}
		else {// CompositeFilter
			gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
	        //gridBagConstraints.weightx = 0.0;
			//gridBagConstraints.anchor = java.awt.GridBagConstraints.
	        pnlCustomSettings.add(new JLabel(pFilter.getName()), gridBagConstraints);		
		}
        gridBagConstraints.weightx = 0.0;
		//Col 3 ---> label (a trash can) for delete of the row
        JLabel theDelLabel = new JLabel();

        theDelLabel.setIcon(delIcon);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = pGridY;
        //gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlCustomSettings.add(theDelLabel, gridBagConstraints);
		
        theDelLabel.setName(Integer.toString(pGridY));
        theDelLabel.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {         	
                    	removeFilterWidget(e);
                    }
                }
            );
    	
        this.validate();

	}
	
	public void addNewWidget(Object pObj) {
		//System.out.println("Entering FilterSettingPanel: addNewWidget(Object) ...");
		
		// The parameter pObj is the object selected from Attribute/Filter combobox
		// It can be either (1) a string with prefix "node."/"edge." (2) a CompositeFilter object 
		if (pObj instanceof CompositeFilter) {
			if (pObj == theFilter) {
				return; // Ignore if try to add self
			}
			addWidgetRow((CompositeFilter) pObj, theFilter.getChildren().size()*2);
			// Update theFilter object	
			theFilter.addChild((CompositeFilter) pObj, new Boolean(false));
		}
		else { //(pObj instanceof String)
			String tmpObj = (String)pObj;
			String ctrlAttribute = tmpObj.substring(5);

			int indexType = QuickFind.INDEX_NODES;
			if (tmpObj.startsWith("edge.")) {
				indexType = QuickFind.INDEX_EDGES;
			}
			AtomicFilter newChildFilter = getAtomicFilterFromStr(ctrlAttribute, indexType);			
			addWidgetRow(newChildFilter, theFilter.getChildren().size()*2);
			// Update theFilter object		
			theFilter.addChild(newChildFilter);
		}
		//Update the selection on screen
		FilterUtil.doSelection(theFilter);			
	}

	
	// Determine the child index in filter based on the row index of a component 
	// (TextIndexComboBox or RangeSlider) in the customSetting panel
	private int getChildIndexFromComponent(Component pComponent) {
		int childIndex = -1;
		int componentCount = pnlCustomSettings.getComponentCount();
		for (int i = 0; i<componentCount; i++ ) {
			Component theComponent = pnlCustomSettings.getComponent(i);
			if (theComponent == pComponent){
				if (i<5) {
					childIndex =0;
				}
				else {
					childIndex = (i-2)/5;
				}
				break;
			}
		}
		return childIndex;
	}
	
	
	// remove a GUI widget from the customeSetting panel 
	private void removeFilterWidget(MouseEvent e)
	{
		//System.out.println("Entering FilterSettingPanel.removeFilterWidget()...");
		
		Object _actionObject = e.getSource();
		
		if (_actionObject instanceof JLabel) {
			JLabel _lbl = (JLabel) _actionObject;
			int widgetGridY = (new Integer(_lbl.getName())).intValue();
			int childIndex =widgetGridY /2;
			
			//System.out.println("childIndex ="+childIndex);
			
			theFilter.removeChildAt(childIndex);

			pnlCustomSettings.removeAll();			

			initCustomSetting();
			
			
			//restoreRangeSliderModel();
			
			//if (theSlider.isShowing()) {
			//	rangeModel.setLowValue(theNumericFilter.getLowValue());
			//	rangeModel.setHighValue(theNumericFilter.getHighValue());									
			//}

			//System.out.println("\t here 2: " + theSlider.isShowing());
			//System.out.println("\trangeModel.getLowValue() =" + rangeModel.getLowValue());			
			//System.out.println("\trangeModel.getHighValue() =" + rangeModel.getHighValue());			
			//this.revalidate();
		}
		FilterUtil.doSelection(theFilter);
	}

	
	/**
	 * Listens for Final Selection from User.
	 *
	 * @author Ethan Cerami.
	 */
	class UserSelectionListener implements ActionListener {
		private TextIndexComboBox comboBox;

		/**
		 * Constructor.
		 *
		 * @param comboBox TextIndexComboBox.
		 */
		public UserSelectionListener(TextIndexComboBox comboBox) {
			this.comboBox = comboBox;
		}

		/**
		 * User has made final selection.
		 *
		 * @param e ActionEvent Object.
		 */
		public void actionPerformed(ActionEvent e) {
			//Update the StringFilter after user made a selection in the TextIndexCombobox
						
			//  Get Current User Selection
			Object o = comboBox.getSelectedItem();

			if ((o != null) && o instanceof Hit) {
				Hit hit = (Hit) comboBox.getSelectedItem();

				// Determine the row index of the TextIndexCombobox in the customSetting panel
				int widgetIndex = getChildIndexFromComponent(comboBox); 
								
				//Update theFilter Object
				List<CyFilter> theFilterlist = theFilter.getChildren();
				
				StringFilter theStringFilter = (StringFilter) theFilterlist.get(widgetIndex);
				theStringFilter.setSearchStr(hit.getKeyword());	
			}
			
			//Update the selection on screen
			//System.out.println("FilterSettingPanel. actionEvent from textIndexedComboBox...");	
			FilterUtil.doSelection(theFilter);				
		}
	}

	
	/**
	 * Action to select a range of nodes.
	 *
	 * @author Ethan Cerami.
	 */
	class RangeSelectionListener implements ChangeListener {
		private JRangeSliderExtended slider;

		/**
		 * Constructor.
		 *
		 * @param slider JRangeSliderExtended Object.
		 */
		public RangeSelectionListener(JRangeSliderExtended slider) {
			this.slider = slider;
		}

		/**
		 * State Change Event.
		 *
		 * @param e ChangeEvent Object.
		 */
		public void stateChanged(ChangeEvent e) {

			//Update theFilter object if the slider is adjusted
			//System.out.println("FilterSettingPanel.stateChanged() ...");
			
			List<CyFilter> theFilterList = theFilter.getChildren();
			
			try {
				NumberRangeModel model = (NumberRangeModel) slider.getModel();
				NumericFilter theNumericFilter = (NumericFilter) theFilterList.get(getChildIndexFromComponent(slider));
					
				//model.setMinValue(numberIndex.getMinimumValue());
				//model.setMaxValue(numberIndex.getMaximumValue());
								
				theNumericFilter.setRange((Number)model.getLowValue(), (Number)model.getHighValue());
				
				//model.setLowValue(theNumericFilter.getLowValue());
				//model.setHighValue(theNumericFilter.getHighValue());								
			}
			catch (Exception ex) {
				//NullPointerException caught -- the slider is not initialized yet								
				System.out.println("FilterSettingPanel.stateChanged():NullPointerException caught -- the slider is not initialized yet");								
			}	

			theFilter.childChanged();
			//Update the selection on screen
			System.out.println("FilterSettingPanel. rangerSlider changed Event received...");	
			FilterUtil.doSelection(theFilter);				

		}
	}
		
	private NumberIndex createNumberIndex(NumericFilter pNumericFilter) {
		final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();					
		currentNetwork = Cytoscape.getCurrentNetwork();

		int indexType = pNumericFilter.getIndexType();
		quickFind.reindexNetwork(currentNetwork, indexType, 
				pNumericFilter.getControllingAttribute().substring(5), new TaskMonitorBase());

		GenericIndex currentIndex = quickFind.getIndex(currentNetwork);
		if (currentIndex == null|| !(currentIndex instanceof NumberIndex)) {
			return null;
		}
		return (NumberIndex) currentIndex;
	}
		
	// Add a label to take up the extra space at the custom setting panel
	private void addBlankLabelToCustomPanel(){
		GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 99;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pnlCustomSettings.add(new JLabel(), gridBagConstraints);
	}
	
	private void initAdvancedSetting() {
		chkSession.setSelected(theFilter.getAdvancedSetting().isSessionChecked());
		chkGlobal.setSelected(theFilter.getAdvancedSetting().isGlobalChecked());
		chkNode.setSelected(theFilter.getAdvancedSetting().isNodeChecked());
		chkEdge.setSelected(theFilter.getAdvancedSetting().isEdgeChecked());
		
		if (theFilter.getAdvancedSetting().getRelation() == Relation.AND) {
			rbtAND.setSelected(true);
			rbtOR.setSelected(false);			
		}
		else { // Relation.OR
			rbtAND.setSelected(false);
			rbtOR.setSelected(true);						
		}
		
		lbAdvancedIcon.setIcon(plusIcon);
		lbAdvancedIcon.addMouseListener( new MouseAdapter() {
			 //Inner class Mouse listener for click on the plus/minus sign.
            public void mouseClicked(MouseEvent e) {         	
    			Object _actionObject = e.getSource();
    			// click on the plus/minus sign to hide/show advancedPanel 
    			if (_actionObject instanceof JLabel) {
    				JLabel _lbl = (JLabel) _actionObject;
    				
    				if (_lbl == lbAdvancedIcon) {
    					if (pnlAdvancedOptions.isVisible()) {
    						pnlAdvancedOptions.setVisible(false);
    				        lbAdvancedIcon.setIcon(plusIcon);
    					}
    					else {
    						pnlAdvancedOptions.setVisible(true);
    				        lbAdvancedIcon.setIcon(minusIcon);
    					}
    				}
    			}
            }
        });
		
		ItemListener l = new MyItemListener();
		chkSession.addItemListener(l);
		chkGlobal.addItemListener(l);
		chkNode.addItemListener(l);
		chkEdge.addItemListener(l);
		chkSource.addItemListener(l);
		chkTarget.addItemListener(l);
		rbtAND.addItemListener(l);
		rbtOR.addItemListener(l);
		chkNegation.addItemListener(l);
		//By default, the AdvancedPanel is invisible
		pnlAdvancedOptions.setVisible(false);
		
	}
	
	//To sync the filter object with the setting Panel
	public class MyItemListener implements ItemListener{
		
		public void itemStateChanged(ItemEvent e) {
			Object soureObj= e.getSource();
			if (soureObj instanceof javax.swing.JCheckBox) {
				JCheckBox theCheckBox = (JCheckBox) soureObj;
				
				if (theCheckBox == chkSession) {
					theFilter.getAdvancedSetting().setSession(chkSession.isSelected());	
					// filter name has a prefix "global." or "session.", refresh CMB to update
					parentPanel.refreshFilterSelectCMB();
				}
				else if (theCheckBox == chkGlobal)
				{
					theFilter.getAdvancedSetting().setGlobal(chkGlobal.isSelected());										
					parentPanel.refreshFilterSelectCMB();
				}
				else if (theCheckBox == chkNode)
				{
					theFilter.getAdvancedSetting().setNode(chkNode.isSelected());
					parentPanel.refreshAttributeCMB();
				}
				else if (theCheckBox == chkEdge)
				{
					theFilter.getAdvancedSetting().setEdge(chkEdge.isSelected());	
					if (!chkEdge.isSelected()) {
						chkSource.setSelected(false);
						chkTarget.setSelected(false);
					}
					parentPanel.refreshAttributeCMB();
				}
				else if (theCheckBox == chkSource)
				{
					theFilter.getAdvancedSetting().setSource(chkSource.isSelected());										
					parentPanel.refreshAttributeCMB();
				}	
				else if (theCheckBox == chkTarget)
				{
					theFilter.getAdvancedSetting().setTarget(chkTarget.isSelected());										
					parentPanel.refreshAttributeCMB();
				}					
				else if (theCheckBox == chkNegation)
				{
					theFilter.setNegation(chkNegation.isSelected());										
				}	
				
				if (theCheckBox == chkSource || theCheckBox == chkTarget) {
					System.out.println("chkSource or theCheckBox is checked");
					if (!chkSource.isSelected() || !chkTarget.isSelected()) {
						chkNode.setSelected(false);
						chkEdge.setSelected(true);
						theFilter.getAdvancedSetting().setNode(false);
						theFilter.getAdvancedSetting().setEdge(true);
					}
					if (!chkSource.isSelected() && !chkTarget.isSelected()) {
						chkEdge.setSelected(false);
					}
				}
				//Update the selection on screen
				if ((theCheckBox == chkNegation)||(theCheckBox == chkEdge)||(theCheckBox == chkNode)) {
					//System.out.println("FilterSettingPanel. chkNode/chkEdge/chkNegation/ is clicked");	
					theFilter.childChanged();//The setting is changed
					FilterUtil.doSelection(theFilter);				
				}
			}
			if (soureObj instanceof javax.swing.JRadioButton) {
				JRadioButton theRadioButton = (JRadioButton) soureObj;
				
				if (theRadioButton == rbtAND) {
					theFilter.getAdvancedSetting().setRelation(Relation.AND);	
				}
				if (theRadioButton == rbtOR) {
					theFilter.getAdvancedSetting().setRelation(Relation.OR);	
				}
				updateRelationLabel();

				//Update the selection on screen
				//System.out.println("FilterSettingPanel. rbtAND/rbtOR is clicked");	
				theFilter.childChanged();
				FilterUtil.doSelection(theFilter);				
			}
		}
	}
	
	
	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        pnlAdvancedSettings = new javax.swing.JPanel();
        pnlAdvancedIcon = new javax.swing.JPanel();
        lbAdvanced = new javax.swing.JLabel();
        lbAdvancedIcon = new javax.swing.JLabel();
        pnlAdvancedOptions = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        chkSession = new javax.swing.JCheckBox();
        chkGlobal = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        chkNode = new javax.swing.JCheckBox();
        chkEdge = new javax.swing.JCheckBox();
        
        jLabel8 = new javax.swing.JLabel();
        chkSource = new javax.swing.JCheckBox();
        chkTarget = new javax.swing.JCheckBox();
        
        // hide source/target row
        jLabel8.setVisible(false);
        chkSource.setVisible(false);
        chkTarget.setVisible(false);
        
        lbRelation = new javax.swing.JLabel();
        rbtAND = new javax.swing.JRadioButton();
        rbtOR = new javax.swing.JRadioButton();
        lbNegation = new javax.swing.JLabel();
        chkNegation = new javax.swing.JCheckBox();

        pnlCustomSettings = new javax.swing.JPanel();
        //lbSpaceHolder = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        pnlAdvancedSettings.setLayout(new java.awt.GridBagLayout());

        pnlAdvancedIcon.setLayout(new java.awt.GridBagLayout());

        lbAdvanced.setText(" Advanced ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        pnlAdvancedIcon.add(lbAdvanced, gridBagConstraints);

        lbAdvancedIcon.setIcon(plusIcon);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        pnlAdvancedIcon.add(lbAdvancedIcon, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        pnlAdvancedSettings.add(pnlAdvancedIcon, gridBagConstraints);

        pnlAdvancedOptions.setLayout(new java.awt.GridBagLayout());

        pnlAdvancedOptions.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jLabel6.setText("Save");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlAdvancedOptions.add(jLabel6, gridBagConstraints);

        chkSession.setSelected(true);
        chkSession.setText("Session");
        chkSession.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSession.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnlAdvancedOptions.add(chkSession, gridBagConstraints);

        chkGlobal.setText("Global");
        chkGlobal.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkGlobal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        pnlAdvancedOptions.add(chkGlobal, gridBagConstraints);

        jLabel7.setText("Select");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlAdvancedOptions.add(jLabel7, gridBagConstraints);

        chkNode.setSelected(true);
        chkNode.setText("Node");
        chkNode.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkNode.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlAdvancedOptions.add(chkNode, gridBagConstraints);

        chkEdge.setSelected(true);
        chkEdge.setText("Edge");
        chkEdge.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkEdge.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlAdvancedOptions.add(chkEdge, gridBagConstraints);
        
        jLabel8.setText("Edge");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        pnlAdvancedOptions.add(jLabel8, gridBagConstraints);


        chkSource.setSelected(true);
        chkSource.setText("Source");
        chkSource.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkSource.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlAdvancedOptions.add(chkSource, gridBagConstraints);

        chkTarget.setSelected(true);
        chkTarget.setText("Target");
        chkTarget.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkTarget.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlAdvancedOptions.add(chkTarget, gridBagConstraints);
        
        
        lbRelation.setText("Relation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 0);
        pnlAdvancedOptions.add(lbRelation, gridBagConstraints);

        rbtAND.setSelected(true);
        rbtAND.setText("AND");
        rbtAND.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbtAND.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlAdvancedOptions.add(rbtAND, gridBagConstraints);

        rbtOR.setText("OR");
        rbtOR.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        rbtOR.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlAdvancedOptions.add(rbtOR, gridBagConstraints);

        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        pnlAdvancedSettings.add(pnlAdvancedOptions, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(pnlAdvancedSettings, gridBagConstraints);

        
        lbNegation.setText("Negation");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        pnlAdvancedOptions.add(lbNegation, gridBagConstraints);

        chkNegation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkNegation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        pnlAdvancedOptions.add(chkNegation, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        pnlAdvancedSettings.add(pnlAdvancedOptions, gridBagConstraints);

        
        
        
        pnlCustomSettings.setLayout(new java.awt.GridBagLayout());

        pnlCustomSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(1, 2, 0, 2);
        add(pnlCustomSettings, gridBagConstraints);

        btnAdd.setText("Add Widgets");
        jPanel1.add(btnAdd);

        btnClose.setText("Close");
        jPanel1.add(btnClose);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        
        //Unomment the following line for test 
        //add(jPanel1, gridBagConstraints);

        
        buttonGroup1.add(rbtAND);
        buttonGroup1.add(rbtOR);
        
    }// </editor-fold>

	
    // Variables declaration - do not modify

    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkEdge;
    private javax.swing.JCheckBox chkGlobal;
    private javax.swing.JCheckBox chkNegation;
    private javax.swing.JCheckBox chkNode;
    private javax.swing.JCheckBox chkSession;
    private javax.swing.JCheckBox chkSource;
    private javax.swing.JCheckBox chkTarget;
    private javax.swing.JLabel lbRelation;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbAdvanced;
    private javax.swing.JLabel lbAdvancedIcon;
    //private javax.swing.JLabel lbSpaceHolder;
    private javax.swing.JPanel pnlAdvancedIcon;
    private javax.swing.JPanel pnlAdvancedOptions;
    private javax.swing.JPanel pnlAdvancedSettings;
    private javax.swing.JPanel pnlCustomSettings;
    private javax.swing.JRadioButton rbtAND;
    private javax.swing.JRadioButton rbtOR;
    private javax.swing.JLabel lbNegation;
    // End of variables declaration


	

}
