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
package org.cytoscape.view.vizmap.gui.internal;

import static org.cytoscape.model.GraphObject.EDGE;
import static org.cytoscape.model.GraphObject.NETWORK;
import static org.cytoscape.model.GraphObject.NODE;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.cytoscape.model.GraphObject;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.DefaultViewEditor;
import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEvent;
import org.cytoscape.view.vizmap.gui.event.SelectedVisualStyleSwitchedEventListener;
import org.jdesktop.swingx.JXList;
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
		DefaultViewEditor, SelectedVisualStyleSwitchedEventListener {

	private final static long serialVersionUID = 1202339876675416L;

	private Map<String, Set<VisualProperty<?>>> vpSets;
	private Map<String, JList> listMap;

	private CyNetworkManager cyNetworkManager;

	private EditorManager editorFactory;

	private VisualStyle selectedStyle;

	private VisualMappingManager vmm;

	private JPopupMenu contextMenu;

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
			final CyNetworkManager cyNetworkManager, VisualStyle defaultStyle,
			VisualMappingManager vmm) {

		super();
		this.vmm = vmm;
		this.selectedStyle = defaultStyle;
		vpSets = new HashMap<String, Set<VisualProperty<?>>>();
		listMap = new HashMap<String, JList>();

		this.cyNetworkManager = cyNetworkManager;
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
				// mainView.updateView();
			}
		});

		setupPopupMenu();
	}

	private void setupPopupMenu() {
		this.contextMenu = new JPopupMenu();
	}

	private void updateVisualPropertyLists() {
		vpSets.clear();

		VisualLexicon lexicon = selectedStyle.getVisualLexicon();

		vpSets.put(NODE, new HashSet<VisualProperty<?>>(lexicon
				.getVisualProperties(NODE)));
		vpSets.put(EDGE, new HashSet<VisualProperty<?>>(lexicon
				.getVisualProperties(EDGE)));
		vpSets.put(NETWORK, new HashSet<VisualProperty<?>>(lexicon
				.getVisualProperties(NETWORK)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.internal.DefaultViewEditor#showDialog(java.awt
	 * .Component)
	 */
	public void showEditor(Component parent) {
		setSize(900, 400);

		// TODO: fix the width/height lock
		// lockSize();
		// lockNodeSizeCheckBox.setSelected(nac.getNodeSizeLocked());

		// mainView.updateView();

		updateVisualPropertyLists();
		// initComponents();
		buildList();

		repaint();
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
		jXPanel1 = new org.jdesktop.swingx.JXPanel();
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

		listMap.put(NODE, nodeList);
		listMap.put(EDGE, edgeList);
		listMap.put(NETWORK, networkList);

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

		GroupLayout jXTitledPanel1Layout = new GroupLayout(jXTitledPanel1
				.getContentContainer());
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
				// vmm.setNetworkView(cyNetworkManager.getCurrentNetworkView());
				// Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());
				applyNewStyle(cyNetworkManager.getCurrentNetworkView());
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
												198, Short.MAX_VALUE).addGap(
												12, 12, 12)));
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
		V newValue = null;
		final JList list = (JList) e.getSource();
		final VisualProperty<V> vp = (VisualProperty<V>) list
				.getSelectedValue();

		if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {

			try {
				newValue = editorFactory.showVisualPropertyValueEditor(this,
						vp, null);

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (newValue != null) {
				System.out.println("New value =======> "
						+ newValue
						+ ", cur view = "
						+ cyNetworkManager.getCurrentNetworkView()
								.getNodeViews().size());
				System.out.println("Nodes = "
						+ cyNetworkManager.getCurrentNetwork().getNodeCount());

				selectedStyle.setDefaultValue(vp, newValue);
				selectedStyle.apply(cyNetworkManager.getCurrentNetworkView());
			}
			updateVisualPropertyLists();
			buildList();
			cyNetworkManager.getCurrentNetworkView().updateView();
			// Cytoscape.redrawGraph(cyNetworkManager.getCurrentNetworkView());
			// mainView.updateView();
			// mainView.repaint();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			if (vp != null) {

				contextMenu.removeAll();
				final CyNetworkView networkView = cyNetworkManager
						.getCurrentNetworkView();

				System.out.println("##### target value =======> "
						+ vp.getDisplayName());

				final JMenuItem lockItemMenu = new JCheckBoxMenuItem(
						"Lock this Visual Property") {

					public void ActionPerformed(ActionEvent e) {
						boolean lock = false;
						if (vp.getObjectType().equals(GraphObject.NETWORK)) {
							lock = networkView.isValueLocked(vp);
						} else if (vp.getObjectType().equals(GraphObject.NODE)) {

						}

						if (lock)
							this.setSelected(false);
						else
							this.setSelected(true);
					}

				};
				contextMenu.add(lockItemMenu);
				// Display Context menu here
				contextMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void applyNewStyle(CyNetworkView view) {
		VisualStyle curVS = vmm.getVisualStyle(view);

		if (curVS == null) {
			// Set new style
			vmm.setVisualStyle(selectedStyle, view);
		}

		System.out.println("Cur VS = " + curVS);
		selectedStyle.apply(cyNetworkManager.getCurrentNetworkView());
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

		VisualPropCellRenderer renderer = new VisualPropCellRenderer();

		for (String key : vpSets.keySet()) {
			DefaultListModel model = new DefaultListModel();
			JList list = listMap.get(key);
			list.setModel(model);
			Set<VisualProperty<?>> vps = vpSets.get(key);
			for (VisualProperty<?> vp : vps) {
				model.addElement(vp);
				System.out.println("##### New VP Def ---> "
						+ vp.getDisplayName() + " = " + vp.getDefault());
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

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {

			Icon icon = null;
			VisualProperty<?> vp = null;

			if (value instanceof VisualProperty<?>) {
				vp = (VisualProperty<?>) value;

				RenderingEngine presentation = cyNetworkManager
						.getCurrentPresentation();
				icon = presentation.getDefaultIcon(vp);
			}
			setText(vp.getDisplayName() + "  =  "
					+ selectedStyle.getDefaultValue(vp));
			setToolTipText(vp.getDisplayName());
			setIcon(icon);
			setFont(isSelected ? SELECTED_FONT : NORMAL_FONT);

			this.setVerticalTextPosition(SwingConstants.CENTER);
			this.setVerticalAlignment(SwingConstants.CENTER);
			this.setIconTextGap(55);

			if (vp != null && vp.getType() != null
					&& vp.getType().equals(String.class))
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
		this.selectedStyle = e.getNewVisualStyle();
		setTitle("Default Appearance for " + selectedStyle.getTitle());

	}
}
