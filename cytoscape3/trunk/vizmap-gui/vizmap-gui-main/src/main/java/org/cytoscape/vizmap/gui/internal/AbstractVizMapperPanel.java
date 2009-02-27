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
package org.cytoscape.vizmap.gui.internal;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.io.util.StreamUtil;
import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.LineStyle;
import org.cytoscape.vizmap.NodeShape;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.gui.DefaultViewEditor;
import org.cytoscape.vizmap.gui.action.VizMapUIAction;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.event.VizMapEventHandlerManager;
import org.cytoscape.vizmap.gui.internal.util.VizMapperUtil;
import org.cytoscape.vizmap.gui.theme.ColorManager;
import org.cytoscape.vizmap.gui.theme.IconManager;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.CyNetworkManager;
import cytoscape.CyOperatingContext;
import cytoscape.util.FileUtil;
import cytoscape.util.swing.DropDownMenuButton;
import cytoscape.view.CySwingApplication;

/**
 * Skeleton of the VizMapper Main panel GUI. This class includes methods setting
 * up GUI components. Actual logics are in the VizMapperMainPanel
 */
public abstract class AbstractVizMapperPanel extends JPanel {
	public static final String CATEGORY_UNUSED = "Unused Properties";
	public static final String GRAPHICAL_MAP_VIEW = "Graphical View";
	public static final String NODE_VISUAL_MAPPING = "Node Visual Mapping";
	public static final String EDGE_VISUAL_MAPPING = "Edge Visual Mapping";

	/*
	 * Resources which will be injected through DI Container
	 */
	
	// Listeners for attribute-related events
	protected AttributeEventsListener nodeAttrListener;
	protected AttributeEventsListener edgeAttrListener;
	protected AttributeEventsListener networkAttrListener;
	
	@Resource
	protected CySwingApplication cytoscapeDesktop;
	@Resource
	protected DefaultViewEditor defViewEditor;
	@Resource
	protected VisualMappingManager vmm;

	@Resource
	protected ColorManager colorMgr;
	@Resource
	protected IconManager iconMgr;
	@Resource
	protected VizMapperMenuManager menuMgr;
	@Resource
	protected EditorFactory editorFactory;
	@Resource
	protected VizMapperUtil vizMapperUtil;

	// Action (context menu) manager
	@Resource
	protected Set<VizMapUIAction> actionList;
	
	protected CyOperatingContext context;
	
	protected StreamUtil streamUtil;

	/*
	 * Combo Box Editors
	 */
	protected PropertyEditor nodeAttrEditor;
	protected PropertyEditor edgeAttrEditor;
	protected PropertyEditor nodeNumericalAttrEditor;
	protected PropertyEditor edgeNumericalAttrEditor;
	protected PropertyEditor mappingTypeEditor;

	@Resource
	protected PropertySheetPanel propertySheetPanel;

	VizMapPropertySheetBuilder vizMapPropertySheetBuilder;

	protected Map<String, Image> defaultImageManager;

	@Resource
	protected DefaultTableCellRenderer emptyBoxRenderer;
	@Resource
	protected DefaultTableCellRenderer filledBoxRenderer;

	protected static final Map<Object, Icon> nodeShapeIcons = NodeShape
			.getIconSet();
	protected static final Map<Object, Icon> arrowShapeIcons = ArrowShape
			.getIconSet();
	protected static final Map<Object, Icon> lineTypeIcons = LineStyle
			.getIconSet();

	@Resource
	protected PropertyRendererRegistry rendReg;
	@Resource
	protected PropertyEditorRegistry editorReg;

	protected VizMapEventHandlerManager vizMapEventHandlerManager;

	protected EditorWindowManager editorWindowManager;

	protected CyNetworkManager cyNetworkManager;

	protected FileUtil fileUtil;

	protected SwingPropertyChangeSupport spcs;
	protected static final long serialVersionUID = -6839011300709287662L;

	public AbstractVizMapperPanel(CySwingApplication desktop,
			DefaultViewEditor defViewEditor, IconManager iconMgr,
			ColorManager colorMgr, VisualMappingManager vmm,
			VizMapperMenuManager menuMgr, EditorFactory editorFactory,
			PropertySheetPanel propertySheetPanel,
			VizMapPropertySheetBuilder vizMapPropertySheetBuilder,
			VizMapEventHandlerManager vizMapEventHandlerManager,
			EditorWindowManager editorWindowManager, CyNetworkManager cyNetworkManager,
			FileUtil fileUtil, CyOperatingContext context, StreamUtil streamUtil) {

		this.cytoscapeDesktop = desktop;
		this.defViewEditor = defViewEditor;
		this.iconMgr = iconMgr;
		this.colorMgr = colorMgr;
		this.vmm = vmm;
		this.menuMgr = menuMgr;
		this.editorFactory = editorFactory;
		this.propertySheetPanel = propertySheetPanel;
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		this.vizMapEventHandlerManager = vizMapEventHandlerManager;
		this.editorWindowManager = editorWindowManager;
		this.cyNetworkManager = cyNetworkManager;
		this.fileUtil = fileUtil;
		this.context = context;
		this.streamUtil = streamUtil;
		spcs = new SwingPropertyChangeSupport(this);

		defaultImageManager = new HashMap<String, Image>();

		initComponents();
		initDefaultEditors();
	}

	private void initDefaultEditors() {
		nodeAttrEditor = editorFactory
				.getDefaultComboBoxEditor("nodeAttrEditor");
		edgeAttrEditor = editorFactory
				.getDefaultComboBoxEditor("edgeAttrEditor");
		nodeNumericalAttrEditor = editorFactory
				.getDefaultComboBoxEditor("nodeNumericalAttrEditor");
		edgeNumericalAttrEditor = editorFactory
				.getDefaultComboBoxEditor("edgeNumericalAttrEditor");
		mappingTypeEditor = editorFactory
				.getDefaultComboBoxEditor("mappingTypeEditor");
	}

	private void initComponents() {
		mainSplitPane = new javax.swing.JSplitPane();
		listSplitPane = new javax.swing.JSplitPane();

		bottomPanel = new javax.swing.JPanel();

		defaultViewImagePanel = new javax.swing.JPanel();
		propertySheetPanel.setTable(new PropertySheetTable());

		vsSelectPanel = new javax.swing.JPanel();

		buttonPanel = new javax.swing.JPanel();

		setVsNameComboBox(new javax.swing.JComboBox());

		optionButton = new DropDownMenuButton(new AbstractAction() {
			private final static long serialVersionUID = 1213748836776579L;

			public void actionPerformed(ActionEvent ae) {
				DropDownMenuButton b = (DropDownMenuButton) ae.getSource();
				menuMgr.getMainMenu().show(b, 0, b.getHeight());
			}
		});

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

		GroupLayout bottomPanelLayout = new GroupLayout(bottomPanel);
		bottomPanel.setLayout(bottomPanelLayout);
		bottomPanelLayout.setHorizontalGroup(bottomPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						noMapListScrollPane, GroupLayout.DEFAULT_SIZE, 272,
						Short.MAX_VALUE).add(buttonPanel,
						GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));
		bottomPanelLayout.setVerticalGroup(bottomPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						bottomPanelLayout.createSequentialGroup().add(
								buttonPanel, GroupLayout.PREFERRED_SIZE, 25,
								GroupLayout.PREFERRED_SIZE).add(
								noMapListScrollPane, GroupLayout.DEFAULT_SIZE,
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

		propertySheetPanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder(null, "Visual Mapping Browser",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12),
						java.awt.Color.darkGray));

		mainSplitPane.setRightComponent(propertySheetPanel);

		vsSelectPanel
				.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
						"Current Visual Style",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION,
						new java.awt.Font("SansSerif", 1, 12),
						java.awt.Color.darkGray));

		optionButton.setToolTipText("Options...");
		optionButton.setIcon(iconMgr.getIcon("optionIcon"));
		optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		optionButton.setComponentPopupMenu(menuMgr.getMainMenu());

		GroupLayout vsSelectPanelLayout = new GroupLayout(vsSelectPanel);
		vsSelectPanel.setLayout(vsSelectPanelLayout);
		vsSelectPanelLayout.setHorizontalGroup(vsSelectPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						vsSelectPanelLayout.createSequentialGroup()
								.addContainerGap().add(getVsNameComboBox(), 0,
										146, Short.MAX_VALUE).addPreferredGap(
										LayoutStyle.RELATED).add(optionButton,
										GroupLayout.PREFERRED_SIZE, 64,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap()));
		vsSelectPanelLayout.setVerticalGroup(vsSelectPanelLayout
				.createParallelGroup(GroupLayout.LEADING).add(
						vsSelectPanelLayout.createSequentialGroup().add(
								vsSelectPanelLayout.createParallelGroup(
										GroupLayout.BASELINE).add(
										getVsNameComboBox(),
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE).add(
										optionButton)) // .addContainerGap(
				// GroupLayout.DEFAULT_SIZE,
				// Short.MAX_VALUE)
				));

		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.LEADING).add(vsSelectPanel,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE).add(mainSplitPane, GroupLayout.DEFAULT_SIZE,
				280, Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout.createParallelGroup(
						GroupLayout.LEADING)
						.add(
								layout.createSequentialGroup().add(
										vsSelectPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(LayoutStyle.RELATED)
										.add(mainSplitPane,
												GroupLayout.DEFAULT_SIZE, 510,
												Short.MAX_VALUE)));
	} // </editor-fold>

	// Variables declaration - do not modify
	protected JPanel defaultViewImagePanel;
	protected JSplitPane mainSplitPane;
	protected JSplitPane listSplitPane;
	protected DropDownMenuButton optionButton;
	private JComboBox vsNameComboBox;
	protected JPanel vsSelectPanel;
	protected JScrollPane noMapListScrollPane;
	protected JPanel buttonPanel;
	protected JButton addButton;
	protected JPanel bottomPanel;

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public PropertyChangeSupport getPropertyChangeSupport() {
		return this.spcs;
	}

	public Map<String, Image> getDefaultImageManager() {
		return this.defaultImageManager;
	}

	/**
	 * Setup menu items.<br>
	 * 
	 * This includes both icon menu and right-click menu.
	 * 
	 */
	private void setMenu() {
		// final Font italicMenu = new Font("SansSerif", Font.ITALIC, 14);
		// rainbow1 = new JMenuItem("Rainbow 1");
		// rainbow2 = new JMenuItem("Rainbow 2 (w/modulations)");
		// randomize = new JMenuItem("Randomize");
		// rainbow1.setFont(italicMenu);
		// rainbow2.setFont(italicMenu);
		//
		// series = new JMenuItem("Series (Number Only)");
		// fit = new JMenuItem("Fit Node Width to Label");
		//
		// brighter = new JMenuItem("Brighter");
		// darker = new JMenuItem("Darker");
		//
		// editAll = new JMenuItem("Edit selected values at once...");
		//
		// delete.setIcon(iconMgr.getIcon("delIcon"));
		// editAll.setIcon(iconMgr.getIcon("editIcon"));
		//
		// rainbow1.addActionListener(new GenerateValueListener(
		// GenerateValueListener.RAINBOW1));
		// rainbow2.addActionListener(new GenerateValueListener(
		// GenerateValueListener.RAINBOW2));
		// randomize.addActionListener(new GenerateValueListener(
		// GenerateValueListener.RANDOM));
		//
		// series.addActionListener(new GenerateSeriesListener());
		// fit.addActionListener(new FitLabelListener());
		//
		// brighter.addActionListener(new BrightnessListener(
		// BrightnessListener.BRIGHTER));
		// darker.addActionListener(new BrightnessListener(
		// BrightnessListener.DARKER));
		//
		// delete.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// removeMapping();
		// }
		// });
		// editAll.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent arg0) {
		// editSelectedCells();
		// }
		// });
		// add.addActionListener(l)
		// select.setIcon(vmIcon);

		// generateValues.add(rainbow1);
		// generateValues.add(rainbow2);
		// generateValues.add(randomize);
		// generateValues.add(series);
		// generateValues.add(fit);

		// modifyValues.add(brighter);
		// modifyValues.add(darker);
		//
		// rainbow1.setEnabled(false);
		// rainbow2.setEnabled(false);
		// randomize.setEnabled(false);
		// series.setEnabled(false);
		// fit.setEnabled(false);
		//
		// brighter.setEnabled(false);
		// darker.setEnabled(false);
		//
		// delete.setEnabled(false);
		// menuMgr.getContextMenu().addPopupMenuListener(this);
	}

	public void setVsNameComboBox(JComboBox vsNameComboBox) {
		this.vsNameComboBox = vsNameComboBox;
	}

	public JComboBox getVsNameComboBox() {
		return vsNameComboBox;
	}

	public JPanel getDefaultViewPanel() {
		return this.defaultViewImagePanel;
	}
}
