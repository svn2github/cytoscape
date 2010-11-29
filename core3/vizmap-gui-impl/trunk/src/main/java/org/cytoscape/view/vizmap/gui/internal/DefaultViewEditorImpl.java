/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.view.vizmap.gui.internal;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.DefaultViewEditor;
import org.cytoscape.view.vizmap.gui.SelectedVisualStyleManager;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEvent;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedListener;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;

/**
 * Dialog for editing default visual property values.<br>
 * This is a modal dialog.
 * 
 * <p>
 * Basic idea is the following:
 * <ul>
 * <li>Build dummy network with 2 nodes and 1 edge.</li>
 * <li>Edit the default appearence of the dummy network</li>
 * <li>Create a image from the dummy.</li>
 * </ul>
 * </p>
 * 
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class DefaultViewEditorImpl extends JDialog implements
		DefaultViewEditor, SelectedVisualStyleSwitchedListener {

	private final static long serialVersionUID = 1202339876675416L;
	
	private static final int ICON_WIDTH = 48;
	private static final int ICON_HEIGHT = 48;

	private final Map<Class<? extends CyTableEntry>, Set<VisualProperty<?>>> vpSets;
	private final Map<Class<? extends CyTableEntry>, JList> listMap;

	private final CyApplicationManager cyApplicationManager;

	private EditorManager editorFactory;

	private VisualMappingManager vmm;
	final SelectedVisualStyleManager selectedManager;

	/**
	 * Creates a new DefaultAppearenceBuilder object.
	 * 
	 * @param parent
	 *            DOCUMENT ME!
	 * @param modal
	 *            DOCUMENT ME!
	 */
	public DefaultViewEditorImpl(final DefaultViewPanelImpl mainView,
			final EditorManager editorFactory,
			final CyApplicationManager cyApplicationManager,
			final VisualMappingManager vmm,
			final SelectedVisualStyleManager selectedManager) {
		super();
		this.vmm = vmm;
		this.selectedManager = selectedManager;
		vpSets = new HashMap<Class<? extends CyTableEntry>, Set<VisualProperty<?>>>();
		listMap = new HashMap<Class<? extends CyTableEntry>, JList>();

		this.cyApplicationManager = cyApplicationManager;
		this.setModal(true);
		this.mainView = mainView;
		this.editorFactory = editorFactory;

		updateVisualPropertyLists();

		initComponents();
		buildList();

		// Listening to resize event.
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				defaultObjectTabbedPane.repaint();
				mainView.repaint();
				mainView.updateView();
			}
		});

	}

	private void updateVisualPropertyLists() {
		vpSets.clear();
		VisualStyle selectedStyle = selectedManager.getCurrentVisualStyle();

		final VisualLexicon lexicon = selectedStyle.getVisualLexicon();

		vpSets.put(CyNode.class,
				getLeafNodes(lexicon.getAllDescendants(TwoDVisualLexicon.NODE)));
		vpSets.put(CyEdge.class,
				getLeafNodes(lexicon.getAllDescendants(TwoDVisualLexicon.EDGE)));
		vpSets.put(CyNetwork.class, getNetworkLeafNodes(lexicon
				.getAllDescendants(TwoDVisualLexicon.NETWORK)));

	}

	private Set<VisualProperty<?>> getLeafNodes(
			final Collection<VisualProperty<?>> props) {
		final VisualStyle selectedStyle = selectedManager
				.getCurrentVisualStyle();
		final VisualLexicon lexicon = selectedStyle.getVisualLexicon();
		final Set<VisualProperty<?>> propSet = new TreeSet<VisualProperty<?>>(
				new VisualPropertyComparator());

		for (VisualProperty<?> vp : props) {
			if (lexicon.getVisualLexiconNode(vp).getChildren().size() == 0)
				propSet.add(vp);
		}

		return propSet;

	}

	private Set<VisualProperty<?>> getNetworkLeafNodes(
			final Collection<VisualProperty<?>> props) {
		final VisualStyle selectedStyle = selectedManager
				.getCurrentVisualStyle();
		final VisualLexicon lexicon = selectedStyle.getVisualLexicon();
		final Set<VisualProperty<?>> propSet = new TreeSet<VisualProperty<?>>(
				new VisualPropertyComparator());

		for (VisualProperty<?> vp : props) {
			if (lexicon.getVisualLexiconNode(vp).getChildren().size() == 0
					&& lexicon.getVisualLexiconNode(vp).getParent()
							.getVisualProperty() == TwoDVisualLexicon.NETWORK)
				propSet.add(vp);
		}

		return propSet;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.internal.DefaultViewEditor#showDialog(java.awt
	 * .Component)
	 */
	public void showEditor(Component parent) {
		updateVisualPropertyLists();
		buildList();

		setSize(900, 450);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.internal.DefaultViewEditor#getDefaultView(java
	 * .lang.String)
	 */
	public JPanel getDefaultView(String vsName) {
		// TODO: update background color
		// mainView.updateBackgroungColor(vmm.getVisualStyle()
		// .getGlobalAppearanceCalculator().getDefaultBackgroundColor());

		// mainView.updateView();

		return mainView;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		jXPanel1 = new JXPanel();
		jXTitledPanel1 = new org.jdesktop.swingx.JXTitledPanel();
		defaultObjectTabbedPane = new javax.swing.JTabbedPane();
		nodeScrollPane = new javax.swing.JScrollPane();
		nodeList = new JXList();
		edgeList = new JXList();
		edgeScrollPane = new javax.swing.JScrollPane();
		globalScrollPane = new javax.swing.JScrollPane();
		lockNodeSizeCheckBox = new javax.swing.JCheckBox();
		applyButton = new javax.swing.JButton();

		networkList = new JXList();

		listMap.put(CyNode.class, nodeList);
		listMap.put(CyEdge.class, edgeList);
		listMap.put(CyNetwork.class, networkList);

		cancelButton = new javax.swing.JButton();
		cancelButton.setVisible(false);

		lockNodeSizeCheckBox.setOpaque(false);

		nodeList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				listActionPerformed(e);
			}
		});

		edgeList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				listActionPerformed(e);
			}
		});

		networkList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				listActionPerformed(e);
			}
		});

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		mainView.setBorder(new javax.swing.border.LineBorder(
				java.awt.Color.darkGray, 1, true));

		GroupLayout jXPanel2Layout = new GroupLayout(mainView);
		mainView.setLayout(jXPanel2Layout);
		jXPanel2Layout.setHorizontalGroup(jXPanel2Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGap(0, 300, Short.MAX_VALUE));
		jXPanel2Layout.setVerticalGroup(jXPanel2Layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGap(0, 237, Short.MAX_VALUE));

		jXTitledPanel1.setTitle("Default Visual Properties");
		// TODO: fix gradient
		// jXTitledPanel1.setTitlePainter(new BasicGradientPainter(
		// new Point2D.Double(.2d, 0), new Color(Color.gray.getRed(),
		// Color.gray.getGreen(), Color.gray.getBlue(), 100),
		// new Point2D.Double(.8d, 0), Color.WHITE));
		jXTitledPanel1.setTitleFont(new java.awt.Font("SansSerif", 1, 12));
		jXTitledPanel1.setMinimumSize(new java.awt.Dimension(300, 27));
		jXTitledPanel1.setPreferredSize(new java.awt.Dimension(300, 27));
		defaultObjectTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

		nodeScrollPane.setViewportView(nodeList);
		edgeScrollPane.setViewportView(edgeList);
		globalScrollPane.setViewportView(networkList);

		defaultObjectTabbedPane.addTab("Node", nodeScrollPane);
		defaultObjectTabbedPane.addTab("Edge", edgeScrollPane);
		defaultObjectTabbedPane.addTab("Network", globalScrollPane);

		GroupLayout jXTitledPanel1Layout = new GroupLayout(
				jXTitledPanel1.getContentContainer());
		jXTitledPanel1.getContentContainer().setLayout(jXTitledPanel1Layout);
		jXTitledPanel1Layout.setHorizontalGroup(jXTitledPanel1Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(defaultObjectTabbedPane,
						GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE));
		jXTitledPanel1Layout.setVerticalGroup(jXTitledPanel1Layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(defaultObjectTabbedPane,
						GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE));

		lockNodeSizeCheckBox.setFont(new java.awt.Font("SansSerif", 1, 12));
		lockNodeSizeCheckBox.setText("Lock Node Width/Height");
		lockNodeSizeCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		lockNodeSizeCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		// TODO: fix lock
		// lockNodeSizeCheckBox.setSelected(nac.getNodeSizeLocked());
		lockNodeSizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lockSize();
			}
		});

		applyButton.setText("Apply");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final CyNetworkView view = cyApplicationManager
						.getCurrentNetworkView();
				if (view != null)
					applyNewStyle(view);
				dispose();
			}
		});

		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
		});

		GroupLayout jXPanel1Layout = new GroupLayout(jXPanel1);
		jXPanel1.setLayout(jXPanel1Layout);
		jXPanel1Layout
				.setHorizontalGroup(jXPanel1Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								jXPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jXPanel1Layout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																jXPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				lockNodeSizeCheckBox,
																				GroupLayout.DEFAULT_SIZE,
																				138,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				cancelButton)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				applyButton))
														.addComponent(
																mainView,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jXTitledPanel1,
												GroupLayout.PREFERRED_SIZE,
												198, Short.MAX_VALUE)
										.addGap(12, 12, 12)));
		jXPanel1Layout
				.setVerticalGroup(jXPanel1Layout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								GroupLayout.Alignment.TRAILING,
								jXPanel1Layout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												jXPanel1Layout
														.createParallelGroup(
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																jXTitledPanel1,
																GroupLayout.Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																270,
																Short.MAX_VALUE)
														.addGroup(
																jXPanel1Layout
																		.createSequentialGroup()
																		.addComponent(
																				mainView,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addGroup(
																				jXPanel1Layout
																						.createParallelGroup(
																								GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								lockNodeSizeCheckBox)
																						.addComponent(
																								cancelButton)
																						.addComponent(
																								applyButton))))
										.addContainerGap()));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(jXPanel1,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addComponent(jXPanel1,
				GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
				Short.MAX_VALUE));
		pack();
	} // </editor-fold>

	private <V> void listActionPerformed(MouseEvent e) {
		final Object source = e.getSource();
		final JList list;
		if (source instanceof JList)
			list = (JList) source;
		else
			return;

		V newValue = null;

		final VisualProperty<V> vp = (VisualProperty<V>) list
				.getSelectedValue();

		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
			final VisualStyle selectedStyle = selectedManager
					.getCurrentVisualStyle();
			final V defaultVal = selectedStyle.getDefaultValue(vp);
			try {
				if (defaultVal != null)
					newValue = editorFactory.showVisualPropertyValueEditor(
							this, vp, defaultVal);
				else
					newValue = editorFactory.showVisualPropertyValueEditor(
							this, vp, vp.getDefault());
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			if (newValue != null) {
				selectedStyle.setDefaultValue(vp, newValue);
				selectedStyle.apply(cyApplicationManager
						.getCurrentNetworkView());
			}

			repaint();
			this.mainView.getView().updateView();
		}
	}

	private void applyNewStyle(CyNetworkView view) {
		VisualStyle curVS = vmm.getVisualStyle(view);
		final VisualStyle selectedStyle = selectedManager
				.getCurrentVisualStyle();

		if (curVS == null) {
			// Set new style
			vmm.setVisualStyle(selectedStyle, view);
		}

		selectedStyle.apply(view);
		view.updateView();
	}

	// Variables declaration - do not modify
	private javax.swing.JButton applyButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JCheckBox lockNodeSizeCheckBox;
	private javax.swing.JScrollPane nodeScrollPane;
	private javax.swing.JScrollPane edgeScrollPane;
	private javax.swing.JScrollPane globalScrollPane;
	private javax.swing.JTabbedPane defaultObjectTabbedPane;
	private JXList nodeList;
	private JXList edgeList;
	private JXList networkList;
	private org.jdesktop.swingx.JXPanel jXPanel1;

	private org.jdesktop.swingx.JXTitledPanel jXTitledPanel1;

	// End of variables declaration
	protected DefaultViewPanelImpl mainView;

	/**
	 * DOCUMENT ME!
	 */
	private void buildList() {

		final VisualPropCellRenderer renderer = new VisualPropCellRenderer();

		for (Class<? extends CyTableEntry> key : vpSets.keySet()) {
			DefaultListModel model = new DefaultListModel();
			JList list = listMap.get(key);
			list.setModel(model);
			Set<VisualProperty<?>> vps = vpSets.get(key);
			for (VisualProperty<?> vp : vps) {
				model.addElement(vp);
				// logger.debug("New Visual Property set to GUI: "
				// + vp.getDisplayName() + " = " + vp.getDefault());
			}
			list.setCellRenderer(renderer);
		}

		// mainView.updateView();
		// mainView.repaint();
	}

	private void lockSize() {
		// TODO fix lock function
		// if (lockNodeSizeCheckBox.isSelected()) {
		// nodeVp.remove(NODE_WIDTH);
		// nodeVp.remove(NODE_HEIGHT);
		// nodeVp.add(NODE_SIZE);
		// nac.setNodeSizeLocked(true);
		// } else {
		// nodeVp.add(NODE_WIDTH);
		// nodeVp.add(NODE_HEIGHT);
		// nodeVp.remove(NODE_SIZE);
		// nac.setNodeSizeLocked(false);
		// }
		//
		// buildList();
		// mainView.updateView();
		// repaint();
	}

	/**
	 * Create cells for each Visual Properties.
	 * 
	 * @author kono
	 * 
	 */
	class VisualPropCellRenderer extends JLabel implements ListCellRenderer {
		private final static long serialVersionUID = 1202339876646385L;

		private final Font SELECTED_FONT = new Font("SansSerif", Font.ITALIC,
				14);
		private final Font NORMAL_FONT = new Font("SansSerif", Font.BOLD, 12);
		private final Color SELECTED_COLOR = new Color(10, 50, 180, 20);
		private final Color SELECTED_FONT_COLOR = new Color(0, 150, 255, 150);

		public VisualPropCellRenderer() {
			setOpaque(true);
		}

		@Override public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			final VisualStyle selectedStyle = selectedManager
					.getCurrentVisualStyle();
			Icon icon = null;
			VisualProperty<Object> vp = null;

			if (value instanceof VisualProperty<?>) {
				vp = (VisualProperty<Object>) value;

				RenderingEngine<?> presentation = cyApplicationManager
						.getCurrentRenderingEngine();
				if (presentation != null)
					icon = presentation.createIcon(vp, selectedStyle.getDefaultValue(vp), ICON_WIDTH, ICON_HEIGHT);
			}
			setText(vp.getDisplayName() + "  =  "
					+ selectedStyle.getDefaultValue(vp));
			setToolTipText(vp.getDisplayName());
			setIcon(icon);
			setFont(isSelected ? SELECTED_FONT : NORMAL_FONT);

			this.setVerticalTextPosition(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
			this.setIconTextGap(55);

			if (vp != null && vp.getRange().getType() != null
					&& vp.getRange().getType().equals(String.class))
				this.setToolTipText(vp.getDefault().toString());

			setBackground(isSelected ? SELECTED_COLOR : list.getBackground());
			setForeground(isSelected ? SELECTED_FONT_COLOR : list
					.getForeground());

			if (icon != null) {
				setPreferredSize(new Dimension(250, icon.getIconHeight() + 12));
			} else {
				setPreferredSize(new Dimension(250, 55));
			}

			this.setBorder(new DropShadowBorder());

			return this;
		}
	}

	// /*
	// * Draw global color icon
	// */
	// class GlobalIcon extends VisualPropertyIcon {
	// private final static long serialVersionUID = 1202339876659938L;
	//
	// public GlobalIcon(String name, Color color) {
	// super(name, color);
	// }
	//
	// public void paintIcon(Component c, Graphics g, int x, int y) {
	// Graphics2D g2d = (Graphics2D) g;
	//
	// g2d.setColor(color);
	// g2d.fillRect(5, 3, 50, 32);
	//
	// g2d.setStroke(new BasicStroke(1f));
	// g2d.setColor(Color.DARK_GRAY);
	// g2d.drawRect(5, 3, 50, 32);
	// }
	// }

	public Component getDefaultView(VisualStyle vs) {
		// mainView.updateView();

		return mainView;
	}

	public void handleEvent(SelectedVisualStyleSwitchedEvent e) {

		final VisualStyle selectedStyle = e.getNewVisualStyle();
		setTitle("Default Appearance for " + selectedStyle.getTitle());

	}

	private static class VisualPropertyComparator implements
			Comparator<VisualProperty<?>> {

		@Override
		public int compare(VisualProperty<?> vp1, VisualProperty<?> vp2) {
			String name1 = vp1.getDisplayName();
			String name2 = vp2.getDisplayName();

			return name1.compareTo(name2);
		}

	}
}
