/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.visual.ui;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.Cytoscape;
import org.cytoscape.GraphObject;
import org.cytoscape.Node;
import org.cytoscape.Edge;

import org.cytoscape.attributes.CyAttributes;
import org.cytoscape.attributes.CyAttributesUtils;
import org.cytoscape.attributes.MultiHashMapListener;

import cytoscape.util.SwingWorker;

import cytoscape.util.swing.DropDownMenuButton;

import org.cytoscape.view.DiscreteValue;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.view.VisualPropertyIcon;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.NetworkPanel;
import org.cytoscape.view.VisualProperty;

import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.vizmap.VisualMappingManager;

import org.cytoscape.vizmap.VisualStyle;

import org.cytoscape.vizmap.calculators.BasicCalculator;
import org.cytoscape.vizmap.calculators.Calculator;

import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassThroughMapping;

import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;
import cytoscape.visual.ui.editors.discrete.CyColorCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyColorPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyDoublePropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyFontPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyLabelPositionPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyStringPropertyEditor;
import cytoscape.visual.ui.editors.discrete.FontCellRenderer;
import cytoscape.visual.ui.editors.discrete.LabelPositionCellRenderer;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;


/**
 * New VizMapper UI main panel.
 *
 * This panel consists of 3 panels:
 * <ul>
 * <li>Global Control Panel
 * <li>Default editor panel
 * <li>Visual Mapping Browser
 * </ul>
 *
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends JPanel implements PropertyChangeListener, PopupMenuListener,
                                                          ChangeListener {
	private final static long serialVersionUID = 1202339867854959L;
	private static final Color UNUSED_COLOR = new Color(100, 100, 100, 50);
	public enum DefaultEditor {
		NODE,
		EDGE,
		GLOBAL;
	}

	private static JPopupMenu menu;
	private static JMenuItem delete;
	private static JMenuItem rainbow1;
	private static JMenuItem rainbow2;
	private static JMenuItem randomize;
	private static JMenuItem series;
	private static JMenuItem fit;
	private static JMenuItem editAll;
	private static JPopupMenu optionMenu;
	private static JMenuItem newVS;
	private static JMenuItem renameVS;
	private static JMenuItem deleteVS;
	private static JMenuItem duplicateVS;
	private static JMenuItem createLegend;
	private static JMenu generateValues;
	private static JMenu modifyValues;
	private static JMenuItem brighter;
	private static JMenuItem darker;

	/*
	 * Icons used in this panel.
	 */
	private static final ImageIcon optionIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_form-properties.png"));
	private static final ImageIcon delIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_delete-16.png"));
	private static final ImageIcon addIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_data-new-table-16.png"));
	private static final ImageIcon rndIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_filters-16.png"));
	private static final ImageIcon renameIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_redo-16.png"));
	private static final ImageIcon duplicateIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_slide-duplicate.png"));
	private static final ImageIcon legendIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_graphic-styles-16.png"));
	private static final ImageIcon editIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_edit-16.png"));
	private static final String DEFAULT_VS_NAME = "default";

	/*
	 * This is a singleton.
	 */
	private static VizMapperMainPanel panel;

	/*
	 * Visual mapping manager. All parameters should be taken from here.
	 */
	private VisualMappingManager vmm;

	/** The Visual Style that is currently being edited by this MainPanel.
	 * Note that the value of this may change as network views are focued
	 * (i.e. this variable should be the only place it is stored in)
	 *  
	 */
	private VisualStyle currentlyEditedVS;
	
	/** The GraphView, the VisualStyle of which is currently edited.
	 * Note that the value of this may change as network views are focued
	 * (i.e. this variable should be the only place it is stored in)
	 * 
	 * Also note that it might be null (when editing a VisualStyle on its own)
	 */
	private GraphView currentView;
	
	/** store the Table state for each VisualStyle in this map, by storing the TableModel*/
	private Map<String, TableModel> vsToModelMap;

	// Keeps current discrete mappings.  NOT PERMANENT
	private final Map<String, Map<Object, Object>> discMapBuffer = new HashMap<String, Map<Object, Object>>();
	private String lastVSName = null;
	private Map<VisualProperty, JDialog> editorWindowManager = new HashMap<VisualProperty, JDialog>();
	private Map<String, Image> defaultImageManager = new HashMap<String, Image>();

	
	private boolean ignore = false;
	
	// For node size lock
	VizMapperProperty nodeSize;
	VizMapperProperty nodeWidth;
	VizMapperProperty nodeHeight;

	/** Creates new form AttributeOrientedPanel */
	private VizMapperMainPanel() {
		vmm = Cytoscape.getVisualMappingManager();
		vmm.addChangeListener(this);

		vsToModelMap = new HashMap<String, TableModel>();
		setMenu();

		// Need to register listener here, instead of CytoscapeDesktop.
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(new VizMapListener());

		initComponents();
		registerCellEditorListeners();
		
		// By default, force to sort property by prop name.
		visualPropertySheetPanel.setSorting(true);
		
		Cytoscape.getNodeAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(this,
																										Cytoscape.getNodeAttributes(),
																										nodeAttrEditor, nodeNumericalAttrEditor));
		Cytoscape.getEdgeAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(this,
																										Cytoscape.getEdgeAttributes(),
																										edgeAttrEditor, edgeNumericalAttrEditor));
		Cytoscape.getNetworkAttributes().getMultiHashMap().addDataListener(new MultiHashMapListenerAdapter(this,
																										   Cytoscape.getNetworkAttributes(),
																										   null, null));
	}

	/*
	 * Register listeners for editors.
	 */
	private void registerCellEditorListeners() {
		nodeAttrEditor.addPropertyChangeListener(this);
		edgeAttrEditor.addPropertyChangeListener(this);

		mappingTypeEditor.addPropertyChangeListener(this);

		colorCellEditor.addPropertyChangeListener(this);
		fontCellEditor.addPropertyChangeListener(this);
		numberCellEditor.addPropertyChangeListener(this);
		stringCellEditor.addPropertyChangeListener(this);
		labelPositionEditor.addPropertyChangeListener(this);
	}

	/**
	 * Get an instance of VizMapper UI panel. This is a singleton.
	 *
	 * @return
	 */
	public static VizMapperMainPanel getVizMapperUI() {
		if (panel == null)
			panel = new VizMapperMainPanel();

		return panel;
	}

	/**
	 * Setup menu items.<br>
	 *
	 * This includes both icon menu and right-click menu.
	 *
	 */
	private void setMenu() {
		/*
		 * Option Menu
		 */
		newVS = new JMenuItem("Create new Visual Style...");
		newVS.setIcon(addIcon);
		newVS.addActionListener(new NewStyleListener());

		deleteVS = new JMenuItem("Delete Visual Style...");
		deleteVS.setIcon(delIcon);
		deleteVS.addActionListener(new RemoveStyleListener());

		renameVS = new JMenuItem("Rename Visual Style...");
		renameVS.setIcon(renameIcon);
		renameVS.addActionListener(new RenameStyleListener());

		duplicateVS = new JMenuItem("Copy existing Visual Style...");
		duplicateVS.setIcon(duplicateIcon);
		duplicateVS.addActionListener(new CopyStyleListener());

		createLegend = new JMenuItem("Create legend from current Visual Style");
		createLegend.setIcon(legendIcon);
		createLegend.addActionListener(new CreateLegendListener());
		optionMenu = new JPopupMenu();
		optionMenu.add(newVS);
		optionMenu.add(deleteVS);
		optionMenu.add(renameVS);
		optionMenu.add(duplicateVS);
		optionMenu.add(createLegend);

		/*
		 * Build right-click menu
		 */
		generateValues = new JMenu("Generate Discrete Values");
		generateValues.setIcon(rndIcon);
		modifyValues = new JMenu("Modify Discrete Values");

		delete = new JMenuItem("Delete mapping");

		final Font italicMenu = new Font("SansSerif", Font.ITALIC, 14);
		rainbow1 = new JMenuItem("Rainbow 1");
		rainbow2 = new JMenuItem("Rainbow 2 (w/modulations)");
		randomize = new JMenuItem("Randomize");
		rainbow1.setFont(italicMenu);
		rainbow2.setFont(italicMenu);

		series = new JMenuItem("Series (Number Only)");
		fit = new JMenuItem("Fit Node Width to Label");

		brighter = new JMenuItem("Brighter");
		darker = new JMenuItem("Darker");

		editAll = new JMenuItem("Edit selected values at once...");

		delete.setIcon(delIcon);
		editAll.setIcon(editIcon);

		rainbow1.addActionListener(new GenerateValueListener(GenerateValueListener.RAINBOW1));
		rainbow2.addActionListener(new GenerateValueListener(GenerateValueListener.RAINBOW2));
		randomize.addActionListener(new GenerateValueListener(GenerateValueListener.RANDOM));

		series.addActionListener(new GenerateSeriesListener());
		fit.addActionListener(new FitLabelListener());

		brighter.addActionListener(new BrightnessListener(BrightnessListener.BRIGHTER));
		darker.addActionListener(new BrightnessListener(BrightnessListener.DARKER));

		delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeMapping();
				}
			});
		editAll.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					editSelectedCells();
				}
			});
		// add.addActionListener(l)
		// select.setIcon(vmIcon);
		menu = new JPopupMenu();
		generateValues.add(rainbow1);
		generateValues.add(rainbow2);
		generateValues.add(randomize);
		generateValues.add(series);
		generateValues.add(fit);

		modifyValues.add(brighter);
		modifyValues.add(darker);

		rainbow1.setEnabled(false);
		rainbow2.setEnabled(false);
		randomize.setEnabled(false);
		series.setEnabled(false);
		fit.setEnabled(false);

		brighter.setEnabled(false);
		darker.setEnabled(false);

		menu.add(delete);
		menu.add(new JSeparator());
		menu.add(generateValues);
		menu.add(modifyValues);
		menu.add(editAll);
		menu.add(new JSeparator());

		delete.setEnabled(false);
		menu.addPopupMenuListener(this);
	}

	public static Object showValueSelectDialog(VisualProperty type, Component caller)
	    throws Exception {
		return EditorFactory.showDiscreteEditor(type);
	}

	/**
	 * GUI initialization code based on the auto-generated code from NetBeans
	 *
	 */
	private void initComponents() {
		mainSplitPane = new javax.swing.JSplitPane();
		defaultAppearencePanel = new javax.swing.JPanel();
		visualPropertySheetPanel = new PropertySheetPanel();
		//System.out.println("putting in initComponents:"+vmm.getVisualStyle().getName()+" -> "+visualPropertySheetPanel.getTable().getModel());
		vsToModelMap.put(currentlyEditedVS.getName(), visualPropertySheetPanel.getTable().getModel());
		
		vsSelectPanel = new javax.swing.JPanel();
		vsNameComboBox = new javax.swing.JComboBox();

		defaultAppearencePanel.setMinimumSize(new Dimension(100, 100));
		defaultAppearencePanel.setPreferredSize(new Dimension(mainSplitPane.getWidth(),
		                                                      this.mainSplitPane.getDividerLocation()));
		defaultAppearencePanel.setSize(defaultAppearencePanel.getPreferredSize());
		defaultAppearencePanel.setLayout(new BorderLayout());

		mainSplitPane.setDividerLocation(120);
		mainSplitPane.setDividerSize(4);
		mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		defaultAppearencePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                              "Defaults",
		                                                                              javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                              javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                              new java.awt.Font("SansSerif",
		                                                                                                1,
		                                                                                                12),
		                                                                              java.awt.Color.darkGray));
		
		mainSplitPane.setLeftComponent(defaultAppearencePanel);

		visualPropertySheetPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                                "Visual Mapping Browser",
		                                                                                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                                javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                                new java.awt.Font("SansSerif",
		                                                                                                  1,
		                                                                                                  12),
		                                                                                java.awt.Color.darkGray));

		mainSplitPane.setRightComponent(visualPropertySheetPanel);

		vsSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                     "Current Visual Style",
		                                                                     javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                     javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                     new java.awt.Font("SansSerif",
		                                                                                       1, 12),
		                                                                     java.awt.Color.darkGray));

		vsNameComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					vsNameComboBoxActionPerformed(evt);
				}
			});

		optionButton = new DropDownMenuButton(new AbstractAction() {
	private final static long serialVersionUID = 1213748836776579L;
				public void actionPerformed(ActionEvent ae) {
					DropDownMenuButton b = (DropDownMenuButton) ae.getSource();
					optionMenu.show(b, 0, b.getHeight());
				}
			});

		optionButton.setToolTipText("Options...");
		optionButton.setIcon(optionIcon);
		optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		optionButton.setComponentPopupMenu(optionMenu);

		org.jdesktop.layout.GroupLayout vsSelectPanelLayout = new org.jdesktop.layout.GroupLayout(vsSelectPanel);
		vsSelectPanel.setLayout(vsSelectPanelLayout);
		vsSelectPanelLayout.setHorizontalGroup(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                          .add(vsSelectPanelLayout.createSequentialGroup()
		                                                                                  .addContainerGap()
		                                                                                  .add(vsNameComboBox,
		                                                                                       0,
		                                                                                       146,
		                                                                                       Short.MAX_VALUE)
		                                                                                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                  .add(optionButton,
		                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                       64,
		                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                  .addContainerGap()));
		vsSelectPanelLayout.setVerticalGroup(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                        .add(vsSelectPanelLayout.createSequentialGroup()
		                                                                                .add(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                        .add(vsNameComboBox,
		                                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                                        .add(optionButton)) // .addContainerGap(
		                                                                                                                            // org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                            // Short.MAX_VALUE)
		));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(vsSelectPanel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     Short.MAX_VALUE)
		                                .add(mainSplitPane,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280,
		                                     Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup()
		                                         .add(vsSelectPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(mainSplitPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              510, Short.MAX_VALUE)));
	} // </editor-fold>

	// Variables declaration - do not modify
	private JPanel defaultAppearencePanel;
	private javax.swing.JSplitPane mainSplitPane;
	private DropDownMenuButton optionButton;
	private PropertySheetPanel visualPropertySheetPanel;
	private javax.swing.JComboBox vsNameComboBox;
	private javax.swing.JPanel vsSelectPanel;

	/*
	 * Renderer and Editors for the cells
	 */

	// For general values (string & number)
	private DefaultTableCellRenderer defCellRenderer = new DefaultTableCellRenderer();

	// For String values
	private CyStringPropertyEditor stringCellEditor = new CyStringPropertyEditor();

	// For colors
	private CyColorCellRenderer collorCellRenderer = new CyColorCellRenderer();
	private CyColorPropertyEditor colorCellEditor = new CyColorPropertyEditor();
	
	// For sizes
	private CyDoublePropertyEditor numberCellEditor = new CyDoublePropertyEditor();

	// For font faces
	private CyFontPropertyEditor fontCellEditor = new CyFontPropertyEditor();
	private FontCellRenderer fontCellRenderer = new FontCellRenderer();

	// For label positions
	private LabelPositionCellRenderer labelPositionRenderer = new LabelPositionCellRenderer();
	private CyLabelPositionPropertyEditor labelPositionEditor = new CyLabelPositionPropertyEditor();

	// Others
	private DefaultTableCellRenderer emptyBoxRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer filledBoxRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer continuousRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer discreteRenderer = new DefaultTableCellRenderer();

	/*
	 * Controlling attr selector
	 */
	private CyComboBoxPropertyEditor nodeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor nodeNumericalAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeNumericalAttrEditor = new CyComboBoxPropertyEditor();

	// For mapping types.
	private CyComboBoxPropertyEditor mappingTypeEditor = new CyComboBoxPropertyEditor();
	private PropertyRendererRegistry rendReg = new PropertyRendererRegistry();
	private PropertyEditorRegistry editorReg = new PropertyEditorRegistry();

	// End of variables declaration
	private void vsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		final String vsName = (String) vsNameComboBox.getSelectedItem();

		if (vsName != null) {
			if (currentView.equals(Cytoscape.getNullNetworkView())) {
				switchVS(vsName, false);
			} else {
				switchVS(vsName, true);
			}
		}
	}

	private void switchVS(String vsName) {
		switchVS(vsName, true);
	}

	/** set given TableModel as model for PropertySheetPanel widget;
	 * workaround bug in PropertySheetPanel.
	 * model _must_ be a PropertySheetTableModel since it is cast to one somwhere.
	 */
	private void setModel(TableModel model){
		visualPropertySheetPanel.getTable().setModel(model);
		
		// workaround the fact that PropertySheetPanel caches the TableModel and thus would ignore the model change.
		visualPropertySheetPanel.setTable(visualPropertySheetPanel.getTable());
		// set other stuff that PropertySheetPanel proxies to the model:
		visualPropertySheetPanel.setSorting(true);
		visualPropertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);
	}
	private void switchVS(String vsName, boolean redraw) {
		System.out.println("switching to:"+vsName);
		System.out.println("	from:"+currentlyEditedVS.getName());
		if (ignore)
			return;

		// If new VS name is the same, ignore.
		if (lastVSName == vsName)
			return;
		System.out.println("  switching to:"+vsName);
		closeEditorWindow();

		System.out.println("VS Switched --> " + vsName + ", Last = " + lastVSName);
		//System.out.println("in map: "+vsToModelMap.get(vmm.getVisualStyle().getName()));
		vmm.setVisualStyleForView(currentView, vsName);
		if (vsToModelMap.containsKey(vsName)){
			//System.out.println("swapping in exsisting TableModel:");
			setModel(vsToModelMap.get(vsName));
		} else {
			//System.out.println("no TableModel yet for this VisualStyle, have to create it:");
			PropertySheetTableModel model = new PropertySheetTableModel();
			setModel(model);
			
			vsToModelMap.put(vsName, model);
			setPropertyTable();
		}
		// MLC 03/31/08:
		//lastVSName = vsName;

		vmm.setVisualStyleForView( currentView, vmm.getVisualStyle(vsName) );	

		if (redraw)
			if (currentView != null) Cytoscape.redrawGraph(currentView);

		/*
		 * Draw default view
		 */
		Image defImg = defaultImageManager.get(vsName);

		if(defImg == null) {
			System.out.println("  Default image is not available in the buffer.  Create a new one.");
			updateDefaultImage(vsName,
									(GraphView) ((DefaultViewPanel) DefaultAppearenceBuilder.getDefaultView(vsName)).getView(),
									defaultAppearencePanel.getSize());
			defImg = defaultImageManager.get(vsName);
		}
		// Set the default view to the panel.
		setDefaultPanel(defImg);

		// Cleanup desktop.
		Cytoscape.getDesktop().repaint();
		vsNameComboBox.setSelectedItem(vsName);
		System.out.println("  vs switching done");
	}

	private static final String CATEGORY_UNUSED = "Unused Properties";
	private static final String GRAPHICAL_MAP_VIEW = "Graphical View";
	private static final String NODE_VISUAL_MAPPING = "Node Visual Mapping";
	private static final String EDGE_VISUAL_MAPPING = "Edge Visual Mapping";

	/*
	 * Set Visual Style selector combo box.
	 */
	public void initVizmapperGUI() {
		List<String> vsNames = new ArrayList<String>(vmm.getCalculatorCatalog().getVisualStyleNames());

		final VisualStyle style = currentlyEditedVS;

		// Disable action listeners
		final ActionListener[] li = vsNameComboBox.getActionListeners();

		for (int i = 0; i < li.length; i++)
			vsNameComboBox.removeActionListener(li[i]);

		vsNameComboBox.removeAllItems();

		JPanel defPanel;

		final Dimension panelSize = defaultAppearencePanel.getSize();
		GraphView view;

		Collections.sort(vsNames);

		for (String name : vsNames) {
			vsNameComboBox.addItem(name);
			// MLC 03/31/08:
			// Deceptively, getDefaultView actually actually calls VisualMappingManager.setVisualStyle()
			// so each time we add a combobox item, the visual style is changing.
			// Make sure to set the lastVSName as we change the visual style:
			view = null;
			try{
			System.out.println("visual style name: "+name);
			defPanel = DefaultAppearenceBuilder.getDefaultView(name);
			view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			} catch(Exception e){
				e.printStackTrace();
			} catch(Error e){
				e.printStackTrace();
			}
			if (view != null) {
				System.out.println("Creating Default Image for " + name);
				updateDefaultImage(name, view, panelSize);
			}
		}

		// Switch back to the original style.
		switchVS(style.getName());
		
		// Restore listeners
		for (int i = 0; i < li.length; i++)
			vsNameComboBox.addActionListener(li[i]);
	}

	/**
	 * Create image of a default dummy network and save in a Map object.
	 *
	 * @param vsName
	 * @param view
	 * @param size
	 */
	private void updateDefaultImage(String vsName, GraphView view, Dimension size) {
		Image image = defaultImageManager.remove(vsName);

		if (image != null) {
			image.flush();
			image = null;
		}

		defaultImageManager.put(vsName,
		                        view.createImage((int) size.getWidth(), (int) size.getHeight(), 0.9));
	}

	private void setPropertySheetAppearence() {
		/*
		 * Set Tooltiptext for the table.
		 */
		
		// Pay special attention to preserving the TableModel: otherwise
		// constructing a new PropertySheetTable would replace the TableModel as
		// well, which we don't want to preserve vsToModelMap mapping
		visualPropertySheetPanel.setTable(new PropertySheetTable((PropertySheetTableModel) visualPropertySheetPanel.getTable().getModel()) {
	private final static long serialVersionUID = 1213748836812161L;
				public String getToolTipText(MouseEvent me) {
					final Point pt = me.getPoint();
					final int row = rowAtPoint(pt);

					if (row < 0)
						return null;
					else {
						final Property prop = ((Item) getValueAt(row, 0)).getProperty();

						final Color fontColor;

						if ((prop != null) && (prop.getValue() != null)
						    && (prop.getValue().getClass() == Color.class))
							fontColor = (Color) prop.getValue();
						else
							fontColor = Color.DARK_GRAY;

						final String colorString = Integer.toHexString(fontColor.getRGB());

						/*
						 * Edit
						 */
						if (prop == null)
							return null;

						if (prop.getDisplayName().equals(GRAPHICAL_MAP_VIEW))
							return "Click to edit this mapping...";

						if ((prop.getDisplayName() == "Controlling Attribute")
						    || (prop.getDisplayName() == "Mapping Type"))
							return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#"
							       + colorString.substring(2, 8) + "\"><strong>"
							       + prop.getDisplayName() + " = " + prop.getValue()
							       + "</font></strong></body></html>";
						else if ((prop.getSubProperties() == null)
						         || (prop.getSubProperties().length == 0))
							return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#"
							       + colorString.substring(2, 8) + "\"><strong>"
							       + prop.getDisplayName() + "</font></strong></body></html>";

						return null;
					}
				}
			});

		visualPropertySheetPanel.getTable().getColumnModel().addColumnModelListener(new TableColumnModelListener() {
				public void columnAdded(TableColumnModelEvent arg0) {
					// TODO Auto-generated method stub
				}

				public void columnMarginChanged(ChangeEvent e) {
					updateTableView();
				}

				public void columnMoved(TableColumnModelEvent e) {
					// TODO Auto-generated method stub
				}

				public void columnRemoved(TableColumnModelEvent e) {
					// TODO Auto-generated method stub
				}

				public void columnSelectionChanged(ListSelectionEvent e) {
					// TODO Auto-generated method stub
				}
			});
		visualPropertySheetPanel.setRendererFactory(rendReg);
		visualPropertySheetPanel.setEditorFactory(editorReg);
		/*
		 * By default, show category.
		 */
		visualPropertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);

		visualPropertySheetPanel.getTable().setComponentPopupMenu(menu);

		visualPropertySheetPanel.getTable().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					processMouseClick(e);
				}
			});

		PropertySheetTable table = visualPropertySheetPanel.getTable();
		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setCategoryBackground(new Color(10, 10, 50, 20));
		table.setCategoryForeground(Color.black);
		table.setSelectionBackground(Color.white);
		table.setSelectionForeground(Color.blue);

		/*
		 * Set editors
		 */
		collorCellRenderer.setForeground(Color.DARK_GRAY);
		collorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
		collorCellRenderer.setEvenBackgroundColor(Color.white);

		emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
		emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
		emptyBoxRenderer.setForeground(Color.red);
		emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

		filledBoxRenderer.setBackground(Color.white);
		filledBoxRenderer.setForeground(Color.blue);

		setAttrComboBox();

		final Set mappingTypes = Cytoscape.getVisualMappingManager().getCalculatorCatalog()
		                                  .getMappingNames();

		mappingTypeEditor.setAvailableValues(mappingTypes.toArray());
	}

	private void updateTableView() {
		final PropertySheetTable table = visualPropertySheetPanel.getTable();
		Property shownProp = null;
		final DefaultTableCellRenderer empRenderer = new DefaultTableCellRenderer();

		// Number of rows shown now.
		int rowCount = table.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			shownProp = ((Item) table.getValueAt(i, 0)).getProperty();

			if ((shownProp != null) && (shownProp.getParentProperty() != null)
			    && shownProp.getParentProperty().getDisplayName()
			                .equals("NODE_LABEL_POSITION")) {
				// This is label position cell. Need laeger cell.
				table.setRowHeight(i, 50);
			} else if ((shownProp != null) && shownProp.getDisplayName().equals(GRAPHICAL_MAP_VIEW)) {
				// This is a Continuous Icon cell.
				final Property parent = shownProp.getParentProperty();
				final Object type = ((VizMapperProperty) parent).getHiddenObject();

				if (type instanceof VisualProperty) {
					ObjectMapping mapping;
					VisualProperty vp = (VisualProperty) type;

					mapping = currentlyEditedVS.getCalculator(vp).getMapping(0);

					if (mapping instanceof ContinuousMapping) {
						table.setRowHeight(i, 80);

						int wi = table.getCellRect(0, 1, true).width;
						final ImageIcon icon = ContinuousMappingEditorPanel.getIcon(wi, 70, vp);
						final Class dataType = vp.getDataType();

						if (dataType == Color.class) {
							final DefaultTableCellRenderer gradientRenderer = new DefaultTableCellRenderer();
							gradientRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, gradientRenderer);
						} else if (dataType == Number.class) {
							final DefaultTableCellRenderer cRenderer = new DefaultTableCellRenderer();
							// continuousRenderer.setIcon(icon);
							cRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, cRenderer);
						} else {
							final DefaultTableCellRenderer dRenderer = new DefaultTableCellRenderer();
							// discreteRenderer.setIcon(icon);
							dRenderer.setIcon(icon);
							rendReg.registerRenderer(shownProp, dRenderer);
						}
					}
				}
			} else if ((shownProp != null) && (shownProp.getCategory() != null)
			           && shownProp.getCategory().equals(CATEGORY_UNUSED)) {
				empRenderer.setForeground(UNUSED_COLOR);
				rendReg.registerRenderer(shownProp, empRenderer);
			}
		}

		repaint();
		visualPropertySheetPanel.repaint();
	}

	private void setAttrComboBox() {
		final List<String> names = new ArrayList<String>();
		CyAttributes attr = Cytoscape.getNodeAttributes();
		String[] nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);
		names.add("ID");

		for (String name : nameArray) {
			if (attr.getUserVisible(name) && (attr.getType(name) != CyAttributes.TYPE_UNDEFINED)
			    && (attr.getType(name) != CyAttributes.TYPE_COMPLEX)) {
				names.add(name);
			}
		}

		nodeAttrEditor.setAvailableValues(names.toArray());

		names.clear();

		Class dataClass;

		for (String name : nameArray) {
			dataClass = CyAttributesUtils.getClass(name, attr);

			if ((dataClass == Integer.class) || (dataClass == Double.class)
			    || (dataClass == Float.class))
				names.add(name);
		}

		nodeNumericalAttrEditor.setAvailableValues(names.toArray());

		names.clear();
		attr = Cytoscape.getEdgeAttributes();
		nameArray = attr.getAttributeNames();
		Arrays.sort(nameArray);
		names.add("ID");

		for (String name : nameArray) {
			if (attr.getUserVisible(name) && (attr.getType(name) != CyAttributes.TYPE_UNDEFINED)
			    && (attr.getType(name) != CyAttributes.TYPE_COMPLEX)) {
				names.add(name);
			}
		}

		edgeAttrEditor.setAvailableValues(names.toArray());

		names.clear();

		for (String name : nameArray) {
			dataClass = CyAttributesUtils.getClass(name, attr);

			if ((dataClass == Integer.class) || (dataClass == Double.class)
			    || (dataClass == Float.class))
				names.add(name);
		}

		edgeNumericalAttrEditor.setAvailableValues(names.toArray());

		repaint();
	}

	private void processMouseClick(MouseEvent e) {
		int selected = visualPropertySheetPanel.getTable().getSelectedRow();
		/*
		 * Adjust height if it's an legend icon.
		 */
		updateTableView();

		if (SwingUtilities.isLeftMouseButton(e) && (0 <= selected)) {
			final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selected, 0);
			final Property curProp = item.getProperty();

			if (curProp == null)
				return;

			/*
			 * Create new mapping if double-click on unused val.
			 */
			String category = curProp.getCategory();

			if ((e.getClickCount() == 2) && (category != null)
			    && category.equalsIgnoreCase("Unused Properties")) {
				((VizMapperProperty) curProp).setEditable(true);

				VisualProperty vp = (VisualProperty) ((VizMapperProperty) curProp)
				                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             .getHiddenObject();
				visualPropertySheetPanel.removeProperty(curProp);

				final VizMapperProperty newProp = new VizMapperProperty();
				final VizMapperProperty mapProp = new VizMapperProperty();

				newProp.setDisplayName(vp.getName());
				newProp.setHiddenObject(vp);
				newProp.setValue("Please select a value!");

				if (vp.isNodeProp()) {
					newProp.setCategory(NODE_VISUAL_MAPPING);
					editorReg.registerEditor(newProp, nodeAttrEditor);
				} else {
					newProp.setCategory(EDGE_VISUAL_MAPPING);
					editorReg.registerEditor(newProp, edgeAttrEditor);
				}

				mapProp.setDisplayName("Mapping Type");
				mapProp.setValue("Please select a mapping type!");
				editorReg.registerEditor(mapProp, mappingTypeEditor);

				newProp.addSubProperty(mapProp);
				mapProp.setParentProperty(newProp);
				visualPropertySheetPanel.addProperty(0, newProp);

				expandLastSelectedItem(vp.getName());

				visualPropertySheetPanel.getTable().scrollRectToVisible(new Rectangle(0, 0, 10, 10));
				visualPropertySheetPanel.repaint();

				return;
			} else if ((e.getClickCount() == 1) && (category == null)) {
				/*
				 * Single left-click
				 */
				VisualProperty type = null;

				if ((curProp.getParentProperty() == null)
				    && ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualProperty)
					type = (VisualProperty) ((VizMapperProperty) curProp).getHiddenObject();
				else if (curProp.getParentProperty() != null)
					type = (VisualProperty) ((VizMapperProperty) curProp.getParentProperty()) .getHiddenObject();
				else

					return;

				final ObjectMapping selectedMapping;
				Calculator calc = null;

				calc = currentlyEditedVS.getCalculator(type);

				if (calc == null) {
					return;
				}

				selectedMapping = calc.getMapping(0);

				if (selectedMapping instanceof ContinuousMapping) {
					/*
					 * Need to check other windows.
					 */
					if (editorWindowManager.containsKey(type)) {
						// This means editor is already on display.
						editorWindowManager.get(type).requestFocus();

						return;
					} else {
						try {
							((JDialog) EditorFactory.showContinuousEditor(type)).addPropertyChangeListener(this);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	/*
	 * Set property sheet panel.
	 *
	 * TODO: need to find missing editor problem!
	 */
	private void setPropertyTable() {
		//System.out.println("setPropertyTable on model:"+visualPropertySheetPanel.getTable().getModel());
		setPropertySheetAppearence();

		/*
		 * Clean up sheet
		 */
		for (Property item : visualPropertySheetPanel.getProperties())
			visualPropertySheetPanel.removeProperty(item);
		//System.out.println("cleared PropertyTable, num properties:"+(visualPropertySheetPanel.getProperties()).length);
		
		final List<Calculator> ncList = currentlyEditedVS.getNodeCalculators();
		final List<Calculator> ecList = currentlyEditedVS.getEdgeCalculators();

		editorReg.registerDefaults();

		/* Add properties to the property sheet. */
		setPropertyFromCalculator(ncList, NODE_VISUAL_MAPPING, true);
		setPropertyFromCalculator(ecList, EDGE_VISUAL_MAPPING, false);

		/* Finally, add unused visual properties (as VizMapperProperties) to propList */
		for (VisualProperty vp : byNameSortedVisualProperties(VisualPropertyCatalog.collectionOfVisualProperties(currentView))) { //FIXME: assumes currentView
			if (currentlyEditedVS.getCalculator(vp) == null) {
				VizMapperProperty prop = new VizMapperProperty();
				prop.setCategory(CATEGORY_UNUSED);
				prop.setDisplayName(vp.getName());
				prop.setHiddenObject(vp);
				prop.setValue("Double-Click to create...");
				visualPropertySheetPanel.addProperty(prop);
			}
		}
		//System.out.println("setPropertyTable done, num properties:"+(visualPropertySheetPanel.getProperties()).length);
	}

	/* Returns a sorted list of the visual properties
	 * Needed because VisualProperties themselves are not Comparable,
	 * (forcing them to be comparable would bloat the API and) 
	 */
	private List<VisualProperty> byNameSortedVisualProperties(Collection <VisualProperty>input){ // FIXME: I bet this could be done more java-style (i.e. better)
		List <String> vpNames = new ArrayList<String>();
		for (VisualProperty vp:input){
			vpNames.add(vp.getName());
		}
		Collections.sort(vpNames);
		List <VisualProperty> result = new ArrayList<VisualProperty>(input.size());
		for (String name: vpNames){
			result.add(VisualPropertyCatalog.getVisualProperty(name));
		}
		return result;
	}
	/*
	 * Set value, title, and renderer for each property in the category.
	 */
	private final void setDiscreteProps(VisualProperty type, Map discMapping,
	                                    Set<Object> attrKeys, PropertyEditor editor,
	                                    TableCellRenderer rend, DefaultProperty parent) {
		if (attrKeys == null)
			return;

		Object val = null;
		VizMapperProperty valProp;
		String strVal;

		final List<VizMapperProperty> children = new ArrayList<VizMapperProperty>();

		for (Object key : attrKeys) {
			valProp = new VizMapperProperty();
			strVal = key.toString();
			valProp.setDisplayName(strVal);
			valProp.setName(strVal + "-" + type.toString());
			valProp.setParentProperty(parent);

			try {
				val = discMapping.get(key);
			} catch (Exception e) {
				System.out.println("------- Map = " + discMapping.getClass() + ", class = "
				                   + key.getClass() + ", err = " + e.getMessage());
				System.out.println("------- Key = " + key + ", val = " + val + ", disp = " + strVal);
			}

			if (val != null)
				valProp.setType(val.getClass());

			children.add(valProp);
			rendReg.registerRenderer(valProp, rend);
			editorReg.registerEditor(valProp, editor);

			valProp.setValue(val);
		}

		// Add all children.
		parent.addSubProperties(children);
	}

	/*
	 * Build one property for one visual property.
	 */
	private final void buildProperty(Calculator calc, VizMapperProperty calculatorTypeProp,
	                                 String rootCategory) {
		final VisualProperty type = calc.getVisualProperty();
		/*
		 * Set one calculator
		 */
		calculatorTypeProp.setCategory(rootCategory);
		// calculatorTypeProp.setType(String.class);
		calculatorTypeProp.setDisplayName(type.getName());
		calculatorTypeProp.setHiddenObject(type);
		System.out.println("Build one property for one visual property.");
		/*
		 * Mapping 0 is always currently used mapping.
		 */
		final ObjectMapping firstMap = calc.getMapping(0);
		String attrName;

		if (firstMap != null) {
			final VizMapperProperty mappingHeader = new VizMapperProperty();

			attrName = firstMap.getControllingAttributeName();

			if (attrName == null) {
				calculatorTypeProp.setValue("Select Value");
				rendReg.registerRenderer(calculatorTypeProp, emptyBoxRenderer);
			} else {
				calculatorTypeProp.setValue(attrName);
				rendReg.registerRenderer(calculatorTypeProp, filledBoxRenderer);
			}

			mappingHeader.setDisplayName("Mapping Type");
			mappingHeader.setHiddenObject(firstMap.getClass());

			if (firstMap.getClass() == DiscreteMapping.class)
				mappingHeader.setValue("Discrete Mapping");
			else if (firstMap.getClass() == ContinuousMapping.class)
				mappingHeader.setValue("Continuous Mapping");
			else
				mappingHeader.setValue("Passthrough Mapping");

			mappingHeader.setHiddenObject(firstMap);

			mappingHeader.setParentProperty(calculatorTypeProp);
			calculatorTypeProp.addSubProperty(mappingHeader);
			editorReg.registerEditor(mappingHeader, mappingTypeEditor);

			final CyAttributes attr;
			final Iterator it;
			final int nodeOrEdge;

			if (calc.getVisualProperty().isNodeProp()) {
				attr = Cytoscape.getNodeAttributes();
				it = currentView.getNetwork().nodesIterator();
				editorReg.registerEditor(calculatorTypeProp, nodeAttrEditor);
				nodeOrEdge = ObjectMapping.NODE_MAPPING;
			} else {
				attr = Cytoscape.getEdgeAttributes();
				it = currentView.getNetwork().edgesIterator();
				editorReg.registerEditor(calculatorTypeProp, edgeAttrEditor);
				nodeOrEdge = ObjectMapping.EDGE_MAPPING;
			}

			/*
			 * Discrete Mapping
			 */
			if ((firstMap.getClass() == DiscreteMapping.class) && (attrName != null)) {
				final Map discMapping = ((DiscreteMapping) firstMap).getAll();
				final Set<Object> attrSet = loadKeys(attrName, attr, firstMap, nodeOrEdge);
				Class<?> dataTypeClass = type.getDataType();

				if (dataTypeClass.isAssignableFrom(DiscreteValue.class)){ // FIXME: these should be Enum-like instead!! (or Discrete or something)
					setDiscreteProps(type, discMapping, attrSet, buildCellEditor(type), new ShapeCellRenderer(type), calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(LabelPosition.class)){
					setDiscreteProps(type, discMapping, attrSet, labelPositionEditor, labelPositionRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Number.class)){
					setDiscreteProps(type, discMapping, attrSet, numberCellEditor, defCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Font.class)){
					setDiscreteProps(type, discMapping, attrSet, fontCellEditor, fontCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(String.class)){
					setDiscreteProps(type, discMapping, attrSet, stringCellEditor, defCellRenderer, calculatorTypeProp);
				} else if (dataTypeClass.isAssignableFrom(Color.class)){
					setDiscreteProps(type, discMapping, attrSet, colorCellEditor, collorCellRenderer, calculatorTypeProp);
				} else {
					System.out.println("unknown datatype:"+dataTypeClass);
				}

			} else if ((firstMap.getClass() == ContinuousMapping.class) && (attrName != null)) {
				int wi = this.visualPropertySheetPanel.getTable().getCellRect(0, 1, true).width;

				VizMapperProperty graphicalView = new VizMapperProperty();
				graphicalView.setDisplayName(GRAPHICAL_MAP_VIEW);
				graphicalView.setName(type.getName());
				graphicalView.setParentProperty(calculatorTypeProp);
				calculatorTypeProp.addSubProperty(graphicalView);

				final Class dataType = type.getDataType();
				final ImageIcon icon = ContinuousMappingEditorPanel.getIcon(wi, 70,
				                                                            (VisualProperty) type);

				if (dataType == Color.class) {
					/*
					 * Color-related calcs.
					 */
					final DefaultTableCellRenderer gradientRenderer = new DefaultTableCellRenderer();
					gradientRenderer.setIcon(icon);

					rendReg.registerRenderer(graphicalView, gradientRenderer);
				} else if (dataType == Number.class) {
					/*
					 * Size/Width related calcs.
					 */
					continuousRenderer.setIcon(icon);
					rendReg.registerRenderer(graphicalView, continuousRenderer);
				} else {
					discreteRenderer.setIcon(icon);
					rendReg.registerRenderer(graphicalView, discreteRenderer);
				}
			} else if ((firstMap.getClass() == PassThroughMapping.class) && (attrName != null)) {
				/*
				 * Passthrough
				 */
				String id;
				String value;
				VizMapperProperty oneProperty;

				/*
				 * Accept String only.
				 */
				if (attr.getType(attrName) == CyAttributes.TYPE_STRING) {
					while (it.hasNext()) {
						id = ((GraphObject) it.next()).getIdentifier();

						value = attr.getStringAttribute(id, attrName);
						oneProperty = new VizMapperProperty();

						if (attrName.equals("ID"))
							oneProperty.setValue(id);
						else
							oneProperty.setValue(value);

						// This prop. should not be editable!
						oneProperty.setEditable(false);

						oneProperty.setParentProperty(calculatorTypeProp);
						oneProperty.setDisplayName(id);
						oneProperty.setType(String.class);

						calculatorTypeProp.addSubProperty(oneProperty);
					}
				}
			}
		}

		visualPropertySheetPanel.addProperty(0, calculatorTypeProp);
		visualPropertySheetPanel.setRendererFactory(rendReg);
		visualPropertySheetPanel.setEditorFactory(editorReg);
	}
	private CyComboBoxPropertyEditor buildCellEditor(VisualProperty vp){
		final List<Icon> iconList = new ArrayList<Icon>();
		final List<Object> values = new ArrayList<Object>();
		final Map<Object, VisualPropertyIcon> iconSet = vp.getIconSet();
		System.out.println("building discrete editor for:"+vp.getName());
		for (Object val: iconSet.keySet()){
			iconList.add(iconSet.get(val));
			values.add(val);
		}
		CyComboBoxPropertyEditor editor = new CyComboBoxPropertyEditor();
		editor.setAvailableValues(values.toArray());
		editor.setAvailableIcons(iconList.toArray(new Icon[0]));
		return editor;
	}
	private void setPropertyFromCalculator(List<Calculator> calcList, String rootCategory, boolean atBeginning) {
		VisualProperty type = null;

		for (Calculator calc : calcList) {
			final VizMapperProperty calculatorTypeProp = new VizMapperProperty();
			buildProperty(calc, calculatorTypeProp, rootCategory);

			PropertyEditor editor = editorReg.getEditor(calculatorTypeProp);

			if ((editor == null)
			    && (calculatorTypeProp.getCategory().equals("Unused Properties") == false)) {
				type = (VisualProperty) calculatorTypeProp.getHiddenObject();

				if (type.isNodeProp()) {
					editorReg.registerEditor(calculatorTypeProp, nodeAttrEditor);
				} else {
					editorReg.registerEditor(calculatorTypeProp, edgeAttrEditor);
				}
			}
			//System.out.println("adding used VP: "+calc.getVisualProperty().getName());
			if (atBeginning){
				visualPropertySheetPanel.addProperty(0, calculatorTypeProp);
			} else {
				visualPropertySheetPanel.addProperty(calculatorTypeProp);
			}
		}
	}

	private Set<Object> loadKeys(final String attrName, final CyAttributes attrs,
	                             final ObjectMapping mapping, final int nOre) {
		if (attrName.equals("ID")) {
			return loadID(nOre);
		}

		Map mapAttrs;
		mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);

		if ((mapAttrs == null) || (mapAttrs.size() == 0))
			return new TreeSet<Object>();

		List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
		Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);

		if ((mapAttrClass == null) || !(acceptedClasses.contains(mapAttrClass)))
			return new TreeSet<Object>(); // Return empty set.

		return loadKeySet(mapAttrs);
	}

	/**
	 * Loads the Key Set.
	 */
	private Set<Object> loadKeySet(final Map mapAttrs) {
		final Set<Object> mappedKeys = new TreeSet<Object>();

		final Iterator keyIter = mapAttrs.values().iterator();

		Object o = null;

		while (keyIter.hasNext()) {
			o = keyIter.next();

			if (o instanceof List) {
				List list = (List) o;

				for (int i = 0; i < list.size(); i++) {
					Object vo = list.get(i);

					if (!mappedKeys.contains(vo))
						mappedKeys.add(vo);
				}
			} else {
				if (!mappedKeys.contains(o))
					mappedKeys.add(o);
			}
		}

		return mappedKeys;
	}

	private void setDefaultPanel(final Image defImage) {
		if (defImage == null)
			return;

		defaultAppearencePanel.removeAll();

		final JButton defaultImageButton = new JButton();
		defaultImageButton.setUI(new BlueishButtonUI());
		defaultImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		defaultImageButton.setIcon(new ImageIcon(defImage));
		defaultAppearencePanel.add(defaultImageButton, BorderLayout.CENTER);
		defaultImageButton.addMouseListener(new DefaultMouseListener());
	}

	class DefaultMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
				final String targetName = currentlyEditedVS.getName();
				final String focus = currentView.getIdentifier();

				final DefaultViewPanel panel = (DefaultViewPanel) DefaultAppearenceBuilder.showDialog(Cytoscape.getDesktop());
				updateDefaultImage(targetName, (GraphView) panel.getView(), defaultAppearencePanel.getSize());
				setDefaultPanel(defaultImageManager.get(targetName));

				vmm.setVisualStyleForView(currentView, targetName);
				Cytoscape.getDesktop().setFocus(focus);
				Cytoscape.getDesktop().repaint();
			}
		}
	}

	/**
	 * On/Off listeners.
	 * This is for performance.
	 *
	 * @param on
	 *            DOCUMENT ME!
	 */
	public void enableListeners(boolean on) {
		if (on) {
			Cytoscape.getVisualMappingManager().addChangeListener(this);
			syncStyleBox();
			ignore = false;
		} else {
			Cytoscape.getVisualMappingManager().removeChangeListener(this);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void initializeTableState() {
		//System.out.println("clearing in initializeTableState");
		//vsToModelMap = new HashMap<String, TableModel>(); // propertyMap was re-written here in previous versions, so this might be needed, but maybe not.
		editorWindowManager = new HashMap<VisualProperty, JDialog>();
		defaultImageManager = new HashMap<String, Image>();
	}

	private void manageWindow(final String status, VisualProperty vpt, Object source) {
		if (status.equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_OPENED)) {
			this.editorWindowManager.put(vpt, (JDialog) source);
		} else if (status.equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED)) {
			final VisualProperty type = vpt;

			/*
			 * Update icon
			 */
			final Property[] props = visualPropertySheetPanel.getProperties();
			VizMapperProperty vprop = null;

			for (Property prop : props) {
				vprop = (VizMapperProperty) prop;

				if ((vprop.getHiddenObject() != null) && (type == vprop.getHiddenObject())) {
					vprop = (VizMapperProperty) prop;

					break;
				}
			}

			final Property[] subProps = vprop.getSubProperties();
			vprop = null;

			String name = null;

			for (Property prop : subProps) {
				name = prop.getName();

				if ((name != null) && name.equals(type.getName())) {
					vprop = (VizMapperProperty) prop;

					break;
				}
			}

			final int width = visualPropertySheetPanel.getTable().getCellRect(0, 1, true).width;

			final DefaultTableCellRenderer cRenderer = new DefaultTableCellRenderer();
			cRenderer.setIcon(ContinuousMappingEditorPanel.getIcon(width, 70, type));

			rendReg.registerRenderer(vprop, cRenderer);
			visualPropertySheetPanel.getTable().repaint();
		}
	}

	private void closeEditorWindow() {
		Set<VisualProperty> typeSet = editorWindowManager.keySet();
		Set<VisualProperty> keySet = new HashSet<VisualProperty>();

		for (VisualProperty vpt : typeSet) {
			JDialog window = editorWindowManager.get(vpt);
			manageWindow(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED, vpt, null);
			window.dispose();
			keySet.add(vpt);
		}

		for (VisualProperty type : keySet)
			editorWindowManager.remove(type);
	}

	/**
	 * Handle propeaty change events.
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		//System.out.println("==================GLOBAL Signal: " + e.getPropertyName() + ", SRC = " + e.getSource().toString());
		// Set ignore flag.
		if (e.getPropertyName().equals(Integer.toString(Cytoscape.SESSION_OPENED))) {
			ignore = true;
			enableListeners(false);
		}

		if (ignore)
			return;

		/*
		 * Managing editor windows.
		 */
		if (e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_OPENED)
		    || e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED)) {
			manageWindow(e.getPropertyName(), (VisualProperty) e.getNewValue(), e.getSource());

			if (e.getPropertyName().equals(ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED))
				editorWindowManager.remove((VisualProperty) e.getNewValue());

			return;
		}

		/*
		 * Got global event
		 */
		VisualStyle vs = currentlyEditedVS;
		//System.out.println("==================GLOBAL Signal: " + e.getPropertyName() + ", SRC = " + e.getSource().toString());
		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			String vmName = vs.getName();
			setDefaultPanel(defaultImageManager.get(vmName));
			vsNameComboBox.setSelectedItem(vmName);
			setPropertyTable();
			visualPropertySheetPanel.setSorting(true);
			return;
		} else if (e.getPropertyName().equals(Cytoscape.SESSION_LOADED)
		           || e.getPropertyName().equals(Cytoscape.VIZMAP_LOADED)) {
			final String vsName = vs.getName();
			
			lastVSName = null;
			initVizmapperGUI();
			switchVS(vsName);
			vsNameComboBox.setSelectedItem(vsName);

			return;
		} else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUS)
		           && (e.getSource().getClass() == NetworkPanel.class)) {
			currentView = Cytoscape.getCurrentNetworkView();
			currentlyEditedVS = vmm.getVisualStyleForView(currentView);
			if (vs != null) {

				if (vs.getName().equals(vsNameComboBox.getSelectedItem())) {
					Cytoscape.redrawGraph(currentView);
				} else {
					switchVS(vs.getName(), false);
					vsNameComboBox.setSelectedItem(vs.getName());
					setDefaultPanel(this.defaultImageManager.get(vs.getName()));
				}
			}

			return;
		} else if (e.getPropertyName().equals(Cytoscape.VISUALSTYLE_MODIFIED)){
			System.out.println("got VISUALSTYLE_MODIFIED!");
			setPropertyTable();
		} else if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED)
		           || e.getPropertyName().equals(Cytoscape.NETWORK_LOADED)) {
			setAttrComboBox();
		}

		/***********************************************************************
		 * Below this line, accept only cell editor events.
		 **********************************************************************/
		if (e.getPropertyName().equalsIgnoreCase("value") == false)
			return;
		
		if (e.getNewValue().equals(e.getOldValue()))
			return;

		final PropertySheetTable table = visualPropertySheetPanel.getTable();
		final int selected = table.getSelectedRow();

		/*
		 * Do nothing if not selected.
		 */
		if (selected < 0)
			return;

		Item selectedItem = (Item) visualPropertySheetPanel.getTable().getValueAt(selected, 0);
		VizMapperProperty prop = (VizMapperProperty) selectedItem.getProperty();

		VisualProperty type = null;
		String ctrAttrName = null;

		VizMapperProperty typeRootProp = null;

		if ((prop.getParentProperty() == null) && e.getNewValue() instanceof String) {
			/*
			 * This is a controlling attr name change signal.
			 */
			//System.out.println("This is a controlling attr name change signal.");
			typeRootProp = (VizMapperProperty) prop;
			type = (VisualProperty) ((VizMapperProperty) prop).getHiddenObject();
			ctrAttrName = (String) e.getNewValue();
		} else if ((prop.getParentProperty() == null) && (e.getNewValue() == null)) {
			/*
			 * Empty cell selected. no need to change anything.
			 */
			//System.out.println("Empty cell selected. no need to change anything.");
			return;
		} else {
			//System.out.println("something else");
			typeRootProp = (VizMapperProperty) prop.getParentProperty();

			if (prop.getParentProperty() == null)
				return;

			type = (VisualProperty) ((VizMapperProperty) prop.getParentProperty())
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             .getHiddenObject();
		}

		/*
		 * Mapping type changed
		 */
		//System.out.println("Mapping type changed");
		if (prop.getHiddenObject() instanceof ObjectMapping
		    || prop.getDisplayName().equals("Mapping Type")) {
			//System.out.println("Mapping type changed: " + prop.getHiddenObject());

			if (e.getNewValue() == null)
				return;

			/*
			 * If invalid data type, ignore.
			 */
			final Object parentValue = prop.getParentProperty().getValue();

			if (parentValue != null) {
				ctrAttrName = parentValue.toString();

				final Class dataClass;

				if (type.isNodeProp()) {
					dataClass = CyAttributesUtils.getClass(ctrAttrName,
					                                       Cytoscape.getNodeAttributes());
				} else {
					dataClass = CyAttributesUtils.getClass(ctrAttrName,
					                                       Cytoscape.getEdgeAttributes());
				}

				if (e.getNewValue().equals("Continuous Mapper")
				    && ((dataClass != Integer.class) && (dataClass != Double.class)
				       && (dataClass != Float.class))) {
					JOptionPane.showMessageDialog(this,
					                              "Continuous Mapper can be used with Numbers only.",
					                              "Incompatible Mapping Type!",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			} else {
				return;
			}

			if (e.getNewValue().toString().endsWith("Mapper") == false)
				return;

			switchMapping(prop, e.getNewValue().toString(), prop.getParentProperty().getValue());
			/*
			 * restore expanded props.
			 */
			expandLastSelectedItem(type.getName());
			updateTableView();

			return;
		}

		/*
		 * Extract calculator
		 */
		ObjectMapping mapping;
		final Calculator curCalc;

		curCalc = vs.getCalculator(type);

		if (curCalc == null) {
			return;
		}

		mapping = curCalc.getMapping(0);

		/*
		 * Controlling Attribute has been changed.
		 */
		if (ctrAttrName != null) {
			/*
			 * Ignore if not compatible.
			 */
			final CyAttributes attrForTest;

			if (type.isNodeProp()) {
				attrForTest = Cytoscape.getNodeAttributes();
			} else {
				attrForTest = Cytoscape.getEdgeAttributes();
			}

			final Byte dataType = attrForTest.getType(ctrAttrName);

			// This part is for Continuous Mapping.
			if (mapping instanceof ContinuousMapping) {
				if ((dataType == CyAttributes.TYPE_FLOATING)
				    || (dataType == CyAttributes.TYPE_INTEGER)) {
					// Do nothing
				} else {
					JOptionPane.showMessageDialog(this,
					                              "Continuous Mapper can be used with Numbers only.\nPlease select numerical attributes.",
					                              "Incompatible Mapping Type!",
					                              JOptionPane.INFORMATION_MESSAGE);

					return;
				}
			}

			// If same, do nothing.
			if (ctrAttrName.equals(mapping.getControllingAttributeName()))
				return;

			// Buffer current discrete mapping
			if (mapping instanceof DiscreteMapping) {
				final String curMappingName = curCalc.toString() + "-"
				                              + mapping.getControllingAttributeName();
				final String newMappingName = curCalc.toString() + "-" + ctrAttrName;
				final Map saved = discMapBuffer.get(newMappingName);

				if (saved == null) {
					discMapBuffer.put(curMappingName, ((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
				} else if (saved != null) {
					// Mapping exists
					discMapBuffer.put(curMappingName, ((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
					((DiscreteMapping) mapping).putAll(saved);
				}
			} else {
				mapping.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);
			}

			visualPropertySheetPanel.removeProperty(typeRootProp);

			final VizMapperProperty newRootProp = new VizMapperProperty();
			
			Calculator c = vs.getCalculator(type);
			if (type.isNodeProp())
				buildProperty(c, newRootProp, NODE_VISUAL_MAPPING);
			else
				buildProperty(c, newRootProp, EDGE_VISUAL_MAPPING);

			expandLastSelectedItem(type.getName());
			updateTableView();

			// Finally, update graph view and focus.
			if (currentView != null) Cytoscape.redrawGraph(currentView);

			return;
		}

		// Return if not a Discrete Mapping.
		if (mapping instanceof ContinuousMapping || mapping instanceof PassThroughMapping)
			return;

		Object key = null;

		if ((type.getDataType() == Number.class) || (type.getDataType() == String.class)) {
			key = e.getOldValue();

			if (type.getDataType() == Number.class) {
				numberCellEditor = new CyDoublePropertyEditor();
				numberCellEditor.addPropertyChangeListener(this);
				editorReg.registerEditor(prop, numberCellEditor);
			}
		} else {
			key = ((Item) visualPropertySheetPanel.getTable().getValueAt(selected, 0)).getProperty()
			       .getDisplayName();
		}

		/*
		 * Need to convert this string to proper data types.
		 */
		final CyAttributes attr;
		ctrAttrName = mapping.getControllingAttributeName();

		if (type.isNodeProp()) {
			attr = Cytoscape.getNodeAttributes();
		} else {
			attr = Cytoscape.getEdgeAttributes();
		}

		Byte attrType = attr.getType(ctrAttrName);

		if (attrType != CyAttributes.TYPE_STRING) {
			switch (attrType) {
				case CyAttributes.TYPE_BOOLEAN:
					key = Boolean.valueOf((String) key);

					break;

				case CyAttributes.TYPE_INTEGER:
					key = Integer.valueOf((String) key);

					break;

				case CyAttributes.TYPE_FLOATING:
					key = Double.valueOf((String) key);

					break;

				default:
					break;
			}
		}

		Object newValue = e.getNewValue();

		if (type.getDataType() == Number.class) {
			if ((((Number) newValue).doubleValue() == 0)
			    || (newValue instanceof Number && type.toString().endsWith("OPACITY")
			       && (((Number) newValue).doubleValue() > 255))) {
				int shownPropCount = table.getRowCount();
				Property p = null;
				Object val = null;

				for (int i = 0; i < shownPropCount; i++) {
					p = ((Item) table.getValueAt(i, 0)).getProperty();

					if (p != null) {
						val = p.getDisplayName();

						if ((val != null) && val.equals(key.toString())) {
							p.setValue(((DiscreteMapping) mapping).getMapValue(key));

							return;
						}
					}
				}

				return;
			}
		}

		((DiscreteMapping) mapping).putMapValue(key, newValue);

		/*
		 * Update table and current network view.
		 */
		updateTableView();

		visualPropertySheetPanel.repaint();
		if (currentView != null) Cytoscape.redrawGraph(currentView);
	}

	/**
	 * Switching between mapppings. Each calcs has 3 mappings. The first one
	 * (getMapping(0)) is the current mapping used by calculator.
	 *
	 */
	private void switchMapping(VizMapperProperty prop, String newMapName, Object attrName) {
		if (attrName == null) {
			return;
		}
		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop .getParentProperty())
		                                .getHiddenObject();
		final String newCalcName = currentlyEditedVS.getName() + "-" + type.getName() + "-"
		                           + newMapName;
		// Extract target calculator
		Calculator newCalc = vmm.getCalculatorCatalog().getCalculator(type, newCalcName);
		Calculator oldCalc = null;

		oldCalc = currentlyEditedVS.getCalculator(type);

		/*
		 * If not exist, create new one.
		 */
		if (newCalc == null) {
			newCalc = getNewCalculator(type, newMapName, newCalcName);
			newCalc.getMapping(0).setControllingAttributeName((String) attrName, null, true);
			vmm.getCalculatorCatalog().addCalculator(newCalc);
		}
		newCalc.getMapping(0).setControllingAttributeName((String) attrName, null, true);

		currentlyEditedVS.setCalculator(newCalc);
		/*
		 * If old calc is not standard name, rename it.
		 */
		if (oldCalc != null) {
			final String oldMappingTypeName;

			if (oldCalc.getMapping(0) instanceof DiscreteMapping)
				oldMappingTypeName = "Discrete Mapper";
			else if (oldCalc.getMapping(0) instanceof ContinuousMapping)
				oldMappingTypeName = "Continuous Mapper";
			else if (oldCalc.getMapping(0) instanceof PassThroughMapping)
				oldMappingTypeName = "Passthrough Mapper";
			else
				oldMappingTypeName = null;
			final String oldCalcName = type.getName() + "-" + oldMappingTypeName;
			if (vmm.getCalculatorCatalog().getCalculator(type, oldCalcName) == null) {
				final Calculator newC = getNewCalculator(type, oldMappingTypeName, oldCalcName);
				newC.getMapping(0).setControllingAttributeName((String) attrName, null, false);
				vmm.getCalculatorCatalog().addCalculator(newC);
			}
		}

		Property parent = prop.getParentProperty();
		visualPropertySheetPanel.removeProperty(parent);

		final VizMapperProperty newRootProp = new VizMapperProperty();

		Calculator c = currentlyEditedVS.getCalculator(type);
		if (type.isNodeProp())
			buildProperty(c, newRootProp, NODE_VISUAL_MAPPING);
		else
			buildProperty(c, newRootProp, EDGE_VISUAL_MAPPING);

		expandLastSelectedItem(type.getName());

		// vmm.getNetworkView().redrawGraph(false, true);
		Cytoscape.firePropertyChange(Cytoscape.VISUALSTYLE_MODIFIED, currentlyEditedVS, null);
		if (currentView != null) Cytoscape.redrawGraph(currentView);
		parent = null;
	}

	private void expandLastSelectedItem(String name) {
		final PropertySheetTable table = visualPropertySheetPanel.getTable();
		Item item = null;
		Property curProp;

		for (int i = 0; i < table.getRowCount(); i++) {
			item = (Item) table.getValueAt(i, 0);

			curProp = item.getProperty();

			if ((curProp != null) && (curProp.getDisplayName().equals(name))) {
				table.setRowSelectionInterval(i, i);

				if (item.isVisible() == false) {
					item.toggle();
				}

				return;
			}
		}
	}

	private Calculator getNewCalculator(final VisualProperty type, final String newMappingName,
	                                    final String newCalcName) {

		final CalculatorCatalog catalog = vmm.getCalculatorCatalog();

		Class mapperClass = catalog.getMapping(newMappingName);

		if (mapperClass == null) {
			return null;
		}

		// create the selected mapper
		Class[] conTypes = { Object.class, byte.class };
		Constructor mapperCon;

		try {
			mapperCon = mapperClass.getConstructor(conTypes);
		} catch (NoSuchMethodException exc) {
			// Should not happen...
			System.err.println("Invalid mapper " + mapperClass.getName());

			return null;
		}

		// create the mapper
		final byte mapType; // node or edge calculator

		if (type.isNodeProp())
			mapType = ObjectMapping.NODE_MAPPING;
		else
			mapType = ObjectMapping.EDGE_MAPPING;

		final Object defaultObj = currentlyEditedVS.getDefaultValue(type);

		System.out.println("defobj = " + defaultObj.getClass() + ", Type = " + type.getName());

		final Object[] invokeArgs = { defaultObj, new Byte(mapType) };
		ObjectMapping mapper = null;

		try {
			mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
		} catch (Exception exc) {
			System.err.println("Error creating mapping");
			exc.printStackTrace();
			return null;
		}

		return new BasicCalculator(newCalcName, mapper, type);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param vsName
	 *            DOCUMENT ME!
	 */
	public void setCurrentVS(String vsName) {
		vsNameComboBox.setSelectedItem(vsName);
	}

	/*
	 * Actions for option menu
	 */
	protected class CreateLegendListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836842554L;
		public void actionPerformed(ActionEvent e) {
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					LegendDialog ld = new LegendDialog(Cytoscape.getDesktop(), currentlyEditedVS);
					ld.setLocationRelativeTo(Cytoscape.getDesktop());
					ld.setVisible(true);

					return null;
				}
			};

			worker.start();
		}
	}

	/**
	 * Create a new Visual Style.
	 *
	 * @author kono
	 *
	 */
	private class NewStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836872046L;
		public void actionPerformed(ActionEvent e) {
			final String name = getStyleName(null);

			/*
			 * If name is null, do not create style.
			 */
			if (name == null)
				return;

			// Create the new style
			final VisualStyle newStyle = new VisualStyle(name);
			final List<Calculator> calcs = new ArrayList<Calculator>(vmm.getCalculatorCatalog()
			                                                            .getCalculators());
			// add it to the catalog
			vmm.getCalculatorCatalog().addVisualStyle(newStyle);
			// Apply the new style
			vmm.setVisualStyleForView(currentView, newStyle);

			final JPanel defPanel = DefaultAppearenceBuilder.getDefaultView(name);
			final GraphView view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			final Dimension panelSize = defaultAppearencePanel.getSize();

			if (view != null) {
				System.out.println("Creating Default Image for new visual style " + name);
				updateDefaultImage(name, view, panelSize);
				setDefaultPanel(defaultImageManager.get(name));
			}

			vsNameComboBox.addItem(name);
			switchVS(name);
		}
	}

	/**
	 * Get a new Visual Style name
	 *
	 * @param s
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private String getStyleName(VisualStyle s) {
		String suggestedName = null;

		if (s != null)
			suggestedName = vmm.getCalculatorCatalog().checkVisualStyleName(s.getName());

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(Cytoscape.getDesktop(),
			                                                  "Please enter new name for the visual style.",
			                                                  "Enter Visual Style Name",
			                                                  JOptionPane.QUESTION_MESSAGE, null,
			                                                  null, suggestedName);

			if (ret == null)
				return null;

			String newName = vmm.getCalculatorCatalog().checkVisualStyleName(ret);

			if (newName.equals(ret))
				return ret;

			int alt = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
			                                        "Visual style with name " + ret
			                                        + " already exists,\nrename to " + newName
			                                        + " okay?", "Duplicate visual style name",
			                                        JOptionPane.YES_NO_OPTION,
			                                        JOptionPane.WARNING_MESSAGE, null);

			if (alt == JOptionPane.YES_OPTION)
				return newName;
		}
	}

	/**
	 * Rename a Visual Style<br>
	 *
	 */
	private class RenameStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836901018L;
		public void actionPerformed(ActionEvent e) {
			final VisualStyle currentStyle = currentlyEditedVS;
			final String oldName = currentStyle.getName();
			final String name = getStyleName(currentStyle);

			if (name == null) {
				return;
			}

			lastVSName = name;

			final Image img = defaultImageManager.get(oldName);
			defaultImageManager.put(name, img);
			defaultImageManager.remove(oldName);

			/*
			 * Update name
			 */
			currentStyle.setName(name);

			vmm.getCalculatorCatalog().removeVisualStyle(oldName);
			vmm.getCalculatorCatalog().addVisualStyle(currentStyle);

			vmm.setVisualStyleForView( currentView, currentStyle );

			/*
			 * Update combo box and
			 */
			vsNameComboBox.addItem(name);
			vsNameComboBox.setSelectedItem(name);
			vsNameComboBox.removeItem(oldName);

			final TableModel model = vsToModelMap.get(oldName);
			vsToModelMap.put(name, model);
			vsToModelMap.remove(oldName);
		}
	}

	/**
	 * Remove selected visual style.
	 */
	private class RemoveStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836929313L;
		public void actionPerformed(ActionEvent e) {
			VisualStyle vs = currentlyEditedVS;
			if (vs.getName().equals(DEFAULT_VS_NAME)) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				                              "You cannot delete default style.",
				                              "Cannot remove style!", JOptionPane.ERROR_MESSAGE);

				return;
			}

			// make sure the user really wants to do this
			final String styleName = vs.getName();
			final String checkString = "Are you sure you want to permanently delete"
			                           + " the visual style '" + styleName + "'?";
			int ich = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), checkString,
			                                        "Confirm Delete Style",
			                                        JOptionPane.YES_NO_OPTION);

			if (ich == JOptionPane.YES_OPTION) {
				final CalculatorCatalog catalog = vmm.getCalculatorCatalog();
				catalog.removeVisualStyle(styleName);

				// try to switch to the default style
				VisualStyle currentStyle = catalog.getVisualStyle(DEFAULT_VS_NAME);

				/*
				 * Update Visual Mapping Browser.
				 */
				vsNameComboBox.removeItem(styleName);
				vsNameComboBox.setSelectedItem(currentStyle.getName());
				switchVS(currentStyle.getName());
				defaultImageManager.remove(styleName);
				vsToModelMap.remove(styleName);

				vmm.setVisualStyleForView( currentView, currentStyle );
				if (currentView != null) Cytoscape.redrawGraph(currentView);
			}
		}
	}

	protected class CopyStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836957944L;
		public void actionPerformed(ActionEvent e) {
			final VisualStyle currentStyle = currentlyEditedVS;
			VisualStyle clone = null;

			try {
				clone = (VisualStyle) currentStyle.clone();
			} catch (CloneNotSupportedException exc) {
				System.err.println("Clone not supported exception!");
				exc.printStackTrace();
			}

			final String newName = getStyleName(clone);

			if ((newName == null) || (newName.trim().length() == 0)) {
				return;
			}

			clone.setName(newName);

			// add new style to the catalog
			vmm.getCalculatorCatalog().addVisualStyle(clone);
			vmm.setVisualStyleForView(currentView, clone);

			final JPanel defPanel = DefaultAppearenceBuilder.getDefaultView(newName);
			final GraphView view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			final Dimension panelSize = defaultAppearencePanel.getSize();

			if (view != null) {
				System.out.println("Creating Default Image for new visual style " + newName);
				updateDefaultImage(newName, view, panelSize);
				setDefaultPanel(defaultImageManager.get(newName));
			}

			vsNameComboBox.addItem(newName);
			switchVS(newName);
		}
	}

	/**
	 * Remove a mapping from current visual style.
	 *
	 */
	private void removeMapping() {
		final int selected = visualPropertySheetPanel.getTable().getSelectedRow();

		if (0 <= selected) {
			Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selected, 0);
			Property curProp = item.getProperty();

			if (curProp instanceof VizMapperProperty) {
				final VisualProperty type = (VisualProperty) ((VizMapperProperty) curProp).getHiddenObject();

				if (type == null)
					return;

				String[] message = {
				                       "The Mapping for " + type.getName() + " will be removed.",
				                       "Proceed?"
				                   };

				int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
				                                          "Remove Mapping",
				                                          JOptionPane.YES_NO_OPTION);

				if (value == JOptionPane.YES_OPTION) {
					// If Continuous Mapper is displayed, kill it.
					if (editorWindowManager.get(type) != null) {
						JDialog editor = editorWindowManager.get(type);
						editor.dispose();
						editorWindowManager.remove(type);
					}

					currentlyEditedVS.removeCalculator(type);

					if (currentView != null) Cytoscape.redrawGraph(currentView);

					/*
					 * Finally, move the visual property to "unused list"
					 */
					VizMapperProperty prop = new VizMapperProperty();
					prop.setCategory(CATEGORY_UNUSED);
					prop.setDisplayName(type.getName());
					prop.setHiddenObject(type);
					prop.setValue("Double-Click to create...");
					visualPropertySheetPanel.addProperty(prop);

					visualPropertySheetPanel.removeProperty(curProp);

					visualPropertySheetPanel.repaint();
				}
			}
		}
	}

	/**
	 * Edit all selected cells at once.
	 *
	 * This is for Discrete Mapping only.
	 *
	 */
	private void editSelectedCells() {
		final PropertySheetTable table = visualPropertySheetPanel.getTable();
		final int[] selected = table.getSelectedRows();

		Item item = null;

		// If nothing selected, return.
		if ((selected == null) || (selected.length == 0)) {
			return;
		}

		/*
		 * Test with the first selected item
		 */
		item = (Item) visualPropertySheetPanel.getTable().getValueAt(selected[0], 0);

		VizMapperProperty prop = (VizMapperProperty) item.getProperty();

		if ((prop == null) || (prop.getParentProperty() == null)) {
			return;
		}

		final VisualProperty type = (VisualProperty) ((VizMapperProperty) prop .getParentProperty())
		                                .getHiddenObject();

		/*
		 * Extract calculator
		 */
		final ObjectMapping mapping;
		final CyAttributes attr;

		mapping = currentlyEditedVS.getCalculator(type).getMapping(0);
		if (type.isNodeProp()) {
			attr = Cytoscape.getNodeAttributes();
		} else {
			attr = Cytoscape.getEdgeAttributes();
		}

		if (mapping instanceof ContinuousMapping || mapping instanceof PassThroughMapping)
			return;

		Object newValue = null;

		try {
			newValue = EditorFactory.showDiscreteEditor(type);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (newValue == null)
			return;

		Object key = null;
		final Class keyClass = CyAttributesUtils.getClass(mapping.getControllingAttributeName(),
		                                                  attr);

		for (int i = 0; i < selected.length; i++) {
			/*
			 * First, update property sheet
			 */
			((Item) visualPropertySheetPanel.getTable().getValueAt(selected[i], 0)).getProperty()
			 .setValue(newValue);
			/*
			 * Then update backend.
			 */
			key = ((Item) visualPropertySheetPanel.getTable().getValueAt(selected[i], 0)).getProperty()
			       .getDisplayName();

			if (keyClass == Integer.class) {
				key = Integer.valueOf((String) key);
			} else if (keyClass == Double.class) {
				key = Double.valueOf((String) key);
			} else if (keyClass == Boolean.class) {
				key = Boolean.valueOf((String) key);
			}

			((DiscreteMapping) mapping).putMapValue(key, newValue);
		}

		/*
		 * Update table and current network view.
		 */
		table.repaint();
		if (currentView != null) Cytoscape.redrawGraph(currentView);
	}

	private class GenerateValueListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836986412L;
		private final int MAX_COLOR = 256 * 256 * 256;
		private DiscreteMapping dm;
		protected static final int RAINBOW1 = 1;
		protected static final int RAINBOW2 = 2;
		protected static final int RANDOM = 3;
		private final int functionType;

		public GenerateValueListener(final int type) {
			this.functionType = type;
		}

		/**
		 * User wants to Seed the Discrete Mapper with Random Color Values.
		 */
		public void actionPerformed(ActionEvent e) {
			//Check Selected poperty
			final int selectedRow = visualPropertySheetPanel.getTable().getSelectedRow();

			if (selectedRow < 0)
				return;

			final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selectedRow, 0);
			final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
			final Object hidden = prop.getHiddenObject();

			if (hidden instanceof VisualProperty) {
				final VisualProperty type = (VisualProperty) hidden;

				final Map valueMap = new HashMap();
				final long seed = System.currentTimeMillis();
				final Random rand = new Random(seed);

				final ObjectMapping oMap;

				final CyAttributes attr;
				final int nOre;

				oMap = currentlyEditedVS.getCalculator(type).getMapping(0);
				if (type.isNodeProp()) {
					attr = Cytoscape.getNodeAttributes();
					nOre = ObjectMapping.NODE_MAPPING;
				} else {
					attr = Cytoscape.getEdgeAttributes();
					nOre = ObjectMapping.EDGE_MAPPING;
				}

				// This function is for discrete mapping only.
				if ((oMap instanceof DiscreteMapping) == false)
					return;

				dm = (DiscreteMapping) oMap;

				final Set<Object> attrSet = loadKeys(oMap.getControllingAttributeName(), attr,
				                                     oMap, nOre);

				// Show error if there is no attribute value.
				if (attrSet.size() == 0) {
					JOptionPane.showMessageDialog(panel, "No attribute value is available.",
					                              "Cannot generate values",
					                              JOptionPane.ERROR_MESSAGE);
				}

				/*
				 * Create random colors
				 */
				final float increment = 1f / ((Number) attrSet.size()).floatValue();

				float hue = 0;
				float sat = 0;
				float br = 0;

				if (type.getDataType() == Color.class) {
					int i = 0;

					if (functionType == RAINBOW1) {
						for (Object key : attrSet) {
							hue = hue + increment;
							valueMap.put(key, new Color(Color.HSBtoRGB(hue, 1f, 1f)));
						}
					} else if (functionType == RAINBOW2) {
						for (Object key : attrSet) {
							hue = hue + increment;
							sat = (Math.abs(((Number) Math.cos((8 * i) / (2 * Math.PI))).floatValue()) * 0.7f)
							      + 0.3f;
							br = (Math.abs(((Number) Math.sin(((i) / (2 * Math.PI)) + (Math.PI / 2)))
							               .floatValue()) * 0.7f) + 0.3f;
							valueMap.put(key, new Color(Color.HSBtoRGB(hue, sat, br)));
							i++;
						}
					} else {
						for (Object key : attrSet)
							valueMap.put(key,
							             new Color(((Number) (rand.nextFloat() * MAX_COLOR)) .intValue()));
					}
				} else if ((type.getDataType() == Number.class) && (functionType == RANDOM)) {
					final String range = JOptionPane.showInputDialog(visualPropertySheetPanel,
					                                                 "Please enter the value range (example: 30-100)",
					                                                 "Assign Random Numbers",
					                                                 JOptionPane.PLAIN_MESSAGE);

					String[] rangeVals = range.split("-");

					if (rangeVals.length != 2)
						return;

					Float min = Float.valueOf(rangeVals[0]);
					Float max = Float.valueOf(rangeVals[1]);
					Float valueRange = max - min;

					for (Object key : attrSet)
						valueMap.put(key, (rand.nextFloat() * valueRange) + min);
				}

				dm.putAll(valueMap);
				if (currentView != null) Cytoscape.redrawGraph(currentView);

				visualPropertySheetPanel.removeProperty(prop);

				final VizMapperProperty newRootProp = new VizMapperProperty();

				Calculator c = currentlyEditedVS.getCalculator(type);
				if (type.isNodeProp())
					buildProperty(c, newRootProp, NODE_VISUAL_MAPPING);
				else
					buildProperty(c, newRootProp, EDGE_VISUAL_MAPPING);

				expandLastSelectedItem(type.getName());
			} else {
				System.out.println("Invalid.");
			}

			return;
		}
	}

	private class GenerateSeriesListener extends AbstractAction {
	private final static long serialVersionUID = 121374883715581L;
		private DiscreteMapping dm;

		/**
		 * User wants to Seed the Discrete Mapper with Random Color Values.
		 */
		public void actionPerformed(ActionEvent e) {
			/*
			 * Check Selected poperty
			 */
			final int selectedRow = visualPropertySheetPanel.getTable().getSelectedRow();

			if (selectedRow < 0)
				return;

			final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selectedRow, 0);
			final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
			final Object hidden = prop.getHiddenObject();

			if (hidden instanceof VisualProperty) {
				final VisualProperty type = (VisualProperty) hidden;

				final Map valueMap = new HashMap();
				final ObjectMapping oMap;
				final CyAttributes attr;
				final int nOre;

				oMap = currentlyEditedVS.getCalculator(type).getMapping(0);
				if (type.isNodeProp()) {
					attr = Cytoscape.getNodeAttributes();
					nOre = ObjectMapping.NODE_MAPPING;
				} else {
					attr = Cytoscape.getEdgeAttributes();
					nOre = ObjectMapping.EDGE_MAPPING;
				}

				if ((oMap instanceof DiscreteMapping) == false)
					return;

				dm = (DiscreteMapping) oMap;

				final Set<Object> attrSet = loadKeys(oMap.getControllingAttributeName(), attr,
				                                     oMap, nOre);
				final String start = JOptionPane.showInputDialog(visualPropertySheetPanel,
				                                                 "Please enter start value (1st number in the series)",
				                                                 "0");
				final String increment = JOptionPane.showInputDialog(visualPropertySheetPanel,
				                                                     "Please enter increment", "1");

				if ((increment == null) || (start == null))
					return;

				Float inc;
				Float st;

				try {
					inc = Float.valueOf(increment);
					st = Float.valueOf(start);
				} catch (Exception ex) {
					ex.printStackTrace();
					inc = null;
					st = null;
				}

				if ((inc == null) || (inc < 0) || (st == null) || (st == null)) {
					return;
				}

				if (type.getDataType() == Number.class) {
					for (Object key : attrSet) {
						valueMap.put(key, st);
						st = st + inc;
					}
				}

				dm.putAll(valueMap);

				if (currentView != null) Cytoscape.redrawGraph(currentView);

				visualPropertySheetPanel.removeProperty(prop);

				final VizMapperProperty newRootProp = new VizMapperProperty();

				Calculator c = currentlyEditedVS.getCalculator(type);
				if (type.isNodeProp())
					buildProperty(c, newRootProp, NODE_VISUAL_MAPPING);
				else
					buildProperty(c, newRootProp, EDGE_VISUAL_MAPPING);


				expandLastSelectedItem(type.getName());
			} else {
				System.out.println("Invalid.");
			}

			return;
		}
	}

	private class FitLabelListener extends AbstractAction {
	private final static long serialVersionUID = 121374883744077L;
		private DiscreteMapping dm;
		/**
		 * User wants to Seed the Discrete Mapper with Random Color Values.
		 */
		public void actionPerformed(ActionEvent e) {
			/*
			 * Check Selected poperty
			 */
			final int selectedRow = visualPropertySheetPanel.getTable().getSelectedRow();

			if (selectedRow < 0)
				return;

			final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selectedRow, 0);
			final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
			final Object hidden = prop.getHiddenObject();
			VisualStyle vs = currentlyEditedVS; 
			if (hidden instanceof VisualProperty) {
				final VisualProperty type = (VisualProperty) hidden;

				final Map valueMap = new HashMap();
				final ObjectMapping oMap;
				final CyAttributes attr;

				oMap = vs.getCalculator(type).getMapping(0);
				if (type.isNodeProp()) {
					attr = Cytoscape.getNodeAttributes();
				} else {
					attr = Cytoscape.getEdgeAttributes();
				}

				if ((oMap instanceof DiscreteMapping) == false)
					return;

				dm = (DiscreteMapping) oMap;

				final Calculator nodeLabelCalc = vs.getCalculator(VisualPropertyCatalog.getVisualProperty("NODE_LABEL"));

				if (nodeLabelCalc == null) {
					return;
				}

				final String ctrAttrName = nodeLabelCalc.getMapping(0).getControllingAttributeName();
				dm.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);

				// final Set<Object> attrSet =
				// loadKeys(oMap.getControllingAttributeName(), attr, oMap);
				DiscreteMapping wm = null;

				if ((type.getName().equals("NODE_WIDTH"))) {
					wm = (DiscreteMapping) vs.getCalculator(VisualPropertyCatalog.getVisualProperty("NODE_WIDTH")).getMapping(0);

					wm.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);

					Set<Object> attrSet1;

					if (ctrAttrName.equals("ID")) {
						attrSet1 = new TreeSet<Object>();

						for (Object node : currentView.getNetwork().nodesList()) {
							attrSet1.add(((Node) node).getIdentifier());
						}
					} else {
						attrSet1 = loadKeys(wm.getControllingAttributeName(), attr, wm,
						                    ObjectMapping.NODE_MAPPING);
					}

					Integer height = ((Number) vs.getDefaultValue(VisualPropertyCatalog.getVisualProperty("NODE_FONT_SIZE")) ).intValue();
					Integer fontSize = ((Number) vs.getDefaultValue(VisualPropertyCatalog.getVisualProperty("NODE_FONT_SIZE"))) .intValue();
					int strLen;

					String labelString = null;
					String[] listObj;
					int longest = 0;

					if (attr.getType(ctrAttrName) == CyAttributes.TYPE_SIMPLE_LIST) {
						wm.setControllingAttributeName("ID", currentView.getNetwork(), false);

						attrSet1 = new TreeSet<Object>();

						for (Object node : currentView.getNetwork().nodesList()) {
							attrSet1.add(((Node) node).getIdentifier());
						}

						GraphView net = currentView;
						String text;

						for (Object node : net.getGraphPerspective().nodesList()) {
							text = net.getNodeView((Node) node).getLabel().getText();
							strLen = text.length();

							if (strLen != 0) {
								listObj = text.split("\\n");
								longest = 0;

								for (String s : listObj) {
									if (s.length() > longest) {
										longest = s.length();
									}
								}

								strLen = longest;

								if (strLen > 25) {
									valueMap.put(((Node) node).getIdentifier(),
									             strLen * fontSize * 0.6);
								} else {
									valueMap.put(((Node) node).getIdentifier(),
									             strLen * fontSize * 0.8);
								}
							}
						}
					} else {
						for (Object label : attrSet1) {
							labelString = label.toString();
							strLen = labelString.length();

							if (strLen != 0) {
								if (labelString.contains("\n")) {
									listObj = labelString.split("\\n");
									longest = 0;

									for (String s : listObj) {
										if (s.length() > longest) {
											longest = s.length();
										}
									}

									strLen = longest;
								}

								if (strLen > 25) {
									valueMap.put(label, strLen * fontSize * 0.6);
								} else {
									valueMap.put(label, strLen * fontSize * 0.8);
								}
							}
						}
					}
				} else if ((type.getName().equals("NODE_HEIGHT"))) {
					wm = (DiscreteMapping) vs.getCalculator(VisualPropertyCatalog.getVisualProperty("NODE_HEIGHT")).getMapping(0);

					wm.setControllingAttributeName(ctrAttrName, currentView.getNetwork(), false);

					Set<Object> attrSet1;

					if (ctrAttrName.equals("ID")) {
						attrSet1 = new TreeSet<Object>();

						for (Object node : currentView.getNetwork().nodesList()) {
							attrSet1.add(((Node) node).getIdentifier());
						}
					} else {
						attrSet1 = loadKeys(wm.getControllingAttributeName(), attr, wm,
						                    ObjectMapping.NODE_MAPPING);
					}

					Integer fontSize = ((Number) vs.getDefaultValue(VisualPropertyCatalog.getVisualProperty("NODE_FONT_SIZE"))).intValue();
					int strLen;

					String labelString = null;
					String[] listObj;

					if (attr.getType(ctrAttrName) == CyAttributes.TYPE_SIMPLE_LIST) {
						wm.setControllingAttributeName("ID", currentView.getNetwork(), false);

						attrSet1 = new TreeSet<Object>();

						for (Object node : currentView.getNetwork().nodesList()) {
							attrSet1.add(((Node) node).getIdentifier());
						}

						String text;

						for (Object node : currentView.getGraphPerspective().nodesList()) {
							text = currentView.getNodeView((Node) node).getLabel().getText();
							strLen = text.length();

							if (strLen != 0) {
								listObj = text.split("\\n");
								valueMap.put(((Node) node).getIdentifier(),
								             listObj.length * fontSize * 1.6);
							}
						}
					} else {
						for (Object label : attrSet1) {
							labelString = label.toString();
							strLen = labelString.length();

							if (strLen != 0) {
								if (labelString.contains("\n")) {
									listObj = labelString.split("\\n");

									strLen = listObj.length;
								} else {
									strLen = 1;
								}

								valueMap.put(label, strLen * fontSize * 1.6);
							}
						}
					}
				}

				wm.putAll(valueMap);

				if (currentView != null) Cytoscape.redrawGraph(currentView);

				visualPropertySheetPanel.removeProperty(prop);

				final VizMapperProperty newRootProp = new VizMapperProperty();

				if (type.isNodeProp())
					buildProperty(vs.getCalculator(type), newRootProp, NODE_VISUAL_MAPPING);
				else
					buildProperty(vs.getCalculator(type), newRootProp, EDGE_VISUAL_MAPPING);

				expandLastSelectedItem(type.getName());
			} else {
				System.out.println("Invalid.");
			}

			return;
		}
	}

	private class BrightnessListener extends AbstractAction {
	private final static long serialVersionUID = 121374883775182L;
		private DiscreteMapping dm;
		protected static final int DARKER = 1;
		protected static final int BRIGHTER = 2;
		private final int functionType;

		public BrightnessListener(final int type) {
			this.functionType = type;
		}

		/**
		 * User wants to Seed the Discrete Mapper with Random Color Values.
		 */
		public void actionPerformed(ActionEvent e) {
			/*
			 * Check Selected poperty
			 */
			final int selectedRow = visualPropertySheetPanel.getTable().getSelectedRow();

			if (selectedRow < 0) {
				return;
			}

			final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selectedRow, 0);
			final VizMapperProperty prop = (VizMapperProperty) item.getProperty();
			final Object hidden = prop.getHiddenObject();

			if (hidden instanceof VisualProperty) {
				final VisualProperty type = (VisualProperty) hidden;

				final Map valueMap = new HashMap();
				final ObjectMapping oMap;

				final CyAttributes attr;
				final int nOre;

				oMap = currentlyEditedVS.getCalculator(type).getMapping(0);
				if (type.isNodeProp()) {
					attr = Cytoscape.getNodeAttributes();
					nOre = ObjectMapping.NODE_MAPPING;
				} else {
					attr = Cytoscape.getEdgeAttributes();
					nOre = ObjectMapping.EDGE_MAPPING;
				}

				if ((oMap instanceof DiscreteMapping) == false) {
					return;
				}

				dm = (DiscreteMapping) oMap;

				final Set<Object> attrSet = loadKeys(oMap.getControllingAttributeName(), attr,
				                                     oMap, nOre);

				/*
				 * Create random colors
				 */
				if (type.getDataType() == Color.class) {
					Object c;

					if (functionType == BRIGHTER) {
						for (Object key : attrSet) {
							c = dm.getMapValue(key);

							if ((c != null) && c instanceof Color) {
								valueMap.put(key, ((Color) c).brighter());
							}
						}
					} else if (functionType == DARKER) {
						for (Object key : attrSet) {
							c = dm.getMapValue(key);

							if ((c != null) && c instanceof Color) {
								valueMap.put(key, ((Color) c).darker());
							}
						}
					}
				}

				dm.putAll(valueMap);
				if (currentView != null) Cytoscape.redrawGraph(currentView);

				visualPropertySheetPanel.removeProperty(prop);

				final VizMapperProperty newRootProp = new VizMapperProperty();

				Calculator c = currentlyEditedVS.getCalculator(type);
				if (type.isNodeProp())
					buildProperty(c, newRootProp, NODE_VISUAL_MAPPING);
				else
					buildProperty(c, newRootProp, EDGE_VISUAL_MAPPING);

				expandLastSelectedItem(type.getName());
			} else {
				System.out.println("Invalid.");
			}

			return;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		disableAllPopup();
	}

	private void disableAllPopup() {
		rainbow1.setEnabled(false);
		rainbow2.setEnabled(false);
		randomize.setEnabled(false);
		series.setEnabled(false);
		fit.setEnabled(false);
		brighter.setEnabled(false);
		darker.setEnabled(false);
		delete.setEnabled(false);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	/**
	 * Check the selected VPT and enable/disable menu items.
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		disableAllPopup();

		final int selected = visualPropertySheetPanel.getTable().getSelectedRow();

		if (0 > selected) {
			return;
		}

		final Item item = (Item) visualPropertySheetPanel.getTable().getValueAt(selected, 0);
		final Property curProp = item.getProperty();

		if (curProp == null)
			return;

		VizMapperProperty prop = ((VizMapperProperty) curProp);

		if (prop.getHiddenObject() instanceof VisualProperty
		    && (prop.getDisplayName().contains("Mapping Type") == false)
		    && (prop.getValue() != null)
		    && (prop.getValue().toString().startsWith("Please select") == false)) {
			// Enble delete menu
			delete.setEnabled(true);

			Property[] children = prop.getSubProperties();

			for (Property p : children) {
				if ((p.getDisplayName() != null) && p.getDisplayName().contains("Mapping Type")) {
					if ((p.getValue() == null)
					    || (p.getValue().equals("Discrete Mapping") == false)) {
						return;
					}
				}
			}

			VisualProperty type = ((VisualProperty) prop.getHiddenObject());

			Class dataType = type.getDataType();

			if (dataType == Color.class) {
				rainbow1.setEnabled(true);
				rainbow2.setEnabled(true);
				randomize.setEnabled(true);
				brighter.setEnabled(true);
				darker.setEnabled(true);
			} else if (dataType == Number.class) {
				randomize.setEnabled(true);
				series.setEnabled(true);
			}

			if (type.getName().equals("NODE_WIDTH") || type.getName().equals("NODE_HEIGHT")) {
				fit.setEnabled(true);
			}
		}

		return;
	}

	/**
	 * <p>
	 * If user selects ID as controlling attributes name, cretate list of IDs
	 * from actual list of nodes/edges.
	 * </p>
	 *
	 * @return
	 */
	private Set<Object> loadID(final int nOre) {
		Set<Object> ids = new TreeSet<Object>();

		List<? extends GraphObject> obj;

		if (nOre == ObjectMapping.NODE_MAPPING) { // FIXME: assumes currentView
			obj = currentView.getGraphPerspective().nodesList();
		} else {
			obj = currentView.getGraphPerspective().edgesList();
		}

		for (GraphObject o : obj) {
			ids.add( o.getIdentifier() );
		}

		return ids;
	}

	// /**
	/**
	 * DOCUMENT ME!
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void stateChanged(ChangeEvent e) {
		System.out.println("vizmappermainpanel: statechanged"+e);
		final String selectedName = (String) vsNameComboBox.getSelectedItem();
		final String currentName = currentlyEditedVS.getName();
		
		if (ignore)
			return;

		System.out.println("Got VMM Change event.  Cur VS in VMM: " + currentName);

		if ((selectedName == null) || (currentName == null) || (currentView == null) || currentView.equals(Cytoscape.getNullNetworkView()))
			return;

		// Update GUI based on CalcCatalog's state.
		if (!findVSName(currentName)) {
			syncStyleBox();
		} else {
			// Bug fix: 0001802: if VS already existed in combobox, select it
			for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
				if (vsNameComboBox.getItemAt(i).equals(currentName)) {
					vsNameComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		// kono: should be placed here.
		// MLC 03/31/08 BEGIN:
		// Make fure we update the lastVSName based on anything that changes the visual style:
		lastVSName = currentName;
		// MLC 03/31/08 END.
	}

	private void syncStyleBox() {

		String curStyleName = currentlyEditedVS.getName();

		String styleName;
		List<String> namesInBox = new ArrayList<String>();
		namesInBox.addAll(vmm.getCalculatorCatalog().getVisualStyleNames());

		for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
			styleName = vsNameComboBox.getItemAt(i).toString();

			if (vmm.getCalculatorCatalog().getVisualStyle(styleName) == null) {
				// No longer exists in the VMM.  Remove.
				//System.out.println("No longer exists in the VMM.  Removing in syncStyleBox()");
				vsNameComboBox.removeItem(styleName);
				defaultImageManager.remove(styleName);
				vsToModelMap.remove(styleName);
			}
		}

		Collections.sort(namesInBox);

		// Reset combobox items.
		vsNameComboBox.removeAllItems();

		for (String name : namesInBox)
			vsNameComboBox.addItem(name);

		// Bug fix: 0001721: 
		//Note: Because vsNameComboBox.removeAllItems() will fire unwanted event, 
		// vmm.getVisualStyle().getName() will not be the same as curStyleName
		if ((curStyleName == null) || curStyleName.trim().equals(""))
			switchVS(currentlyEditedVS.getName());
		else
			switchVS(curStyleName);
	}

	// return true iff 'match' is found as a name within the
	// vsNameComboBox.
	private boolean findVSName(String match) {
		for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
			if (vsNameComboBox.getItemAt(i).equals(match)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public Object getSelectedItem() {
		final JTable table = visualPropertySheetPanel.getTable();

		return table.getModel().getValueAt(table.getSelectedRow(), 0);
	}

	//**************************************************************************
	// MultiHashMapListenerAdaptor

	private class MultiHashMapListenerAdapter implements MultiHashMapListener {

		// ref to members
		private final JPanel container;
		private final CyAttributes attr;
		private final CyComboBoxPropertyEditor attrEditor;
		private final CyComboBoxPropertyEditor numericalAttrEditor;
		private final List<String> attrEditorNames;
		private final List<String> numericalAttrEditorNames;

		/**
		 * Constructor.
		 *
		 * @param cyAttributes CyAttributes
		 */
		MultiHashMapListenerAdapter(JPanel container, CyAttributes cyAttributes, CyComboBoxPropertyEditor attrEditor, CyComboBoxPropertyEditor numericalAttrEditor) {
			
			// init some members
			this.attr = cyAttributes;
			this.container = container;
			this.attrEditor = attrEditor;
			this.numericalAttrEditor = numericalAttrEditor;
			this.attrEditorNames = new ArrayList<String>();
			this.numericalAttrEditorNames = new ArrayList<String>();

			// populate our lists
			populateLists();
		}

		/**
		 *  Our implementation of MultiHashMapListener.attributeValueAssigned().
		 *
		 * @param objectKey String
		 * @param attributeName String
		 * @param keyIntoValue Object[]
		 * @param oldAttributeValue Object
		 * @param newAttributeValue Object
		 */
		public void attributeValueAssigned(String objectKey, String attributeName,
										   Object[] keyIntoValue, Object oldAttributeValue,
										   Object newAttributeValue) {

			// we do not process network attributes
			if (attr == Cytoscape.getNetworkAttributes()) return;

			// conditional repaint container
			boolean repaint = false;

			// this code gets called a lot
			// so i've decided to keep the next two if statements as is, 
			// rather than create a shared general routine to call

			// if attribute is not in attrEditorNames, add it if we support its type
			if (!attrEditorNames.contains(attributeName)) {
				byte type = attr.getType(attributeName);
				if (attr.getUserVisible(attributeName) && (type != CyAttributes.TYPE_UNDEFINED) && (type != CyAttributes.TYPE_COMPLEX)) {
					attrEditorNames.add(attributeName);
					Collections.sort(attrEditorNames);
					attrEditor.setAvailableValues(attrEditorNames.toArray());
					repaint = true;
				}
			}

			// if attribute is not contained in numericalAttrEditorNames, add it if we support its class
			if (!numericalAttrEditorNames.contains(attributeName)) {
				Class dataClass = CyAttributesUtils.getClass(attributeName, attr);
				if ((dataClass == Integer.class) || (dataClass == Double.class) || (dataClass == Float.class)) {
					numericalAttrEditorNames.add(attributeName);
					Collections.sort(numericalAttrEditorNames);
					numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
					repaint = true;
				}
			}
			
			if (repaint) container.repaint();
		}

		/**
		 *  Our implementation of MultiHashMapListener.attributeValueRemoved().
		 *
		 * @param objectKey String
		 * @param attributeName String
		 * @param keyIntoValue Object[]
		 * @param attributeValue Object
		 */
		public void attributeValueRemoved(String objectKey, String attributeName,
										  Object[] keyIntoValue, Object attributeValue) {
			allAttributeValuesRemoved(objectKey, attributeName);
		}

		/**
		 *  Our implementation of MultiHashMapListener.allAttributeValuesRemoved()
		 *
		 * @param objectKey String
		 * @param attributeName String
		 */
		public void allAttributeValuesRemoved(String objectKey, String attributeName) {

			// we do not process network attributes
			if (attr == Cytoscape.getNetworkAttributes()) return;

			// conditional repaint container
			boolean repaint = false;

			// this code gets called a lot
			// so i've decided to keep the next two if statements as is, 
			// rather than create a shared general routine to call

			// if attribute is in attrEditorNames, remove it
			if (attrEditorNames.contains(attributeName)) {
				attrEditorNames.remove(attributeName);
				Collections.sort(attrEditorNames);
				attrEditor.setAvailableValues(attrEditorNames.toArray());
				repaint = true;
			}

			// if attribute is in numericalAttrEditorNames, remove it
			if (numericalAttrEditorNames.contains(attributeName)) {
				numericalAttrEditorNames.remove(attributeName);
				Collections.sort(numericalAttrEditorNames);
				numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
				repaint = true;
			}

			if (repaint) container.repaint();
		}

		/**
		 * Method to populate attrEditorNames & numericalAttrEditorNames on object instantiation.
		 */
		private void populateLists() {

			// get attribute names & sort
			String[] nameArray = attr.getAttributeNames();
			Arrays.sort(nameArray);

			// populate attrEditorNames & numericalAttrEditorNames
			attrEditorNames.add("ID");
			byte type;
			Class dataClass;
			for (String name : nameArray) {
				type = attr.getType(name);
				if (attr.getUserVisible(name) && (type != CyAttributes.TYPE_UNDEFINED) && (type != CyAttributes.TYPE_COMPLEX)) {
					attrEditorNames.add(name);
				}
				dataClass = CyAttributesUtils.getClass(name, attr);
				if ((dataClass == Integer.class) || (dataClass == Double.class) || (dataClass == Float.class)) {
					numericalAttrEditorNames.add(name);
				}
			}
		}
	}
}

