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

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.RowSetEvent;
import org.cytoscape.model.events.RowSetListener;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.event.VizMapEventHandler;
import org.cytoscape.vizmap.gui.event.VizMapEventHandlerManager;
import org.cytoscape.vizmap.gui.theme.ColorManager;
import org.cytoscape.vizmap.gui.theme.IconManager;
import org.cytoscape.vizmap.mappings.ObjectMapping;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.Cytoscape;
import cytoscape.view.CySwingApplication;

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
 * @version 0.6
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends AbstractVizMapperPanel implements
		PropertyChangeListener, PopupMenuListener, ChangeListener {

	private final static long serialVersionUID = 1202339867854959L;

	private boolean ignore = false;
	private String lastVSName = null;

	// Current networks/views
	private CyNetwork targetNetwork;
	private GraphView targetView;

	/**
	 * Create new instance of VizMapperMainPanel object. GUI layout is handled
	 * by abstract class.
	 * 
	 * @param desktop
	 * @param dab
	 * @param iconMgr
	 * @param colorMgr
	 * @param vmm
	 * @param menuMgr
	 * @param editorFactory
	 */
	public VizMapperMainPanel(CySwingApplication desktop,
			DefaultAppearenceBuilder dab, IconManager iconMgr,
			ColorManager colorMgr, VisualMappingManager vmm,
			VizMapperMenuManager menuMgr, EditorFactory editorFactory,
			PropertySheetPanel propertySheetPanel,
			VizMapPropertySheetBuilder vizMapPropertySheetBuilder,
			VizMapEventHandlerManager vizMapEventHandlerManager, EditorWindowManager editorWindowManager) {
		super(desktop, dab, iconMgr, colorMgr, vmm, menuMgr, editorFactory,
				propertySheetPanel, vizMapPropertySheetBuilder,
				vizMapEventHandlerManager, editorWindowManager);

		buildVizMapperGUI();
	}

	private void buildVizMapperGUI() {
		vmm.addChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				new VizMapListener());

		registerCellEditorListeners();
		addVisualStyleChangeAction();

		// By default, force to sort property by prop name.
		propertySheetPanel.setSorting(true);

		// TODO Register these listeners as services
//		AttrEventListener ael1 = new AttrEventListener(this, getTargetNetwork()
//				.getNodeCyDataTables().get(CyNetwork.DEFAULT_ATTRS),
//				nodeAttrEditor, nodeNumericalAttrEditor);
//		AttrEventListener ael2 = new AttrEventListener(this, getTargetNetwork()
//				.getEdgeCyDataTables().get(CyNetwork.DEFAULT_ATTRS),
//				edgeAttrEditor, edgeNumericalAttrEditor);
//		AttrEventListener ael3 = new AttrEventListener(this, getTargetNetwork()
//				.getNetworkCyDataTables().get(CyNetwork.DEFAULT_ATTRS), null,
//				null);
		refreshUI();

		cytoscapeDesktop.getCytoPanel(SwingConstants.WEST).add(
				"VizMapper\u2122", this);
		cytoscapeDesktop.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);
	}

	/*
	 * Register listeners for editors.
	 */
	private void registerCellEditorListeners() {
		for (PropertyEditor p : editorFactory.getCellEditors()) {
			p.addPropertyChangeListener(this);

			if (p instanceof PropertyChangeListener)
				spcs.addPropertyChangeListener((PropertyChangeListener) p);
		}
	}

	private void addVisualStyleChangeAction() {
		getVsNameComboBox().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final String vsName = (String) getVsNameComboBox()
						.getSelectedItem();
				if (vsName != null) {
					if (getTargetView().equals(Cytoscape.getNullNetworkView()))
						switchVS(vsName, false);
					else
						switchVS(vsName, true);
				}
			}
		});
	}

	public void setLastVSName(final String newName) {
		this.lastVSName = newName;
	}

	public void switchVS(String vsName) {
		switchVS(vsName, true);
	}

	public void switchVS(String vsName, boolean redraw) {
		if (ignore)
			return;

		// If new VS name is the same, ignore.
		if (lastVSName == vsName)
			return;

		editorWindowManager.closeAllEditorWindows();

		vmm.setNetworkView(getTargetView());
		vmm.setVisualStyle(vsName);

		if (vizMapPropertySheetBuilder.getPropertyMap().containsKey(vsName)) {
			final List<Property> props = vizMapPropertySheetBuilder
					.getPropertyMap().get(vsName);
			final Map<String, Property> unused = new TreeMap<String, Property>();

			/*
			 * Remove currently shown property
			 */
			for (Property item : propertySheetPanel.getProperties())
				propertySheetPanel.removeProperty(item);

			/*
			 * Add properties to current property sheet.
			 */
			for (Property prop : props) {
				if (prop.getCategory().startsWith(CATEGORY_UNUSED) == false) {
					if (prop.getCategory().equals(NODE_VISUAL_MAPPING)) {
						propertySheetPanel.addProperty(0, prop);
					} else {
						propertySheetPanel.addProperty(prop);
					}
				} else {
					unused.put(prop.getDisplayName(), prop);
				}
			}

			final List<String> keys = new ArrayList<String>(unused.keySet());
			Collections.sort(keys);

			for (Object key : keys) {
				propertySheetPanel.addProperty(unused.get(key));
			}
		} else {
			vizMapPropertySheetBuilder.setPropertyTable();
			updateAttributeList();
		}

		vmm.setVisualStyleForView(getTargetView(), vmm.getVisualStyle(vsName));

		if (redraw)
			Cytoscape.redrawGraph(getTargetView());

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
		spcs.firePropertyChange("UPDATE_LOCK", null, lockState);
		propertySheetPanel.setSorting(true);

		// Cleanup desktop.
		// cytoscapeDesktop.repaint();
		getVsNameComboBox().setSelectedItem(vsName);
	}

	public void refreshUI() {
		List<String> vsNames = new ArrayList<String>(vmm.getCalculatorCatalog()
				.getVisualStyleNames());

		final VisualStyle style = vmm.getVisualStyle();

		// Disable action listeners
		final ActionListener[] li = getVsNameComboBox().getActionListeners();

		for (int i = 0; i < li.length; i++)
			getVsNameComboBox().removeActionListener(li[i]);

		getVsNameComboBox().removeAllItems();

		JPanel defPanel;

		final Dimension panelSize = defaultViewImagePanel.getSize();
		GraphView view;

		Collections.sort(vsNames);

		for (String name : vsNames) {
			getVsNameComboBox().addItem(name);
			defPanel = defAppBldr.getDefaultView(name);
			view = (GraphView) ((DefaultViewPanel) defPanel).getView();

			if (view != null)
				updateDefaultImage(name, view, panelSize);
		}

		vmm.setNetworkView(getTargetView());

		// Switch back to the original style.
		switchVS(style.getName());

		// Sync check box and actual lock state
		spcs.firePropertyChange("UPDATE_LOCK", null, true);

		// switchNodeSizeLock(lockSize.isSelected());

		// Restore listeners
		for (int i = 0; i < li.length; i++)
			getVsNameComboBox().addActionListener(li[i]);
	}

	/**
	 * Create image of a default dummy network and save in a Map object.
	 * 
	 * @param vsName
	 * @param view
	 * @param size
	 */
	public void updateDefaultImage(String vsName, GraphView view, Dimension size) {
		Image image = defaultImageManager.remove(vsName);

		if (image != null) {
			image.flush();
			image = null;
		}

		defaultImageManager.put(vsName, view.createImage((int) size.getWidth(),
				(int) size.getHeight(), 0.9));
	}

	public void updateAttributeList() {
		vizMapPropertySheetBuilder.setAttrComboBox();
		final Set mappingTypes = vmm.getCalculatorCatalog().getMappingNames();

		// mappingTypeEditor.setAvailableValues(mappingTypes.toArray());
		spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "mappingTypeEditor",
				mappingTypes.toArray());
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
	public void setDefaultViewImagePanel(final Image defImage) {
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

	public JPanel getDefaultPanel() {
		return defaultViewImagePanel;
	}

	class DefaultMouseListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
				final CyNetwork net = vmm.getNetwork();

				if (net == null)
					return;

				final String targetName = vmm.getVisualStyle().getName();
				final Long focus = net.getSUID();

				final DefaultViewPanel panel = (DefaultViewPanel) defAppBldr
						.showDialog(null);
				updateDefaultImage(targetName, (GraphView) panel.getView(),
						defaultViewImagePanel.getSize());
				setDefaultViewImagePanel(defaultImageManager.get(targetName));

				vmm.setNetworkView(getTargetView());
				vmm.setVisualStyle(targetName);

				// cytoscapeDesktop.setFocus(focus);
				// cytoscapeDesktop.repaint();
			}
		}
	}

	/**
	 * On/Off listeners. This is for performance.
	 * 
	 * @param on
	 *            DOCUMENT ME!
	 */
	private void enableListeners(boolean on) {
		if (on) {
			vmm.addChangeListener(this);
			syncStyleBox();
			ignore = false;
		} else {
			vmm.removeChangeListener(this);
		}
	}

	public void propertyChange(PropertyChangeEvent e) {
		// Set ignore flag.
		if (e.getPropertyName().equals(
				Integer.toString(Cytoscape.SESSION_OPENED))) {
			ignore = true;
			enableListeners(false);
		}

		if (ignore)
			return;

		final VizMapEventHandler handler = vizMapEventHandlerManager
				.getHandler(e.getPropertyName());
		if (handler != null)
			handler.processEvent(e);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vsName
	 *            DOCUMENT ME!
	 */
	public void setCurrentVS(String vsName) {
		getVsNameComboBox().setSelectedItem(vsName);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param arg0
	 *            DOCUMENT ME!
	 */
	public void popupMenuCanceled(PopupMenuEvent arg0) {
		// TODO: replace this to firePropertyChange
		// disableAllPopup();
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
		// disableAllPopup();

		final int selected = propertySheetPanel.getTable().getSelectedRow();

		if (0 > selected) {
			return;
		}

		final Item item = (Item) propertySheetPanel.getTable().getValueAt(
				selected, 0);
		final Property curProp = item.getProperty();

		if (curProp == null)
			return;

		VizMapperProperty prop = ((VizMapperProperty) curProp);

		if (prop.getHiddenObject() instanceof VisualPropertyType
				&& (prop.getDisplayName().contains("Mapping Type") == false)
				&& (prop.getValue() != null)
				&& (prop.getValue().toString().startsWith("Please select") == false)) {
			// Enble delete menu
			// delete.setEnabled(true);
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

			// if (dataType == Color.class) {
			// rainbow1.setEnabled(true);
			// rainbow2.setEnabled(true);
			// randomize.setEnabled(true);
			// brighter.setEnabled(true);
			// darker.setEnabled(true);
			// } else if (dataType == Number.class) {
			// randomize.setEnabled(true);
			// series.setEnabled(true);
			// }
			//
			// if ((type == VisualPropertyType.NODE_WIDTH)
			// || (type == VisualPropertyType.NODE_HEIGHT)) {
			// fit.setEnabled(true);
			// }
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
			obj = getTargetView().getGraphPerspective().getNodeList();
		} else {
			obj = getTargetView().getGraphPerspective().getEdgeList();
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
		final String selectedName = (String) getVsNameComboBox()
				.getSelectedItem();
		final String currentName = vmm.getVisualStyle().getName();

		final GraphView curView = getTargetView();

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
			for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
				if (getVsNameComboBox().getItemAt(i).equals(currentName)) {
					getVsNameComboBox().setSelectedIndex(i);

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

		for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
			styleName = getVsNameComboBox().getItemAt(i).toString();

			if (vmm.getCalculatorCatalog().getVisualStyle(styleName) == null) {
				// No longer exists in the VMM. Remove.
				getVsNameComboBox().removeItem(styleName);
				defaultImageManager.remove(styleName);
				vizMapPropertySheetBuilder.getPropertyMap().remove(styleName);
			}
		}

		Collections.sort(namesInBox);

		// Reset combobox items.
		getVsNameComboBox().removeAllItems();

		for (String name : namesInBox)
			getVsNameComboBox().addItem(name);

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
		for (int i = 0; i < getVsNameComboBox().getItemCount(); i++) {
			if (getVsNameComboBox().getItemAt(i).equals(match)) {
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
		final JTable table = propertySheetPanel.getTable();

		return table.getModel().getValueAt(table.getSelectedRow(), 0);
	}

	public void setTargetNetwork(CyNetwork targetNetwork) {
		this.targetNetwork = targetNetwork;
	}

	public CyNetwork getTargetNetwork() {
		return targetNetwork;
	}

	public void setTargetView(GraphView targetView) {
		this.targetView = targetView;
	}

	public GraphView getTargetView() {
		return targetView;
	}

	// **************************************************************************
	// MultiHashMapListenerAdaptor
	private class AttrEventListener implements ColumnDeletedListener,
			RowSetListener {
		// ref to members
		private final JPanel container;
		private final CyDataTable attr;
		private final PropertyEditor attrEditor;
		private final PropertyEditor numericalAttrEditor;
		private final List<String> attrEditorNames;
		private final List<String> numericalAttrEditorNames;

		/**
		 * Constructor.
		 * 
		 * @param cyAttributes
		 *            CyDataTable
		 */
		AttrEventListener(JPanel container, CyDataTable cyAttributes,
				PropertyEditor attrEditor, PropertyEditor numericalAttrEditor) {
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
			if (attr == getTargetNetwork().getNetworkCyDataTables().get(
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
				// attrEditor.setAvailableValues(attrEditorNames.toArray());
				spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "attrEditor",
						attrEditorNames.toArray());
				repaint = true;
			}

			// if attribute is not contained in numericalAttrEditorNames, add it
			// if we support its class
			if (!numericalAttrEditorNames.contains(attributeName)) {
				Class<?> dataClass = attr.getColumnTypeMap().get(attributeName);

				if ((dataClass == Integer.class) || (dataClass == Double.class)) {
					numericalAttrEditorNames.add(attributeName);
					Collections.sort(numericalAttrEditorNames);
					// numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
					spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
							"numericalAttrEditorNames",
							numericalAttrEditorNames.toArray());
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
			if (attr == getTargetNetwork().getNetworkCyDataTables().get(
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
				// attrEditor.setAvailableValues(attrEditorNames.toArray());
				spcs.firePropertyChange("UPDATE_AVAILABLE_VAL", "attrEditor",
						attrEditorNames.toArray());
				repaint = true;
			}

			// if attribute is in numericalAttrEditorNames, remove it
			if (numericalAttrEditorNames.contains(attributeName)) {
				numericalAttrEditorNames.remove(attributeName);
				Collections.sort(numericalAttrEditorNames);
				// numericalAttrEditor.setAvailableValues(numericalAttrEditorNames.toArray());
				spcs.firePropertyChange("UPDATE_AVAILABLE_VAL",
						"numericalAttrEditor", numericalAttrEditorNames
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
