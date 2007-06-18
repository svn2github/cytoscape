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
import cytoscape.filters.StringFilter;
import cytoscape.filters.NumericFilter;
import cytoscape.filters.AdvancedSetting;
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

import cytoscape.quickfind.util.QuickFindFactory;

import cytoscape.quickfind.util.QuickFind;

import cytoscape.widgets.autocomplete.view.TextIndexComboBox;
import cytoscape.widgets.autocomplete.index.GenericIndex;
import cytoscape.widgets.autocomplete.index.IndexFactory;
import cytoscape.widgets.autocomplete.index.NumberIndex;
import cytoscape.widgets.autocomplete.index.TextIndex;
import cytoscape.widgets.autocomplete.view.ComboBoxFactory;
import cytoscape.widgets.autocomplete.view.TextIndexComboBox;
import cytoscape.widgets.autocomplete.index.Hit;
import cytoscape.widgets.slider.JRangeSliderExtended;
import prefuse.data.query.NumberRangeModel;
import prefuse.util.ui.JRangeSlider;
import cytoscape.quickfind.test.quickfind.test.TaskMonitorBase;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.event.ChangeEvent;
import java.awt.Component;



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

	public FilterSettingPanel(FilterMainPanel pParent, Object pFilterObj) {
		theFilter = (CompositeFilter) pFilterObj;
		parentPanel = pParent;
		
		initComponents();
		
		initAdvancedSetting();
		
		initCustomSetting();		
	}
	
	private void initCustomSetting() {
		Vector theCustomFilterVect = theFilter.getAtomicFilterVect();
		for (int i=0; i <theCustomFilterVect.size();i++) {
			addCustomFilterWidget((AtomicFilter)theCustomFilterVect.elementAt(i), i);
		}	
		addBlankLabelToCustomPanel();
		
		//this.validate();
		//this.repaint();

		//Restore initial values for RangerSliders 
		//Note: rangerSlider can not be set to their initial value until they are visible on screen
		restoreRangeSliderModel();
	}
	
	
	private void restoreRangeSliderModel(){
		Vector<AtomicFilter> theAtomicVect = theFilter.getAtomicFilterVect();
		if (theAtomicVect == null)
			return;

		for (int i=0; i<theAtomicVect.size(); i++ ) {
			
			if (theAtomicVect.elementAt(i) instanceof NumericFilter) {
				System.out.println("theAtomicVect.elementAt(" + i + ") is instance of NumericFilter");
				System.out.println("theAtomicVect.elementAt(i) = "+theAtomicVect.elementAt(i).toString());
				
				NumericFilter theNumericFilter = (NumericFilter) theAtomicVect.elementAt(i);
				
				int componentIndex = i*3 +1;
				JRangeSliderExtended theSlider = (JRangeSliderExtended) pnlCustomSettings.getComponent(componentIndex);
				NumberRangeModel rangeModel = (NumberRangeModel) theSlider.getModel();
								
				final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
				
				currentNetwork = Cytoscape.getCurrentNetwork();
				
				int indexType = getIndexTypeForAttribute(theNumericFilter.getAttributeName());
				quickFind.reindexNetwork(currentNetwork, indexType, 
						theNumericFilter.getAttributeName().substring(5), new TaskMonitorBase());

				GenericIndex currentIndex = quickFind.getIndex(currentNetwork);
				NumberIndex numberIndex = (NumberIndex) currentIndex;
				
					try {

						rangeModel.setMinValue(numberIndex.getMinimumValue());
						rangeModel.setMaxValue(numberIndex.getMaximumValue());
						rangeModel.setLowValue(theNumericFilter.getLowValue());
						rangeModel.setHighValue(theNumericFilter.getHighValue());			
					}
					catch (Exception e) {
						// slider not shown on screen yet
						System.out.println("restoreRangeSliderModel(): slider not shown on screen yet ");			
					}			
			}
		}

	}
	
	//Return value could be, QuickFind.INDEX_NODES,QuickFind.INDEX_EDGES, or -1 (unknown)  
	private int getIndexTypeForAttribute(String pAttributeName) {
		int indexType = -1;
		if (pAttributeName.startsWith("node.")){
			indexType = QuickFind.INDEX_NODES;
			System.out.println("indexType = QuickFind.INDEX_NODES");
		}
		else if (pAttributeName.startsWith("edge.")){
			indexType = QuickFind.INDEX_EDGES;	
			System.out.println("indexType = QuickFind.INDEX_EDGES");
		}

		return indexType;
	}
	
	private TextIndexComboBox getTextIndexComboBox(AtomicFilter pAtomicFilter){
		TextIndexComboBox comboBox = null;

		//node or edge?
		int indexType = getIndexTypeForAttribute(pAtomicFilter.getAttributeName());

		try {
			final QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();

			currentNetwork = Cytoscape.getCurrentNetwork();

			quickFind.reindexNetwork(currentNetwork, indexType, pAtomicFilter.getAttributeName().substring(5), new TaskMonitorBase());

			GenericIndex currentIndex = quickFind.getIndex(currentNetwork);

			TextIndex textIndex = (TextIndex) currentIndex;

			comboBox = ComboBoxFactory.createTextIndexComboBox(textIndex, 2.0);

			//  Set Size of ComboBox Display, based on # of specific chars
			comboBox.setPrototypeDisplayValue("01234567");
			//comboBox.setToolTipText("Please select or load a network to "
			//		+ "activate search functionality.");

			//  Set Max Size of ComboBox to match preferred size
			comboBox.setMaximumSize(comboBox.getPreferredSize());

		} catch (Exception e) {
			System.out.println("Exception in TextIndexing: FilterSettingpanel.addCustomFilterWidget()");
		}

		comboBox.setName(pAtomicFilter.getAttributeName());
		comboBox.setSelectedItem(pAtomicFilter.getSearchValues()[0]);

		ActionListener listener = new UserSelectionListener(comboBox);
		comboBox.addFinalSelectionListener(listener);

		return comboBox;
	}
	
	
	private JRangeSliderExtended getRangerSlider(NumericFilter pNumericFilter) {

		//NumberRangeModel rangeModel = new NumberRangeModel(1.5, 2.0, 1.0, 3.0);
		NumberRangeModel rangeModel = new NumberRangeModel(0.0, 0.0, 0.0, 0.0);
		JRangeSliderExtended rangeSlider = new JRangeSliderExtended(rangeModel, JRangeSlider.HORIZONTAL,
                JRangeSlider.LEFTRIGHT_TOPBOTTOM);
				
		rangeSlider.setMinimumSize(new Dimension(100,20));
		rangeSlider.setPreferredSize(new Dimension(100,20));

		RangeSelectionListener rangeSelectionListener = new RangeSelectionListener(rangeSlider);
		rangeSlider.addChangeListener(rangeSelectionListener);
		rangeSlider.setName(pNumericFilter.getAttributeName());
					
		return rangeSlider;
	}
	
	
	private void addCustomFilterWidget(AtomicFilter pAtomicFilter, int pGridY) {
	
		System.out.println("FiltersettingPanel.addCustomFilterWidget() ... ");
				
		int indexType = getIndexTypeForAttribute(pAtomicFilter.getAttributeName());

		if (indexType == -1) { //indexType = Unknown, skip ...
			System.out.println("indexType = Unknown, skip ...");	
			return;
		}

		//Col 1 ---> a label to display the attribute Name
		JLabel theLabel = new JLabel();
        theLabel.setText(pAtomicFilter.getAttributeName().substring(5));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridy = pGridY;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pnlCustomSettings.add(theLabel, gridBagConstraints);

		//Col 2 ---> the filter widget, either TextIndexComboBox or RangerSlider
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = pGridY;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);

		if (pAtomicFilter instanceof StringFilter) {
	        pnlCustomSettings.add(getTextIndexComboBox(pAtomicFilter), gridBagConstraints);		
		}
		else if (pAtomicFilter instanceof NumericFilter) {
			pnlCustomSettings.add(getRangerSlider((NumericFilter) pAtomicFilter), gridBagConstraints);		
		}
		
		//Col 3 ---> label (a trash can) for delete of the row
        JLabel theDelLabel = new JLabel();

        theDelLabel.setIcon(delIcon);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = pGridY;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        pnlCustomSettings.add(theDelLabel, gridBagConstraints);
		
        theDelLabel.setName(Integer.toString(pGridY));
        theDelLabel.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {         	
                    	removeAtomicFilter(e);
                    }
                }
            );
        
		this.validate();
	}

	public void addFilterWidget(String pAttributeName){
		// Create appropriate filter type, based on the attribute type

		if (pAttributeName.startsWith("node.")) {
			int attributeType = Cytoscape.getNodeAttributes().getType(pAttributeName.substring(5));
			
			if ((attributeType == CyAttributes.TYPE_INTEGER)
				||(attributeType == CyAttributes.TYPE_FLOATING)) {
				addFilterWidget(new NumericFilter(pAttributeName));				
			}
			else if (attributeType == CyAttributes.TYPE_STRING) {
				addFilterWidget(new StringFilter(pAttributeName));
			}
			else {
				System.out.println("AttributeType is neither numeric nor string!");
			}
		}
		if (pAttributeName.startsWith("edge.")) {

			//?????????????????????????
			//?????????????????????????
		}		
	}

	
	public void addFilterWidget(AtomicFilter pAtomicFilter) {
		addCustomFilterWidget(pAtomicFilter,theFilter.getAtomicFilterVect().size());
		// Update theFilter object		
		theFilter.getAtomicFilterVect().add(pAtomicFilter);
	}

	
	// Determine the row index of a component (TextIndexComboBox or RangeSlider) 
	// in the customSetting panel
	private int getWidgetRowIndex(Component pComponent) {
		int widgetIndex = -1;
		int componentCount = pnlCustomSettings.getComponentCount();
		for (int i = 0; i<componentCount; i++ ) {
			Component theComponent = pnlCustomSettings.getComponent(i);
			if (theComponent == pComponent){
				widgetIndex = i/3; // There are three components for each row
				break;
			}
		}
		return widgetIndex;
	}
	
	
	// remove a GUI widget from the customeSetting panel 
	private void removeAtomicFilter(MouseEvent e)
	{
		Object _actionObject = e.getSource();
		
		if (_actionObject instanceof JLabel) {
			JLabel _lbl = (JLabel) _actionObject;
			theFilter.removeAtomicFilterAt((new Integer(_lbl.getName())).intValue());
			
			pnlCustomSettings.removeAll();
			initCustomSetting();
			
			this.validate();
		}
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
				int widgetIndex = getWidgetRowIndex(comboBox); 
								
				//Update theFilter Object
				Vector<AtomicFilter> theAtomicFilterVect = theFilter.getAtomicFilterVect();
				
				StringFilter theStringFilter = (StringFilter) theAtomicFilterVect.elementAt(widgetIndex);
				theStringFilter.setSearchStr(hit.getKeyword());				
			}
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
			
			Vector<AtomicFilter> theAtomicFilterVect = theFilter.getAtomicFilterVect();
			
			try {
				NumberRangeModel model = (NumberRangeModel) slider.getModel();
				
				NumericFilter theNumericFilter = (NumericFilter) theAtomicFilterVect.
								elementAt(getWidgetRowIndex(slider));
				String[] theSearchValues = new String[2];
				theSearchValues[0] = model.getLowValue().toString();
				theSearchValues[1] = model.getHighValue().toString();
				theNumericFilter.setSearchValues(theSearchValues);				
			}
			catch (Exception ex) {
				//NullPointerException caught -- the slider is not initialized yet								
			}	
		}
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
		chkSource.setSelected(theFilter.getAdvancedSetting().isSourceChecked());
		chkTarget.setSelected(theFilter.getAdvancedSetting().isTargetChecked());

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
				}
				else if (theCheckBox == chkGlobal)
				{
					theFilter.getAdvancedSetting().setGlobal(chkGlobal.isSelected());										
				}
				else if (theCheckBox == chkNode)
				{
					theFilter.getAdvancedSetting().setNode(chkNode.isSelected());										
				}
				else if (theCheckBox == chkEdge)
				{
					theFilter.getAdvancedSetting().setEdge(chkEdge.isSelected());										
				}
				else if (theCheckBox == chkSource)
				{
					theFilter.getAdvancedSetting().setSource(chkSource.isSelected());										
				}
				else if (theCheckBox == chkTarget)
				{
					theFilter.getAdvancedSetting().setTarget(chkTarget.isSelected());										
				}
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

        jLabel8.setText("Interaction");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
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
        
        //Unomment the following line fro test 
        //add(jPanel1, gridBagConstraints);

    }// </editor-fold>

	
    // Variables declaration - do not modify

    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox chkEdge;
    private javax.swing.JCheckBox chkGlobal;
    private javax.swing.JCheckBox chkNode;
    private javax.swing.JCheckBox chkSession;
    private javax.swing.JCheckBox chkSource;
    private javax.swing.JCheckBox chkTarget;
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

    // End of variables declaration


	

}
