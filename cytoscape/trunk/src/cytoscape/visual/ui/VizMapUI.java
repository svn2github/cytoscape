//------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//------------------------------------------------------------------------------
package cytoscape.visual.ui;
//------------------------------------------------------------------------------
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import cytoscape.dialogs.GridBagGroup;
import cytoscape.dialogs.MiscGB;
//------------------------------------------------------------------------------

/**
 * Primary UI class for the Set Visual Properties dialog box.
 */
public class VizMapUI extends JDialog {
    // constants for attribute types, one for each tab
    public static final byte NODE_COLOR = 0;
    public static final byte NODE_BORDER_COLOR = 1;
    public static final byte NODE_LINETYPE = 2;
    public static final byte NODE_SHAPE = 3;
    public static final byte NODE_SIZE = 4;
    public static final byte NODE_LABEL = 5;
    public static final byte NODE_LABEL_FONT = 6;
    public static final byte EDGE_COLOR = 7;
    public static final byte EDGE_LINETYPE = 8;
    public static final byte EDGE_SRCARROW = 9;
    public static final byte EDGE_TGTARROW = 10;
    public static final byte EDGE_LABEL = 11;
    public static final byte EDGE_LABEL_FONT = 12;
    public static final byte NODE_TOOLTIP = 13;
    public static final byte EDGE_TOOLTIP = 14;
    
    // for creating VizMapTabs with font face/size on one page
    public static final byte NODE_FONT_FACE = 122;
    public static final byte NODE_FONT_SIZE = 123;
    public static final byte EDGE_FONT_FACE = 124;
    public static final byte EDGE_FONT_SIZE = 125;

    // for creating VizMapTabs with locked node height/width
    public static final byte NODE_HEIGHT = 126;
    public static final byte NODE_WIDTH = 127;

    // VisualMappingManager for the graph.
    protected VisualMappingManager VMM;
    /** The content pane for the dialog */
    private JPanel mainPane;
    private GridBagGroup mainGBG;
    private JPanel actionButtonsPanel, attrSelectorPanel;
    /** The content pane for the JTabbedPanes */
    private JPanel tabPaneContainer;
    /** Keeps track of contained tabs */
    private VizMapTab[] tabs;
    /**
     *  All known VisualStyles
     */
    protected Collection styles;
	
    // kludge!
    private boolean initialized = false;

    /**
     *	Make and display the Set Visual Properties UI.
     *
     *	@param	VMM	VisualMappingManager for the graph
     */
    public VizMapUI(VisualMappingManager VMM) {
	super(VMM.getCytoscapeWindow().getMainFrame(), "Set Visual Properties");
	
	this.VMM = VMM;
	this.mainGBG = new GridBagGroup();
	this.mainPane = mainGBG.panel;
	//MiscGB.pad(mainGBG.constraints, 2, 2);
	//MiscGB.inset(mainGBG.constraints, 3);
	this.tabs = new VizMapTab[EDGE_LABEL_FONT + 1];
	this.tabPaneContainer = new JPanel(false);

	JTabbedPane nodePane = new JTabbedPane();
	JTabbedPane edgePane = new JTabbedPane();
	
	// add panes to tabbed panes
	for (byte i = NODE_COLOR; i <= NODE_LABEL_FONT; i++) {
	    VizMapTab tab;
	    if (i == NODE_SIZE)
		tab = new VizMapSizeTab(this, VMM, i);
	    else if (i == NODE_LABEL_FONT)
		tab = new VizMapFontTab(this, VMM, i);
	    else
		tab = new VizMapAttrTab(this, VMM, i);
	    nodePane.add(tab);
	    tabs[i] = tab;
	}
	for (byte i = EDGE_COLOR; i <= EDGE_LABEL_FONT; i++) {
	    VizMapTab tab;
	    if (i == EDGE_LABEL_FONT)
		tab = new VizMapFontTab(this, VMM, i);
	    else
		tab = new VizMapAttrTab(this, VMM, i);
	    edgePane.add(tab);
	    tabs[i] = tab;
	}
	
	// global default pane
	JPanel defaultPane = new DefaultPanel(this, VMM);
	
	// node/edge/default selector
	ButtonGroup grp = new ButtonGroup();
	JToggleButton nodeSelect = new JToggleButton("Node Attributes", true);
	JToggleButton edgeSelect = new JToggleButton("Edge Attributes", false);
	JToggleButton defSelect = new JToggleButton("Global Defaults", false);
	grp.add(defSelect);
	grp.add(nodeSelect);
	grp.add(edgeSelect);
	Color nodeColor = new Color(130, 150, 129);
	Color edgeColor = new Color(124, 134, 173);
	Color defColor = new Color(201,201,15);
	nodeSelect.addActionListener(new AttrSelector(nodePane,nodeColor));
	edgeSelect.addActionListener(new AttrSelector(edgePane,edgeColor));
	defSelect.addActionListener(new AttrSelector(defaultPane,defColor));
	
	this.attrSelectorPanel = new JPanel(new FlowLayout(), false);
	attrSelectorPanel.add(nodeSelect);
	attrSelectorPanel.add(edgeSelect);
	attrSelectorPanel.add(defSelect);
 	//attrSelectorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

	StyleSelector vizStylePanel = new StyleSelector(this);

	MiscGB.insert(mainGBG, vizStylePanel, 0, 0, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
	MiscGB.insert(mainGBG, attrSelectorPanel, 0, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
	MiscGB.insert(mainGBG, tabPaneContainer, 0, 2, 1, 1, 1, 1, GridBagConstraints.BOTH);
	
	// add apply & cancel button
	this.actionButtonsPanel = new JPanel();
	JButton applyButton = new JButton("Apply");
	applyButton.addActionListener(new ApplyAction());
	JButton closeButton = new JButton("Close");
	closeButton.addActionListener(new CloseAction());
	actionButtonsPanel.add(applyButton);
	actionButtonsPanel.add(closeButton);
	
	MiscGB.insert(mainGBG, actionButtonsPanel, 0, 3, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);
	
	setContentPane(mainPane);
	pack();
	//this.show();
	nodeSelect.doClick();
	initialized = true;
    }
    /**
     * StyleSelector implements the style selection control at the top of the
     * VizMapUI.
     */
    protected class StyleSelector extends JPanel {
	/**
	 *  Reference to catalog
	 */
	protected CalculatorCatalog catalog;

	/**
	 *  Combo box for style selection
	 */
	protected JComboBox styleComboBox;

	/**
	 *  GridBagGroup for layout
	 */
	protected GridBagGroup styleGBG;

	/**
	 *  Currently selected style
	 */
	protected VisualStyle currentStyle;

	/**
	 *  Parent JDialog
	 */
	protected JDialog mainUIDialog;

	/**
	 *  Reference to calculator catalog
	 */
	protected StyleSelector(JDialog parent) {
	    super(false);
	    this.mainUIDialog = parent;
	    this.catalog = VMM.getCalculatorCatalog();
	    styles = catalog.getVisualStyles();
	    this.styleGBG = new GridBagGroup("Style");
	    MiscGB.pad(styleGBG.constraints, 2, 2);
	    MiscGB.inset(styleGBG.constraints, 3);
	    
	    resetStyles();

	    // new style button
	    JButton newStyle = new JButton("New");
	    newStyle.addActionListener(new NewStyleListener());
	    MiscGB.insert(styleGBG, newStyle, 0, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

	    // duplicate style button
	    JButton dupeStyle = new JButton("Duplicate");
	    dupeStyle.addActionListener(new DupeStyleListener());
	    MiscGB.insert(styleGBG, dupeStyle, 1, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

	    // rename style button
	    JButton renStyle = new JButton("Rename");
	    renStyle.addActionListener(new RenStyleListener());
	    MiscGB.insert(styleGBG, renStyle, 2, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

	    // remove style button
	    JButton rmStyle = new JButton("Remove");
	    rmStyle.addActionListener(new RmStyleListener());
	    MiscGB.insert(styleGBG, rmStyle, 3, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL);

	    add(styleGBG.panel);
	}
	
	public String getStyleName(VisualStyle s) {
	    String suggestedName = null;
	    if (s != null)
		suggestedName = this.catalog.checkVisualStyleName(s.getName());
	    // keep prompting for input until user cancels or we get a valid name
	    while(true) {
		String ret = (String) JOptionPane.showInputDialog(mainUIDialog,
								  "Name for new visual style",
								  "Visual Style Name Input",
								  JOptionPane.QUESTION_MESSAGE,
								  null, null,
								  suggestedName);
		if (ret == null) {
		    return null;
		}
		String newName = catalog.checkVisualStyleName(ret);
		if (newName.equals(ret))
		    return ret;
		int alt = JOptionPane.showConfirmDialog(mainUIDialog,
							"Visual style with name " + ret + " already exists,\nrename to " + newName + " okay?",
							"Duplicate visual style name",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null);
		if (alt == JOptionPane.YES_OPTION)
		    return newName;
	    }
	}

	protected class NewStyleListener extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {
		// just create a new style with all mappers set to none
		// get a name for the new calculator
		String name = getStyleName(null);
		if (name == null)
		    return;
		currentStyle = new VisualStyle(name);
		catalog.addVisualStyle(currentStyle);
		VMM.setVisualStyle(currentStyle);
		resetStyles();
	    }
	}

	protected class RenStyleListener extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {
		String name = getStyleName(currentStyle);
		if (name == null)
		    return;
		currentStyle.setName(name);
		resetStyles();
	    }
	}

	protected class RmStyleListener extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {
		if (styles.size() == 1) {
		    JOptionPane.showMessageDialog(mainUIDialog,
						  "There must be at least one visual style",
						  "Cannot remove style",
						  JOptionPane.ERROR_MESSAGE);
		    return;
		}
		catalog.removeVisualStyle(currentStyle.getName());
		currentStyle = (VisualStyle) styles.iterator().next();
		VMM.setVisualStyle(currentStyle);
		resetStyles();
	    }
	}

	protected class DupeStyleListener extends AbstractAction {
	    public void actionPerformed(ActionEvent e) {
		VisualStyle clone = null;
		try {
		    clone = (VisualStyle) currentStyle.clone();
		}
		catch (CloneNotSupportedException exc) {
		    System.err.println("Clone not supported exception!");
		}
		// get new name for clone
		String newName = getStyleName(clone);
		if (newName == null)
		    return;
		clone.setName(newName);
		catalog.addVisualStyle(clone);
		currentStyle = clone;
		VMM.setVisualStyle(currentStyle);
		resetStyles();
	    }
	}
	
	protected class StyleSelectionListener implements ItemListener {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    currentStyle = (VisualStyle) styleComboBox.getSelectedItem();
		    VMM.setVisualStyle(currentStyle);
		    visualStyleChanged();
		}
	    }
	}

	/**
	 *  Populates the styles combo box
	 */
	protected void setupStyleComboBox() {
	    Object styleArray[] = new Object[styles.size()];
	    Iterator styleIter = styles.iterator();
	    for (int i = 0; styleIter.hasNext(); i++) {
		styleArray[i] = (VisualStyle)styleIter.next();
	    }
	    this.styleComboBox = new JComboBox(styleArray);
	    this.styleComboBox.setSelectedItem(null);
	    // attach listener
	    this.styleComboBox.addItemListener(new StyleSelectionListener());
	    if (this.currentStyle == null)
		this.currentStyle = (VisualStyle) styleArray[0];
	    this.styleComboBox.setSelectedItem(this.currentStyle);
	}
	 
	/**
	 *  Reset the style selection controls.
	 */
	public void resetStyles() {
	    // reset local style collection
	    styles = catalog.getVisualStyles();
	    if (this.styleComboBox != null)
		this.styleGBG.panel.remove(this.styleComboBox);
	    setupStyleComboBox();
	    MiscGB.insert(this.styleGBG, this.styleComboBox, 0, 0, 4, 1, 1, 0, GridBagConstraints.HORIZONTAL);
	    validate();
	    repaint();
	}
    }
    
    private class AttrSelector implements ActionListener {
	private JComponent myTab;
	private Color bgColor;
	private AttrSelector(JComponent myTab, Color bg) {
	    this.myTab = myTab;
	    this.bgColor = bg;
	}
	public void actionPerformed(ActionEvent e) {
	    tabPaneContainer.removeAll();
	    tabPaneContainer.add(myTab);
	    tabPaneContainer.setBackground(bgColor);
	    actionButtonsPanel.setBackground(bgColor);
	    attrSelectorPanel.setBackground(bgColor);
	    pack();
	    repaint();
	}
    }
    
    // apply button action listener
    private class ApplyAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    VMM.applyAppearances();
	}
    }

    // close button action listener
    private class CloseAction extends AbstractAction {
	public void actionPerformed(ActionEvent e) {
	    dispose();
	}
    }
    
    /**
     * When the data structures (eg. NodeAttributes, EdgeAttributes) change,
     * refresh the UI.
     */
    public void refreshUI() {
	for (int i = 0; i < tabs.length; i++) {
	    tabs[i].refreshUI();
	}
    }

    /**
     * When the currently selected visual styles changed, a new set of calculators
     * with their corresponding interfaces must be switched into the UI.
     */
    public void visualStyleChanged() {
	for (int i = 0; i < tabs.length; i++) {
	    tabs[i].visualStyleChanged();
	}
	validate();
	repaint();
    }

    /**
     * Due to a Java AWT design choice, {@link Component}s may belong to only one
     * {@link Container}. This causes problems in the VizMapper when there are
     * attributes that share calculators, such as Node Color and Node Border Color.
     * Each VizMapTab calls this method when switching calculators to ensure that
     * the newly selected calculator is not already selected elsewhere.
     * @param	selectedCalc	Calculator that the calling VizMapTab is trying to
     *				switch to.
     * @return	true if calculator already selected elsewhere, false otherwise
     */
    VizMapTab checkCalcSelected(Calculator selectedCalc) {
	if (!initialized)
	    return null;
	VizMapTab selected = null;
	for (int i = 0; i < tabs.length && (selected == null); i++) {
	    VizMapTab t = tabs[i];
	    selected = t.checkCalcSelected(selectedCalc);
	}
	return selected;
    }

    /**
     * Ensure that the calculator to be removed isn't used in other visual styles.
     * If it is, return the names of visual styles that are currently using it.
     * 
     * @param	c	calculator to check usage for
     * @return	names of visual styles using the calculator
     */
    public Vector checkCalculatorUsage(Calculator c) {
	Vector conflicts = new Vector();
	for (Iterator iter = styles.iterator(); iter.hasNext();) {
	    VisualStyle vs = (VisualStyle) iter.next();
	    String styleName = vs.checkConflictingCalculator(c);
	    if (styleName != null)
		conflicts.add(styleName);
	}
	return conflicts;
    }
}
