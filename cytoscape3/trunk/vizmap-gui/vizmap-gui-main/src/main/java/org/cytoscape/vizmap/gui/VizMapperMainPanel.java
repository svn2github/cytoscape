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
package org.cytoscape.vizmap.gui;

import static org.cytoscape.vizmap.VisualPropertyType.NODE_LABEL_POSITION;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.RowSetEvent;
import org.cytoscape.model.events.RowSetListener;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.EdgeAppearanceCalculator;
import org.cytoscape.vizmap.LineStyle;
import org.cytoscape.vizmap.NodeAppearanceCalculator;
import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.calculators.BasicCalculator;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.internal.editors.discrete.CyComboBoxPropertyEditor;
import org.cytoscape.vizmap.gui.theme.ColorManager;
import org.cytoscape.vizmap.gui.theme.IconManager;
import org.cytoscape.vizmap.gui.util.VizMapperUtil;
import org.cytoscape.vizmap.icon.ArrowIcon;
import org.cytoscape.vizmap.icon.NodeIcon;
import org.cytoscape.vizmap.icon.VisualPropertyIcon;
import org.cytoscape.vizmap.mappings.ContinuousMapping;
import org.cytoscape.vizmap.mappings.DiscreteMapping;
import org.cytoscape.vizmap.mappings.ObjectMapping;
import org.cytoscape.vizmap.mappings.PassThroughMapping;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.Cytoscape;
import cytoscape.util.swing.DropDownMenuButton;
import cytoscape.view.CySwingApplication;
import cytoscape.view.NetworkPanel;

/**
 * New VizMapper UI main panel. Refactored for Cytoscape 3.
 * 
 * This panel consists of 3 panels:
 * <ul>
 * <li>Global Control Panel
 * <li>Default editor panel
 * <li>Visual Mapping Browser
 * </ul>
 * 
 * 
 * @version 0.6
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends JPanel implements
		PropertyChangeListener, PopupMenuListener, ChangeListener {
	
	private final static long serialVersionUID = 1202339867854959L;

	// Default Visual Style Name
	protected static final String DEFAULT_VS_NAME = "default";

	/*
	 * Fields which will be injected by Spring.
	 */
	@Resource
	private CySwingApplication cytoscapeDesktop;
	@Resource
	private DefaultAppearenceBuilder defAppBldr;
	
	private VisualMappingManager vmm;

	// Resource managers
	@Resource
	private ColorManager colorMgr;
	@Resource
	private IconManager iconMgr;
	
	@Resource
	private VizMapperMenuManager menuMgr;

	@Resource
	private EditorFactory editorFactory;
	
	@Resource
	VizMapperUtil vizMapperUtil;

	// Action (context menu) manager
	@Resource
	Set<VizMapperAction> actionList;

	//private static JMenu generateValues;
	private static JMenu modifyValues;
	private static JMenuItem brighter, darker;
	private static JCheckBoxMenuItem lockSize;

	/*
	 * Keeps Properties in the browser.
	 */
	private Map<String, List<Property>> propertyMap;

	// Keeps current discrete mappings. NOT PERMANENT
	private final Map<String, Map<Object, Object>> discMapBuffer = new HashMap<String, Map<Object, Object>>();
	private String lastVSName = null;
	private JScrollPane noMapListScrollPane;
	private List<VisualPropertyType> unusedVisualPropType;
	private JPanel buttonPanel;
	private JButton addButton;
	private JPanel bottomPanel;
	private Map<VisualPropertyType, JDialog> editorWindowManager = new HashMap<VisualPropertyType, JDialog>();
	private Map<String, Image> defaultImageManager = new HashMap<String, Image>();
	private boolean ignore = false;

	// For node size lock
	VizMapperProperty nodeSize;
	VizMapperProperty nodeWidth;
	VizMapperProperty nodeHeight;

	private CyNetwork targetNetwork;
	private GraphView targetView;

	private List<CyNetwork> targetNetworks;
	private List<GraphView> targetViews;

	public VizMapperMainPanel(CySwingApplication desktop,
			DefaultAppearenceBuilder dab, IconManager iconMgr,
			ColorManager colorMgr, VisualMappingManager vmm, VizMapperMenuManager menuMgr, EditorFactory editorFactory) {
		this.cytoscapeDesktop = desktop;
		this.defAppBldr = dab;
		this.iconMgr = iconMgr;
		this.colorMgr = colorMgr;
		this.vmm = vmm;
		this.menuMgr = menuMgr;
		this.editorFactory = editorFactory;
		
		startVizMapper();
	}
	
	
	private void startVizMapper() {
		vmm.addChangeListener(this);

		//numberCellEditor = new CyDoublePropertyEditor(this);

		propertyMap = new HashMap<String, List<Property>>();
		setMenu();

		// Need to register listener here, instead of CytoscapeDesktop.
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new VizMapListener());

		initComponents();
		registerCellEditorListeners();

		// By default, force to sort property by prop name.
		visualPropertySheetPanel.setSorting(true);

		// TODO Register these listeners as services
		// AttrEventListener ael1 = new AttrEventListener(this,
		// currentNetwork.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS),
		// nodeAttrEditor, nodeNumericalAttrEditor);
		// AttrEventListener ael2 = new AttrEventListener(this,
		// currentNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS),
		// edgeAttrEditor, edgeNumericalAttrEditor);
		// AttrEventListener ael3 = new AttrEventListener(this,
		// currentNetwork.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS),
		// null, null);

		
		cytoscapeDesktop.getCytoPanel(SwingConstants.WEST).add(
				"VizMapper\u2122", this);
		cytoscapeDesktop.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

		// This may cause things to flash and update if a session is loaded.
		initVizmapperGUI();
	}
	
	

	/*
	 * Register listeners for editors.
	 */
	private void registerCellEditorListeners() {
		nodeAttrEditor.addPropertyChangeListener(this);
		edgeAttrEditor.addPropertyChangeListener(this);

		mappingTypeEditor.addPropertyChangeListener(this);

		for ( PropertyEditor p : editorFactory.getCellEditors() )
			p.addPropertyChangeListener(this);

	}

	/**
	 * Get an instance of VizMapper UI panel. This is a singleton.
	 * 
	 * @return public static VizMapperMainPanel getVizMapperUI() { if (panel ==
	 *         null) panel = new VizMapperMainPanel();
	 * 
	 *         return panel; }
	 */

	/**
	 * Will be used to show/hide node size props.
	 * 
	 * @param isLock
	 */
	private void switchNodeSizeLock(boolean isLock) {
		final Property[] props = visualPropertySheetPanel.getProperties();

		if (isLock && (nodeSize != null)) {
			// Case 1: Locked. Need to remove width/height props.
			boolean isNodeSizeExist = false;

			for (Property prop : props) {
				if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_SIZE.getName()))
					isNodeSizeExist = true;

				if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_HEIGHT.getName())) {
					nodeHeight = (VizMapperProperty) prop;
					visualPropertySheetPanel.removeProperty(prop);
				} else if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_WIDTH.getName())) {
					nodeWidth = (VizMapperProperty) prop;
					visualPropertySheetPanel.removeProperty(prop);
				}
			}

			if (isNodeSizeExist == false)
				visualPropertySheetPanel.addProperty(nodeSize);
		} else {
			// Case 2: Unlocked. Need to add W/H.
			boolean isNodeWExist = false;
			boolean isNodeHExist = false;

			for (Property prop : props) {
				if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_SIZE.getName())) {
					nodeSize = (VizMapperProperty) prop;
					visualPropertySheetPanel.removeProperty(prop);
				}

				if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_WIDTH.getName()))
					isNodeWExist = true;

				if (prop.getDisplayName().equals(
						VisualPropertyType.NODE_HEIGHT.getName()))
					isNodeHExist = true;
			}

			if (isNodeHExist == false) {
				if (nodeHeight != null)
					visualPropertySheetPanel.addProperty(nodeHeight);
			}

			if (isNodeWExist == false) {
				if (nodeHeight != null)
					visualPropertySheetPanel.addProperty(nodeWidth);
			}
		}

		visualPropertySheetPanel.repaint();

		final String targetName = vmm.getVisualStyle().getName();

		updateDefaultImage(targetName,
				(GraphView) ((DefaultViewPanel) defAppBldr
						.getDefaultView(targetName)).getView(),
				defaultViewImagePanel.getSize());
		setDefaultViewImagePanel(defaultImageManager.get(targetName));
	}

	/**
	 * Setup menu items.<br>
	 * 
	 * This includes both icon menu and right-click menu.
	 * 
	 */
	private void setMenu() {

		lockSize = new JCheckBoxMenuItem("Lock Node Width/Height");
		lockSize.setSelected(true);
		lockSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lockSize.isSelected()) {
					vmm.getVisualStyle().getNodeAppearanceCalculator()
							.setNodeSizeLocked(true);
					switchNodeSizeLock(true);
				} else {
					vmm.getVisualStyle().getNodeAppearanceCalculator()
							.setNodeSizeLocked(false);
					switchNodeSizeLock(false);
				}

				Cytoscape.redrawGraph(targetView);
			}
		});

//		delete = new JMenuItem("Delete mapping");

//		final Font italicMenu = new Font("SansSerif", Font.ITALIC, 14);
//		rainbow1 = new JMenuItem("Rainbow 1");
//		rainbow2 = new JMenuItem("Rainbow 2 (w/modulations)");
//		randomize = new JMenuItem("Randomize");
//		rainbow1.setFont(italicMenu);
//		rainbow2.setFont(italicMenu);
//
//		series = new JMenuItem("Series (Number Only)");
//		fit = new JMenuItem("Fit Node Width to Label");
//
//		brighter = new JMenuItem("Brighter");
//		darker = new JMenuItem("Darker");
//
//		editAll = new JMenuItem("Edit selected values at once...");
//
//		delete.setIcon(iconMgr.getIcon("delIcon"));
//		editAll.setIcon(iconMgr.getIcon("editIcon"));
//
//		rainbow1.addActionListener(new GenerateValueListener(
//				GenerateValueListener.RAINBOW1));
//		rainbow2.addActionListener(new GenerateValueListener(
//				GenerateValueListener.RAINBOW2));
//		randomize.addActionListener(new GenerateValueListener(
//				GenerateValueListener.RANDOM));
//
//		series.addActionListener(new GenerateSeriesListener());
//		fit.addActionListener(new FitLabelListener());
//
//		brighter.addActionListener(new BrightnessListener(
//				BrightnessListener.BRIGHTER));
//		darker.addActionListener(new BrightnessListener(
//				BrightnessListener.DARKER));
//
//		delete.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				removeMapping();
//			}
//		});
//		editAll.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				editSelectedCells();
//			}
//		});
		// add.addActionListener(l)
		// select.setIcon(vmIcon);
		
//		generateValues.add(rainbow1);
//		generateValues.add(rainbow2);
//		generateValues.add(randomize);
//		generateValues.add(series);
//		generateValues.add(fit);

//		modifyValues.add(brighter);
//		modifyValues.add(darker);
//
//		rainbow1.setEnabled(false);
//		rainbow2.setEnabled(false);
//		randomize.setEnabled(false);
//		series.setEnabled(false);
//		fit.setEnabled(false);
//
//		brighter.setEnabled(false);
//		darker.setEnabled(false);
//
//		rightClickMenu.add(delete);
//		rightClickMenu.add(new JSeparator());
//		rightClickMenu.add(generateValues);
//		rightClickMenu.add(modifyValues);
//		rightClickMenu.add(editAll);
//		rightClickMenu.add(new JSeparator());
//		rightClickMenu.add(lockSize);
//
//		delete.setEnabled(false);
		//menuMgr.getContextMenu().addPopupMenuListener(this);
	}

//	public static void apply(Object newValue, VisualPropertyType type) {
//		if (newValue != null)
//			type.setDefault(Cytoscape.getVisualMappingManager()
//					.getVisualStyle(), newValue);
//	}
//
//	public Object showValueSelectDialog(VisualPropertyType type,
//			Component caller) throws Exception {
//		return editorFactory.showDiscreteEditor(type);
//	}

	protected JComboBox getVsNameComboBox() {
		return vsNameComboBox;
	}

	protected void setLastVSName(final String newName) {
		this.lastVSName = newName;
	}

	protected Map<String, List<Property>> getPropertyMap() {
		return propertyMap;
	}
	
	protected PropertySheetPanel getPropertySheetPanel() {
		return visualPropertySheetPanel;
	}
	
	protected Map<VisualPropertyType, JDialog> getEditorWindowManager() {
		return editorWindowManager;
	}
	
	protected List<VisualPropertyType> getUnusedVisualPropType() {
		return unusedVisualPropType;
	}
	

	/**
	 * GUI initialization code based on the auto-generated code from NetBeans
	 * 
	 */
	private void initComponents() {
		mainSplitPane = new javax.swing.JSplitPane();
		listSplitPane = new javax.swing.JSplitPane();

		bottomPanel = new javax.swing.JPanel();

		defaultViewImagePanel = new javax.swing.JPanel();
		visualPropertySheetPanel = new PropertySheetPanel();
		visualPropertySheetPanel.setTable(new PropertySheetTable());

		vsSelectPanel = new javax.swing.JPanel();
		vsNameComboBox = new javax.swing.JComboBox();

		buttonPanel = new javax.swing.JPanel();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		buttonPanel.setLayout(gridbag);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = GridBagConstraints.REMAINDER;

		addButton = new javax.swing.JButton();

		addButton.setUI(new BlueishButtonUI());

		gridbag.setConstraints(addButton, constraints);
		buttonPanel.add(addButton);

		constraints.gridx = 2;
		constraints.gridy = 0;

		mainSplitPane.setDividerLocation(120);
		mainSplitPane.setDividerSize(4);
		// TODO why do we have to do this?
		mainSplitPane.setSize(new Dimension(100, 120));

		defaultViewImagePanel.setMinimumSize(new Dimension(100, 100));
		defaultViewImagePanel.setPreferredSize(new Dimension(mainSplitPane
				.getWidth(), mainSplitPane.getDividerLocation()));
		defaultViewImagePanel.setSize(defaultViewImagePanel.getPreferredSize());
		defaultViewImagePanel.setLayout(new BorderLayout());

		listSplitPane.setDividerLocation(400);
		listSplitPane.setDividerSize(5);
		listSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

		noMapListScrollPane = new javax.swing.JScrollPane();
		noMapListScrollPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null, "Unused Visual Properties",
						javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12)));
		noMapListScrollPane
				.setToolTipText("To Create New Mapping, Drag & Drop List Item to Browser.");

		org.jdesktop.layout.GroupLayout bottomPanelLayout = new org.jdesktop.layout.GroupLayout(
				bottomPanel);
		bottomPanel.setLayout(bottomPanelLayout);
		bottomPanelLayout.setHorizontalGroup(bottomPanelLayout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(noMapListScrollPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272,
						Short.MAX_VALUE).add(buttonPanel,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));
		bottomPanelLayout
				.setVerticalGroup(bottomPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								bottomPanelLayout
										.createSequentialGroup()
										.add(
												buttonPanel,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												25,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(
												noMapListScrollPane,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												135, Short.MAX_VALUE)));

		listSplitPane.setLeftComponent(mainSplitPane);
		listSplitPane.setRightComponent(bottomPanel);

		mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		defaultViewImagePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null, "Defaults",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12),
						java.awt.Color.darkGray));

		mainSplitPane.setLeftComponent(defaultViewImagePanel);

		visualPropertySheetPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null, "Visual Mapping Browser",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12),
						java.awt.Color.darkGray));

		mainSplitPane.setRightComponent(visualPropertySheetPanel);

		vsSelectPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
						"Current Visual Style",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12),
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
				menuMgr.getMainMenu().show(b, 0, b.getHeight());
			}
		});

		optionButton.setToolTipText("Options...");
		optionButton.setIcon(iconMgr.getIcon("optionIcon"));
		optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		optionButton.setComponentPopupMenu(menuMgr.getMainMenu());

		org.jdesktop.layout.GroupLayout vsSelectPanelLayout = new org.jdesktop.layout.GroupLayout(
				vsSelectPanel);
		vsSelectPanel.setLayout(vsSelectPanelLayout);
		vsSelectPanelLayout
				.setHorizontalGroup(vsSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								vsSelectPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.add(vsNameComboBox, 0, 146,
												Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												optionButton,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												64,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));
		vsSelectPanelLayout
				.setVerticalGroup(vsSelectPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								vsSelectPanelLayout
										.createSequentialGroup()
										.add(
												vsSelectPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																vsNameComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(optionButton)) // .addContainerGap(
						// org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						// Short.MAX_VALUE)
						));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(vsSelectPanel,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.add(mainSplitPane,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().add(vsSelectPanel,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.RELATED).add(
								mainSplitPane,
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								510, Short.MAX_VALUE)));
	} // </editor-fold>

	// Variables declaration - do not modify
	private JPanel defaultViewImagePanel;
	private javax.swing.JSplitPane mainSplitPane;
	private javax.swing.JSplitPane listSplitPane;
	private DropDownMenuButton optionButton;
	private PropertySheetPanel visualPropertySheetPanel;
	private javax.swing.JComboBox vsNameComboBox;
	private javax.swing.JPanel vsSelectPanel;

	// Others
	private DefaultTableCellRenderer emptyBoxRenderer = new DefaultTableCellRenderer();
	private DefaultTableCellRenderer filledBoxRenderer = new DefaultTableCellRenderer();

	/*
	 * Controlling attr selector
	 */
	private CyComboBoxPropertyEditor nodeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor nodeNumericalAttrEditor = new CyComboBoxPropertyEditor();
	private CyComboBoxPropertyEditor edgeNumericalAttrEditor = new CyComboBoxPropertyEditor();

	// For mapping types.
	private CyComboBoxPropertyEditor mappingTypeEditor = new CyComboBoxPropertyEditor();
	private static final Map<Object, Icon> nodeShapeIcons = NodeShape
			.getIconSet();
	private static final Map<Object, Icon> arrowShapeIcons = ArrowShape
			.getIconSet();
	private static final Map<Object, Icon> lineTypeIcons = LineStyle.getIconSet();
	private PropertyRendererRegistry rendReg = new PropertyRendererRegistry();
	private PropertyEditorRegistry editorReg = new PropertyEditorRegistry();

	// End of variables declaration
	private void vsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		final String vsName = (String) vsNameComboBox.getSelectedItem();

		if (vsName != null) {
			if (targetView.equals(Cytoscape.getNullNetworkView())) {
				switchVS(vsName, false);
			} else {
				switchVS(vsName, true);
			}
		}
	}

	protected void switchVS(String vsName) {
		switchVS(vsName, true);
	}

	private void switchVS(String vsName, boolean redraw) {
		if (ignore)
			return;

		// If new VS name is the same, ignore.
		if (lastVSName == vsName)
			return;

		closeEditorWindow();

		System.out.println("VS Switched --> " + vsName + ", Last = "
				+ lastVSName);
		vmm.setNetworkView(targetView);

		// MLC 03/31/08:
		// NOTE: Will cause stateChanged() to be called:
		vmm.setVisualStyle(vsName);

		if (propertyMap.containsKey(vsName)) {
			final List<Property> props = propertyMap.get(vsName);
			final Map<String, Property> unused = new TreeMap<String, Property>();

			/*
			 * Remove currently shown property
			 */
			for (Property item : visualPropertySheetPanel.getProperties())
				visualPropertySheetPanel.removeProperty(item);

			/*
			 * Add properties to current property sheet.
			 */
			for (Property prop : props) {
				if (prop.getCategory().startsWith(CATEGORY_UNUSED) == false) {
					if (prop.getCategory().equals(NODE_VISUAL_MAPPING)) {
						visualPropertySheetPanel.addProperty(0, prop);
					} else {
						visualPropertySheetPanel.addProperty(prop);
					}
				} else {
					unused.put(prop.getDisplayName(), prop);
				}
			}

			final List<String> keys = new ArrayList<String>(unused.keySet());
			Collections.sort(keys);

			for (Object key : keys) {
				visualPropertySheetPanel.addProperty(unused.get(key));
			}
		} else
			setPropertyTable();

		// MLC 03/31/08:
		// lastVSName = vsName;
		vmm.setVisualStyleForView(targetView, vmm.getVisualStyle(vsName));

		if (redraw)
			Cytoscape.redrawGraph(targetView);

		/*
		 * Draw default view
		 */
		Image defImg = defaultImageManager.get(vsName);

		if (defImg == null) {
			// Default image is not available in the buffer. Create a new one.
			updateDefaultImage(vsName,
					(GraphView) ((DefaultViewPanel) defAppBldr
							.getDefaultView(vsName)).getView(),
					defaultViewImagePanel.getSize());
			defImg = defaultImageManager.get(vsName);
		}

		// Set the default view to the panel.
		setDefaultViewImagePanel(defImg);

		// Sync. lock state
		final boolean lockState = vmm.getVisualStyle()
				.getNodeAppearanceCalculator().getNodeSizeLocked();
		lockSize.setSelected(lockState);
		switchNodeSizeLock(lockState);

		visualPropertySheetPanel.setSorting(true);

		// Cleanup desktop.
		//cytoscapeDesktop.repaint();
		vsNameComboBox.setSelectedItem(vsName);
	}

	protected static final String CATEGORY_UNUSED = "Unused Properties";
	private static final String GRAPHICAL_MAP_VIEW = "Graphical View";
	protected static final String NODE_VISUAL_MAPPING = "Node Visual Mapping";
	protected static final String EDGE_VISUAL_MAPPING = "Edge Visual Mapping";

	/*
	 * Set Visual Style selector combo box.
	 */
	private void initVizmapperGUI() {
		List<String> vsNames = new ArrayList<String>(vmm.getCalculatorCatalog()
				.getVisualStyleNames());

		final VisualStyle style = vmm.getVisualStyle();

		// Disable action listeners
		final ActionListener[] li = vsNameComboBox.getActionListeners();

		for (int i = 0; i < li.length; i++)
			vsNameComboBox.removeActionListener(li[i]);

		vsNameComboBox.removeAllItems();

		JPanel defPanel;

		final Dimension panelSize = defaultViewImagePanel.getSize();
		GraphView view;

		Collections.sort(vsNames);

		for (String name : vsNames) {
			vsNameComboBox.addItem(name);
			// MLC 03/31/08:
			// Deceptively, getDefaultView actually actually calls
			// VisualMappingManager.setVisualStyle()
			// so each time we add a combobox item, the visual style is
			// changing.
			// Make sure to set the lastVSName as we change the visual style:
			defPanel = defAppBldr.getDefaultView(name);
			view = (GraphView) ((DefaultViewPanel) defPanel).getView();

			if (view != null) {
				System.out.println("Creating Default Image for " + name);
				updateDefaultImage(name, view, panelSize);
			}
		}

		vmm.setNetworkView(targetView);

		// Switch back to the original style.
		switchVS(style.getName());

		// Sync check box and actual lock state
		switchNodeSizeLock(lockSize.isSelected());

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
	protected void updateDefaultImage(String vsName, GraphView view,
			Dimension size) {
		Image image = defaultImageManager.remove(vsName);

		if (image != null) {
			image.flush();
			image = null;
		}

		defaultImageManager.put(vsName, view.createImage((int) size.getWidth(),
				(int) size.getHeight(), 0.9));
	}

	protected Map<String, Image> getDefaultImageManager() {
		return defaultImageManager;
	}

	private void setPropertySheetAppearence() {
		/*
		 * Set Tooltiptext for the table.
		 */
		visualPropertySheetPanel.setTable(new PropertySheetTable() {
			private final static long serialVersionUID = 1213748836812161L;

			public String getToolTipText(MouseEvent me) {
				final Point pt = me.getPoint();
				final int row = rowAtPoint(pt);

				if (row < 0)
					return null;
				else {
					final Property prop = ((Item) getValueAt(row, 0))
							.getProperty();

					final Color fontColor;

					if ((prop != null) && (prop.getValue() != null)
							&& (prop.getValue().getClass() == Color.class))
						fontColor = (Color) prop.getValue();
					else
						fontColor = Color.DARK_GRAY;

					final String colorString = Integer.toHexString(fontColor
							.getRGB());

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
								+ colorString.substring(2, 8)
								+ "\"><strong>"
								+ prop.getDisplayName()
								+ " = "
								+ prop.getValue()
								+ "</font></strong></body></html>";
					else if ((prop.getSubProperties() == null)
							|| (prop.getSubProperties().length == 0))
						return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#"
								+ colorString.substring(2, 8)
								+ "\"><strong>"
								+ prop.getDisplayName()
								+ "</font></strong></body></html>";

					return null;
				}
			}
		});

		visualPropertySheetPanel.getTable().getColumnModel()
				.addColumnModelListener(new TableColumnModelListener() {
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

		/*
		 * By default, show category.
		 */
		visualPropertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);

		visualPropertySheetPanel.getTable().setComponentPopupMenu(
				menuMgr.getContextMenu());

		visualPropertySheetPanel.getTable().addMouseListener(
				new MouseAdapter() {
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
		emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
		emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
		emptyBoxRenderer.setForeground(Color.red);
		emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

		filledBoxRenderer.setBackground(Color.white);
		filledBoxRenderer.setForeground(Color.blue);

		setAttrComboBox();

		final Set mappingTypes = vmm
				.getCalculatorCatalog().getMappingNames();

		mappingTypeEditor.setAvailableValues(mappingTypes.toArray());

		VisualPropertyIcon newIcon;

		List<Icon> iconList = new ArrayList<Icon>();
		final List<NodeShape> nodeShapes = new ArrayList<NodeShape>();

		for (Object key : nodeShapeIcons.keySet()) {
			NodeShape shape = (NodeShape) key;

			if (shape.isSupported()) {
				iconList.add(nodeShapeIcons.get(key));
				nodeShapes.add(shape);
			}
		}

		Icon[] iconArray = new Icon[iconList.size()];
		String[] shapeNames = new String[iconList.size()];

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((NodeIcon) iconList.get(i)).clone();
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = nodeShapes.get(i).getShapeName();
		}

		//shapeCellEditor.setAvailableValues(nodeShapes.toArray());
		//shapeCellEditor.setAvailableIcons(iconArray);

		iconList.clear();
		iconList.addAll(arrowShapeIcons.values());
		iconArray = new Icon[iconList.size()];

		String[] arrowNames = new String[iconList.size()];
		Set arrowShapes = arrowShapeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = ((ArrowIcon) iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(40);
			newIcon.setBottomPadding(-9);
			iconArray[i] = newIcon;
			arrowNames[i] = newIcon.getName();
		}

		//arrowCellEditor.setAvailableValues(arrowShapes.toArray());
//		arrowCellEditor.setAvailableIcons(iconArray);

		iconList = new ArrayList();
		iconList.addAll(lineTypeIcons.values());
		iconArray = new Icon[iconList.size()];
		shapeNames = new String[iconList.size()];

		Set lineTypes = lineTypeIcons.keySet();

		for (int i = 0; i < iconArray.length; i++) {
			newIcon = (VisualPropertyIcon) (iconList.get(i));
			newIcon.setIconHeight(16);
			newIcon.setIconWidth(16);
			iconArray[i] = newIcon;
			shapeNames[i] = newIcon.getName();
		}

		//lineCellEditor.setAvailableValues(lineTypes.toArray());
		//lineCellEditor.setAvailableIcons(iconArray);
	}

	private void updateTableView() {
		final PropertySheetTable table = visualPropertySheetPanel.getTable();
		Property shownProp = null;
		final DefaultTableCellRenderer empRenderer = new DefaultTableCellRenderer();

		// Number of rows shown now.
		int rowCount = table.getRowCount();

		for (int i = 0; i < rowCount; i++) {
			shownProp = ((Item) table.getValueAt(i, 0)).getProperty();

			if ((shownProp != null)
					&& (shownProp.getParentProperty() != null)
					&& shownProp.getParentProperty().getDisplayName().equals(
							NODE_LABEL_POSITION.getName())) {
				// This is label position cell. Need laeger cell.
				table.setRowHeight(i, 50);
			} else if ((shownProp != null)
					&& shownProp.getDisplayName().equals(GRAPHICAL_MAP_VIEW)) {
				// This is a Continuous Icon cell.
				final Property parent = shownProp.getParentProperty();
				final Object type = ((VizMapperProperty) parent)
						.getHiddenObject();

				if (type instanceof VisualPropertyType) {
					ObjectMapping mapping;

					if (((VisualPropertyType) type).isNodeProp())
						mapping = vmm.getVisualStyle()
								.getNodeAppearanceCalculator().getCalculator(
										((VisualPropertyType) type))
								.getMapping(0);
					else
						mapping = vmm.getVisualStyle()
								.getEdgeAppearanceCalculator().getCalculator(
										((VisualPropertyType) type))
								.getMapping(0);

					if (mapping instanceof ContinuousMapping) {
						table.setRowHeight(i, 80);

						int wi = table.getCellRect(0, 1, true).width;
						final TableCellRenderer cRenderer = editorFactory.getContinuousCellRenderer((VisualPropertyType) type,wi,70); 
						rendReg.registerRenderer(shownProp, cRenderer);
					}
				}
			} else if ((shownProp != null) && (shownProp.getCategory() != null)
					&& shownProp.getCategory().equals(CATEGORY_UNUSED)) {
				empRenderer.setForeground(colorMgr.getColor("UNUSED_COLOR"));
				rendReg.registerRenderer(shownProp, empRenderer);
			}
		}

		repaint();
		visualPropertySheetPanel.repaint();
	}

	private void setAttrComboBox() {
		final List<String> names = new ArrayList<String>();

		// TODO remove the next line too!
		if (targetNetwork == null)
			return;

		CyDataTable attr = /* TODO */targetNetwork.getNodeCyDataTables().get(
				CyNetwork.DEFAULT_ATTRS);

		// TODO remove the next line too!
		if (attr == null)
			return;

		Map<String, Class<?>> cols = attr.getColumnTypeMap();
		names.addAll(cols.keySet());

		Collections.sort(names);

		nodeAttrEditor.setAvailableValues(names.toArray());

		names.clear();

		for (String name : cols.keySet()) {
			Class<?> dataClass = cols.get(name);

			if ((dataClass == Integer.class) || (dataClass == Double.class))
				names.add(name);
		}

		Collections.sort(names);
		nodeNumericalAttrEditor.setAvailableValues(names.toArray());

		names.clear();

		attr = targetNetwork.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS);
		cols = attr.getColumnTypeMap();
		names.addAll(cols.keySet());
		Collections.sort(names);

		edgeAttrEditor.setAvailableValues(names.toArray());

		names.clear();

		for (String name : cols.keySet()) {
			Class<?> dataClass = cols.get(name);

			if ((dataClass == Integer.class) || (dataClass == Double.class))
				names.add(name);
		}

		Collections.sort(names);
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
			final Item item = (Item) visualPropertySheetPanel.getTable()
					.getValueAt(selected, 0);
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

				VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) curProp)
						.getHiddenObject();
				visualPropertySheetPanel.removeProperty(curProp);

				final VizMapperProperty newProp = new VizMapperProperty();
				final VizMapperProperty mapProp = new VizMapperProperty();

				newProp.setDisplayName(type.getName());
				newProp.setHiddenObject(type);
				newProp.setValue("Please select a value!");

				if (type.isNodeProp()) {
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

				expandLastSelectedItem(type.getName());

				visualPropertySheetPanel.getTable().scrollRectToVisible(
						new Rectangle(0, 0, 10, 10));
				visualPropertySheetPanel.repaint();

				return;
			} else if ((e.getClickCount() == 1) && (category == null)) {
				/*
				 * Single left-click
				 */
				VisualPropertyType type = null;

				if ((curProp.getParentProperty() == null)
						&& ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualPropertyType)
					type = (VisualPropertyType) ((VizMapperProperty) curProp)
							.getHiddenObject();
				else if (curProp.getParentProperty() != null)
					type = (VisualPropertyType) ((VizMapperProperty) curProp
							.getParentProperty()).getHiddenObject();
				else

					return;

				final ObjectMapping selectedMapping;
				Calculator calc = null;

				if (type.isNodeProp()) {
					calc = vmm.getVisualStyle().getNodeAppearanceCalculator()
							.getCalculator(type);
				} else {
					calc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
							.getCalculator(type);
				}

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
							((JDialog) editorFactory.showContinuousEditor(this, type))
									.addPropertyChangeListener(this);
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
		setPropertySheetAppearence();

		/*
		 * Clean up sheet
		 */
		for (Property item : visualPropertySheetPanel.getProperties())
			visualPropertySheetPanel.removeProperty(item);

		final NodeAppearanceCalculator nac = vmm.getVisualStyle()
				.getNodeAppearanceCalculator();

		final EdgeAppearanceCalculator eac = vmm.getVisualStyle()
				.getEdgeAppearanceCalculator();

		final List<Calculator> nacList = nac.getCalculators();
		final List<Calculator> eacList = eac.getCalculators();

		editorReg.registerDefaults();

		/*
		 * Add properties to the browser.
		 */
		List<Property> propRecord = new ArrayList<Property>();

		setPropertyFromCalculator(nacList, NODE_VISUAL_MAPPING, propRecord);
		setPropertyFromCalculator(eacList, EDGE_VISUAL_MAPPING, propRecord);

		// Save it for later use.
		propertyMap.put(vmm.getVisualStyle().getName(), propRecord);

		/*
		 * Finally, build unused list
		 */
		setUnused(propRecord);
	}

	/*
	 * Add unused visual properties to the property sheet
	 */
	private void setUnused(List<Property> propList) {
		buildList();
		Collections.sort(unusedVisualPropType);

		for (VisualPropertyType type : unusedVisualPropType) {
			VizMapperProperty prop = new VizMapperProperty();
			prop.setCategory(CATEGORY_UNUSED);
			prop.setDisplayName(type.getName());
			prop.setHiddenObject(type);
			prop.setValue("Double-Click to create...");
			// prop.setEditable(false);
			visualPropertySheetPanel.addProperty(prop);
			propList.add(prop);
		}
	}

	/*
	 * Set value, title, and renderer for each property in the category.
	 */
	private final void setDiscreteProps(VisualPropertyType type,
			Map discMapping, Set<Object> attrKeys, PropertyEditor editor,
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
				System.out.println("------- Map = " + discMapping.getClass()
						+ ", class = " + key.getClass() + ", err = "
						+ e.getMessage());
				System.out.println("------- Key = " + key + ", val = " + val
						+ ", disp = " + strVal);
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
	final void buildProperty(Calculator calc,
			VizMapperProperty calculatorTypeProp, String rootCategory) {
		final VisualPropertyType type = calc.getVisualPropertyType();
		/*
		 * Set one calculator
		 */
		calculatorTypeProp.setCategory(rootCategory);
		// calculatorTypeProp.setType(String.class);
		calculatorTypeProp.setDisplayName(type.getName());
		calculatorTypeProp.setHiddenObject(type);

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

			final CyDataTable attr;
			final Iterator it;
			final int nodeOrEdge;

			if(targetNetwork == null)
				return;
			
			if (calc.getVisualPropertyType().isNodeProp()) {
				attr = targetNetwork.getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				it = targetNetwork.getNodeList().iterator();
				editorReg.registerEditor(calculatorTypeProp, nodeAttrEditor);
				nodeOrEdge = ObjectMapping.NODE_MAPPING;
			} else {
				attr = targetNetwork.getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
				it = targetNetwork.getNodeList().iterator();
				editorReg.registerEditor(calculatorTypeProp, edgeAttrEditor);
				nodeOrEdge = ObjectMapping.EDGE_MAPPING;
			}

			/*
			 * Discrete Mapping
			 */
			if ((firstMap.getClass() == DiscreteMapping.class) && (attrName != null)) {
				final Map discMapping = ((DiscreteMapping) firstMap).getAll();

				// final Set<Object> attrSet = loadKeys(attrName, attr, firstMap, nodeOrEdge);
				final Set<Object> attrSet = new TreeSet<Object>(
						attr.getColumnValues( firstMap.getControllingAttributeName(),
							attr.getColumnTypeMap().get( firstMap .getControllingAttributeName())));

				setDiscreteProps(type, discMapping, attrSet,
							editorFactory.getDiscreteCellEditor(type), 
							editorFactory.getDiscreteCellRenderer(type),
							calculatorTypeProp);
			} else if ((firstMap.getClass() == ContinuousMapping.class) && (attrName != null)) {
				int wi = this.visualPropertySheetPanel.getTable().getCellRect(0,1,true).width;

				VizMapperProperty graphicalView = new VizMapperProperty();
				graphicalView.setDisplayName(GRAPHICAL_MAP_VIEW);
				graphicalView.setName(type.getName());
				graphicalView.setParentProperty(calculatorTypeProp);
				calculatorTypeProp.addSubProperty(graphicalView);

				TableCellRenderer crenderer = editorFactory.getContinuousCellRenderer(type,wi,70);
				rendReg.registerRenderer(graphicalView,crenderer);
			} else if ((firstMap.getClass() == PassThroughMapping.class)
					&& (attrName != null)) {
				// Passthrough
				String id;
				String value;
				VizMapperProperty oneProperty;

				// Accept String only.
				if (attr.getColumnTypeMap().get(attrName) == String.class) {
					while (it.hasNext()) {
						GraphObject go = ((GraphObject) it.next());
						id = go.attrs().get("name", String.class);

						value = go.attrs().get(attrName, String.class);
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

	private void setPropertyFromCalculator(List<Calculator> calcList,
			String rootCategory, List<Property> propRecord) {
		VisualPropertyType type = null;

		for (Calculator calc : calcList) {
			final VizMapperProperty calculatorTypeProp = new VizMapperProperty();
			buildProperty(calc, calculatorTypeProp, rootCategory);

			PropertyEditor editor = editorReg.getEditor(calculatorTypeProp);

			if ((editor == null)
					&& (calculatorTypeProp.getCategory().equals(
							"Unused Properties") == false)) {
				type = (VisualPropertyType) calculatorTypeProp
						.getHiddenObject();

				if (type.isNodeProp()) {
					editorReg
							.registerEditor(calculatorTypeProp, nodeAttrEditor);
				} else {
					editorReg
							.registerEditor(calculatorTypeProp, edgeAttrEditor);
				}
			}

			propRecord.add(calculatorTypeProp);
		}
	}

	/*
	 * private Set<Object> loadKeys(final String attrName, final CyDataTable
	 * attrs, final ObjectMapping mapping, final int nOre) { if
	 * (attrName.equals("ID")) { return loadID(nOre); }
	 * 
	 * Map mapAttrs; mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);
	 * 
	 * if ((mapAttrs == null) || (mapAttrs.size() == 0)) return new
	 * TreeSet<Object>();
	 * 
	 * List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
	 * Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);
	 * 
	 * if ((mapAttrClass == null) || !(acceptedClasses.contains(mapAttrClass)))
	 * return new TreeSet<Object>(); // Return empty set.
	 * 
	 * return loadKeySet(mapAttrs); }
	 */

	/**
	 * Loads the Key Set. private Set<Object> loadKeySet(final Map mapAttrs) {
	 * final Set<Object> mappedKeys = new TreeSet<Object>();
	 * 
	 * final Iterator keyIter = mapAttrs.values().iterator();
	 * 
	 * Object o = null;
	 * 
	 * while (keyIter.hasNext()) { o = keyIter.next();
	 * 
	 * if (o instanceof List) { List list = (List) o;
	 * 
	 * for (int i = 0; i < list.size(); i++) { Object vo = list.get(i);
	 * 
	 * if (!mappedKeys.contains(vo)) mappedKeys.add(vo); } } else { if
	 * (!mappedKeys.contains(o)) mappedKeys.add(o); } }
	 * 
	 * return mappedKeys; }
	 */
	protected void setDefaultViewImagePanel(final Image defImage) {
		if (defImage == null)
			return;

		defaultViewImagePanel.removeAll();

		final JButton defaultImageButton = new JButton();
		defaultImageButton.setUI(new BlueishButtonUI());
		defaultImageButton.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		defaultImageButton.setIcon(new ImageIcon(defImage));
		defaultViewImagePanel.add(defaultImageButton, BorderLayout.CENTER);
		defaultImageButton.addMouseListener(new DefaultMouseListener());
	}

	protected JPanel getDefaultPanel() {
		return defaultViewImagePanel;
	}

	class DefaultMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
				final String targetName = vmm.getVisualStyle().getName();
				final Long focus = vmm.getNetwork().getSUID();

				final DefaultViewPanel panel = (DefaultViewPanel) defAppBldr
						.showDialog(null);
				updateDefaultImage(targetName, (GraphView) panel.getView(),
						defaultViewImagePanel.getSize());
				setDefaultViewImagePanel(defaultImageManager.get(targetName));

				vmm.setNetworkView(targetView);
				vmm.setVisualStyle(targetName);
				//cytoscapeDesktop.setFocus(focus);
//				cytoscapeDesktop.repaint();
			}
		}
	}

	/**
	 * On/Off listeners. This is for performance.
	 * 
	 * @param on
	 *            DOCUMENT ME!
	 */
	public void enableListeners(boolean on) {
		if (on) {
			vmm.addChangeListener(this);
			syncStyleBox();
			ignore = false;
		} else {
			vmm.removeChangeListener(this);
		}
	}

	/**
	 * DOCUMENT ME!
	 */
	public void initializeTableState() {
		propertyMap = new HashMap<String, List<Property>>();
		editorWindowManager = new HashMap<VisualPropertyType, JDialog>();
		defaultImageManager = new HashMap<String, Image>();
	}

	private void manageWindow(final String status, VisualPropertyType vpt, Object source) {
		if (status.equals(EditorFactory.EDITOR_WINDOW_OPENED)) {
			this.editorWindowManager.put(vpt, (JDialog) source);
		} else if (status.equals(EditorFactory.EDITOR_WINDOW_CLOSED)) {
			final VisualPropertyType type = vpt;

			/*
			 * Update icon
			 */
			final Property[] props = visualPropertySheetPanel.getProperties();
			VizMapperProperty vprop = null;

			for (Property prop : props) {
				vprop = (VizMapperProperty) prop;

				if ((vprop.getHiddenObject() != null)
						&& (type == vprop.getHiddenObject())) {
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

			final int width = visualPropertySheetPanel.getTable().getCellRect(
					0, 1, true).width;

			final TableCellRenderer cRenderer = editorFactory.getContinuousCellRenderer(type,width,70); 
			rendReg.registerRenderer(vprop, cRenderer);
			visualPropertySheetPanel.getTable().repaint();
		}
	}

	private void closeEditorWindow() {
		Set<VisualPropertyType> typeSet = editorWindowManager.keySet();
		Set<VisualPropertyType> keySet = new HashSet<VisualPropertyType>();

		for (VisualPropertyType vpt : typeSet) {
			JDialog window = editorWindowManager.get(vpt);
			manageWindow(EditorFactory.EDITOR_WINDOW_CLOSED, vpt, null);
			window.dispose();
			keySet.add(vpt);
		}

		for (VisualPropertyType type : keySet)
			editorWindowManager.remove(type);
	}

	/**
	 * Handle propeaty change events.
	 * 
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
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
		if (e.getPropertyName().equals(EditorFactory.EDITOR_WINDOW_OPENED)
				|| e.getPropertyName().equals(EditorFactory.EDITOR_WINDOW_CLOSED)) {
			manageWindow(e.getPropertyName(), (VisualPropertyType) e .getNewValue(), e.getSource());

			if (e.getPropertyName().equals(EditorFactory.EDITOR_WINDOW_CLOSED))
				editorWindowManager.remove((VisualPropertyType) e.getNewValue());

			return;
		}

		/*
		 * Got global event
		 */

		// System.out.println("==================GLOBAL Signal: " +
		// e.getPropertyName() + ", SRC = " + e.getSource().toString());
		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			String vmName = vmm.getVisualStyle().getName();
			setDefaultViewImagePanel(defaultImageManager.get(vmName));
			vsNameComboBox.setSelectedItem(vmName);
			vmm.setVisualStyle(vmName);
			setPropertyTable();
			visualPropertySheetPanel.setSorting(true);

			return;
		} else if (e.getPropertyName().equals(Cytoscape.SESSION_LOADED)
				|| e.getPropertyName().equals(Cytoscape.VIZMAP_LOADED)) {
			final String vsName = vmm.getVisualStyle().getName();
			System.out.println("got VIZMAP_LOADED");

			lastVSName = null;
			initVizmapperGUI();
			switchVS(vsName);
			vsNameComboBox.setSelectedItem(vsName);
			vmm.setVisualStyle(vsName);

			return;
		} else if (e.getPropertyName().equals(
				CySwingApplication.NETWORK_VIEW_FOCUS)
				&& (e.getSource().getClass() == NetworkPanel.class)) {
			final VisualStyle vs = vmm.getVisualStyleForView(vmm
					.getNetworkView());

			if (vs != null) {
				vmm.setNetworkView(targetView);

				if (vs.getName().equals(vsNameComboBox.getSelectedItem())) {
					Cytoscape.redrawGraph(targetView);
				} else {
					switchVS(vs.getName(), false);
					vsNameComboBox.setSelectedItem(vs.getName());
					setDefaultViewImagePanel(this.defaultImageManager.get(vs
							.getName()));
				}
			}

			targetNetwork = Cytoscape.getNetwork((Long) (e.getNewValue()));
			targetView = Cytoscape.getNetworkView((Long) (e.getNewValue()));

			return;
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

		Item selectedItem = (Item) visualPropertySheetPanel.getTable()
				.getValueAt(selected, 0);
		VizMapperProperty prop = (VizMapperProperty) selectedItem.getProperty();

		VisualPropertyType type = null;
		String ctrAttrName = null;

		VizMapperProperty typeRootProp = null;

		if ((prop.getParentProperty() == null)
				&& e.getNewValue() instanceof String) {
			/*
			 * This is a controlling attr name change signal.
			 */
			typeRootProp = (VizMapperProperty) prop;
			type = (VisualPropertyType) ((VizMapperProperty) prop)
					.getHiddenObject();
			ctrAttrName = (String) e.getNewValue();
		} else if ((prop.getParentProperty() == null)
				&& (e.getNewValue() == null)) {
			/*
			 * Empty cell selected. no need to change anything.
			 */
			return;
		} else {
			typeRootProp = (VizMapperProperty) prop.getParentProperty();

			if (prop.getParentProperty() == null)
				return;

			type = (VisualPropertyType) ((VizMapperProperty) prop
					.getParentProperty()).getHiddenObject();
		}

		/*
		 * Mapping type changed
		 */
		if (prop.getHiddenObject() instanceof ObjectMapping
				|| prop.getDisplayName().equals("Mapping Type")) {
			System.out.println("Mapping type changed: "
					+ prop.getHiddenObject());

			if (e.getNewValue() == null)
				return;

			/*
			 * If invalid data type, ignore.
			 */
			final Object parentValue = prop.getParentProperty().getValue();

			if (parentValue != null) {
				ctrAttrName = parentValue.toString();

				CyDataTable attr;

				if (type.isNodeProp()) {
					attr = targetNetwork.getNodeCyDataTables().get(
							CyNetwork.DEFAULT_ATTRS);
				} else {
					attr = targetNetwork.getEdgeCyDataTables().get(
							CyNetwork.DEFAULT_ATTRS);
				}

				final Class<?> dataClass = attr.getColumnTypeMap().get(
						ctrAttrName);

				if (e.getNewValue().equals("Continuous Mapper")
						&& ((dataClass != Integer.class) && (dataClass != Double.class))) {
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

			switchMapping(prop, e.getNewValue().toString(), prop
					.getParentProperty().getValue());

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

		if (type.isNodeProp()) {
			curCalc = vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type);
		} else {
			curCalc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type);
		}

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
			final CyDataTable attrForTest;

			if (type.isNodeProp()) {
				attrForTest = targetNetwork.getNodeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
			} else {
				attrForTest = targetNetwork.getEdgeCyDataTables().get(
						CyNetwork.DEFAULT_ATTRS);
			}

			final Class<?> dataType = attrForTest.getColumnTypeMap().get(
					ctrAttrName);

			// This part is for Continuous Mapping.
			if (mapping instanceof ContinuousMapping) {
				if ((dataType == Double.class) || (dataType == Integer.class)) {
					// Do nothing
				} else {
					JOptionPane
							.showMessageDialog(
									this,
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
				final String newMappingName = curCalc.toString() + "-"
						+ ctrAttrName;
				final Map saved = discMapBuffer.get(newMappingName);

				if (saved == null) {
					discMapBuffer.put(curMappingName,
							((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, vmm
							.getNetwork(), false);
				} else if (saved != null) {
					// Mapping exists
					discMapBuffer.put(curMappingName,
							((DiscreteMapping) mapping).getAll());
					mapping.setControllingAttributeName(ctrAttrName, vmm
							.getNetwork(), false);
					((DiscreteMapping) mapping).putAll(saved);
				}
			} else {
				mapping.setControllingAttributeName(ctrAttrName, vmm
						.getNetwork(), false);
			}

			visualPropertySheetPanel.removeProperty(typeRootProp);

			final VizMapperProperty newRootProp = new VizMapperProperty();

			if (type.isNodeProp())
				buildProperty(vmm.getVisualStyle()
						.getNodeAppearanceCalculator().getCalculator(type),
						newRootProp, NODE_VISUAL_MAPPING);
			else
				buildProperty(vmm.getVisualStyle()
						.getEdgeAppearanceCalculator().getCalculator(type),
						newRootProp, EDGE_VISUAL_MAPPING);

			removeProperty(typeRootProp);

			if (propertyMap.get(vmm.getVisualStyle().getName()) != null)
				propertyMap.get(vmm.getVisualStyle().getName())
						.add(newRootProp);

			typeRootProp = null;

			expandLastSelectedItem(type.getName());
			updateTableView();

			// Finally, update graph view and focus.
			vmm.setNetworkView(targetView);
			Cytoscape.redrawGraph(targetView);

			return;
		}

		// Return if not a Discrete Mapping.
		if (mapping instanceof ContinuousMapping
				|| mapping instanceof PassThroughMapping)
			return;

		Object key = null;

		if ((type.getDataType() == Number.class)
				|| (type.getDataType() == String.class)) {
			key = e.getOldValue();

// TODO WTF?
//			if (type.getDataType() == Number.class) {
//				numberCellEditor = new CyDoublePropertyEditor(this);
//				numberCellEditor.addPropertyChangeListener(this);
//				editorReg.registerEditor(prop, numberCellEditor);
//			}
		} else {
			key = ((Item) visualPropertySheetPanel.getTable().getValueAt(
					selected, 0)).getProperty().getDisplayName();
		}

		/*
		 * Need to convert this string to proper data types.
		 */
		final CyDataTable attr;
		ctrAttrName = mapping.getControllingAttributeName();

		if (type.isNodeProp()) {
			attr = targetNetwork.getNodeCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS);
		} else {
			attr = targetNetwork.getEdgeCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS);
		}

		// Byte attrType = attr.getType(ctrAttrName);
		Class<?> attrType = attr.getColumnTypeMap().get(ctrAttrName);

		if (attrType == Boolean.class)
			key = Boolean.valueOf((String) key);
		else if (attrType == Integer.class)
			key = Integer.valueOf((String) key);
		else if (attrType == Double.class)
			key = Double.valueOf((String) key);

		Object newValue = e.getNewValue();

		if (type.getDataType() == Number.class) {
			if ((((Number) newValue).doubleValue() == 0)
					|| (newValue instanceof Number
							&& type.toString().endsWith("OPACITY") && (((Number) newValue)
							.doubleValue() > 255))) {
				int shownPropCount = table.getRowCount();
				Property p = null;
				Object val = null;

				for (int i = 0; i < shownPropCount; i++) {
					p = ((Item) table.getValueAt(i, 0)).getProperty();

					if (p != null) {
						val = p.getDisplayName();

						if ((val != null) && val.equals(key.toString())) {
							p.setValue(((DiscreteMapping) mapping)
									.getMapValue(key));

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
		vmm.setNetworkView(targetView);
		Cytoscape.redrawGraph(targetView);
	}

	/**
	 * Switching between mapppings. Each calcs has 3 mappings. The first one
	 * (getMapping(0)) is the current mapping used by calculator.
	 * 
	 */
	private void switchMapping(VizMapperProperty prop, String newMapName,
			Object attrName) {
		if (attrName == null) {
			return;
		}

		final VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) prop
				.getParentProperty()).getHiddenObject();
		final String newCalcName = vmm.getVisualStyle().getName() + "-"
				+ type.getName() + "-" + newMapName;

		// Extract target calculator
		Calculator newCalc = vmm.getCalculatorCatalog().getCalculator(type,
				newCalcName);

		Calculator oldCalc = null;

		if (type.isNodeProp())
			oldCalc = vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type);
		else
			oldCalc = vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type);

		/*
		 * If not exist, create new one.
		 */
		if (newCalc == null) {
			newCalc = getNewCalculator(type, newMapName, newCalcName);
			newCalc.getMapping(0).setControllingAttributeName(
					(String) attrName, null, true);
			vmm.getCalculatorCatalog().addCalculator(newCalc);
		}

		newCalc.getMapping(0).setControllingAttributeName((String) attrName,
				null, true);

		if (type.isNodeProp()) {
			vmm.getVisualStyle().getNodeAppearanceCalculator().setCalculator(
					newCalc);
		} else
			vmm.getVisualStyle().getEdgeAppearanceCalculator().setCalculator(
					newCalc);

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

			final String oldCalcName = type.getName() + "-"
					+ oldMappingTypeName;

			if (vmm.getCalculatorCatalog().getCalculator(type, oldCalcName) == null) {
				final Calculator newC = getNewCalculator(type,
						oldMappingTypeName, oldCalcName);
				newC.getMapping(0).setControllingAttributeName(
						(String) attrName, null, false);
				vmm.getCalculatorCatalog().addCalculator(newC);
			}
		}

		Property parent = prop.getParentProperty();
		visualPropertySheetPanel.removeProperty(parent);

		final VizMapperProperty newRootProp = new VizMapperProperty();

		if (type.isNodeProp())
			buildProperty(vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type), newRootProp, NODE_VISUAL_MAPPING);
		else
			buildProperty(vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type), newRootProp, EDGE_VISUAL_MAPPING);

		expandLastSelectedItem(type.getName());

		removeProperty(parent);

		if (propertyMap.get(vmm.getVisualStyle().getName()) != null) {
			propertyMap.get(vmm.getVisualStyle().getName()).add(newRootProp);
		}

		// vmm.getNetworkView().redrawGraph(false, true);
		Cytoscape.redrawGraph(targetView);
		parent = null;
	}

	void expandLastSelectedItem(String name) {
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

	private Calculator getNewCalculator(final VisualPropertyType type,
			final String newMappingName, final String newCalcName) {
		System.out.println("Mapper = " + newMappingName);

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

		final Object defaultObj = type.getDefault(vmm.getVisualStyle());

		System.out.println("defobj = " + defaultObj.getClass() + ", Type = "
				+ type.getName());

		final Object[] invokeArgs = { defaultObj, new Byte(mapType) };
		ObjectMapping mapper = null;

		try {
			mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
		} catch (Exception exc) {
			System.err.println("Error creating mapping");

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

	private void buildList() {
		unusedVisualPropType = new ArrayList<VisualPropertyType>();

		final VisualStyle vs = vmm.getVisualStyle();
		final NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
		final EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();

		ObjectMapping mapping = null;

		for (VisualPropertyType type : VisualPropertyType.values()) {
			Calculator calc = nac.getCalculator(type);

			if (calc == null) {
				calc = eac.getCalculator(type);

				if (calc != null)
					mapping = calc.getMapping(0);
			} else
				mapping = calc.getMapping(0);

			if ((mapping == null) && type.isAllowed())
				unusedVisualPropType.add(type);

			mapping = null;
		}
	}



	protected void removeMapping(final VisualPropertyType type) {
		if (type.isNodeProp()) {
			vmm.getVisualStyle().getNodeAppearanceCalculator()
					.removeCalculator(type);
		} else {
			vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.removeCalculator(type);
		}

		Cytoscape.redrawGraph(targetView);

		final Property[] props = visualPropertySheetPanel.getProperties();
		Property toBeRemoved = null;

		for (Property p : props) {
			if (p.getDisplayName().equals(type.getName())) {
				toBeRemoved = p;

				break;
			}
		}

		visualPropertySheetPanel.removeProperty(toBeRemoved);

		removeProperty(toBeRemoved);

		/*
		 * Finally, move the visual property to "unused list"
		 */
		unusedVisualPropType.add(type);

		VizMapperProperty prop = new VizMapperProperty();
		prop.setCategory(CATEGORY_UNUSED);
		prop.setDisplayName(type.getName());
		prop.setHiddenObject(type);
		prop.setValue("Double-Click to create...");
		visualPropertySheetPanel.addProperty(prop);

		if (propertyMap.get(vmm.getVisualStyle().getName()) != null) {
			propertyMap.get(vmm.getVisualStyle().getName()).add(prop);
		}

		visualPropertySheetPanel.repaint();
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
		item = (Item) visualPropertySheetPanel.getTable().getValueAt(
				selected[0], 0);

		VizMapperProperty prop = (VizMapperProperty) item.getProperty();

		if ((prop == null) || (prop.getParentProperty() == null)) {
			return;
		}

		final VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) prop
				.getParentProperty()).getHiddenObject();

		/*
		 * Extract calculator
		 */
		final ObjectMapping mapping;
		final CyDataTable attr;

		if (type.isNodeProp()) {
			mapping = vmm.getVisualStyle().getNodeAppearanceCalculator()
					.getCalculator(type).getMapping(0);
			attr = targetNetwork.getNodeCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS);
		} else {
			mapping = vmm.getVisualStyle().getEdgeAppearanceCalculator()
					.getCalculator(type).getMapping(0);
			attr = targetNetwork.getEdgeCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS);
		}

		if (mapping instanceof ContinuousMapping
				|| mapping instanceof PassThroughMapping)
			return;

		Object newValue = null;

		try {
			newValue = editorFactory.showDiscreteEditor(this, type);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (newValue == null)
			return;

		Object key = null;
		final Class<?> keyClass = attr.getColumnTypeMap().get(
				mapping.getControllingAttributeName());

		for (int i = 0; i < selected.length; i++) {
			/*
			 * First, update property sheet
			 */
			((Item) visualPropertySheetPanel.getTable().getValueAt(selected[i],
					0)).getProperty().setValue(newValue);
			/*
			 * Then update backend.
			 */
			key = ((Item) visualPropertySheetPanel.getTable().getValueAt(
					selected[i], 0)).getProperty().getDisplayName();

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
		vmm.setNetworkView(targetView);
		Cytoscape.redrawGraph(targetView);
	}

	/*
	 * Remove an entry in the browser.
	 */
	protected void removeProperty(final Property prop) {
		List<Property> targets = new ArrayList<Property>();

		if (propertyMap.get(vmm.getVisualStyle().getName()) == null) {
			return;
		}

		for (Property p : propertyMap.get(vmm.getVisualStyle().getName())) {
			if (p.getDisplayName().equals(prop.getDisplayName())) {
				targets.add(p);
			}
		}

		for (Property p : targets) {
			System.out.println("Removed: " + p.getDisplayName());
			propertyMap.get(vmm.getVisualStyle().getName()).remove(p);
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
//		rainbow1.setEnabled(false);
//		rainbow2.setEnabled(false);
//		randomize.setEnabled(false);
//		series.setEnabled(false);
//		fit.setEnabled(false);
//		brighter.setEnabled(false);
//		darker.setEnabled(false);
//		delete.setEnabled(false);
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

		final int selected = visualPropertySheetPanel.getTable()
				.getSelectedRow();

		if (0 > selected) {
			return;
		}

		final Item item = (Item) visualPropertySheetPanel.getTable()
				.getValueAt(selected, 0);
		final Property curProp = item.getProperty();

		if (curProp == null)
			return;

		VizMapperProperty prop = ((VizMapperProperty) curProp);

		if (prop.getHiddenObject() instanceof VisualPropertyType
				&& (prop.getDisplayName().contains("Mapping Type") == false)
				&& (prop.getValue() != null)
				&& (prop.getValue().toString().startsWith("Please select") == false)) {
			// Enble delete menu
			//delete.setEnabled(true);

			Property[] children = prop.getSubProperties();

			for (Property p : children) {
				if ((p.getDisplayName() != null)
						&& p.getDisplayName().contains("Mapping Type")) {
					if ((p.getValue() == null)
							|| (p.getValue().equals("Discrete Mapping") == false)) {
						return;
					}
				}
			}

			VisualPropertyType type = ((VisualPropertyType) prop
					.getHiddenObject());

			Class dataType = type.getDataType();

//			if (dataType == Color.class) {
//				rainbow1.setEnabled(true);
//				rainbow2.setEnabled(true);
//				randomize.setEnabled(true);
//				brighter.setEnabled(true);
//				darker.setEnabled(true);
//			} else if (dataType == Number.class) {
//				randomize.setEnabled(true);
//				series.setEnabled(true);
//			}
//
//			if ((type == VisualPropertyType.NODE_WIDTH)
//					|| (type == VisualPropertyType.NODE_HEIGHT)) {
//				fit.setEnabled(true);
//			}
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

		if (nOre == ObjectMapping.NODE_MAPPING) {
			obj = targetView.getGraphPerspective().getNodeList();
		} else {
			obj = targetView.getGraphPerspective().getEdgeList();
		}

		for (GraphObject o : obj) {
			ids.add(o.attrs().get("name", String.class));
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
		final String selectedName = (String) vsNameComboBox.getSelectedItem();
		final String currentName = vmm.getVisualStyle().getName();

		final GraphView curView = targetView;

		if (ignore)
			return;

		System.out.println("Got VMM Change event.  Cur VS in VMM: "
				+ vmm.getVisualStyle().getName());

		if ((selectedName == null) || (currentName == null)
				|| (curView == null)
				|| curView.equals(Cytoscape.getNullNetworkView()))
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
		// Make fure we update the lastVSName based on anything that changes the
		// visual style:
		lastVSName = currentName;

		// MLC 03/31/08 END.
	}

	private void syncStyleBox() {
		String curStyleName = vmm.getVisualStyle().getName();

		String styleName;
		List<String> namesInBox = new ArrayList<String>();
		namesInBox.addAll(vmm.getCalculatorCatalog().getVisualStyleNames());

		for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
			styleName = vsNameComboBox.getItemAt(i).toString();

			if (vmm.getCalculatorCatalog().getVisualStyle(styleName) == null) {
				// No longer exists in the VMM. Remove.
				vsNameComboBox.removeItem(styleName);
				defaultImageManager.remove(styleName);
				propertyMap.remove(styleName);
			}
		}

		Collections.sort(namesInBox);

		// Reset combobox items.
		vsNameComboBox.removeAllItems();

		for (String name : namesInBox)
			vsNameComboBox.addItem(name);

		// Bug fix: 0001721:
		// Note: Because vsNameComboBox.removeAllItems() will fire unwanted
		// event,
		// vmm.getVisualStyle().getName() will not be the same as curStyleName
		if ((curStyleName == null) || curStyleName.trim().equals(""))
			switchVS(vmm.getVisualStyle().getName());
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

	// **************************************************************************
	// MultiHashMapListenerAdaptor
	private class AttrEventListener implements ColumnDeletedListener,
			RowSetListener {
		// ref to members
		private final JPanel container;
		private final CyDataTable attr;
		private final CyComboBoxPropertyEditor attrEditor;
		private final CyComboBoxPropertyEditor numericalAttrEditor;
		private final List<String> attrEditorNames;
		private final List<String> numericalAttrEditorNames;

		/**
		 * Constructor.
		 * 
		 * @param cyAttributes
		 *            CyDataTable
		 */
		AttrEventListener(JPanel container, CyDataTable cyAttributes,
				CyComboBoxPropertyEditor attrEditor,
				CyComboBoxPropertyEditor numericalAttrEditor) {
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
		 * Our implementation of MultiHashMapListener.attributeValueAssigned().
		 * 
		 * @param objectKey
		 *            String
		 * @param attributeName
		 *            String
		 * @param keyIntoValue
		 *            Object[]
		 * @param oldAttributeValue
		 *            Object
		 * @param newAttributeValue
		 *            Object
		 */
		public void handleEvent(RowSetEvent e) {
			CyRow row = e.getSource();
			String attributeName = e.getColumnName();

			// we do not process network attributes
			if (attr == targetNetwork.getNetworkCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS))
				return;

			// conditional repaint container
			boolean repaint = false;

			// this code gets called a lot
			// so i've decided to keep the next two if statements as is,
			// rather than create a shared general routine to call

			// if attribute is not in attrEditorNames, add it if we support its
			// type
			if (!attrEditorNames.contains(attributeName)) {
				attrEditorNames.add(attributeName);
				Collections.sort(attrEditorNames);
				attrEditor.setAvailableValues(attrEditorNames.toArray());
				repaint = true;
			}

			// if attribute is not contained in numericalAttrEditorNames, add it
			// if we support its class
			if (!numericalAttrEditorNames.contains(attributeName)) {
				Class<?> dataClass = attr.getColumnTypeMap().get(attributeName);

				if ((dataClass == Integer.class) || (dataClass == Double.class)) {
					numericalAttrEditorNames.add(attributeName);
					Collections.sort(numericalAttrEditorNames);
					numericalAttrEditor
							.setAvailableValues(numericalAttrEditorNames
									.toArray());
					repaint = true;
				}
			}

			if (repaint)
				container.repaint();
		}

		/**
		 * Our implementation of
		 * MultiHashMapListener.allAttributeValuesRemoved()
		 * 
		 * @param objectKey
		 *            String
		 * @param attributeName
		 *            String
		 */
		public void handleEvent(ColumnDeletedEvent e) {
			String attributeName = e.getColumnName();

			// we do not process network attributes
			if (attr == targetNetwork.getNetworkCyDataTables().get(
					CyNetwork.DEFAULT_ATTRS))
				return;

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
				numericalAttrEditor.setAvailableValues(numericalAttrEditorNames
						.toArray());
				repaint = true;
			}

			if (repaint)
				container.repaint();
		}

		/**
		 * Method to populate attrEditorNames & numericalAttrEditorNames on
		 * object instantiation.
		 */
		private void populateLists() {
			// get attribute names & sort

			// populate attrEditorNames & numericalAttrEditorNames
			// TODO - this is bad and is only hear to get things working
			// initially
			if (attr == null)
				return;

			List<String> names = new ArrayList<String>(attr.getColumnTypeMap()
					.keySet());
			Collections.sort(names);
			attrEditorNames.add("ID");

			byte type;
			Class<?> dataClass;

			for (String name : names) {
				attrEditorNames.add(name);
				dataClass = attr.getColumnTypeMap().get(name);

				if ((dataClass == Integer.class) || (dataClass == Double.class)) {
					numericalAttrEditorNames.add(name);
				}
			}
		}
	}
}
